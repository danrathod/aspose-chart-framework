package com.chartframework.model;

import com.chartframework.enums.ExcelChartType;
import lombok.Builder;
import lombok.Value;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Primary public API input DTO — carries everything the framework needs
 * to generate a single chart in a given Excel file.
 *
 * <h2>Key Design</h2>
 * <p>The caller passes a <b>file path</b> to an existing {@code .xlsx} file
 * (or a path where a new file should be created). The framework loads / creates
 * the Workbook internally, so <b>consumers do not need Aspose.Cells on their
 * classpath</b>. After chart generation the workbook is saved to
 * {@link #outputFilePath} (or back to {@link #inputFilePath} when no separate
 * output path is given).</p>
 *
 * <h2>Usage Example</h2>
 * <pre>{@code
 * ChartRequest request = ChartRequest.builder()
 *     .inputFilePath("reports/dashboard.xlsx")
 *     .outputFilePath("reports/dashboard_out.xlsx")  // optional
 *     .targetSheetName("Dashboard")
 *     .chartType(ExcelChartType.COLUMN)
 *     .placement(ChartPlacement.of(2, 0, 20, 8))
 *     .data(List.of(
 *         List.of("Month", "Sales", "Profit"),
 *         List.of("Jan",   12000,   3000),
 *         List.of("Feb",   15000,   4500)
 *     ))
 *     .config(ChartConfig.builder()
 *         .chartTitle("Monthly Performance")
 *         .showLegend(true)
 *         .build())
 *     .build();
 * }</pre>
 *
 * <h2>Data Layout Convention</h2>
 * <ul>
 *   <li><b>Row 0</b>  — header: first cell ignored; remaining cells = series names.</li>
 *   <li><b>Row 1..N</b> — data: first cell = category label; rest = numeric values.</li>
 * </ul>
 */
@Value
@Builder
public class ChartRequest {

    // ── File paths ────────────────────────────────────────────────────────────

    /**
     * Path to the input Excel file ({@code .xlsx}).
     * <ul>
     *   <li>If the file <b>exists</b> it is loaded as the workbook.</li>
     *   <li>If the file does <b>not exist</b>, a new blank workbook is created
     *       and saved to this path (or to {@link #outputFilePath}) after chart
     *       generation.</li>
     * </ul>
     * Must not be {@code null} or blank.
     */
    String inputFilePath;

    /**
     * Path where the modified workbook will be saved after chart creation.
     * <p>When {@code null} or blank the workbook is saved back to
     * {@link #inputFilePath} (in-place update).</p>
     */
    String outputFilePath;

    // ── Chart identity ────────────────────────────────────────────────────────

    /**
     * Name of the <em>visible</em> worksheet where the chart should be placed.
     * If the sheet does not yet exist, it will be created automatically by
     * {@link com.chartframework.service.ChartService}.
     */
    String targetSheetName;

    /** Excel chart type to create. */
    ExcelChartType chartType;

    /** Bounding-box coordinates for chart placement (zero-based row/column). */
    ChartPlacement placement;

    // ── Data ─────────────────────────────────────────────────────────────────

    /**
     * Chart data as a 2-D list of {@code Object} values.
     * String, Number (Integer/Long/Double), Boolean, and java.util.Date values
     * are all handled correctly when writing to the hidden data sheet.
     */
    List<List<Object>> data;

    // ── Optional ──────────────────────────────────────────────────────────────

    /**
     * Visual / metadata configuration for the chart.
     * When {@code null}, {@link ChartConfig#withDefaults()} is applied.
     */
    ChartConfig config;

    /**
     * Optional preferred base-name for the hidden data sheet.
     * A numeric suffix is always appended to guarantee uniqueness.
     * When {@code null} the framework uses {@code __chartdata_} as the prefix.
     */
    String preferredHiddenSheetBaseName;

    // ── Convenience helpers ───────────────────────────────────────────────────

    /** Returns the effective config, falling back to defaults when {@code null}. */
    public ChartConfig effectiveConfig() {
        return config != null ? config : ChartConfig.withDefaults();
    }

    /**
     * Resolves the save path: uses {@link #outputFilePath} when set,
     * otherwise falls back to {@link #inputFilePath}.
     */
    public String effectiveOutputPath() {
        return (outputFilePath != null && !outputFilePath.isBlank())
                ? outputFilePath
                : inputFilePath;
    }

    /**
     * Converts a raw {@code Object[][]} array to the
     * {@code List<List<Object>>} format expected by this DTO.
     */
    public static List<List<Object>> toListData(Object[][] raw) {
        if (raw == null) return List.of();
        List<List<Object>> result = new ArrayList<>(raw.length);
        for (Object[] row : raw) {
            result.add(row != null ? Arrays.asList(row) : List.of());
        }
        return result;
    }
}
