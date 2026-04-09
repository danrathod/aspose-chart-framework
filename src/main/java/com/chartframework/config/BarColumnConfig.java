package com.chartframework.config;

import lombok.Builder;
import lombok.Value;

/**
 * Bar and Column chart-specific configuration.
 *
 * <pre>{@code
 * BarColumnConfig barConfig = BarColumnConfig.builder()
 *     .gapWidth(150)
 *     .overlap(0)
 *     .shape(BarColumnConfig.BarShape.BOX)
 *     .build();
 * }</pre>
 */
@Value
@Builder
public class BarColumnConfig {

    public enum BarShape { BOX, CYLINDER, CONE, PYRAMID }

    /**
     * Gap width between bar/column clusters as a percentage of bar width (0–500).
     * 0 = no gap, 150 = default Excel gap. Null = Aspose default.
     */
    @Builder.Default
    Integer gapWidth = 150;

    /**
     * Overlap percentage between series bars in the same cluster (-100 to 100).
     * 0 = bars side by side, 100 = bars fully overlapping.
     * Null = 0 (no overlap).
     */
    @Builder.Default
    Integer overlap = 0;

    /**
     * The 3D bar/column shape for shape-decorated charts.
     * Only applies to Cylinder, Cone, and Pyramid chart types.
     * Null = BOX (standard rectangular bars).
     */
    BarShape shape;

    /**
     * Gap depth as a percentage for 3D bar/column charts (0–500).
     * Controls spacing between series in the depth dimension. Null = Aspose default.
     */
    Integer gapDepth;

    // ── Convenience factories ─────────────────────────────────────────────────

    public static BarColumnConfig defaults() {
        return BarColumnConfig.builder().build();
    }

    /** Tightly packed bars (small gap, slight overlap). */
    public static BarColumnConfig tight() {
        return BarColumnConfig.builder()
                .gapWidth(80)
                .overlap(-10)
                .build();
    }

    /** Wide gap for a cleaner, more spacious look. */
    public static BarColumnConfig spacious() {
        return BarColumnConfig.builder()
                .gapWidth(220)
                .build();
    }
}
