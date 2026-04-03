package com.chartframework.factory;

import com.chartframework.enums.ExcelChartType;
import com.chartframework.exception.ChartFrameworkException;
import com.chartframework.strategy.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.EnumMap;
import java.util.Map;

/**
 * Factory that resolves the correct {@link ChartStrategy} for a given
 * {@link ExcelChartType}.
 *
 * <h2>Design: Registry-based Factory</h2>
 * <p>Strategies are registered in an {@link EnumMap} at construction time,
 * eliminating all {@code if/else} or {@code switch} chains. Adding a new
 * chart type is a one-liner in {@link #buildRegistry()}.</p>
 *
 * <h2>Coverage</h2>
 * <p>All chart types documented at
 * https://docs.aspose.com/cells/net/chart-types/ are registered, including:</p>
 * <ul>
 *   <li>Column, Bar, Line, Pie, Area, Scatter, Bubble, Radar, Stock, Surface</li>
 *   <li>Cylinder, Cone, Pyramid (shape-decorated column/bar variants)</li>
 *   <li>Funnel, Treemap, Sunburst, Histogram, Pareto, Box &amp; Whisker,
 *       Waterfall, Map (all Excel 2016+ types)</li>
 *   <li>Combo (custom combination / dual-axis)</li>
 * </ul>
 */
public class ChartStrategyFactory {

    private static final Logger log = LoggerFactory.getLogger(ChartStrategyFactory.class);

    private final Map<ExcelChartType, ChartStrategy> registry;

