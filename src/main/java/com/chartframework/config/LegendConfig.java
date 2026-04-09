package com.chartframework.config;

import lombok.Builder;
import lombok.Value;

/**
 * Full legend configuration.
 *
 * <pre>{@code
 * LegendConfig legend = LegendConfig.builder()
 *     .visible(true)
 *     .position(LegendConfig.Position.BOTTOM)
 *     .font(FontConfig.of("Calibri", 10))
 *     .background(FillConfig.solid("#FFFFFF"))
 *     .border(BorderConfig.solid("#CCCCCC", 0.5))
 *     .overlay(false)
 *     .build();
 * }</pre>
 */
@Value
@Builder
public class LegendConfig {

    public enum Position { TOP, BOTTOM, LEFT, RIGHT, CORNER, CUSTOM }

    /** Whether the legend is visible. Defaults to true. */
    @Builder.Default
    Boolean visible = true;

    /** Legend position relative to the plot area. Defaults to BOTTOM. */
    @Builder.Default
    Position position = Position.BOTTOM;

    /** Legend text font settings. Null = chart default. */
    FontConfig font;

    /** Legend background fill. Null = auto (usually white). */
    FillConfig background;

    /** Legend border styling. Null = auto. */
    BorderConfig border;

    /**
     * Whether the legend may overlay the plot area.
     * False = legend is outside the plot area and the plot area shrinks.
     * Defaults to false.
     */
    @Builder.Default
    Boolean overlay = false;

    // ── Convenience factories ─────────────────────────────────────────────────

    public static LegendConfig hidden() {
        return LegendConfig.builder().visible(false).build();
    }

    public static LegendConfig at(Position position) {
        return LegendConfig.builder().visible(true).position(position).build();
    }

    public boolean isVisible() {
        return visible == null || visible;
    }
}
