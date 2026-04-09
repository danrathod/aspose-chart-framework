package com.chartframework.config;

import lombok.Builder;
import lombok.Value;

/**
 * Scatter and Bubble chart-specific configuration.
 *
 * <pre>{@code
 * ScatterBubbleConfig scatterConfig = ScatterBubbleConfig.builder()
 *     .bubbleSizeRepresentation(ScatterBubbleConfig.BubbleSizeAs.AREA)
 *     .bubbleScale(100)
 *     .showNegativeBubbles(true)
 *     .negativeBubbleFill(FillConfig.solid("#FF5252"))
 *     .build();
 * }</pre>
 */
@Value
@Builder
public class ScatterBubbleConfig {

    public enum BubbleSizeAs { AREA, WIDTH }

    /**
     * Whether bubble sizes represent the bubble area or diameter/width.
     * Defaults to AREA (matches Excel default).
     */
    @Builder.Default
    BubbleSizeAs bubbleSizeRepresentation = BubbleSizeAs.AREA;

    /**
     * Bubble scale factor as a percentage of the default size (0–300).
     * 100 = default size. Null = 100.
     */
    @Builder.Default
    Integer bubbleScale = 100;

    /**
     * Whether to show bubbles with negative bubble-size values.
     * Defaults to true.
     */
    @Builder.Default
    Boolean showNegativeBubbles = true;

    /**
     * Fill style for bubbles with negative values.
     * Only used when {@link #showNegativeBubbles} is true.
     * Null = Aspose default (inverted/transparent).
     */
    FillConfig negativeBubbleFill;

    // ── Convenience factories ─────────────────────────────────────────────────

    public static ScatterBubbleConfig defaults() {
        return ScatterBubbleConfig.builder().build();
    }

    /** Bubble chart with area-based sizing scaled to 80% and red negatives. */
    public static ScatterBubbleConfig standard() {
        return ScatterBubbleConfig.builder()
                .bubbleSizeRepresentation(BubbleSizeAs.AREA)
                .bubbleScale(80)
                .showNegativeBubbles(true)
                .negativeBubbleFill(FillConfig.solid("#FF5252"))
                .build();
    }
}
