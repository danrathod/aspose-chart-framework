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
 * eliminating all {@code if/else} or {@code switch} chains.
 * Every {@link ExcelChartType} constant has an entry — zero gaps.</p>
 *
 * <h2>Extension</h2>
 * <p>Override {@link #buildRegistry()} in a subclass, or call
 * {@link #registerStrategy} at runtime to inject custom strategies.</p>
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
     * @throws ChartFrameworkException if no strategy is registered.
     */
    public ChartStrategy getStrategy(ExcelChartType chartType) {
        ChartStrategy strategy = registry.get(chartType);
        if (strategy == null) {
            throw new ChartFrameworkException(
                    "No ChartStrategy registered for chart type: " + chartType
                            + " (" + chartType.getDisplayName() + ").");
        }
        log.debug("Resolved strategy [{}] for chart type [{}]",
                strategy.getClass().getSimpleName(), chartType.getDisplayName());
        return strategy;
    }

    /**
     * Registers or replaces a strategy at runtime.
     */
    public void registerStrategy(ExcelChartType chartType, ChartStrategy strategy) {
        registry.put(chartType, strategy);
        log.info("Registered custom strategy [{}] for chart type [{}]",
                strategy.getClass().getSimpleName(), chartType.getDisplayName());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Registry builder
    // ─────────────────────────────────────────────────────────────────────────

    protected Map<ExcelChartType, ChartStrategy> buildRegistry() {
        Map<ExcelChartType, ChartStrategy> map = new EnumMap<>(ExcelChartType.class);

        // ── Shared stateless strategy instances ───────────────────────────────
        ChartStrategy columnBar  = new ColumnBarChartStrategy();
        ChartStrategy lineArea   = new LineAreaChartStrategy();
        ChartStrategy pie        = new PieDonutChartStrategy();
        ChartStrategy scatter    = new ScatterChartStrategy();
        ChartStrategy bubble     = new BubbleChartStrategy();
        ChartStrategy radar      = new RadarChartStrategy();
        ChartStrategy stock      = new StockChartStrategy();
        ChartStrategy surface    = new SurfaceChartStrategy();
        ChartStrategy cylConePyr = new CylinderConePyramidChartStrategy();
        ChartStrategy funnel     = new FunnelChartStrategy();
        ChartStrategy treemapSun = new TreemapSunburstChartStrategy();
        ChartStrategy histPar    = new HistogramParetoChartStrategy();
        ChartStrategy boxWhisker = new BoxWhiskerChartStrategy();
        ChartStrategy waterfall  = new WaterfallChartStrategy();
        ChartStrategy mapChart   = new MapChartStrategy();
        ChartStrategy combo      = new ComboChartStrategy();

        // ── Column ────────────────────────────────────────────────────────────
        map.put(ExcelChartType.COLUMN,                          columnBar);
        map.put(ExcelChartType.COLUMN_STACKED,                  columnBar);
        map.put(ExcelChartType.COLUMN_100_PERCENT_STACKED,      columnBar);
        map.put(ExcelChartType.COLUMN_3D,                       columnBar);
        map.put(ExcelChartType.COLUMN_3D_CLUSTERED,             columnBar);
        map.put(ExcelChartType.COLUMN_3D_STACKED,               columnBar);
        map.put(ExcelChartType.COLUMN_3D_100_PERCENT_STACKED,   columnBar);

        // ── Bar ───────────────────────────────────────────────────────────────
        map.put(ExcelChartType.BAR,                             columnBar);
        map.put(ExcelChartType.BAR_STACKED,                     columnBar);
        map.put(ExcelChartType.BAR_100_PERCENT_STACKED,         columnBar);
        map.put(ExcelChartType.BAR_3D_CLUSTERED,                columnBar);
        map.put(ExcelChartType.BAR_3D_STACKED,                  columnBar);
        map.put(ExcelChartType.BAR_3D_100_PERCENT_STACKED,      columnBar);

        // ── Line ──────────────────────────────────────────────────────────────
        map.put(ExcelChartType.LINE,                                        lineArea);
        map.put(ExcelChartType.LINE_STACKED,                                lineArea);
        map.put(ExcelChartType.LINE_100_PERCENT_STACKED,                    lineArea);
        map.put(ExcelChartType.LINE_WITH_DATA_MARKERS,                      lineArea);
        map.put(ExcelChartType.LINE_STACKED_WITH_DATA_MARKERS,              lineArea);
        map.put(ExcelChartType.LINE_100_PERCENT_STACKED_WITH_DATA_MARKERS,  lineArea);
        map.put(ExcelChartType.LINE_3D,                                     lineArea);

        // ── Area ──────────────────────────────────────────────────────────────
        map.put(ExcelChartType.AREA,                            lineArea);
        map.put(ExcelChartType.AREA_STACKED,                    lineArea);
        map.put(ExcelChartType.AREA_100_PERCENT_STACKED,        lineArea);
        map.put(ExcelChartType.AREA_3D,                         lineArea);
        map.put(ExcelChartType.AREA_3D_STACKED,                 lineArea);
        map.put(ExcelChartType.AREA_3D_100_PERCENT_STACKED,     lineArea);

        // ── Pie / Doughnut ────────────────────────────────────────────────────
        map.put(ExcelChartType.PIE,             pie);
        map.put(ExcelChartType.PIE_3D,          pie);
        map.put(ExcelChartType.PIE_EXPLODED,    pie);
        map.put(ExcelChartType.PIE_3D_EXPLODED, pie);
        map.put(ExcelChartType.PIE_PIE,         pie);   // Pie of Pie
        map.put(ExcelChartType.PIE_BAR,         pie);   // Bar of Pie
        map.put(ExcelChartType.DOUGHNUT,        pie);
        map.put(ExcelChartType.DOUGHNUT_EXPLODED, pie);

        // ── Scatter ───────────────────────────────────────────────────────────
        map.put(ExcelChartType.SCATTER,                                         scatter);
        map.put(ExcelChartType.SCATTER_CONNECTED_BY_CURVES_WITH_DATA_MARKER,    scatter);
        map.put(ExcelChartType.SCATTER_CONNECTED_BY_CURVES_WITHOUT_DATA_MARKER, scatter);
        map.put(ExcelChartType.SCATTER_CONNECTED_BY_LINES_WITH_DATA_MARKER,     scatter);
        map.put(ExcelChartType.SCATTER_CONNECTED_BY_LINES_WITHOUT_DATA_MARKER,  scatter);

        // ── Bubble ────────────────────────────────────────────────────────────
        map.put(ExcelChartType.BUBBLE,    bubble);
        map.put(ExcelChartType.BUBBLE_3D, bubble);

        // ── Radar ─────────────────────────────────────────────────────────────
        map.put(ExcelChartType.RADAR,                   radar);
        map.put(ExcelChartType.RADAR_WITH_DATA_MARKERS, radar);
        map.put(ExcelChartType.RADAR_FILLED,            radar);

        // ── Stock ─────────────────────────────────────────────────────────────
        map.put(ExcelChartType.STOCK_HIGH_LOW_CLOSE,              stock);
        map.put(ExcelChartType.STOCK_OPEN_HIGH_LOW_CLOSE,         stock);
        map.put(ExcelChartType.STOCK_VOLUME_HIGH_LOW_CLOSE,       stock);
        map.put(ExcelChartType.STOCK_VOLUME_OPEN_HIGH_LOW_CLOSE,  stock);

        // ── Surface ───────────────────────────────────────────────────────────
        map.put(ExcelChartType.SURFACE_3D,               surface);
        map.put(ExcelChartType.SURFACE_WIREFRAME_3D,     surface);
        map.put(ExcelChartType.SURFACE_CONTOUR,          surface);
        map.put(ExcelChartType.SURFACE_WIREFRAME_CONTOUR,surface);

        // ── Cylinder ─────────────────────────────────────────────────────────
        map.put(ExcelChartType.CYLINDER,                    cylConePyr);
        map.put(ExcelChartType.CYLINDER_STACKED,            cylConePyr);
        map.put(ExcelChartType.CYLINDER_100_PERCENT_STACKED,cylConePyr);
        map.put(ExcelChartType.CYLINDRICAL_BAR,             cylConePyr);
        map.put(ExcelChartType.CYLINDRICAL_BAR_STACKED,     cylConePyr);
        map.put(ExcelChartType.CYLINDRICAL_BAR_100_STACKED, cylConePyr);
        map.put(ExcelChartType.CYLINDRICAL_COLUMN_3D,       cylConePyr);

        // ── Cone ─────────────────────────────────────────────────────────────
        map.put(ExcelChartType.CONE,                    cylConePyr);
        map.put(ExcelChartType.CONE_STACKED,            cylConePyr);
        map.put(ExcelChartType.CONE_100_PERCENT_STACKED,cylConePyr);
        map.put(ExcelChartType.CONICAL_BAR,             cylConePyr);
        map.put(ExcelChartType.CONICAL_BAR_STACKED,     cylConePyr);
        map.put(ExcelChartType.CONICAL_BAR_100_STACKED, cylConePyr);
        map.put(ExcelChartType.CONICAL_COLUMN_3D,       cylConePyr);

        // ── Pyramid ───────────────────────────────────────────────────────────
        map.put(ExcelChartType.PYRAMID,                     cylConePyr);
        map.put(ExcelChartType.PYRAMID_STACKED,             cylConePyr);
        map.put(ExcelChartType.PYRAMID_100_PERCENT_STACKED, cylConePyr);
        map.put(ExcelChartType.PYRAMID_BAR,                 cylConePyr);
        map.put(ExcelChartType.PYRAMID_BAR_STACKED,         cylConePyr);
        map.put(ExcelChartType.PYRAMID_BAR_100_STACKED,     cylConePyr);
        map.put(ExcelChartType.PYRAMID_COLUMN_3D,           cylConePyr);

        // ── Excel 2016+ Modern Types ──────────────────────────────────────────
        map.put(ExcelChartType.FUNNEL,      funnel);
        map.put(ExcelChartType.TREEMAP,     treemapSun);
        map.put(ExcelChartType.SUNBURST,    treemapSun);
        map.put(ExcelChartType.HISTOGRAM,   histPar);
        map.put(ExcelChartType.PARETO_LINE, histPar);
        map.put(ExcelChartType.BOX_WHISKER, boxWhisker);
        map.put(ExcelChartType.WATERFALL,   waterfall);
        map.put(ExcelChartType.MAP,         mapChart);

        // ── Combo ─────────────────────────────────────────────────────────────
        map.put(ExcelChartType.CUSTOM_COMBINATION, combo);

        return map;
    }
}
