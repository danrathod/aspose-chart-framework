package com.chartframework.model;

import lombok.Builder;
import lombok.Value;

/**
 * Immutable value object representing the cell-coordinate bounding box
 * in which a chart will be anchored inside the target worksheet.
 *
 * <p>All coordinates are <b>zero-based</b> row/column indices, matching
 * the Aspose.Cells convention.</p>
 *
 * <pre>
 *   ChartPlacement placement = ChartPlacement.builder()
 *       .startRow(2).startColumn(0)
 *       .endRow(20).endColumn(8)
 *       .build();
 * </pre>
 */
@Value
@Builder
public class ChartPlacement {

    /** Zero-based row index of the chart's top-left corner. */
    int startRow;

    /** Zero-based column index of the chart's top-left corner. */
    int startColumn;

    /** Zero-based row index of the chart's bottom-right corner (exclusive). */
    int endRow;

    /** Zero-based column index of the chart's bottom-right corner (exclusive). */
    int endColumn;

    // ── Convenience factory ───────────────────────────────────────────────────

    /**
     * Shorthand factory for the common case where you know all four coordinates.
     */
    public static ChartPlacement of(int startRow, int startCol, int endRow, int endCol) {
        return ChartPlacement.builder()
                .startRow(startRow)
                .startColumn(startCol)
                .endRow(endRow)
                .endColumn(endCol)
                .build();
    }
}
