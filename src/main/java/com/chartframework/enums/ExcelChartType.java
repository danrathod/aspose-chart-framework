package com.chartframework.enums;

import com.aspose.cells.ChartType;

/**
 * Enum representing all Excel chart types supported by Aspose.Cells for Java.
 *
 * <p>Every constant maps 1-to-1 to a verified field in
 * {@link com.aspose.cells.ChartType}:
 * https://reference.aspose.com/cells/java/com.aspose.cells/charttype/
 *
 * <h2>Key Naming Rules in the Aspose.Cells Java API</h2>
 * <ul>
 *   <li>3D suffix: {@code _3_D} — e.g. {@code AREA_3_D}, {@code LINE_3_D},
 *       {@code BUBBLE_3_D}, {@code COLUMN_3_D}, {@code PIE_3_D}</li>
 *   <li>Clustered Column = {@code COLUMN}  (not COLUMN_CLUSTERED)</li>
 *   <li>Clustered Bar    = {@code BAR}     (not BAR_CLUSTERED)</li>
 *   <li>Pie of Pie       = {@code PIE_PIE} (not PIE_OF_PIE)</li>
 *   <li>Bar of Pie       = {@code PIE_BAR} (not BAR_OF_PIE)</li>
 *   <li>Scatter with/without markers: singular {@code _DATA_MARKER} (not MARKERS)</li>
 *   <li>Conical 3D col:    {@code CONICAL_COLUMN_3_D}</li>
 *   <li>Cylindrical 3D col:{@code CYLINDRICAL_COLUMN_3_D}</li>
 *   <li>Pyramid 3D col:    {@code PYRAMID_COLUMN_3_D}</li>
 *   <li>Combo charts have no dedicated ChartType constant — create as COLUMN
 *       then call {@code series.setType(ChartType.LINE)} on individual series.</li>
 * </ul>
 */
public enum ExcelChartType {

    // ── Column ────────────────────────────────────────────────────────────────
    /** Clustered Column — ChartType.COLUMN */
    COLUMN                          (ChartType.COLUMN,                              "Clustered Column"),
    COLUMN_STACKED                  (ChartType.COLUMN_STACKED,                      "Stacked Column"),
    COLUMN_100_PERCENT_STACKED      (ChartType.COLUMN_100_PERCENT_STACKED,          "100% Stacked Column"),
    /** 3D Column — ChartType.COLUMN_3_D */
    COLUMN_3D                       (ChartType.COLUMN_3_D,                          "3D Column"),
    COLUMN_3D_CLUSTERED             (ChartType.COLUMN_3_D_CLUSTERED,                "3D Clustered Column"),
    COLUMN_3D_STACKED               (ChartType.COLUMN_3_D_STACKED,                  "3D Stacked Column"),
    COLUMN_3D_100_PERCENT_STACKED   (ChartType.COLUMN_3_D_100_PERCENT_STACKED,      "3D 100% Stacked Column"),

    // ── Bar ───────────────────────────────────────────────────────────────────
    /** Clustered Bar — ChartType.BAR */
    BAR                             (ChartType.BAR,                                 "Clustered Bar"),
    BAR_STACKED                     (ChartType.BAR_STACKED,                         "Stacked Bar"),
    BAR_100_PERCENT_STACKED         (ChartType.BAR_100_PERCENT_STACKED,             "100% Stacked Bar"),
    BAR_3D_CLUSTERED                (ChartType.BAR_3_D_CLUSTERED,                   "3D Clustered Bar"),
    BAR_3D_STACKED                  (ChartType.BAR_3_D_STACKED,                     "3D Stacked Bar"),
    BAR_3D_100_PERCENT_STACKED      (ChartType.BAR_3_D_100_PERCENT_STACKED,         "3D 100% Stacked Bar"),

