package com.chartframework.model;

import com.chartframework.enums.ExcelChartType;
import lombok.Builder;
import lombok.Value;

import java.util.List;

/**
 * Top-level request DTO for creating <b>multiple charts in a single call</b>.
 *
 * <h2>Design</h2>
 * <p>File-path concerns (input / output Excel file) live here at the batch
 * level — not inside each {@link ChartRequest}. This means:</p>
 * <ul>
 *   <li>The Excel file is loaded <em>once</em>, all charts are generated, then
 *       the file is saved <em>once</em>.</li>
 *   <li>Each individual {@link ChartRequest} is purely chart-specific
 *       (sheet name, type, placement, data, config).</li>
 * </ul>
 *
 * <h2>Hidden Sheet Strategy</h2>
 * <p>All charts in a single {@code ChartBatchRequest} share the same hidden
 * data sheet. Chart data blocks are written one below the other, each preceded
 * by a title label row and separated by 2 blank rows. A new hidden sheet is
 * created only when the next block would exceed the configured row threshold
 * (Excel maximum minus a safety buffer).</p>
 *
 * <h2>Usage Example</h2>
 * <pre>{@code
 * ChartBatchRequest batch = ChartBatchRequest.builder()
 *     .inputFilePath("reports/dashboard.xlsx")
 *     .outputFilePath("reports/dashboard_final.xlsx")  // optional
 *     .charts(List.of(
 *         ChartRequest.builder()
 *             .targetSheetName("Dashboard")
 *             .chartType(ExcelChartType.COLUMN)
 *             .placement(ChartPlacement.of(0, 0, 18, 8))
 *             .data(salesData)
 *             .config(ChartConfig.builder().chartTitle("Monthly Sales").build())
 *             .build(),
 *         ChartRequest.builder()
 *             .targetSheetName("Dashboard")
 *             .chartType(ExcelChartType.PIE)
 *             .placement(ChartPlacement.of(19, 0, 37, 8))
 *             .data(regionData)
 *             .config(ChartConfig.builder().chartTitle("Regional Revenue").build())
 *             .build()
 *     ))
 *     .build();
 *
 * String savedPath = chartService.createCharts(batch);
 * }</pre>
 */
@Value
@Builder
public class ChartBatchRequest {

    // ── File paths ────────────────────────────────────────────────────────────

    /**
     * Path to the input Excel file ({@code .xlsx}).
     * <ul>
     *   <li>If the file <b>exists</b>, it is loaded as the workbook.</li>
     *   <li>If the file does <b>not exist</b>, a new blank workbook is created.</li>
     * </ul>
     * Must not be {@code null} or blank.
     */
    String inputFilePath;

    /**
     * Path where the modified workbook will be saved after all charts are created.
     * <p>When {@code null} or blank, the workbook is saved back to
     * {@link #inputFilePath} (in-place update).</p>
     */
    String outputFilePath;

    // ── Charts ────────────────────────────────────────────────────────────────

    /**
     * Ordered list of chart requests to process.
     * Charts are processed in list order. All charts in this batch share
     * the same hidden data sheet (with overflow to additional hidden sheets
     * only when the row threshold is approached).
     * Must not be {@code null} or empty.
     */
    List<ChartRequest> charts;

    // ── Optional hidden-sheet customisation ───────────────────────────────────

    /**
     * Optional preferred base-name for the hidden data sheet(s) generated
     * for this batch. A numeric suffix is always appended to guarantee
     * uniqueness. When {@code null}, defaults to {@code __chartdata_}.
     */
    String preferredHiddenSheetBaseName;

    // ── Convenience helpers ───────────────────────────────────────────────────

    /**
     * Resolves the effective save path.
     * Falls back to {@link #inputFilePath} when {@link #outputFilePath} is absent.
     */
    public String effectiveOutputPath() {
        return (outputFilePath != null && !outputFilePath.isBlank())
                ? outputFilePath
                : inputFilePath;
    }

    // ── Convenience factory for a single-chart batch ──────────────────────────

    /**
     * Convenience factory: wraps a single {@link ChartRequest} in a batch,
     * avoiding boilerplate for the common one-chart use case.
     *
     * <pre>{@code
     * ChartBatchRequest batch = ChartBatchRequest.singleChart(
     *     "reports/report.xlsx", null, myChartRequest);
     * }</pre>
     *
     * @param inputFilePath  Path to the input Excel file.
     * @param outputFilePath Path to save to (null = in-place).
     * @param chart          The single chart request.
     */
    public static ChartBatchRequest singleChart(String inputFilePath,
                                                String outputFilePath,
                                                ChartRequest chart) {
        return ChartBatchRequest.builder()
                .inputFilePath(inputFilePath)
                .outputFilePath(outputFilePath)
                .charts(List.of(chart))
                .build();
    }
}
