package com.chartframework.config;

import lombok.Builder;
import lombok.Value;

/**
 * Border / line styling for chart area borders, plot area borders,
 * axis lines, gridlines, and series line formatting.
 *
 * <pre>{@code
 * BorderConfig border = BorderConfig.builder()
 *     .color("#CCCCCC")
 *     .widthPt(0.75)
 *     .lineStyle(LineStyle.SOLID)
 *     .build();
 * }</pre>
 */
@Value
@Builder
public class BorderConfig {

    public enum LineStyle { SOLID, DASH, DOT, DASH_DOT, DASH_DOT_DOT, NONE }

    /** Border line colour as hex RGB e.g. {@code "#CCCCCC"}. Null = auto. */
    String color;

    /** Line width in points. Null = default (0.75pt typically). */
    Double widthPt;

    /** Line dash style. Defaults to SOLID. */
    LineStyle lineStyle;

    /** Whether the border is visible at all. Null = auto. */
    Boolean visible;

    // ── Convenience factories ─────────────────────────────────────────────────

    public static BorderConfig none() {
        return BorderConfig.builder().visible(false).lineStyle(LineStyle.NONE).build();
    }

    public static BorderConfig solid(String color, double widthPt) {
        return BorderConfig.builder()
                .color(color)
                .widthPt(widthPt)
                .lineStyle(LineStyle.SOLID)
                .visible(true)
                .build();
    }

    public static BorderConfig dashed(String color) {
        return BorderConfig.builder()
                .color(color)
                .lineStyle(LineStyle.DASH)
                .visible(true)
                .build();
    }
}
