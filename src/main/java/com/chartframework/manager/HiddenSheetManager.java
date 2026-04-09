package com.chartframework.manager;

import com.aspose.cells.*;
import com.chartframework.exception.ChartFrameworkException;
import com.chartframework.model.ChartBatchRequest;
import com.chartframework.model.ChartConfig;
import com.chartframework.model.ChartRequest;
import com.chartframework.model.DataRange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Manages hidden data sheets for chart data storage.
 *
 * <h2>Shared-Sheet Strategy (per batch request)</h2>
 * <p>All charts in a single {@link ChartBatchRequest} share the same hidden
 * worksheet. Data blocks are laid out sequentially:</p>
 * <pre>
 *   Row 0:  ■ Monthly Sales  [Clustered Column]    ← title label (bold)
 *   Row 1:  Month | Sales | Profit                 ← data header
 *   Row 2:  Jan   | 12000 | 3000                   ← data rows
 *   Row 3:  Feb   | 15000 | 4500
 *   Row 4:  (blank)
 *   Row 5:  (blank)                                ← 2 blank separator rows
 *   Row 6:  ■ Regional Revenue  [Pie]              ← next chart's title label
 *   Row 7:  Region | Revenue
 *   ...
 * </pre>
 *
 * <h2>Overflow</h2>
 * <p>A new hidden sheet is created only when the next chart's block would push
 * the row cursor past {@code MAX_EXCEL_ROWS - ROW_BUFFER} (Option C).
 * The default buffer is {@value #ROW_BUFFER} rows.</p>
 *
 * <h2>Isolation between batches</h2>
 * <p>Each call to {@link #writeDataForBatch} starts fresh — it creates at least
 * one new hidden sheet regardless of what sheets already exist in the workbook.
 * This guarantees that data from different batch requests never mix.</p>
 */
public class HiddenSheetManager {

    private static final Logger log = LoggerFactory.getLogger(HiddenSheetManager.class);

    /** Prefix for all framework-managed hidden sheets. */
    public static final String HIDDEN_SHEET_PREFIX = "__chartdata_";

    /** Total Excel row limit. */
    private static final int MAX_EXCEL_ROWS = 1_048_576;

    /**
     * Safety buffer subtracted from {@link #MAX_EXCEL_ROWS} to determine
     * when a new hidden sheet must be created (Option C).
     */
    public static final int ROW_BUFFER = 1_000;

    /** Row threshold: if cursor would exceed this, overflow to a new sheet. */
    private static final int ROW_THRESHOLD = MAX_EXCEL_ROWS - ROW_BUFFER;

    /** Blank rows written between consecutive chart data blocks. */
    private static final int BLOCK_SEPARATOR_ROWS = 2;

    /** Global counter — guarantees unique sheet names across all batches. */
    private final AtomicInteger sheetCounter = new AtomicInteger(0);

    // ─────────────────────────────────────────────────────────────────────────
    // Public API
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Writes all chart data from the batch into one or more hidden sheets and
     * returns one {@link DataRange} per chart (in the same order as the input list).
     *
     * <p>All charts in the batch are written to the <em>same</em> hidden sheet
     * unless the row threshold is reached, in which case a new hidden sheet is
     * created transparently and writing continues there.</p>
     *
     * @param batchRequest The batch request providing charts and base sheet name.
     * @param workbook     The live Aspose Workbook managed by ChartService.
     * @return A list of {@link DataRange} objects — one per chart, in input order.
     */
    public List<DataRange> writeDataForBatch(ChartBatchRequest batchRequest,
                                             Workbook workbook) {
        List<ChartRequest> charts   = batchRequest.getCharts();
        String             baseName = resolveBaseName(batchRequest);
        List<DataRange>    results  = new ArrayList<>(charts.size());

        // Create the first hidden sheet for this batch
        Worksheet currentSheet = createHiddenSheet(workbook, generateSheetName(baseName, workbook));
        int currentRow = 0;  // absolute row cursor within currentSheet

        log.info("Writing data for batch of {} chart(s) starting on hidden sheet '{}'",
                charts.size(), currentSheet.getName());

        for (int i = 0; i < charts.size(); i++) {
            ChartRequest chart = charts.get(i);

            // Row cost of this block = 1 title row + data rows
            int blockHeight = 1 + chart.getData().size();
            // Separator rows before this block (not added for the very first block)
            int separatorRows = (currentRow == 0) ? 0 : BLOCK_SEPARATOR_ROWS;
            int totalRowsNeeded = separatorRows + blockHeight;

            // Overflow check — does this block fit in the current sheet?
            if (currentRow > 0 && currentRow + totalRowsNeeded > ROW_THRESHOLD) {
                log.info("Row threshold reached at row {} — creating new hidden sheet for remaining charts.", currentRow);
                currentSheet = createHiddenSheet(workbook, generateSheetName(baseName, workbook));
                currentRow   = 0;
                separatorRows = 0;
                totalRowsNeeded = blockHeight;
            }

            // Skip separator rows (they are naturally blank — cursor just advances)
            currentRow += separatorRows;

            // --- Write title label row ---
            writeTitleRow(currentSheet, currentRow, chart, i + 1);
            int titleRow = currentRow;
            currentRow++;

            // --- Write data rows ---
            int dataStartRow = currentRow;
            writeRows(currentSheet, chart.getData(), currentRow, 0);
            int dataEndRow = currentRow + chart.getData().size() - 1;
            currentRow += chart.getData().size();

            // Build DataRange pointing at the DATA rows only (not the title row),
            // so formula references in strategies are correct.
            ChartConfig config     = chart.effectiveConfig();
            int         totalCols  = chart.getData().get(0).size();
            int         dataRowCnt = config.isFirstRowIsHeader()
                    ? chart.getData().size() - 1
                    : chart.getData().size();
            int         seriesCnt  = config.isFirstColumnIsCategory()
                    ? totalCols - 1
                    : totalCols;

            DataRange range = DataRange.builder()
                    .sheetName(currentSheet.getName())
                    .startRow(dataStartRow)
                    .endRow(dataEndRow)
                    .startColumn(0)
                    .endColumn(totalCols - 1)
                    .dataRowCount(dataRowCnt)
                    .seriesCount(seriesCnt)
                    .build();

            results.add(range);

            log.debug("Chart[{}] '{}' → sheet='{}', titleRow={}, dataRows={}-{}",
                    i, chart.deriveLabel(), currentSheet.getName(),
                    titleRow, dataStartRow, dataEndRow);
        }

        log.info("Batch data write complete. {} DataRange(s) produced.", results.size());
        return results;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Private helpers
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Writes the chart's title label into a single merged cell row.
     * The label is styled bold so it stands out visually from the data.
     *
     * <p>Format: {@code ■ <chartTitle>  [<ChartType>]}  or
     * {@code ■ Chart N  [<ChartType>]} when no title is configured.</p>
     */
    private void writeTitleRow(Worksheet ws, int row, ChartRequest chart, int chartNumber) {
        String label = chart.deriveLabel();

        Cell titleCell = ws.getCells().get(row, 0);
        titleCell.putValue(label);

        // Apply bold style to the title cell so it is clearly distinguishable
        try {
            Style style = titleCell.getStyle();
            style.getFont().setBold(true);
            titleCell.setStyle(style);
        } catch (Exception e) {
            log.debug("Could not apply bold style to title row (non-fatal): {}", e.getMessage());
        }
    }

    /**
     * Writes all rows of a 2-D data list into the worksheet starting at
     * ({@code startRow}, {@code startCol}).
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
                setCellValue(cells.get(startRow + r, startCol + c), row.get(c));
            }
        }
    }

    /** Type-aware cell value assignment. */
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

    /**
     * Creates a brand-new hidden worksheet in the workbook.
     * Throws if a name collision occurs (defensive — the counter loop prevents this).
     */
    private Worksheet createHiddenSheet(Workbook workbook, String sheetName) {
        WorksheetCollection sheets = workbook.getWorksheets();
        if (sheets.get(sheetName) != null) {
            throw new ChartFrameworkException(
                    "Hidden sheet '" + sheetName + "' already exists.");
        }
        int       index = sheets.add(sheetName);
        Worksheet ws    = sheets.get(index);
        ws.setVisible(false);
        log.debug("Created hidden sheet '{}' at index {}", sheetName, index);
        return ws;
    }

    /**
     * Generates a unique sheet name, looping with incremented counter until
     * no collision is found in the current workbook.
     */
    private String generateSheetName(String baseName, Workbook workbook) {
        String candidate;
        do {
            int suffix = sheetCounter.incrementAndGet();
            candidate  = baseName + suffix;
            if (candidate.length() > 31) {
                // Excel sheet name max = 31 chars; trim from the left
                candidate = candidate.substring(candidate.length() - 31);
            }
        } while (workbook.getWorksheets().get(candidate) != null);
        return candidate;
    }

    /** Resolves the sheet name base, sanitising illegal Excel chars. */
    private String resolveBaseName(ChartBatchRequest batchRequest) {
        String preferred = batchRequest.getPreferredHiddenSheetBaseName();
        if (preferred != null && !preferred.isBlank()) {
            return sanitize(preferred);
        }
        return HIDDEN_SHEET_PREFIX;
    }

    /** Strips characters illegal in Excel sheet names. */
    private String sanitize(String name) {
        return name.replaceAll("[\\\\/?*\\[\\]:]", "_");
    }
}
