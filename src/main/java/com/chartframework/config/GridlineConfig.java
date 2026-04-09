package com.chartframework.config;

import lombok.Builder;
import lombok.Value;

/**
 * Gridline styling configuration used inside {@link AxisConfig}.
 *
 * <pre>{@code
 * GridlineConfig major = GridlineConfig.solid("#E0E0E0", 0.5);
 * GridlineConfig minor = GridlineConfig.dashed("#F5F5F5");
 * }</pre>
 */
@Value
@Builder
public class GridlineConfig {

    /** Whether gridlines are shown. Defaults to true. */
    @Builder.Default
    Boolean visible = true;

    /** Gridline colour as hex RGB. Null = chart default (usually light grey). */
    String color;

    /** Line width in points. Null = chart default. */
    Double widthPt;

    /** Dash style. Defaults to SOLID. */
    @Builder.Default
    BorderConfig.LineStyle lineStyle = BorderConfig.LineStyle.SOLID;

    // ── Convenience factories ─────────────────────────────────────────────────

    public static GridlineConfig hidden() {
        return GridlineConfig.builder().visible(false).build();
    }

    public static GridlineConfig solid(String color, double widthPt) {
        return GridlineConfig.builder()
                .visible(true)
                .color(color)
                .widthPt(widthPt)
                .lineStyle(BorderConfig.LineStyle.SOLID)
                .build();
    }

    public static GridlineConfig dashed(String color) {
        return GridlineConfig.builder()
                .visible(true)
                .color(color)
                .lineStyle(BorderConfig.LineStyle.DASH)
                .build();
    }

    public boolean isVisible() {
        return visible == null || visible;
    }
}
