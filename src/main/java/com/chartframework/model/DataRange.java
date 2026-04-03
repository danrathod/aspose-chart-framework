package com.chartframework.model;

import lombok.Builder;
import lombok.Value;

/**
 * Captures the exact cell range within the hidden data sheet where a
 * particular chart's data has been written.
 *
 * <p>This object is produced by {@link com.chartframework.manager.HiddenSheetManager}
 * and consumed by {@link com.chartframework.strategy.ChartStrategy} implementations
 * so they can build the correct Aspose formula references.</p>
 *
 * <p>All indices are <b>zero-based</b>.</p>
 */
@Value
@Builder
public class DataRange {

    /** Name of the hidden worksheet where the data lives. */
    String sheetName;

    /** Zero-based index of the first data row (typically the header row). */
    int startRow;

    /** Zero-based index of the last data row (inclusive). */
    int endRow;

    /** Zero-based index of the first data column. */
    int startColumn;

    /** Zero-based index of the last data column (inclusive). */
    int endColumn;

    /** Total number of data rows (excluding header if present). */
    int dataRowCount;

    /** Total number of series columns (excluding category column if present). */
    int seriesCount;

    // ── Cell reference helpers ────────────────────────────────────────────────

    /**
     * Builds an Aspose-style absolute cell range formula for a rectangular region.
     *
     * <p>Example: sheetName="__data_1", startRow=0, startCol=0, endRow=4, endCol=3
     * → {@code "'__data_1'!$A$1:$D$5"}</p>
     */
    public String toAbsoluteRange(int fromRow, int fromCol, int toRow, int toCol) {
        return String.format("'%s'!$%s$%d:$%s$%d",
                sheetName,
                columnIndexToLetter(fromCol), fromRow + 1,
                columnIndexToLetter(toCol),   toRow   + 1);
    }

    /**
     * Builds a single-column absolute range reference.
     *
     * <p>Example: column 1 (B), rows 1-4 → {@code "'__data_1'!$B$2:$B$5"}</p>
     */
    public String toColumnRange(int col, int fromRow, int toRow) {
        return String.format("'%s'!$%s$%d:$%s$%d",
                sheetName,
                columnIndexToLetter(col), fromRow + 1,
                columnIndexToLetter(col), toRow   + 1);
    }

    /**
     * Builds a single-row absolute range reference.
     */
    public String toRowRange(int row, int fromCol, int toCol) {
        return String.format("'%s'!$%s$%d:$%s$%d",
                sheetName,
                columnIndexToLetter(fromCol), row + 1,
                columnIndexToLetter(toCol),   row + 1);
    }

    // ── Static helpers ────────────────────────────────────────────────────────

    /**
     * Converts a zero-based column index to its Excel letter representation.
     * 0 → "A", 1 → "B", 25 → "Z", 26 → "AA", etc.
     */
    public static String columnIndexToLetter(int col) {
        StringBuilder sb = new StringBuilder();
        col++; // make 1-based
        while (col > 0) {
            int rem = (col - 1) % 26;
            sb.insert(0, (char) ('A' + rem));
            col = (col - 1) / 26;
        }
        return sb.toString();
    }
}