    // ── Line ──────────────────────────────────────────────────────────────────
    LINE                                        (ChartType.LINE,                                        "Line"),
    LINE_STACKED                                (ChartType.LINE_STACKED,                                "Stacked Line"),
    LINE_100_PERCENT_STACKED                    (ChartType.LINE_100_PERCENT_STACKED,                    "100% Stacked Line"),
    LINE_WITH_DATA_MARKERS                      (ChartType.LINE_WITH_DATA_MARKERS,                      "Line with Data Markers"),
    LINE_STACKED_WITH_DATA_MARKERS              (ChartType.LINE_STACKED_WITH_DATA_MARKERS,              "Stacked Line with Data Markers"),
    LINE_100_PERCENT_STACKED_WITH_DATA_MARKERS  (ChartType.LINE_100_PERCENT_STACKED_WITH_DATA_MARKERS,  "100% Stacked Line with Data Markers"),
    /** 3D Line — ChartType.LINE_3_D */
    LINE_3D                                     (ChartType.LINE_3_D,                                    "3D Line"),

    // ── Pie ───────────────────────────────────────────────────────────────────
    PIE                             (ChartType.PIE,                                 "Pie"),
    /** 3D Pie — ChartType.PIE_3_D */
    PIE_3D                          (ChartType.PIE_3_D,                             "3D Pie"),
    PIE_EXPLODED                    (ChartType.PIE_EXPLODED,                        "Exploded Pie"),
    /** 3D Exploded Pie — ChartType.PIE_3_D_EXPLODED */
    PIE_3D_EXPLODED                 (ChartType.PIE_3_D_EXPLODED,                    "3D Exploded Pie"),
    /** Pie of Pie — ChartType.PIE_PIE */
    PIE_PIE                         (ChartType.PIE_PIE,                             "Pie of Pie"),
    /** Bar of Pie — ChartType.PIE_BAR */
    PIE_BAR                         (ChartType.PIE_BAR,                             "Bar of Pie"),

    // ── Doughnut ─────────────────────────────────────────────────────────────
    DOUGHNUT                        (ChartType.DOUGHNUT,                            "Doughnut"),
    DOUGHNUT_EXPLODED               (ChartType.DOUGHNUT_EXPLODED,                   "Exploded Doughnut"),

    // ── Area ──────────────────────────────────────────────────────────────────
    AREA                            (ChartType.AREA,                                "Area"),
    AREA_STACKED                    (ChartType.AREA_STACKED,                        "Stacked Area"),
    AREA_100_PERCENT_STACKED        (ChartType.AREA_100_PERCENT_STACKED,            "100% Stacked Area"),
    /** 3D Area — ChartType.AREA_3_D */
    AREA_3D                         (ChartType.AREA_3_D,                            "3D Area"),
    AREA_3D_STACKED                 (ChartType.AREA_3_D_STACKED,                    "3D Stacked Area"),
    AREA_3D_100_PERCENT_STACKED     (ChartType.AREA_3_D_100_PERCENT_STACKED,        "3D 100% Stacked Area"),

    // ── Scatter (XY) ─────────────────────────────────────────────────────────
    SCATTER                                             (ChartType.SCATTER,                                             "Scatter"),
    SCATTER_CONNECTED_BY_CURVES_WITH_DATA_MARKER        (ChartType.SCATTER_CONNECTED_BY_CURVES_WITH_DATA_MARKER,        "Scatter — Smooth Lines with Markers"),
    SCATTER_CONNECTED_BY_CURVES_WITHOUT_DATA_MARKER     (ChartType.SCATTER_CONNECTED_BY_CURVES_WITHOUT_DATA_MARKER,     "Scatter — Smooth Lines without Markers"),
    SCATTER_CONNECTED_BY_LINES_WITH_DATA_MARKER         (ChartType.SCATTER_CONNECTED_BY_LINES_WITH_DATA_MARKER,         "Scatter — Straight Lines with Markers"),
    SCATTER_CONNECTED_BY_LINES_WITHOUT_DATA_MARKER      (ChartType.SCATTER_CONNECTED_BY_LINES_WITHOUT_DATA_MARKER,      "Scatter — Straight Lines without Markers"),

