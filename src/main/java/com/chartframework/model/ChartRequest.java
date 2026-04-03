package com.chartframework.model;

import com.aspose.cells.Workbook;
import com.chartframework.enums.ExcelChartType;
import lombok.Builder;
import lombok.Value;

import java.util.List;

/**
 * Primary public API input DTO — carries everything the framework needs
 * to generate a single chart in a given workbook.
 *
 * <h2>Usage Example</h2>
 * <pre>{@code
 * List<List<Object>> data = List.of(
 *     List.of("Month",   "Sales",  "Profit"),
 *     List.of("Jan",     12000,    3000),
 *     List.of("Feb",     15000,    4500),
 *     List.of("Mar",     18000,    6000)
 * );
 *
 * ChartRequest request = ChartRequest.builder()
 *     .workbook(workbook)
 *     .targetSheetName("Dashboard")
 *     .chartType(ExcelChartType.COLUMN_CLUSTERED)
 *     .placement(ChartPlacement.of(2, 0, 20, 8))
 *     .data(data)
 *     .config(ChartConfig.builder()
 *         .chartTitle("Monthly Performance")
 *         .categoryAxisTitle("Month")
 *         .valueAxisTitle("Amount (USD)")
 *         .showLegend(true)
 *         .showDataLabels(false)
 *         .build())
 *     .build();
 * }</pre>
 *
 * <h2>Data Layout Convention</h2>
 * <p>The {@code data} list is treated as a flat 2-D table:</p>
 * <ul>
 *   <li><b>Row 0</b>  — header row: first cell is ignored (or used as category label),
 *       remaining cells become series names.</li>
 *   <li><b>Row 1..N</b> — data rows: first cell is the category label, remaining cells
 *       are numeric values for each series.</li>
 * </ul>
 * <p>This behaviour can be altered via {@link ChartConfig#isFirstRowIsHeader()} and
 * {@link ChartConfig#isFirstColumnIsCategory()}.</p>
 */
@Value
@Builder
public class ChartRequest {

    // ── Required: workbook & target ───────────────────────────────────────────

    /**
     * The live Aspose {@link Workbook} instance to which the chart will be added.
     * The caller is responsible for saving / closing the workbook after all charts
     * have been generated.
     */
    Workbook workbook;

    /**
     * Name of the <em>visible</em> worksheet where the chart object should be placed.
     * The sheet must already exist in the workbook.
     */
    String targetSheetName;

    // ── Required: chart identity ──────────────────────────────────────────────

    /** Excel chart type to create. */
    ExcelChartType chartType;

    /** Bounding-box coordinates for chart placement (zero-based row/column). */
    ChartPlacement placement;

    // ── Required: data ────────────────────────────────────────────────────────

    /**
     * Chart data as a 2-D list of {@code Object} values.
     * Strings, numbers (Integer, Long, Double), Dates, and Booleans are all
     * handled automatically when writing to the hidden data sheet.
     */
    List<List<Object>> data;

    // ── Optional: visual config ───────────────────────────────────────────────

    /**
     * Visual / metadata configuration for the chart.
     * When {@code null}, {@link ChartConfig#withDefaults()} is used.
     */
    ChartConfig config;

    // ── Optional: hidden-sheet customisation ──────────────────────────────────

    /**
     * Optional preferred base-name for the hidden data sheet.
     * The {@link com.chartframework.manager.HiddenSheetManager} will auto-append
     * a numeric suffix to guarantee uniqueness. When {@code null}, the framework
     * derives a name from {@code targetSheetName} + chart index.
     */
    String preferredHiddenSheetBaseName;

    // ── Convenience ───────────────────────────────────────────────────────────

    /** Returns the effective config, falling back to defaults if {@code null}. */
    public ChartConfig effectiveConfig() {
        return config != null ? config : ChartConfig.withDefaults();
    }

    /**
     * Convenience method to accept raw {@code Object[][]} data instead of a
     * {@code List<List<Object>>} — wraps it for internal use.
     */
    public static List<List<Object>> toListData(Object[][] raw) {
        if (raw == null) return List.of();
        List<List<Object>> result = new java.util.ArrayList<>(raw.length);
        for (Object[] row : raw) {
            result.add(row != null ? java.util.Arrays.asList(row) : List.of());
        }
        return result;
    }
}
