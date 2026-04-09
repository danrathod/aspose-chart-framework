package com.chartframework.config;

import lombok.Builder;
import lombok.Value;

/**
 * Data point marker configuration for line, scatter, and radar charts.
 *
 * <pre>{@code
 * MarkerConfig marker = MarkerConfig.builder()
 *     .style(MarkerConfig.MarkerStyle.CIRCLE)
 *     .size(8)
 *     .foregroundColor("#1565C0")
 *     .backgroundColor("#FFFFFF")
 *     .build();
 * }</pre>
 */
@Value
@Builder
public class MarkerConfig {

    public enum MarkerStyle {
        NONE, AUTOMATIC,
        SQUARE, DIAMOND, TRIANGLE, X, STAR,
        DOW_JONES, STANDARD_DEVIATION,
        CIRCLE, PLUS, DASH
    }

    /** Marker shape. NONE = no markers; AUTOMATIC = Excel default. */
    @Builder.Default
    MarkerStyle style = MarkerStyle.AUTOMATIC;

    /** Marker size in points. Null = chart default (typically 7pt). */
    Integer size;

    /** Marker foreground (outline) colour as hex RGB. Null = auto. */
    String foregroundColor;

    /** Marker background (fill) colour as hex RGB. Null = auto (same as series). */
    String backgroundColor;

    /** Marker border width in points. Null = auto. */
    Double borderWidthPt;

    // ── Convenience factories ─────────────────────────────────────────────────

    public static MarkerConfig none() {
        return MarkerConfig.builder().style(MarkerStyle.NONE).build();
    }

    public static MarkerConfig circle(int size, String color) {
        return MarkerConfig.builder()
                .style(MarkerStyle.CIRCLE)
                .size(size)
                .foregroundColor(color)
                .backgroundColor(color)
                .build();
    }
}
