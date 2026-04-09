package com.chartframework.config;

import lombok.Builder;
import lombok.Value;

/**
 * Visual configuration for the chart area (outer border/background of the
 * whole chart frame) and the plot area (the inner region where data is drawn).
 *
 * <pre>{@code
 * PlotAreaConfig plotArea = PlotAreaConfig.builder()
 *     .plotAreaFill(FillConfig.solid("#FAFAFA"))
 *     .plotAreaBorder(BorderConfig.solid("#E0E0E0", 0.5))
 *     .chartAreaFill(FillConfig.solid("#FFFFFF"))
 *     .chartAreaBorder(BorderConfig.none())
 *     .roundedCorners(true)
 *     .build();
 * }</pre>
 */
@Value
@Builder
public class PlotAreaConfig {

    // ── Plot area (inner data area) ───────────────────────────────────────────

    /** Background fill of the plot area. Null = default (usually white or none). */
    FillConfig plotAreaFill;

    /** Border around the plot area. Null = default. */
    BorderConfig plotAreaBorder;

    // ── Chart area (outer frame around the entire chart) ─────────────────────

    /** Background fill of the outer chart frame. Null = default (usually white). */
    FillConfig chartAreaFill;

    /** Border around the outer chart frame. Null = default. */
    BorderConfig chartAreaBorder;

    /** Rounded corners on the outer chart frame. Defaults to false. */
    @Builder.Default
    Boolean roundedCorners = false;

    /** Shadow effect on the chart area. Defaults to false. */
    @Builder.Default
    Boolean shadow = false;

    // ── Convenience factory ───────────────────────────────────────────────────

    /** Clean white background, no chart border, light grey plot area border. */
    public static PlotAreaConfig clean() {
        return PlotAreaConfig.builder()
                .chartAreaFill(FillConfig.solid("#FFFFFF"))
                .chartAreaBorder(BorderConfig.none())
                .plotAreaFill(FillConfig.solid("#FAFAFA"))
                .plotAreaBorder(BorderConfig.solid("#E0E0E0", 0.5))
                .build();
    }

    /** Fully transparent backgrounds — useful when embedding in coloured slides. */
    public static PlotAreaConfig transparent() {
        return PlotAreaConfig.builder()
                .chartAreaFill(FillConfig.none())
                .chartAreaBorder(BorderConfig.none())
                .plotAreaFill(FillConfig.none())
                .plotAreaBorder(BorderConfig.none())
                .build();
    }
}
