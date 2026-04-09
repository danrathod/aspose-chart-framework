package com.chartframework.config;

import lombok.Builder;
import lombok.Value;

/**
 * Configuration for a chart title or axis title.
 *
 * <p>Used for the main chart title, subtitle (ODS), category axis title,
 * value axis title, and secondary value axis title.</p>
 *
 * <pre>{@code
 * TitleConfig title = TitleConfig.builder()
 *     .text("Monthly Sales Performance")
 *     .fontSize(14)
 *     .bold(true)
 *     .fontColor("#1565C0")
 *     .visible(true)
 *     .build();
 * }</pre>
 */
@Value
@Builder
public class TitleConfig {

    /** The title text. Null = no title shown. */
    String text;

    /** Whether the title is visible. Defaults to true when text is non-blank. */
    Boolean visible;

    /** Font name (e.g. "Calibri", "Arial"). Null = chart default. */
    String fontName;

    /** Font size in points. Null = chart default. */
    Integer fontSize;

    /** Bold font. Null = chart default. */
    Boolean bold;

    /** Italic font. Null = chart default. */
    Boolean italic;

    /** Font colour as hex RGB string e.g. {@code "#1565C0"}. Null = auto. */
    String fontColor;

    /** Horizontal rotation angle in degrees (-90 to 90). Null = 0 (horizontal). */
    Integer rotationAngle;

    // ── Convenience factory ───────────────────────────────────────────────────

    /** Creates a plain text title with default styling. */
    public static TitleConfig of(String text) {
        return TitleConfig.builder().text(text).build();
    }

    /** Creates a bold title with specified font size. */
    public static TitleConfig bold(String text, int fontSize) {
        return TitleConfig.builder()
                .text(text)
                .bold(true)
                .fontSize(fontSize)
                .build();
    }

    public boolean isVisible() {
        if (visible != null) return visible;
        return text != null && !text.isBlank();
    }
}
