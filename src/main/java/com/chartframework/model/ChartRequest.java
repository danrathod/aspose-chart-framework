package com.chartframework.model;

import com.chartframework.enums.ExcelChartType;
import lombok.Builder;
import lombok.Value;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Chart-specific request DTO — describes a single chart to be generated.
 *
 * <h2>Role within the API</h2>
 * <p>A {@code ChartRequest} is always contained inside a
 * {@link ChartBatchRequest}, which carries the Excel file paths. This means
 * {@code ChartRequest} is purely about <em>what</em> chart to draw and
 * <em>where</em> to draw it — with no file I/O concerns.</p>
 *
 * <h2>Data Layout Convention</h2>
 * <ul>
 *   <li><b>Row 0</b> — header row: first cell ignored; remaining = series names.</li>
 *   <li><b>Row 1..N</b> — data rows: first cell = category label; rest = values.</li>
 * </ul>
 *
 * <h2>Usage (always inside a ChartBatchRequest)</h2>
 * <pre>{@code
 * ChartRequest chart = ChartRequest.builder()
 *     .targetSheetName("Dashboard")
 *     .chartType(ExcelChartType.COLUMN)
 *     .placement(ChartPlacement.of(2, 0, 20, 8))
 *     .data(List.of(
 *         List.of("Month", "Sales", "Profit"),
 *         List.of("Jan",   12000,   3000),
 *         List.of("Feb",   15000,   4500)
 *     ))
 *     .config(ChartConfig.builder()
 *         .chartTitle("Monthly Sales")
 *         .showLegend(true)
 *         .build())
 *     .build();
 * }</pre>
 */
@Value
@Builder
public class ChartRequest {

    // ── Chart identity ────────────────────────────────────────────────────────

    /**
     * Name of the <em>visible</em> worksheet where the chart should be placed.
     * The sheet is created automatically if it does not exist.
     */
    String targetSheetName;

    /** Excel chart type to create. */
    ExcelChartType chartType;

    /** Bounding-box coordinates for chart placement (zero-based row/column). */
    ChartPlacement placement;

    // ── Data ─────────────────────────────────────────────────────────────────

    /**
     * Chart data as a 2-D list of {@code Object} values.
     * Supported value types: String, Number (Integer/Long/Double/BigDecimal),
     * Boolean, java.util.Date.
     */
    List<List<Object>> data;

    // ── Optional ──────────────────────────────────────────────────────────────

    /**
     * Visual / metadata configuration for the chart.
     * When {@code null}, {@link ChartConfig#withDefaults()} is applied.
     */
    ChartConfig config;

    // ── Convenience helpers ───────────────────────────────────────────────────

    /** Returns the effective config, falling back to defaults when {@code null}. */
    public ChartConfig effectiveConfig() {
        return config != null ? config : ChartConfig.withDefaults();
    }

    /**
     * Derives a human-readable label for this chart used as the title row
     * written above the data block in the hidden sheet.
     * Priority: chartTitle from config → chartType display name.
     */
    public String deriveLabel() {
        ChartConfig cfg = effectiveConfig();
        if (cfg.getChartTitle() != null && !cfg.getChartTitle().isBlank()) {
            return "■ " + cfg.getChartTitle() + "  [" + chartType.getDisplayName() + "]";
        }
        return "■ " + chartType.getDisplayName();
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