package com.chartframework.config;

import lombok.Builder;
import lombok.Value;

/**
 * Background fill configuration for chart areas, plot areas, and series bars.
 *
 * <p>Supports solid color fills and gradient fills.
 * When {@code fillType} is {@code NONE}, the background is transparent.</p>
 *
 * <pre>{@code
 * // Solid white background
 * FillConfig fill = FillConfig.solid("#FFFFFF");
 *
 * // Two-colour gradient
 * FillConfig grad = FillConfig.builder()
 *     .fillType(FillType.GRADIENT)
 *     .solidColor("#1565C0")
 *     .gradientColor("#E3F2FD")
 *     .build();
 * }</pre>
 */
@Value
@Builder
public class FillConfig {

    public enum FillType { NONE, SOLID, GRADIENT, PATTERN }

    /**
     * Fill type. Defaults to {@code SOLID} when {@link #solidColor} is set,
     * {@code NONE} otherwise.
     */
    FillType fillType;

    /** Solid or primary gradient colour as hex RGB e.g. {@code "#FFFFFF"}. */
    String solidColor;

    /** Secondary gradient colour (used when {@code fillType == GRADIENT}). */
    String gradientColor;

    /** Background opacity (0.0 = fully transparent, 1.0 = fully opaque). */
    Double opacity;

    // ── Convenience factories ─────────────────────────────────────────────────

    public static FillConfig none() {
        return FillConfig.builder().fillType(FillType.NONE).build();
    }

    public static FillConfig solid(String hexColor) {
        return FillConfig.builder()
                .fillType(FillType.SOLID)
                .solidColor(hexColor)
                .build();
    }

    public static FillConfig gradient(String primaryColor, String secondaryColor) {
        return FillConfig.builder()
                .fillType(FillType.GRADIENT)
                .solidColor(primaryColor)
                .gradientColor(secondaryColor)
                .build();
    }
}
