package com.chartframework.config;

import lombok.Builder;
import lombok.Value;
import lombok.Singular;

import java.util.List;

/**
 * Per-series visual styling configuration.
 *
 * <p>Used inside {@link com.chartframework.config.SeriesConfig} to style
 * individual data series — their fill colour, line style, and markers.</p>
 *
 * <pre>{@code
 * SeriesStyleConfig style = SeriesStyleConfig.builder()
 *     .fillColor("#1565C0")
 *     .lineColor("#1565C0")
 *     .lineWidthPt(2.0)
 *     .smooth(true)
 *     .marker(MarkerConfig.circle(7, "#1565C0"))
 *     .build();
 * }</pre>
 */
@Value
@Builder
public class SeriesStyleConfig {

    /** Fill colour for bars/areas/pie slices as hex RGB. Null = auto palette. */
    String fillColor;

    /** Fill opacity (0.0 - 1.0). Null = 1.0 (fully opaque). */
    Double fillOpacity;

    /** Line/border colour as hex RGB. Null = auto. */
    String lineColor;

    /** Line width in points (for line, scatter, and area outlines). Null = auto. */
    Double lineWidthPt;

    /** Line dash style. Null = solid. */
    BorderConfig.LineStyle lineDashStyle;

    /** Whether the line is smoothed (Bezier curves). For line charts. Defaults to false. */
    @Builder.Default
    Boolean smooth = false;

    /** Marker configuration for data points. Null = chart-type default. */
    MarkerConfig marker;

    /** Whether to shade the area under a line series (area-style fill). */
    @Builder.Default
    Boolean shadedArea = false;

    /** Colour for the shaded area under the line (when shadedArea=true). Null = auto. */
    String shadedAreaColor;

    // ── Convenience factories ─────────────────────────────────────────────────

    public static SeriesStyleConfig solidColor(String hexColor) {
        return SeriesStyleConfig.builder()
                .fillColor(hexColor)
                .lineColor(hexColor)
                .build();
    }

    public static SeriesStyleConfig smoothLine(String hexColor, double widthPt) {
        return SeriesStyleConfig.builder()
                .lineColor(hexColor)
                .lineWidthPt(widthPt)
                .smooth(true)
                .marker(MarkerConfig.circle(6, hexColor))
                .build();
    }
}
