package com.chartframework.config;

import lombok.Builder;
import lombok.Value;

/**
 * Radar chart-specific configuration.
 *
 * <pre>{@code
 * RadarChartConfig radarConfig = RadarChartConfig.builder()
 *     .style(RadarChartConfig.RadarStyle.FILLED)
 *     .axisLabelsVisible(true)
 *     .build();
 * }</pre>
 */
@Value
@Builder
public class RadarChartConfig {

    public enum RadarStyle {
        /** Standard radar with lines only (no markers). */
        STANDARD,
        /** Radar with data point markers. */
        WITH_MARKERS,
        /** Filled radar — series areas are filled with semi-transparent color. */
        FILLED
    }

    /**
     * Visual style of the radar chart.
     * Defaults to STANDARD.
     */
    @Builder.Default
    RadarStyle style = RadarStyle.STANDARD;

    /**
     * Whether axis (spoke) labels are shown around the perimeter.
     * Defaults to true.
     */
    @Builder.Default
    Boolean axisLabelsVisible = true;

    /**
     * Fill opacity for FILLED radar style (0.0–1.0).
     * Defaults to 0.4 (semi-transparent).
     */
    @Builder.Default
    Double fillOpacity = 0.4;

    // ── Convenience factories ─────────────────────────────────────────────────

    public static RadarChartConfig filled() {
        return RadarChartConfig.builder()
                .style(RadarStyle.FILLED)
                .fillOpacity(0.35)
                .build();
    }

    public static RadarChartConfig withMarkers() {
        return RadarChartConfig.builder()
                .style(RadarStyle.WITH_MARKERS)
                .build();
    }
}
