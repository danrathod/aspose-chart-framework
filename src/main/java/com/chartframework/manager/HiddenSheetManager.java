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
 *   <li>Generate unique names for hidden data sheets.</li>
 *   <li>Create (and immediately hide) the sheet.</li>
 *   <li>Write raw chart data into the hidden sheet with type-aware dispatch.</li>
 *   <li>Return a {@link DataRange} that tells chart strategies exactly where
 *       the data lives so they can build correct Aspose formula references.</li>
 * </ol>
 *
 * <h2>Hidden Sheet Naming</h2>
 * <p>All sheets are prefixed with {@value #HIDDEN_SHEET_PREFIX} and suffixed
 * with an atomically-incremented counter: {@code __chartdata_1},
 * {@code __chartdata_2}, etc. One hidden sheet is created per
 * {@link #writeData} call, guaranteeing complete data isolation between charts.</p>
 */
public class HiddenSheetManager {

    private static final Logger log = LoggerFactory.getLogger(HiddenSheetManager.class);

    /** Prefix used for all framework-managed hidden sheets. */
    public static final String HIDDEN_SHEET_PREFIX = "__chartdata_";

    private final AtomicInteger sheetCounter = new AtomicInteger(0);

    // ─────────────────────────────────────────────────────────────────────────
    // Public API
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Writes the chart data from {@code request} into a new hidden sheet of
     * {@code workbook} and returns a {@link DataRange} describing the written region.
     *
     * @param request  The chart request carrying data and config.
     * @param workbook The live Aspose workbook (managed by {@link com.chartframework.service.ChartService}).
     * @return A {@link DataRange} with the hidden sheet name and row/column bounds.
     */
    public DataRange writeData(ChartRequest request, Workbook workbook) {
        List<List<Object>> data  = request.getData();
        ChartConfig        config = request.effectiveConfig();
        String             sheetName = generateUniqueSheetName(request, workbook);

        Worksheet hiddenSheet = createHiddenSheet(workbook, sheetName);

        int startRow  = 0;
        int startCol  = 0;
        int totalRows = data.size();
        int totalCols = data.isEmpty() ? 0 : data.get(0).size();

        writeRows(hiddenSheet, data, startRow, startCol);

        int dataRowCount = config.isFirstRowIsHeader() ? totalRows - 1 : totalRows;
        int seriesCount  = config.isFirstColumnIsCategory() ? totalCols - 1 : totalCols;

        DataRange range = DataRange.builder()
                .sheetName(sheetName)
                .startRow(startRow)
                .endRow(startRow + totalRows - 1)
                .startColumn(startCol)
                .endColumn(startCol + totalCols - 1)
                .dataRowCount(dataRowCount)
                .seriesCount(seriesCount)
                .build();

        log.info("Written chart data to hidden sheet '{}' — rows={}, cols={}",
                sheetName, totalRows, totalCols);
        return range;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Private helpers
    // ─────────────────────────────────────────────────────────────────────────

    private String generateUniqueSheetName(ChartRequest request, Workbook workbook) {
        String baseName = (request.getPreferredHiddenSheetBaseName() != null
                && !request.getPreferredHiddenSheetBaseName().isBlank())
                ? sanitize(request.getPreferredHiddenSheetBaseName())
                : HIDDEN_SHEET_PREFIX;

        String candidate;
        do {
            int index = sheetCounter.incrementAndGet();
            candidate = baseName + index;
            // Truncate to Excel's 31-character sheet-name limit
            if (candidate.length() > 31) {
                candidate = candidate.substring(candidate.length() - 31);
            }
        } while (workbook.getWorksheets().get(candidate) != null); // ensure uniqueness

        return candidate;
    }

    private Worksheet createHiddenSheet(Workbook workbook, String sheetName) {
        WorksheetCollection sheets = workbook.getWorksheets();
        if (sheets.get(sheetName) != null) {
            throw new ChartFrameworkException(
                    "Hidden sheet '" + sheetName + "' already exists in the workbook.");
        }
        int       index = sheets.add(sheetName);
        Worksheet ws    = sheets.get(index);
        ws.setVisible(false);
        log.debug("Created hidden sheet '{}' at index {}", sheetName, index);
        return ws;
    }

    private void writeRows(Worksheet ws,
                           List<List<Object>> data,
                           int startRow,
                           int startCol) {
        Cells cells = ws.getCells();
        for (int r = 0; r < data.size(); r++) {
            List<Object> row = data.get(r);
            if (row == null) continue;
            for (int c = 0; c < row.size(); c++) {
                setCellValue(cells.get(startRow + r, startCol + c), row.get(c));
            }
        }
    }

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

    private String sanitize(String name) {
        return name.replaceAll("[\\\\/?*\\[\\]:]", "_");
    }
}
