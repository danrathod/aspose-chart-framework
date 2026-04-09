package com.chartframework.config;

import lombok.Builder;
import lombok.Value;

/**
 * Data label configuration — controls what information is displayed
 * on chart data points and how it is formatted.
 *
 * <pre>{@code
 * DataLabelConfig labels = DataLabelConfig.builder()
 *     .visible(true)
 *     .showValue(true)
 *     .showPercentage(false)
 *     .showCategoryName(false)
 *     .showSeriesName(false)
 *     .separator(", ")
 *     .position(DataLabelConfig.Position.OUTSIDE_END)
 *     .numberFormat("$#,##0")
 *     .font(FontConfig.of("Calibri", 9))
 *     .build();
 * }</pre>
 */
@Value
@Builder
public class DataLabelConfig {

    public enum Position {
        ABOVE, BELOW, CENTER, INSIDE_BASE, INSIDE_END,
        OUTSIDE_END, LEFT, RIGHT, BEST_FIT
    }

    /** Whether data labels are shown at all. */
    @Builder.Default
    Boolean visible = false;

    /** Show the data point value. */
    @Builder.Default
    Boolean showValue = true;

    /** Show the percentage (most useful for Pie charts). */
    @Builder.Default
    Boolean showPercentage = false;

    /** Show the category name (X-axis label). */
    @Builder.Default
    Boolean showCategoryName = false;

    /** Show the series name. */
    @Builder.Default
    Boolean showSeriesName = false;

    /** Show the bubble size (Bubble charts only). */
    @Builder.Default
    Boolean showBubbleSize = false;

    /** Separator string between multiple shown elements. Defaults to ", ". */
    @Builder.Default
    String separator = ", ";

    /** Label position relative to the data point. Null = chart-type default. */
    Position position;

    /** Number format for the value (e.g. {@code "#,##0"}, {@code "0.0%"}). Null = auto. */
    String numberFormat;

    /** Label text font. Null = chart default. */
    FontConfig font;

    /** Label background fill. Null = transparent. */
    FillConfig background;

    /** Label border. Null = none. */
    BorderConfig border;

    // ── Convenience factories ─────────────────────────────────────────────────

    public static DataLabelConfig hidden() {
        return DataLabelConfig.builder().visible(false).build();
    }

    /** Show only the value with default styling. */
    public static DataLabelConfig valueOnly() {
        return DataLabelConfig.builder()
                .visible(true)
                .showValue(true)
                .build();
    }

    /** Show value and percentage — typical for pie charts. */
    public static DataLabelConfig valueAndPercent() {
        return DataLabelConfig.builder()
                .visible(true)
                .showValue(true)
                .showPercentage(true)
                .build();
    }

    /** Show percentage only — typical for pie charts. */
    public static DataLabelConfig percentOnly() {
        return DataLabelConfig.builder()
                .visible(true)
                .showValue(false)
                .showPercentage(true)
                .build();
    }

    public boolean isVisible() {
        return visible != null && visible;
    }
}
