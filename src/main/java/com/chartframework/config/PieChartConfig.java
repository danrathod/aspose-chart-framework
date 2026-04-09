package com.chartframework.config;

import lombok.Builder;
import lombok.Value;

import java.util.List;

/**
 * Pie and Doughnut chart-specific configuration.
 *
 * <pre>{@code
 * PieChartConfig pieConfig = PieChartConfig.builder()
 *     .firstSliceAngle(90)            // rotate so largest slice starts at top
 *     .explodeAllSlices(10)           // pull all slices 10% away from center
 *     .explodeSlice(0, 20)            // additionally explode slice 0 by 20%
 *     .holeSize(50)                   // doughnut hole size as % (doughnut only)
 *     .showLeaderLines(true)          // leader lines from labels to slices
 *     .dataLabels(DataLabelConfig.valueAndPercent())
 *     .build();
 * }</pre>
 */
@Value
@Builder
public class PieChartConfig {

    /**
     * Angle of the first slice measured clockwise from the top (0–360 degrees).
     * 0 = first slice starts at the top (12 o'clock). Null = 0.
     */
    @Builder.Default
    Integer firstSliceAngle = 0;

    /**
     * Global explosion percentage applied to all slices (0–400).
     * 0 = no explosion, 100 = slice pulled fully away from center.
     * Null = 0 (no explosion).
     */
    Integer explodeAllSlices;

    /**
     * Per-slice explosion overrides as a list indexed to the data rows.
     * Each entry is the explosion percentage for that slice (0–400).
     * Null entries fall back to {@link #explodeAllSlices}.
     */
    List<Integer> sliceExplosions;

    /**
     * Doughnut hole size as a percentage of the doughnut radius (10–90).
     * Only effective for Doughnut chart types. Null = Aspose default (50).
     */
    Integer holeSize;

    /**
     * Whether to show leader lines from data labels to the pie slices.
     * Most useful when labels are outside the pie. Defaults to true.
     */
    @Builder.Default
    Boolean showLeaderLines = true;

    /**
     * Data label configuration for this pie/doughnut chart.
     * Overrides the base {@link DataLabelConfig} if both are set.
     */
    DataLabelConfig dataLabels;

    // ── Convenience factories ─────────────────────────────────────────────────

    /** Standard pie with percentage labels, first slice at top. */
    public static PieChartConfig standard() {
        return PieChartConfig.builder()
                .firstSliceAngle(0)
                .showLeaderLines(true)
                .dataLabels(DataLabelConfig.percentOnly())
                .build();
    }

    /** Exploded pie — all slices pulled out by 10%. */
    public static PieChartConfig exploded() {
        return PieChartConfig.builder()
                .explodeAllSlices(10)
                .dataLabels(DataLabelConfig.valueAndPercent())
                .build();
    }

    /** Standard doughnut with 50% hole. */
    public static PieChartConfig doughnut() {
        return PieChartConfig.builder()
                .holeSize(50)
                .dataLabels(DataLabelConfig.valueAndPercent())
                .build();
    }

    /** Returns the explosion for a given slice index (falls back to global). */
    public int explosionFor(int sliceIndex) {
        if (sliceExplosions != null && sliceIndex < sliceExplosions.size()) {
            Integer override = sliceExplosions.get(sliceIndex);
            if (override != null) return override;
        }
        return explodeAllSlices != null ? explodeAllSlices : 0;
    }
}
