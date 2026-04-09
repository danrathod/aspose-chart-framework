package com.chartframework.config;

import lombok.Builder;
import lombok.Value;

/**
 * Line chart-specific configuration.
 *
 * <pre>{@code
 * LineChartConfig lineConfig = LineChartConfig.builder()
 *     .smooth(true)
 *     .showDropLines(false)
 *     .showHighLowLines(false)
 *     .showUpDownBars(false)
 *     .build();
 * }</pre>
 */
@Value
@Builder
public class LineChartConfig {

    /**
     * Whether all line series use smooth Bezier curves instead of straight segments.
     * Individual series can override this via {@link SeriesStyleConfig#getSmooth()}.
     * Defaults to false.
     */
    @Builder.Default
    Boolean smooth = false;

    /**
     * Show drop lines from data points vertically down to the category axis.
     * Defaults to false.
     */
    @Builder.Default
    Boolean showDropLines = false;

    /** Style of the drop lines. Null = chart default (thin grey solid). */
    BorderConfig dropLineStyle;

    /**
     * Show high-low lines connecting the highest and lowest value in each category
     * across all series. Useful for stock-style line charts. Defaults to false.
     */
    @Builder.Default
    Boolean showHighLowLines = false;

    /** Style of the high-low lines. Null = chart default. */
    BorderConfig highLowLineStyle;

    /**
     * Show up-down bars between the first and last series at each category.
     * Defaults to false.
     */
    @Builder.Default
    Boolean showUpDownBars = false;

    // ── Convenience factories ─────────────────────────────────────────────────

    public static LineChartConfig smooth() {
        return LineChartConfig.builder().smooth(true).build();
    }

    public static LineChartConfig straight() {
        return LineChartConfig.builder().smooth(false).build();
    }

    public boolean isSmooth() {
        return smooth != null && smooth;
    }
}
