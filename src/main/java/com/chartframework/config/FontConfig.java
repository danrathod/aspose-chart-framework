package com.chartframework.config;

import lombok.Builder;
import lombok.Value;

/**
 * Reusable font configuration applied to any chart text element
 * (axis tick labels, legend text, data label text, etc.).
 *
 * <pre>{@code
 * FontConfig font = FontConfig.builder()
 *     .name("Calibri")
 *     .size(10)
 *     .bold(false)
 *     .color("#333333")
 *     .build();
 * }</pre>
 */
@Value
@Builder
public class FontConfig {

    /** Font family name (e.g. "Calibri", "Arial", "Courier New"). */
    String name;

    /** Font size in points. */
    Integer size;

    /** Bold weight. */
    Boolean bold;

    /** Italic style. */
    Boolean italic;

    /** Underline. */
    Boolean underline;

    /** Font colour as hex RGB string e.g. {@code "#333333"}. */
    String color;

    // ── Convenience factories ─────────────────────────────────────────────────

    public static FontConfig of(String name, int size) {
        return FontConfig.builder().name(name).size(size).build();
    }

    public static FontConfig bold(int size) {
        return FontConfig.builder().size(size).bold(true).build();
    }
}
