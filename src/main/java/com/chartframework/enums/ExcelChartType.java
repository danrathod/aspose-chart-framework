package com.chartframework.enums;

import com.aspose.cells.ChartType;

/**
 * Enum representing ALL Excel chart types supported by Aspose.Cells Java.
 *
 * <p>Sourced from the official Aspose.Cells Java API reference:
 * https://reference.aspose.com/cells/java/com.aspose.cells/charttype/
 * and the chart-types documentation page.</p>
 *
 * <p>Each constant maps directly to an Aspose.Cells {@link ChartType} integer
 * constant, providing a type-safe, self-documenting API surface.</p>
 *
 * <h2>Chart Families</h2>
 * <ul>
 *   <li>Column / Bar</li>
 *   <li>Line</li>
 *   <li>Pie / Doughnut</li>
 *   <li>Area</li>
 *   <li>Scatter (XY)</li>
 *   <li>Bubble</li>
 *   <li>Radar</li>
 *   <li>Stock (OHLC)</li>
 *   <li>Surface / Contour</li>
 *   <li>Cylinder</li>
 *   <li>Cone</li>
 *   <li>Pyramid</li>
 *   <li>Funnel          ← Excel 2016+</li>
 *   <li>Treemap         ← Excel 2016+</li>
 *   <li>Sunburst        ← Excel 2016+</li>
 *   <li>Histogram       ← Excel 2016+</li>
 *   <li>Pareto          ← Excel 2016+</li>
 *   <li>Box & Whisker   ← Excel 2016+</li>
 *   <li>Waterfall       ← Excel 2016+</li>
 *   <li>Map (Region)    ← Excel 2019+</li>
 *   <li>Combo (Custom Combination)</li>
 * </ul>
 */
public enum ExcelChartType {

    // ── Column ────────────────────────────────────────────────────────────────
    COLUMN_CLUSTERED            (ChartType.COLUMN_CLUSTERED,                "Clustered Column"),
    COLUMN_STACKED              (ChartType.COLUMN_STACKED,                  "Stacked Column"),
    COLUMN_100_STACKED          (ChartType.COLUMN_100_PERCENT_STACKED,      "100% Stacked Column"),
    COLUMN_3D_CLUSTERED         (ChartType.COLUMN3_D_CLUSTERED,             "3D Clustered Column"),
    COLUMN_3D_STACKED           (ChartType.COLUMN3_D_STACKED,               "3D Stacked Column"),
    COLUMN_3D_100_STACKED       (ChartType.COLUMN3_D_100_PERCENT_STACKED,   "3D 100% Stacked Column"),
    COLUMN_3D                   (ChartType.COLUMN3_D,                       "3D Column"),

    // ── Bar ───────────────────────────────────────────────────────────────────
    BAR_CLUSTERED               (ChartType.BAR_CLUSTERED,                   "Clustered Bar"),
    BAR_STACKED                 (ChartType.BAR_STACKED,                     "Stacked Bar"),
    BAR_100_STACKED             (ChartType.BAR_100_PERCENT_STACKED,         "100% Stacked Bar"),
    BAR_3D_CLUSTERED            (ChartType.BAR3_D_CLUSTERED,                "3D Clustered Bar"),
    BAR_3D_STACKED              (ChartType.BAR3_D_STACKED,                  "3D Stacked Bar"),
    BAR_3D_100_STACKED          (ChartType.BAR3_D_100_PERCENT_STACKED,      "3D 100% Stacked Bar"),

    // ── Line ──────────────────────────────────────────────────────────────────
    LINE                                    (ChartType.LINE,                                    "Line"),
    LINE_STACKED                            (ChartType.LINE_STACKED,                            "Stacked Line"),
    LINE_100_STACKED                        (ChartType.LINE_100_PERCENT_STACKED,                "100% Stacked Line"),
    LINE_WITH_DATA_MARKERS                  (ChartType.LINE_WITH_DATA_MARKERS,                  "Line with Data Markers"),
    LINE_STACKED_WITH_DATA_MARKERS          (ChartType.LINE_STACKED_WITH_DATA_MARKERS,          "Stacked Line with Data Markers"),
    LINE_100_STACKED_WITH_DATA_MARKERS      (ChartType.LINE_100_PERCENT_STACKED_WITH_DATA_MARKERS, "100% Stacked Line with Data Markers"),
    LINE_3D                                 (ChartType.LINE3_D,                                 "3D Line"),

