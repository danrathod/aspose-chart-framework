package com.chartframework.service;

import com.aspose.cells.Workbook;
import com.aspose.cells.Worksheet;
import com.aspose.cells.WorksheetCollection;
import com.chartframework.builder.ChartBuilder;
import com.chartframework.exception.ChartFrameworkException;
import com.chartframework.factory.ChartStrategyFactory;
import com.chartframework.manager.HiddenSheetManager;
import com.chartframework.model.ChartRequest;
import com.chartframework.model.DataRange;
import com.chartframework.strategy.ChartStrategy;
import com.chartframework.validator.ChartRequestValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Primary orchestration layer — the single public API entry point for the
 * chart generation framework.
 *
 * <h2>Consumer-Friendly Design</h2>
 * <p>Consumers pass an Excel <b>file path</b> (not a Workbook). The framework
 * handles all Aspose.Cells interactions internally:</p>
 * <ol>
 *   <li>Loads the workbook from {@link ChartRequest#getInputFilePath()}
 *       (creates a blank workbook if the file does not exist).</li>
 *   <li>Ensures the target sheet exists (creates it if absent).</li>
 *   <li>Writes chart data into a hidden sheet.</li>
 *   <li>Creates and configures the chart.</li>
 *   <li>Saves the workbook to {@link ChartRequest#effectiveOutputPath()}.</li>
 * </ol>
 * <p>Consumers therefore have <b>zero dependency on Aspose.Cells</b> in their
 * own code — they only interact with the framework's public model classes.</p>
 *
 * <h2>Orchestration Flow</h2>
 * <pre>
 *   createChart(request)
 *       │
 *       ├─1─ ChartRequestValidator     → validate all inputs (fail fast)
 *       ├─2─ WorkbookLoader            → load/create Workbook from file path
 *       ├─3─ SheetEnsurer              → create target sheet if absent
 *       ├─4─ HiddenSheetManager        → write data to new hidden sheet
 *       ├─5─ ChartBuilder              → create &amp; position Chart object
 *       ├─6─ ChartStrategyFactory      → resolve ChartStrategy
 *       ├─7─ ChartStrategy             → configure series, axes, legend
 *       └─8─ WorkbookSaver             → save workbook to output path
 * </pre>
 *
 * <h2>Usage Example</h2>
 * <pre>{@code
 * ChartService service = ChartService.create();
 *
 * service.createChart(ChartRequest.builder()
 *     .inputFilePath("reports/dashboard.xlsx")
 *     .targetSheetName("Dashboard")
 *     .chartType(ExcelChartType.COLUMN)
 *     .placement(ChartPlacement.of(2, 0, 20, 8))
 *     .data(salesData)
 *     .config(ChartConfig.builder()
 *         .chartTitle("Q1 Sales")
 *         .showLegend(true)
 *         .build())
 *     .build());
 * // Workbook saved automatically to "reports/dashboard.xlsx"
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
     * default collaborators. Suitable for use outside a DI container.
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
    // Public API
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Creates a chart in the Excel file specified by the request.
     *
     * <p>The method:</p>
     * <ol>
     *   <li>Validates the request.</li>
     *   <li>Loads (or creates) the workbook from {@link ChartRequest#getInputFilePath()}.</li>
     *   <li>Ensures the target sheet exists, creating it if necessary.</li>
     *   <li>Writes chart data into a new hidden sheet.</li>
     *   <li>Creates and fully configures the chart.</li>
     *   <li>Saves the workbook to {@link ChartRequest#effectiveOutputPath()}.</li>
     * </ol>
     *
     * @param request Fully populated {@link ChartRequest}.
     * @return The output file path where the workbook was saved.
     * @throws com.chartframework.exception.ChartValidationException if validation fails.
     * @throws com.chartframework.exception.ChartFrameworkException  for all other errors.
     */
    public String createChart(ChartRequest request) {
        log.info("▶ createChart() — type=[{}], sheet=[{}], input=[{}]",
                request != null ? request.getChartType() : "null",
                request != null ? request.getTargetSheetName() : "null",
                request != null ? request.getInputFilePath() : "null");

        // Step 1 — Validate
        validator.validate(request);

        // Step 2 — Load or create workbook
        Workbook workbook = loadOrCreate(request.getInputFilePath());

        // Step 3 — Ensure target sheet exists
        ensureSheetExists(workbook, request.getTargetSheetName());

        // Step 4 — Write data to hidden sheet
        DataRange dataRange = hiddenSheetManager.writeData(request, workbook);

        // Step 5 — Create & position chart in target sheet
        com.aspose.cells.Chart chart = chartBuilder.buildChart(request, workbook);

        // Step 6 — Resolve strategy
        ChartStrategy strategy = strategyFactory.getStrategy(request.getChartType());

        // Step 7 — Configure series, axes, labels, legend
        strategy.configure(chart, request, dataRange);

        // Recalculate chart formulas (non-fatal if it warns)
        try {
            chart.calculate();
        } catch (Exception e) {
            log.warn("Chart calculation warning (non-fatal): {}", e.getMessage());
        }

        // Step 8 — Save workbook
        String outputPath = request.effectiveOutputPath();
        saveWorkbook(workbook, outputPath);

        log.info("✔ Chart [{}] created and saved to '{}'",
                request.getChartType().getDisplayName(), outputPath);

        return outputPath;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Private helpers
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Loads an existing workbook from the given path, or creates a fresh blank
     * workbook if the file does not exist.
     */
    private Workbook loadOrCreate(String filePath) {
        try {
            File file = new File(filePath);
            if (file.exists() && file.isFile()) {
                log.debug("Loading existing workbook from '{}'", filePath);
                return new Workbook(filePath);
            } else {
                log.debug("File '{}' not found — creating a new blank workbook.", filePath);
                // Ensure parent directories exist
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

    /**
     * Ensures a worksheet with the given name exists in the workbook.
     * Creates it if it is absent.
     */
    private void ensureSheetExists(Workbook workbook, String sheetName) {
        WorksheetCollection sheets = workbook.getWorksheets();
        Worksheet existing = sheets.get(sheetName);
        if (existing == null) {
            int idx = sheets.add(sheetName);
            log.debug("Target sheet '{}' did not exist — created at index {}.", sheetName, idx);
        }
    }

    /**
     * Saves the workbook to the given file path.
     * Creates parent directories if needed.
     */
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
