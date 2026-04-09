package com.chartframework.service;

import com.aspose.cells.Chart;
import com.aspose.cells.Workbook;
import com.aspose.cells.Worksheet;
import com.aspose.cells.WorksheetCollection;
import com.chartframework.builder.ChartBuilder;
import com.chartframework.exception.ChartFrameworkException;
import com.chartframework.factory.ChartStrategyFactory;
import com.chartframework.manager.HiddenSheetManager;
import com.chartframework.model.ChartBatchRequest;
import com.chartframework.model.ChartRequest;
import com.chartframework.model.DataRange;
import com.chartframework.strategy.ChartStrategy;
import com.chartframework.validator.ChartRequestValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

/**
 * Primary orchestration layer — the single public API entry point.
 *
 * <h2>Primary API: {@link #createCharts(ChartBatchRequest)}</h2>
 * <p>Accepts a {@link ChartBatchRequest} containing one input file path and
 * <em>multiple</em> {@link ChartRequest}s. The workbook is loaded once,
 * all charts are generated, and the file is saved once.</p>
 *
 * <h2>Convenience API: {@link #createChart(String, String, ChartRequest)}</h2>
 * <p>Thin wrapper around {@link #createCharts} for the single-chart use case.</p>
 *
 * <h2>Hidden Sheet Strategy</h2>
 * <p>All charts in a single batch share the same hidden data sheet.
 * Each chart's data block is preceded by a bold title label row and followed
 * by 2 blank separator rows. A new hidden sheet is created only when the
 * next block would exceed the row threshold
 * (Excel max − {@link HiddenSheetManager#ROW_BUFFER}).</p>
 *
 * <h2>Pipeline per batch</h2>
 * <pre>
 *   createCharts(batchRequest)
 *       │
 *       ├─1─ ChartRequestValidator        → validate batch + all individual charts
 *       ├─2─ WorkbookLoader               → load / create Workbook from file path
 *       ├─3─ SheetEnsurer                 → create target sheets that don't exist
 *       ├─4─ HiddenSheetManager           → write ALL charts' data sequentially
 *       │                                    returns List&lt;DataRange&gt; (one per chart)
 *       ├─5─ For each chart:
 *       │       ChartBuilder              → create &amp; position Chart object
 *       │       ChartStrategyFactory      → resolve ChartStrategy
 *       │       ChartStrategy.configure() → add series, axes, legend, labels
 *       └─6─ WorkbookSaver               → save workbook to effectiveOutputPath
 * </pre>
 *
 * <h2>Usage Example</h2>
 * <pre>{@code
 * ChartService service = ChartService.create();
 *
 * String savedPath = service.createCharts(ChartBatchRequest.builder()
 *     .inputFilePath("reports/dashboard.xlsx")
 *     .charts(List.of(
 *         ChartRequest.builder()
 *             .targetSheetName("Dashboard")
 *             .chartType(ExcelChartType.COLUMN)
 *             .placement(ChartPlacement.of(0, 0, 18, 8))
 *             .data(salesData)
 *             .config(ChartConfig.builder().chartTitle("Monthly Sales").build())
 *             .build(),
 *         ChartRequest.builder()
 *             .targetSheetName("Dashboard")
 *             .chartType(ExcelChartType.PIE)
 *             .placement(ChartPlacement.of(19, 0, 37, 8))
 *             .data(regionData)
 *             .config(ChartConfig.builder().chartTitle("Regional Revenue").build())
 *             .build()
 *     ))
 *     .build());
 * }</pre>
 */
public class ChartService {

    private static final Logger log = LoggerFactory.getLogger(ChartService.class);

    private final ChartRequestValidator validator;
    private final HiddenSheetManager    hiddenSheetManager;
    private final ChartBuilder          chartBuilder;
    private final ChartStrategyFactory  strategyFactory;

    // ─────────────────────────────────────────────────────────────────────────
    // Construction
    // ─────────────────────────────────────────────────────────────────────────

    /** Full constructor for dependency injection. */
    public ChartService(ChartRequestValidator validator,
                        HiddenSheetManager    hiddenSheetManager,
                        ChartBuilder          chartBuilder,
                        ChartStrategyFactory  strategyFactory) {
        this.validator          = validator;
        this.hiddenSheetManager = hiddenSheetManager;
        this.chartBuilder       = chartBuilder;
        this.strategyFactory    = strategyFactory;
    }