    // ── Pie ───────────────────────────────────────────────────────────────────
    PIE                         (ChartType.PIE,                             "Pie"),
    PIE_3D                      (ChartType.PIE3_D,                          "3D Pie"),
    PIE_EXPLODED                (ChartType.PIE_EXPLODED,                    "Exploded Pie"),
    PIE_3D_EXPLODED             (ChartType.PIE3_D_EXPLODED,                 "3D Exploded Pie"),
    PIE_OF_PIE                  (ChartType.PIE_OF_PIE,                      "Pie of Pie"),
    BAR_OF_PIE                  (ChartType.BAR_OF_PIE,                      "Bar of Pie"),

    // ── Doughnut ─────────────────────────────────────────────────────────────
    DOUGHNUT                    (ChartType.DOUGHNUT,                        "Doughnut"),
    DOUGHNUT_EXPLODED           (ChartType.DOUGHNUT_EXPLODED,               "Exploded Doughnut"),

    // ── Area ──────────────────────────────────────────────────────────────────
    AREA                        (ChartType.AREA,                            "Area"),
    AREA_STACKED                (ChartType.AREA_STACKED,                    "Stacked Area"),
    AREA_100_STACKED            (ChartType.AREA_100_PERCENT_STACKED,        "100% Stacked Area"),
    AREA_3D                     (ChartType.AREA_3_D,                        "3D Area"),
    AREA_3D_STACKED             (ChartType.AREA_3_D_STACKED,                "3D Stacked Area"),
    AREA_3D_100_STACKED         (ChartType.AREA_3_D_100_PERCENT_STACKED,    "3D 100% Stacked Area"),

    // ── Scatter (XY) ─────────────────────────────────────────────────────────
    SCATTER                                         (ChartType.SCATTER,                                         "Scatter"),
    SCATTER_CONNECTED_CURVES                        (ChartType.SCATTER_CONNECTED_BY_CURVES,                     "Scatter with Smooth Lines"),
    SCATTER_CONNECTED_CURVES_WITH_DATA_MARKERS      (ChartType.SCATTER_CONNECTED_BY_CURVES_WITH_DATA_MARKER,    "Scatter with Smooth Lines and Markers"),
    SCATTER_CONNECTED_LINES                         (ChartType.SCATTER_CONNECTED_BY_LINES,                      "Scatter with Straight Lines"),
    SCATTER_CONNECTED_LINES_WITH_DATA_MARKERS       (ChartType.SCATTER_CONNECTED_BY_LINES_WITH_DATA_MARKER,     "Scatter with Straight Lines and Markers"),

    // ── Bubble ────────────────────────────────────────────────────────────────
    BUBBLE                      (ChartType.BUBBLE,                          "Bubble"),
    BUBBLE_3D                   (ChartType.BUBBLE3_D,                       "3D Bubble"),

    // ── Radar ─────────────────────────────────────────────────────────────────
    RADAR                       (ChartType.RADAR,                           "Radar"),
    RADAR_WITH_DATA_MARKERS     (ChartType.RADAR_WITH_DATA_MARKERS,         "Radar with Data Markers"),
    RADAR_FILLED                (ChartType.RADAR_FILLED,                    "Filled Radar"),

    // ── Stock (OHLC) ─────────────────────────────────────────────────────────
    STOCK_HIGH_LOW_CLOSE                (ChartType.STOCK_HIGH_LOW_CLOSE,                "High-Low-Close Stock"),
    STOCK_OPEN_HIGH_LOW_CLOSE           (ChartType.STOCK_OPEN_HIGH_LOW_CLOSE,           "Open-High-Low-Close Stock"),
    STOCK_VOLUME_HIGH_LOW_CLOSE         (ChartType.STOCK_VOLUME_HIGH_LOW_CLOSE,         "Volume-High-Low-Close Stock"),
    STOCK_VOLUME_OPEN_HIGH_LOW_CLOSE    (ChartType.STOCK_VOLUME_OPEN_HIGH_LOW_CLOSE,    "Volume-Open-High-Low-Close Stock"),

    // ── Surface / Contour ────────────────────────────────────────────────────
    SURFACE_3D                  (ChartType.SURFACE3_D,                      "3D Surface"),
    SURFACE_WIREFRAME_3D        (ChartType.SURFACE_WIREFRAME3_D,            "Wireframe 3D Surface"),
    SURFACE_CONTOUR             (ChartType.SURFACE_CONTOUR,                 "Contour"),
    SURFACE_CONTOUR_WIREFRAME   (ChartType.SURFACE_WIREFRAME_CONTOUR,       "Wireframe Contour"),

