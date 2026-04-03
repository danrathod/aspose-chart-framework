package com.chartframework.manager;

import com.aspose.cells.*;
import com.chartframework.exception.ChartFrameworkException;
import com.chartframework.model.ChartConfig;
import com.chartframework.model.ChartRequest;
import com.chartframework.model.DataRange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Manages the lifecycle of hidden data sheets within a workbook.
 *
 * <h2>Responsibilities</h2>
 * <ol>
 *   <li>Generate unique names for hidden data sheets so multiple charts
 *       never collide on the same data region.</li>
 *   <li>Create (or locate existing) hidden sheets.</li>
 *   <li>Write raw chart data into a hidden sheet, determining the next
 *       available row to avoid overwriting existing data.</li>
 *   <li>Return a {@link DataRange} that tells chart strategies exactly
 *       where the data lives.</li>
 * </ol>
 *
 * <h2>Hidden Sheet Naming Convention</h2>
 * <p>All hidden sheets are prefixed with {@value #HIDDEN_SHEET_PREFIX}.
 * Each call to {@link #writeData} appends an atomically-incremented counter,
 * producing names like {@code __chartdata_1}, {@code __chartdata_2}, etc.
 * The framework creates one hidden sheet <em>per chart request</em> by default
 * to guarantee complete data isolation and simplify range references.</p>
 */
public class HiddenSheetManager {

    private static final Logger log = LoggerFactory.getLogger(HiddenSheetManager.class);

    /** Prefix that visually groups all framework-managed hidden sheets. */
    public static final String HIDDEN_SHEET_PREFIX = "__chartdata_";

    /**
     * Global counter — using AtomicInteger makes the manager safe for
     * concurrent use within a single JVM (though Aspose Workbook itself
     * is not thread-safe and must be locked externally if shared).
     */
    private final AtomicInteger sheetCounter = new AtomicInteger(0);

    // ─────────────────────────────────────────────────────────────────────────
    // Public API
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Writes the chart data from {@code request} into a freshly-created hidden
     * sheet and returns a {@link DataRange} describing the written region.
     *
     * @param request The chart request carrying the data to persist.
     * @return A {@link DataRange} with exact sheet + row/column indices.
     */
    public DataRange writeData(ChartRequest request) {
        Workbook       workbook   = request.getWorkbook();
        List<List<Object>> data   = request.getData();
        ChartConfig    config     = request.effectiveConfig();
        String         sheetName  = generateUniqueSheetName(request);

        Worksheet hiddenSheet = createHiddenSheet(workbook, sheetName);

        int startRow    = 0;
        int startColumn = 0;
        int totalRows   = data.size();
        int totalCols   = data.isEmpty() ? 0 : data.get(0).size();

        writeRows(hiddenSheet, data, startRow, startColumn);

        int dataRowCount = config.isFirstRowIsHeader() ? totalRows - 1 : totalRows;
        int seriesCount  = config.isFirstColumnIsCategory() ? totalCols - 1 : totalCols;

        DataRange range = DataRange.builder()
                .sheetName(sheetName)
                .startRow(startRow)
                .endRow(startRow + totalRows - 1)
                .startColumn(startColumn)
                .endColumn(startColumn + totalCols - 1)
                .dataRowCount(dataRowCount)
                .seriesCount(seriesCount)
                .build();

        log.info("Written chart data to hidden sheet '{}', rows={}, cols={}, range={}",
                sheetName, totalRows, totalCols,
                range.toAbsoluteRange(startRow, startColumn,
                        startRow + totalRows - 1, startColumn + totalCols - 1));
        return range;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Private helpers
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Generates a unique hidden sheet name. The caller may supply a preferred
     * base name; we always append the counter to guarantee uniqueness.
     */
    private String generateUniqueSheetName(ChartRequest request) {
        int    index    = sheetCounter.incrementAndGet();
        String baseName = (request.getPreferredHiddenSheetBaseName() != null
                && !request.getPreferredHiddenSheetBaseName().isBlank())
                ? sanitize(request.getPreferredHiddenSheetBaseName())
                : HIDDEN_SHEET_PREFIX;

        // Excel sheet names max length = 31 chars; trim if necessary
        String candidate = baseName + index;
        if (candidate.length() > 31) {
            candidate = candidate.substring(candidate.length() - 31);
        }
        return candidate;
    }

    /**
     * Creates a brand-new sheet in the workbook, hides it, and returns it.
     * Throws if a sheet with the same name already exists (shouldn't happen
     * given the counter, but defensive check is good practice).
     */
    private Worksheet createHiddenSheet(Workbook workbook, String sheetName) {
        WorksheetCollection sheets = workbook.getWorksheets();

        // Safety: if somehow a name collision occurs, throw early
        if (sheets.get(sheetName) != null) {
            throw new ChartFrameworkException(
                    "Hidden sheet '" + sheetName + "' already exists — counter may have wrapped.");
        }

        int index   = sheets.add(sheetName);
        Worksheet ws = sheets.get(index);
        ws.setVisible(false);   // ← hide from end-user

        log.debug("Created hidden sheet '{}' at index {}", sheetName, index);
        return ws;
    }

    /**
     * Writes a 2-D list of objects into the worksheet starting at
     * ({@code startRow}, {@code startColumn}).
     *
     * <p>Type dispatch order: {@code Number} → numeric cell,
     * {@code Date} → date cell, {@code Boolean} → boolean cell,
     * everything else → string cell.</p>
     */
    private void writeRows(Worksheet ws,
                           List<List<Object>> data,
                           int startRow,
                           int startCol) {
        Cells cells = ws.getCells();

        for (int r = 0; r < data.size(); r++) {
            List<Object> row = data.get(r);
            if (row == null) continue;

            for (int c = 0; c < row.size(); c++) {
                Object value = row.get(c);
                Cell   cell  = cells.get(startRow + r, startCol + c);
                setCellValue(cell, value);
            }
        }
    }

    /** Type-safe cell value assignment. */
    private void setCellValue(Cell cell, Object value) {
        if (value == null) {
            cell.putValue("");
        } else if (value instanceof Number num) {
            cell.putValue(num.doubleValue());
        } else if (value instanceof Boolean b) {
            cell.putValue(b);
        } else if (value instanceof Date d) {
            cell.putValue(d);
        } else {
            cell.putValue(value.toString());
        }
    }

    /** Strips characters that are illegal in Excel sheet names. */
    private String sanitize(String name) {
        // Excel prohibits: \ / ? * [ ]  and the colon char
        return name.replaceAll("[\\\\/?*\\[\\]:]", "_");
    }
}
