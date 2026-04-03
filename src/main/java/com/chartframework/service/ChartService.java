package com.chartframework.service;

import com.aspose.cells.Chart;
import com.chartframework.builder.ChartBuilder;
import com.chartframework.factory.ChartStrategyFactory;
import com.chartframework.manager.HiddenSheetManager;
import com.chartframework.model.ChartRequest;
import com.chartframework.model.DataRange;
import com.chartframework.strategy.ChartStrategy;
import com.chartframework.validator.ChartRequestValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Primary orchestration layer — the single public API entry point for the
 * chart generation framework.
 *
 * <h2>Orchestration Flow</h2>
 * <pre>
 *   createChart(request)
 *       │
 *       ├─1─ ChartRequestValidator  → validate all inputs (fail fast)
 *       │
 *       ├─2─ HiddenSheetManager     → write data to a new hidden sheet
 *       │                             returns DataRange (sheet + row/col refs)
 *       │
 *       ├─3─ ChartBuilder           → create &amp; position the Chart object
 *       │                             in the target visible worksheet
 *       │
 *       ├─4─ ChartStrategyFactory   → resolve the correct ChartStrategy
 *       │
 *       └─5─ ChartStrategy          → configure series, axes, labels, legend
 * </pre>
 *
 * <h2>Thread Safety</h2>
 * <p>This service itself is stateless; all mutable state lives in the
 * caller-owned {@link com.aspose.cells.Workbook}. Do not share a single
 * Workbook across threads without external synchronisation.</p>
 *
 * <h2>Usage Example</h2>
 * <pre>{@code
 * ChartService service = ChartService.create();  // or inject via DI
 *
 * service.createChart(ChartRequest.builder()
 *     .workbook(wb)
 *     .targetSheetName("Dashboard")
 *     .chartType(ExcelChartType.COLUMN_CLUSTERED)
 *     .placement(ChartPlacement.of(2, 0, 20, 8))
 *     .data(salesData)
 *     .config(ChartConfig.builder()
 *         .chartTitle("Q1 Sales")
 *         .showLegend(true)
 *         .build())
 *     .build());
 *
 * // Call again for additional charts — no collision
 * service.createChart(anotherRequest);
 *
 * wb.save("output.xlsx");
 * }</pre>
 */
public class ChartService {

    private static final Logger log = LoggerFactory.getLogger(ChartService.class);

    // ── Collaborators (injected, never null) ──────────────────────────────────
    private final ChartRequestValidator  validator;
    private final HiddenSheetManager     hiddenSheetManager;
    private final ChartBuilder           chartBuilder;
    private final ChartStrategyFactory   strategyFactory;

    // ─────────────────────────────────────────────────────────────────────────
    // Construction
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Full constructor for dependency injection (DI frameworks, testing).
     */
    public ChartService(ChartRequestValidator  validator,
                        HiddenSheetManager     hiddenSheetManager,
                        ChartBuilder           chartBuilder,
                        ChartStrategyFactory   strategyFactory) {
        this.validator          = validator;
        this.hiddenSheetManager = hiddenSheetManager;
        this.chartBuilder       = chartBuilder;
        this.strategyFactory    = strategyFactory;
    }

    /**
     * Convenience factory — creates a fully wired {@link ChartService} with
     * default collaborators. Suitable for standalone use outside a DI container.
     */
    public static ChartService create() {
        return new ChartService(
                new ChartRequestValidator(),
                new HiddenSheetManager(),
                new ChartBuilder(),
                new ChartStrategyFactory()
        );
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Public API  ← single entry point
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Creates a chart in the specified workbook according to the given request.
     *
     * <p>This method is safe to call multiple times on the same workbook —
     * each call produces a new hidden sheet with a unique name, ensuring
     * complete data isolation between charts.</p>
     *
     * @param request Fully populated {@link ChartRequest} describing the
     *                chart to create.
     * @throws com.chartframework.exception.ChartValidationException  if the
     *         request fails validation.
     * @throws com.chartframework.exception.ChartFrameworkException   if chart
     *         creation fails for any other reason.
     */
    public void createChart(ChartRequest request) {
        log.info("▶ createChart() — type=[{}], sheet=[{}]",
                request != null ? request.getChartType() : "null",
                request != null ? request.getTargetSheetName() : "null");

        // Step 1 — Validate
        validator.validate(request);

        // Step 2 — Write data to hidden sheet
        DataRange dataRange = hiddenSheetManager.writeData(request);
        log.debug("  Data written → hidden sheet='{}', rows={}, series={}",
                dataRange.getSheetName(),
                dataRange.getDataRowCount(),
                dataRange.getSeriesCount());

        // Step 3 — Create & position the chart in the target sheet
        Chart chart = chartBuilder.buildChart(request);

        // Step 4 — Resolve strategy
        ChartStrategy strategy = strategyFactory.getStrategy(request.getChartType());

        // Step 5 — Configure series, axes, labels, legend
        strategy.configure(chart, request, dataRange);

        // Recalculate chart formulas
        try {
            chart.calculate();
        } catch (Exception e) {
            log.warn("Chart calculation warning (non-fatal): {}", e.getMessage());
        }

        log.info("✔ Chart created: [{}] on sheet '[{}]'",
                request.getChartType().getDisplayName(),
                request.getTargetSheetName());
    }
}
