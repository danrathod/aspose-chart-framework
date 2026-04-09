package com.chartframework.config;

import lombok.Builder;
import lombok.Value;

/**
 * 3D view configuration for charts that support a three-dimensional perspective
 * (3D Column, 3D Bar, 3D Pie, 3D Area, Surface, etc.).
 *
 * <pre>{@code
 * ThreeDConfig threeD = ThreeDConfig.builder()
 *     .rotationX(15)        // tilt (elevation), 0-90
 *     .rotationY(20)        // rotation, 0-360
 *     .perspective(30)      // depth perspective, 0-100
 *     .heightPercent(100)   // chart height as % of width, 5-500
 *     .rightAngleAxes(true) // if true, perspective is ignored
 *     .build();
 * }</pre>
 */
@Value
@Builder
public class ThreeDConfig {

    /**
     * Elevation / tilt angle around the X axis (degrees, 0–90).
     * Null = Aspose default (typically 15).
     */
    Integer rotationX;

    /**
     * Rotation around the Y axis / perspective rotation (degrees, 0–360).
     * Null = Aspose default (typically 20).
     */
    Integer rotationY;

    /**
     * Perspective depth (0–100). Higher = more pronounced perspective.
     * Ignored when {@link #rightAngleAxes} is true.
     * Null = Aspose default (typically 30).
     */
    Integer perspective;

    /**
     * Chart height as a percentage of chart width (5–500).
     * Null = Aspose default (100 = square aspect ratio).
     */
    Integer heightPercent;

    /**
     * If true, axes are drawn at right angles (isometric view), and
     * {@link #perspective} is ignored.
     * Null = Aspose default (true for most 3D chart types).
     */
    Boolean rightAngleAxes;

    /**
     * Depth of data series as a percentage of chart width (20–2000).
     * Controls how deep the 3D effect appears.
     * Null = Aspose default (100).
     */
    Integer depthPercent;

    // ── Convenience factories ─────────────────────────────────────────────────

    /** Standard isometric-style 3D view (right angles, no perspective). */
    public static ThreeDConfig isometric() {
        return ThreeDConfig.builder()
                .rotationX(15)
                .rotationY(20)
                .rightAngleAxes(true)
                .build();
    }

    /** Perspective 3D view with moderate depth. */
    public static ThreeDConfig perspective() {
        return ThreeDConfig.builder()
                .rotationX(15)
                .rotationY(20)
                .perspective(30)
                .rightAngleAxes(false)
                .build();
    }
}