    // ── Cylinder ─────────────────────────────────────────────────────────────
    CYLINDER                        (ChartType.CYLINDER,                        "Cylinder"),
    CYLINDER_STACKED                (ChartType.CYLINDER_STACKED,                "Stacked Cylinder"),
    CYLINDER_100_STACKED            (ChartType.CYLINDER_100_PERCENT_STACKED,    "100% Stacked Cylinder"),
    CYLINDER_BAR                    (ChartType.CYLINDRICAL_BAR,                 "Cylindrical Bar"),
    CYLINDER_BAR_STACKED            (ChartType.CYLINDRICAL_BAR_STACKED,         "Stacked Cylindrical Bar"),
    CYLINDER_BAR_100_STACKED        (ChartType.CYLINDRICAL_BAR_100_PERCENT_STACKED, "100% Stacked Cylindrical Bar"),
    CYLINDER_3D                     (ChartType.CYLINDRICAL_COLUMN3_D,           "3D Cylindrical Column"),

    // ── Cone ─────────────────────────────────────────────────────────────────
    CONE                        (ChartType.CONE,                            "Cone"),
    CONE_STACKED                (ChartType.CONE_STACKED,                    "Stacked Cone"),
    CONE_100_STACKED            (ChartType.CONE_100_PERCENT_STACKED,        "100% Stacked Cone"),
    CONE_BAR                    (ChartType.CONICAL_BAR,                     "Conical Bar"),
    CONE_BAR_STACKED            (ChartType.CONICAL_BAR_STACKED,             "Stacked Conical Bar"),
    CONE_BAR_100_STACKED        (ChartType.CONICAL_BAR_100_PERCENT_STACKED, "100% Stacked Conical Bar"),
    CONE_3D                     (ChartType.CONICAL_COLUMN3_D,               "3D Conical Column"),

    // ── Pyramid ──────────────────────────────────────────────────────────────
    PYRAMID                     (ChartType.PYRAMID,                         "Pyramid"),
    PYRAMID_STACKED             (ChartType.PYRAMID_STACKED,                 "Stacked Pyramid"),
    PYRAMID_100_STACKED         (ChartType.PYRAMID_100_PERCENT_STACKED,     "100% Stacked Pyramid"),
    PYRAMID_BAR                 (ChartType.PYRAMID_BAR,                     "Pyramid Bar"),
    PYRAMID_BAR_STACKED         (ChartType.PYRAMID_BAR_STACKED,             "Stacked Pyramid Bar"),
    PYRAMID_BAR_100_STACKED     (ChartType.PYRAMID_BAR_100_PERCENT_STACKED, "100% Stacked Pyramid Bar"),
    PYRAMID_3D                  (ChartType.PYRAMID_COLUMN3_D,               "3D Pyramid Column"),

    // ── Excel 2016+ Modern Chart Types ───────────────────────────────────────

    /** Funnel chart — values across stages in a process (e.g. sales pipeline). */
    FUNNEL                      (ChartType.FUNNEL,                          "Funnel"),

    /** Treemap — hierarchical data as nested rectangles. */
    TREEMAP                     (ChartType.TREEMAP,                         "Treemap"),

    /** Sunburst — hierarchical data as concentric rings. */
    SUNBURST                    (ChartType.SUNBURST,                        "Sunburst"),

    /** Histogram — frequency distribution column chart. */
    HISTOGRAM                   (ChartType.HISTOGRAM,                       "Histogram"),

    /** Pareto — histogram combined with a cumulative percentage line. */
    PARETO                      (ChartType.PARETO_LINE,                     "Pareto"),

    /** Box and Whisker — distribution quartiles with outliers. */
    BOX_WHISKER                 (ChartType.BOX_WHISKER,                     "Box and Whisker"),

    /** Waterfall — running total showing cumulative effect of sequential values. */
    WATERFALL                   (ChartType.WATERFALL,                       "Waterfall"),

    /** Map (Region / Filled Map) — geographic data mapped to regions. Excel 2019+. */
    MAP                         (ChartType.MAP,                             "Map (Region)"),

    // ── Combo ─────────────────────────────────────────────────────────────────
    /** Custom combination / dual-axis chart (e.g. column + line). */
    COMBO                       (ChartType.CUSTOM_COMBINATION,              "Combo / Custom Combination");

    // ─────────────────────────────────────────────────────────────────────────

    private final int    asposeChartType;
    private final String displayName;

    ExcelChartType(int asposeChartType, String displayName) {
        this.asposeChartType = asposeChartType;
        this.displayName     = displayName;
    }

    /** Returns the raw Aspose.Cells {@code ChartType} int value. */
    public int getAsposeChartType() { return asposeChartType; }

    /** Human-readable label suitable for logging / error messages. */
    public String getDisplayName()  { return displayName; }
}
