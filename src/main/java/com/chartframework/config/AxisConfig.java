package com.chartframework.config;

import lombok.Builder;
import lombok.Value;

/**
 * Comprehensive axis configuration covering category axis, value axis,
 * and secondary value axis.
 *
 * <pre>{@code
 * AxisConfig xAxis = AxisConfig.builder()
 *     .title(TitleConfig.of("Month"))
 *     .visible(true)
 *     .tickLabelFont(FontConfig.of("Calibri", 9))
 *     .tickLabelRotation(-45)
 *     .majorGridlines(GridlineConfig.solid("#E0E0E0", 0.5))
 *     .minorGridlines(GridlineConfig.dashed("#F5F5F5"))
 *     .axisLine(BorderConfig.solid("#999999", 0.75))
 *     .crossesAt(0.0)
 *     .reversed(false)
 *     .build();
 *
 * AxisConfig yAxis = AxisConfig.builder()
 *     .title(TitleConfig.of("Revenue (USD)"))
 *     .minValue(0.0)
 *     .maxValue(200_000.0)
 *     .majorUnit(50_000.0)
 *     .numberFormat("$#,##0")
 *     .majorGridlines(GridlineConfig.solid("#E0E0E0", 0.5))
 *     .build();
 * }</pre>
 */
@Value
@Builder
public class AxisConfig {

    // ── Visibility ────────────────────────────────────────────────────────────

    /** Whether the axis is visible at all. Defaults to true. */
    @Builder.Default
    Boolean visible = true;

    // ── Title ─────────────────────────────────────────────────────────────────

    /** Axis title text and styling. Null = no title shown. */
    TitleConfig title;

    // ── Scale (value axis) ────────────────────────────────────────────────────

    /** Minimum value of the axis scale. Null = auto-calculated by Aspose. */
    Double minValue;

    /** Maximum value of the axis scale. Null = auto-calculated by Aspose. */
    Double maxValue;

    /** Major unit (interval between major gridlines). Null = auto. */
    Double majorUnit;

    /** Minor unit (interval between minor gridlines). Null = auto. */
    Double minorUnit;

    /** Whether the axis scale is logarithmic. Defaults to false. */
    @Builder.Default
    Boolean logScale = false;

    /** Base for logarithmic scale (e.g. 10). Effective only when logScale=true. */
    @Builder.Default
    Double logBase = 10.0;

    /** Reverse the axis direction (high to low). Defaults to false. */
    @Builder.Default
    Boolean reversed = false;

    // ── Tick labels ───────────────────────────────────────────────────────────

    /** Tick label font settings. Null = chart default. */
    FontConfig tickLabelFont;

    /**
     * Tick label rotation angle in degrees.
     * 0 = horizontal, -45 = diagonal down-left, 90 = vertical.
     * Null = 0.
     */
    Integer tickLabelRotation;

    /** Number format string for value axis labels (e.g. {@code "$#,##0"}, {@code "0.0%"}). */
    String numberFormat;

    /** How many tick labels to skip (e.g. 2 = show every other label). Null = auto. */
    Integer tickLabelSpacing;

    // ── Gridlines ─────────────────────────────────────────────────────────────

    /** Major gridline style. Null = Aspose default (usually light grey solid). */
    GridlineConfig majorGridlines;

    /** Minor gridline style. Null = not shown. */
    GridlineConfig minorGridlines;

    // ── Axis line ─────────────────────────────────────────────────────────────

    /** The axis spine line style. Null = chart default. */
    BorderConfig axisLine;

    // ── Crossing ─────────────────────────────────────────────────────────────

    /**
     * Value at which this axis crosses the perpendicular axis.
     * Null = auto (typically at 0 for value axes).
     */
    Double crossesAt;

    // ── Convenience factories ─────────────────────────────────────────────────

    /** Simple axis with just a title label. */
    public static AxisConfig withTitle(String titleText) {
        return AxisConfig.builder()
                .title(TitleConfig.of(titleText))
                .build();
    }

    /** Value axis with explicit min/max bounds and a number format. */
    public static AxisConfig bounded(double min, double max, String numberFormat) {
        return AxisConfig.builder()
                .minValue(min)
                .maxValue(max)
                .numberFormat(numberFormat)
                .build();
    }

    public boolean isVisible() {
        return visible == null || visible;
    }
}