    // ── Bubble ────────────────────────────────────────────────────────────────
    BUBBLE                          (ChartType.BUBBLE,                              "Bubble"),
    /** 3D Bubble — ChartType.BUBBLE_3_D */
    BUBBLE_3D                       (ChartType.BUBBLE_3_D,                          "3D Bubble"),

    // ── Radar ─────────────────────────────────────────────────────────────────
    RADAR                           (ChartType.RADAR,                               "Radar"),
    RADAR_WITH_DATA_MARKERS         (ChartType.RADAR_WITH_DATA_MARKERS,             "Radar with Data Markers"),
    RADAR_FILLED                    (ChartType.RADAR_FILLED,                        "Filled Radar"),

    // ── Stock (OHLC) ─────────────────────────────────────────────────────────
    STOCK_HIGH_LOW_CLOSE                (ChartType.STOCK_HIGH_LOW_CLOSE,                "High-Low-Close Stock"),
    STOCK_OPEN_HIGH_LOW_CLOSE           (ChartType.STOCK_OPEN_HIGH_LOW_CLOSE,           "Open-High-Low-Close Stock"),
    STOCK_VOLUME_HIGH_LOW_CLOSE         (ChartType.STOCK_VOLUME_HIGH_LOW_CLOSE,         "Volume-High-Low-Close Stock"),
    STOCK_VOLUME_OPEN_HIGH_LOW_CLOSE    (ChartType.STOCK_VOLUME_OPEN_HIGH_LOW_CLOSE,    "Volume-Open-High-Low-Close Stock"),

    // ── Surface / Contour ─────────────────────────────────────────────────────
    /** 3D Surface — ChartType.SURFACE_3_D */
    SURFACE_3D                      (ChartType.SURFACE_3_D,                         "3D Surface"),
    /** Wireframe 3D Surface — ChartType.SURFACE_WIREFRAME_3_D */
    SURFACE_WIREFRAME_3D            (ChartType.SURFACE_WIREFRAME_3_D,               "Wireframe 3D Surface"),
    SURFACE_CONTOUR                 (ChartType.SURFACE_CONTOUR,                     "Contour"),
    SURFACE_WIREFRAME_CONTOUR       (ChartType.SURFACE_WIREFRAME_CONTOUR,           "Wireframe Contour"),

    // ── Cylinder ─────────────────────────────────────────────────────────────
    CYLINDER                        (ChartType.CYLINDER,                            "Cylinder"),
    CYLINDER_STACKED                (ChartType.CYLINDER_STACKED,                    "Stacked Cylinder"),
    CYLINDER_100_PERCENT_STACKED    (ChartType.CYLINDER_100_PERCENT_STACKED,        "100% Stacked Cylinder"),
    /** Cylindrical Bar (horizontal) — ChartType.CYLINDRICAL_BAR */
    CYLINDRICAL_BAR                 (ChartType.CYLINDRICAL_BAR,                     "Cylindrical Bar"),
    CYLINDRICAL_BAR_STACKED         (ChartType.CYLINDRICAL_BAR_STACKED,             "Stacked Cylindrical Bar"),
    CYLINDRICAL_BAR_100_STACKED     (ChartType.CYLINDRICAL_BAR_100_PERCENT_STACKED, "100% Stacked Cylindrical Bar"),
    /** 3D Cylindrical Column — ChartType.CYLINDRICAL_COLUMN_3_D */
    CYLINDRICAL_COLUMN_3D           (ChartType.CYLINDRICAL_COLUMN_3_D,              "3D Cylindrical Column"),