    public ChartStrategyFactory() {
        this.registry = buildRegistry();
        log.info("ChartStrategyFactory initialised with {} registered strategies.", registry.size());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Public API
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Returns the strategy for the requested chart type.
     *
     * @throws ChartFrameworkException if no strategy is registered for the type.
     */
    public ChartStrategy getStrategy(ExcelChartType chartType) {
        ChartStrategy strategy = registry.get(chartType);
        if (strategy == null) {
            throw new ChartFrameworkException(
                    "No ChartStrategy registered for chart type: " + chartType
                            + " (" + chartType.getDisplayName() + "). "
                            + "Register it in ChartStrategyFactory#buildRegistry().");
        }
        log.debug("Resolved strategy [{}] for chart type [{}]",
                strategy.getClass().getSimpleName(), chartType.getDisplayName());
        return strategy;
    }

    /**
     * Registers or replaces a strategy at runtime (useful for testing /
     * plugin-style extensibility).
     */
    public void registerStrategy(ExcelChartType chartType, ChartStrategy strategy) {
        registry.put(chartType, strategy);
        log.info("Registered custom strategy [{}] for chart type [{}]",
                strategy.getClass().getSimpleName(), chartType.getDisplayName());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Registry — override to customise
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Builds the default strategy registry.
     * Subclasses may call {@code super.buildRegistry()} and add/replace entries.
     */
    protected Map<ExcelChartType, ChartStrategy> buildRegistry() {
        Map<ExcelChartType, ChartStrategy> map = new EnumMap<>(ExcelChartType.class);

        // ── Shared (stateless) strategy instances ─────────────────────────────
        ChartStrategy columnBar     = new ColumnBarChartStrategy();
        ChartStrategy lineArea      = new LineAreaChartStrategy();
        ChartStrategy pie           = new PieDonutChartStrategy();
        ChartStrategy scatter       = new ScatterChartStrategy();
        ChartStrategy bubble        = new BubbleChartStrategy();
        ChartStrategy radar         = new RadarChartStrategy();
        ChartStrategy stock         = new StockChartStrategy();
        ChartStrategy surface       = new SurfaceChartStrategy();
        ChartStrategy cylConePyr    = new CylinderConePyramidChartStrategy();
        ChartStrategy funnel        = new FunnelChartStrategy();
        ChartStrategy treemapSun    = new TreemapSunburstChartStrategy();
        ChartStrategy histPareto    = new HistogramParetoChartStrategy();
        ChartStrategy boxWhisker    = new BoxWhiskerChartStrategy();
        ChartStrategy waterfall     = new WaterfallChartStrategy();
        ChartStrategy map           = new MapChartStrategy();
        ChartStrategy combo         = new ComboChartStrategy();

        // ── Column ────────────────────────────────────────────────────────────
        map.put(ExcelChartType.COLUMN_CLUSTERED,        columnBar);
        map.put(ExcelChartType.COLUMN_STACKED,          columnBar);
        map.put(ExcelChartType.COLUMN_100_STACKED,      columnBar);
        map.put(ExcelChartType.COLUMN_3D_CLUSTERED,     columnBar);
        map.put(ExcelChartType.COLUMN_3D_STACKED,       columnBar);
        map.put(ExcelChartType.COLUMN_3D_100_STACKED,   columnBar);
        map.put(ExcelChartType.COLUMN_3D,               columnBar);

        // ── Bar ───────────────────────────────────────────────────────────────
        map.put(ExcelChartType.BAR_CLUSTERED,           columnBar);
        map.put(ExcelChartType.BAR_STACKED,             columnBar);
        map.put(ExcelChartType.BAR_100_STACKED,         columnBar);
        map.put(ExcelChartType.BAR_3D_CLUSTERED,        columnBar);
        map.put(ExcelChartType.BAR_3D_STACKED,          columnBar);
        map.put(ExcelChartType.BAR_3D_100_STACKED,      columnBar);

        // ── Line ──────────────────────────────────────────────────────────────
        map.put(ExcelChartType.LINE,                                lineArea);
        map.put(ExcelChartType.LINE_STACKED,                        lineArea);
        map.put(ExcelChartType.LINE_100_STACKED,                    lineArea);
        map.put(ExcelChartType.LINE_WITH_DATA_MARKERS,              lineArea);
        map.put(ExcelChartType.LINE_STACKED_WITH_DATA_MARKERS,      lineArea);
        map.put(ExcelChartType.LINE_100_STACKED_WITH_DATA_MARKERS,  lineArea);
        map.put(ExcelChartType.LINE_3D,                             lineArea);

        // ── Area ──────────────────────────────────────────────────────────────
        map.put(ExcelChartType.AREA,                lineArea);
        map.put(ExcelChartType.AREA_STACKED,        lineArea);
        map.put(ExcelChartType.AREA_100_STACKED,    lineArea);
        map.put(ExcelChartType.AREA_3D,             lineArea);
        map.put(ExcelChartType.AREA_3D_STACKED,     lineArea);
        map.put(ExcelChartType.AREA_3D_100_STACKED, lineArea);

        // ── Pie / Doughnut ────────────────────────────────────────────────────
        map.put(ExcelChartType.PIE,              pie);
        map.put(ExcelChartType.PIE_3D,           pie);
        map.put(ExcelChartType.PIE_EXPLODED,     pie);
        map.put(ExcelChartType.PIE_3D_EXPLODED,  pie);
        map.put(ExcelChartType.PIE_OF_PIE,       pie);
        map.put(ExcelChartType.BAR_OF_PIE,       pie);
        map.put(ExcelChartType.DOUGHNUT,         pie);
        map.put(ExcelChartType.DOUGHNUT_EXPLODED,pie);

        // ── Scatter ───────────────────────────────────────────────────────────
        map.put(ExcelChartType.SCATTER,                                     scatter);
        map.put(ExcelChartType.SCATTER_CONNECTED_CURVES,                    scatter);
        map.put(ExcelChartType.SCATTER_CONNECTED_CURVES_WITH_DATA_MARKERS,  scatter);
        map.put(ExcelChartType.SCATTER_CONNECTED_LINES,                     scatter);
        map.put(ExcelChartType.SCATTER_CONNECTED_LINES_WITH_DATA_MARKERS,   scatter);

        // ── Bubble ────────────────────────────────────────────────────────────
        map.put(ExcelChartType.BUBBLE,      bubble);
        map.put(ExcelChartType.BUBBLE_3D,   bubble);

        // ── Radar ─────────────────────────────────────────────────────────────
        map.put(ExcelChartType.RADAR,                   radar);
        map.put(ExcelChartType.RADAR_WITH_DATA_MARKERS, radar);
        map.put(ExcelChartType.RADAR_FILLED,            radar);

        // ── Stock ─────────────────────────────────────────────────────────────
        map.put(ExcelChartType.STOCK_HIGH_LOW_CLOSE,              stock);
        map.put(ExcelChartType.STOCK_OPEN_HIGH_LOW_CLOSE,         stock);
        map.put(ExcelChartType.STOCK_VOLUME_HIGH_LOW_CLOSE,       stock);
        map.put(ExcelChartType.STOCK_VOLUME_OPEN_HIGH_LOW_CLOSE,  stock);

        // ── Surface / Contour ─────────────────────────────────────────────────
        map.put(ExcelChartType.SURFACE_3D,               surface);
        map.put(ExcelChartType.SURFACE_WIREFRAME_3D,     surface);
        map.put(ExcelChartType.SURFACE_CONTOUR,          surface);
        map.put(ExcelChartType.SURFACE_CONTOUR_WIREFRAME,surface);

        // ── Cylinder ─────────────────────────────────────────────────────────
        map.put(ExcelChartType.CYLINDER,                cylConePyr);
        map.put(ExcelChartType.CYLINDER_STACKED,        cylConePyr);
        map.put(ExcelChartType.CYLINDER_100_STACKED,    cylConePyr);
        map.put(ExcelChartType.CYLINDER_BAR,            cylConePyr);
        map.put(ExcelChartType.CYLINDER_BAR_STACKED,    cylConePyr);
        map.put(ExcelChartType.CYLINDER_BAR_100_STACKED,cylConePyr);
        map.put(ExcelChartType.CYLINDER_3D,             cylConePyr);

        // ── Cone ─────────────────────────────────────────────────────────────
        map.put(ExcelChartType.CONE,                cylConePyr);
        map.put(ExcelChartType.CONE_STACKED,        cylConePyr);
        map.put(ExcelChartType.CONE_100_STACKED,    cylConePyr);
        map.put(ExcelChartType.CONE_BAR,            cylConePyr);
        map.put(ExcelChartType.CONE_BAR_STACKED,    cylConePyr);
        map.put(ExcelChartType.CONE_BAR_100_STACKED,cylConePyr);
        map.put(ExcelChartType.CONE_3D,             cylConePyr);

        // ── Pyramid ───────────────────────────────────────────────────────────
        map.put(ExcelChartType.PYRAMID,                 cylConePyr);
        map.put(ExcelChartType.PYRAMID_STACKED,         cylConePyr);
        map.put(ExcelChartType.PYRAMID_100_STACKED,     cylConePyr);
        map.put(ExcelChartType.PYRAMID_BAR,             cylConePyr);
        map.put(ExcelChartType.PYRAMID_BAR_STACKED,     cylConePyr);
        map.put(ExcelChartType.PYRAMID_BAR_100_STACKED, cylConePyr);
        map.put(ExcelChartType.PYRAMID_3D,              cylConePyr);

        // ── Excel 2016+ Modern Types ──────────────────────────────────────────
        map.put(ExcelChartType.FUNNEL,      funnel);
        map.put(ExcelChartType.TREEMAP,     treemapSun);
        map.put(ExcelChartType.SUNBURST,    treemapSun);
        map.put(ExcelChartType.HISTOGRAM,   histPareto);
        map.put(ExcelChartType.PARETO,      histPareto);
        map.put(ExcelChartType.BOX_WHISKER, boxWhisker);
        map.put(ExcelChartType.WATERFALL,   waterfall);
        map.put(ExcelChartType.MAP,         map);

        // ── Combo ─────────────────────────────────────────────────────────────
        map.put(ExcelChartType.COMBO, combo);

        return map;
    }
}