    /** Convenience factory — creates a fully wired service with default collaborators. */
    public static ChartService create() {
        return new ChartService(
                new ChartRequestValidator(),
                new HiddenSheetManager(),
                new ChartBuilder(),
                new ChartStrategyFactory()
        );
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Primary public API
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Creates multiple charts in a single Excel file from one batch request.
     *
     * <p>The workbook is loaded once, all charts are generated, and the file
     * is saved once. All chart data is written to a shared hidden sheet
     * (with overflow to additional hidden sheets only when the row threshold
     * is reached).</p>
     *
     * @param batchRequest Batch containing the input file path and all chart requests.
     * @return The path where the workbook was saved.
     * @throws com.chartframework.exception.ChartValidationException  on validation failures.
     * @throws com.chartframework.exception.ChartFrameworkException   on Aspose-level errors.
     */
    public String createCharts(ChartBatchRequest batchRequest) {
        log.info("▶ createCharts() — {} chart(s), input='{}'",
                batchRequest != null && batchRequest.getCharts() != null
                        ? batchRequest.getCharts().size() : 0,
                batchRequest != null ? batchRequest.getInputFilePath() : "null");

        // Step 1 — Validate entire batch
        validator.validate(batchRequest);

        List<ChartRequest> charts = batchRequest.getCharts();

        // Step 2 — Load or create workbook (once for the whole batch)
        Workbook workbook = loadOrCreate(batchRequest.getInputFilePath());

        // Step 3 — Ensure all target sheets exist (create any that are missing)
        charts.forEach(chart -> ensureSheetExists(workbook, chart.getTargetSheetName()));

        // Step 4 — Write ALL charts' data sequentially into shared hidden sheet(s)
        List<DataRange> dataRanges = hiddenSheetManager.writeDataForBatch(batchRequest, workbook);

        // Step 5 — Create and configure each chart using its corresponding DataRange
        for (int i = 0; i < charts.size(); i++) {
            ChartRequest chart     = charts.get(i);
            DataRange    dataRange = dataRanges.get(i);

            log.debug("Configuring chart[{}]: type={}, sheet='{}'",
                    i, chart.getChartType().getDisplayName(), chart.getTargetSheetName());

            // Create & position the Chart object in the target visible sheet
            Chart asposeChart = chartBuilder.buildChart(chart, workbook);

            // Resolve strategy and configure series / axes / legend / labels
            ChartStrategy strategy = strategyFactory.getStrategy(chart.getChartType());
            strategy.configure(asposeChart, chart, dataRange);

            // Trigger formula recalculation (non-fatal if it warns)
            try {
                asposeChart.calculate();
            } catch (Exception e) {
                log.warn("Chart[{}] calculation warning (non-fatal): {}", i, e.getMessage());
            }
        }

        // Step 6 — Save workbook once, after all charts are created
        String outputPath = batchRequest.effectiveOutputPath();
        saveWorkbook(workbook, outputPath);

        log.info("✔ {} chart(s) created and saved to '{}'", charts.size(), outputPath);
        return outputPath;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Convenience API (single chart)
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Convenience method for creating a single chart.
     * Delegates to {@link #createCharts(ChartBatchRequest)}.
     *
     * @param inputFilePath  Path to the input Excel file.
     * @param outputFilePath Save path (null = in-place update of inputFilePath).
     * @param chart          The single chart request.
     * @return The path where the workbook was saved.
     */
    public String createChart(String inputFilePath,
                              String outputFilePath,
                              ChartRequest chart) {
        return createCharts(ChartBatchRequest.singleChart(inputFilePath, outputFilePath, chart));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Private helpers
    // ─────────────────────────────────────────────────────────────────────────

    private Workbook loadOrCreate(String filePath) {
        try {
            File file = new File(filePath);
            if (file.exists() && file.isFile()) {
                log.debug("Loading existing workbook from '{}'", filePath);
                return new Workbook(filePath);
            } else {
                log.debug("'{}' not found — creating a new blank workbook.", filePath);
                File parent = file.getParentFile();
                if (parent != null && !parent.exists()) {
                    parent.mkdirs();
                }
                return new Workbook();
            }
        } catch (Exception e) {
            throw new ChartFrameworkException(
                    "Failed to load workbook from '" + filePath + "': " + e.getMessage(), e);
        }
    }

    private void ensureSheetExists(Workbook workbook, String sheetName) {
        WorksheetCollection sheets = workbook.getWorksheets();
        if (sheets.get(sheetName) == null) {
            int idx = sheets.add(sheetName);
            log.debug("Target sheet '{}' did not exist — created at index {}.", sheetName, idx);
        }
    }

    private void saveWorkbook(Workbook workbook, String outputPath) {
        try {
            File outFile = new File(outputPath);
            File parent  = outFile.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }
            workbook.save(outputPath);
            log.debug("Workbook saved to '{}'", outputPath);
        } catch (Exception e) {
            throw new ChartFrameworkException(
                    "Failed to save workbook to '" + outputPath + "': " + e.getMessage(), e);
        }
    }
}