    // ── Cone ─────────────────────────────────────────────────────────────────
    CONE                            (ChartType.CONE,                                "Cone"),
    CONE_STACKED                    (ChartType.CONE_STACKED,                        "Stacked Cone"),
    CONE_100_PERCENT_STACKED        (ChartType.CONE_100_PERCENT_STACKED,            "100% Stacked Cone"),
    /** Conical Bar (horizontal) — ChartType.CONICAL_BAR */
    CONICAL_BAR                     (ChartType.CONICAL_BAR,                         "Conical Bar"),
    CONICAL_BAR_STACKED             (ChartType.CONICAL_BAR_STACKED,                 "Stacked Conical Bar"),
    CONICAL_BAR_100_STACKED         (ChartType.CONICAL_BAR_100_PERCENT_STACKED,     "100% Stacked Conical Bar"),
    /** 3D Conical Column — ChartType.CONICAL_COLUMN_3_D */
    CONICAL_COLUMN_3D               (ChartType.CONICAL_COLUMN_3_D,                  "3D Conical Column"),

    // ── Pyramid ───────────────────────────────────────────────────────────────
    PYRAMID                         (ChartType.PYRAMID,                             "Pyramid"),
    PYRAMID_STACKED                 (ChartType.PYRAMID_STACKED,                     "Stacked Pyramid"),
    PYRAMID_100_PERCENT_STACKED     (ChartType.PYRAMID_100_PERCENT_STACKED,         "100% Stacked Pyramid"),
    /** Pyramid Bar (horizontal) — ChartType.PYRAMID_BAR */
    PYRAMID_BAR                     (ChartType.PYRAMID_BAR,                         "Pyramid Bar"),
    PYRAMID_BAR_STACKED             (ChartType.PYRAMID_BAR_STACKED,                 "Stacked Pyramid Bar"),
    PYRAMID_BAR_100_STACKED         (ChartType.PYRAMID_BAR_100_PERCENT_STACKED,     "100% Stacked Pyramid Bar"),
    /** 3D Pyramid Column — ChartType.PYRAMID_COLUMN_3_D */
    PYRAMID_COLUMN_3D               (ChartType.PYRAMID_COLUMN_3_D,                  "3D Pyramid Column"),

    // ── Excel 2016+ Modern Chart Types ───────────────────────────────────────

    /** Funnel — values across stages in a process (e.g. a sales pipeline). */
    FUNNEL                          (ChartType.FUNNEL,                              "Funnel"),

    /** Treemap — hierarchical data as nested rectangles. */
    TREEMAP                         (ChartType.TREEMAP,                             "Treemap"),

    /** Sunburst — hierarchical data as concentric rings. */
    SUNBURST                        (ChartType.SUNBURST,                            "Sunburst"),

    /** Histogram — frequency distribution column chart. */
    HISTOGRAM                       (ChartType.HISTOGRAM,                           "Histogram"),

    /** Pareto — histogram (sorted desc) with a cumulative percentage line. */
    PARETO_LINE                     (ChartType.PARETO_LINE,                         "Pareto"),

    /** Box and Whisker — shows distribution quartiles with outliers. */
    BOX_WHISKER                     (ChartType.BOX_WHISKER,                         "Box and Whisker"),

    /** Waterfall — running total showing cumulative effect of sequential values. */
    WATERFALL                       (ChartType.WATERFALL,                           "Waterfall"),

    /**
     * Map / Region — geographic data mapped to regions. Requires Excel 2019+.
     */
    MAP                             (ChartType.MAP,                                 "Map (Region)");

    // ─────────────────────────────────────────────────────────────────────────
    // NOTE: Combo / Dual-axis charts have NO dedicated ChartType constant.
    // To create a combo chart, add a base ChartType.COLUMN chart, then call
    // series.setType(ChartType.LINE) on the series you want as a line.
    // See ComboChartStrategy for the implementation.
    // ─────────────────────────────────────────────────────────────────────────

    private final int    asposeChartType;
    private final String displayName;

    ExcelChartType(int asposeChartType, String displayName) {
        this.asposeChartType = asposeChartType;
        this.displayName     = displayName;
    }

    /** The raw {@link ChartType} int constant used in Aspose API calls. */
    public int getAsposeChartType() { return asposeChartType; }

    /** Human-readable label suitable for logging and error messages. */
    public String getDisplayName()  { return displayName; }
}
