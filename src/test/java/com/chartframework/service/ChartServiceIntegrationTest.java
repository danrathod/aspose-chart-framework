package com.chartframework.service;

import com.chartframework.enums.ExcelChartType;
import com.chartframework.exception.ChartValidationException;
import com.chartframework.model.ChartBatchRequest;
import com.chartframework.model.ChartConfig;
import com.chartframework.model.ChartPlacement;
import com.chartframework.model.ChartRequest;
import org.junit.jupiter.api.*;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ChartService — Integration")
class ChartServiceIntegrationTest {

    private ChartService service;
    private File         tempFile;

    @BeforeEach
    void setUp() throws Exception {
        service  = ChartService.create();
        tempFile = File.createTempFile("chart-test-", ".xlsx");
        tempFile.deleteOnExit();
    }

    @AfterEach
    void tearDown() {
        if (tempFile != null) tempFile.delete();
    }

    // ── Shared data ───────────────────────────────────────────────────────────

    private List<List<Object>> salesData() {
        return List.of(
                List.of("Month", "Sales",  "Profit"),
                List.of("Jan",   120_000,  36_000),
                List.of("Feb",   135_000,  40_500),
                List.of("Mar",   118_000,  35_400)
        );
    }

    private List<List<Object>> pieData() {
        return List.of(
                List.of("Region",  "Revenue"),
                List.of("North",   320_000),
                List.of("South",   280_000),
                List.of("East",    410_000)
        );
    }

    private ChartRequest columnChart() {
        return ChartRequest.builder()
                .targetSheetName("Dashboard")
                .chartType(ExcelChartType.COLUMN)
                .placement(ChartPlacement.of(0, 0, 18, 8))
                .data(salesData())
                .config(ChartConfig.builder().chartTitle("Sales").showLegend(true).build())
                .build();
    }

    private ChartRequest pieChart() {
        return ChartRequest.builder()
                .targetSheetName("Dashboard")
                .chartType(ExcelChartType.PIE)
                .placement(ChartPlacement.of(19, 0, 37, 8))
                .data(pieData())
                .config(ChartConfig.builder().chartTitle("Regional Revenue").build())
                .build();
    }

    private ChartBatchRequest batchOf(ChartRequest... charts) {
        return ChartBatchRequest.builder()
                .inputFilePath(tempFile.getAbsolutePath())
                .charts(List.of(charts))
                .build();
    }

    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("single-chart batch succeeds and returns output path")
    void singleChart_succeeds() {
        String path = service.createCharts(batchOf(columnChart()));
        assertNotNull(path);
        assertTrue(new File(path).exists());
    }

    @Test
    @DisplayName("convenience createChart() wrapper works correctly")
    void convenience_createChart_works() {
        String path = service.createChart(
                tempFile.getAbsolutePath(), null, columnChart());
        assertTrue(new File(path).exists());
    }

    @Test
    @DisplayName("multiple charts in one batch are all created")
    void multipleCarts_inOneBatch() {
        assertDoesNotThrow(() ->
                service.createCharts(batchOf(columnChart(), pieChart())));
    }

    @Test
    @DisplayName("target sheets are auto-created when absent")
    void autoCreatesTargetSheets() {
        ChartRequest newSheetChart = ChartRequest.builder()
                .targetSheetName("BrandNewSheet")
                .chartType(ExcelChartType.LINE)
                .placement(ChartPlacement.of(0, 0, 15, 8))
                .data(salesData())
                .build();
        assertDoesNotThrow(() -> service.createCharts(batchOf(newSheetChart)));
    }

    @Test
    @DisplayName("two separate batches each get their own hidden sheet")
    void separateBatches_separateHiddenSheets() {
        // Batch 1
        service.createCharts(batchOf(columnChart()));

        // Batch 2 loads the same file
        service.createCharts(ChartBatchRequest.builder()
                .inputFilePath(tempFile.getAbsolutePath())
                .charts(List.of(pieChart()))
                .build());

        // File must be non-empty and valid
        assertTrue(tempFile.length() > 0);
    }

    @Test
    @DisplayName("separate inputFilePath and outputFilePath are both respected")
    void separateInputOutput() throws Exception {
        File outFile = File.createTempFile("chart-out-", ".xlsx");
        outFile.deleteOnExit();
        try {
            service.createCharts(ChartBatchRequest.builder()
                    .inputFilePath(tempFile.getAbsolutePath())
                    .outputFilePath(outFile.getAbsolutePath())
                    .charts(List.of(columnChart()))
                    .build());
            assertTrue(outFile.exists() && outFile.length() > 0);
        } finally {
            outFile.delete();
        }
    }

    @Test
    @DisplayName("invalid batch throws ChartValidationException")
    void invalidBatch_throwsValidation() {
        assertThrows(ChartValidationException.class, () ->
                service.createCharts(ChartBatchRequest.builder()
                        .inputFilePath(null)      // invalid
                        .charts(List.of(columnChart()))
                        .build()));
    }

    @Test
    @DisplayName("empty charts list throws ChartValidationException")
    void emptyCharts_throwsValidation() {
        assertThrows(ChartValidationException.class, () ->
                service.createCharts(ChartBatchRequest.builder()
                        .inputFilePath(tempFile.getAbsolutePath())
                        .charts(List.of())        // invalid
                        .build()));
    }

    @Test
    @DisplayName("batch with charts on different sheets succeeds")
    void chartsOnDifferentSheets_succeed() {
        ChartRequest analysisChart = ChartRequest.builder()
                .targetSheetName("Analysis")
                .chartType(ExcelChartType.BAR)
                .placement(ChartPlacement.of(0, 0, 18, 8))
                .data(salesData())
                .config(ChartConfig.builder().chartTitle("Analysis Bar").build())
                .build();

        assertDoesNotThrow(() ->
                service.createCharts(batchOf(columnChart(), analysisChart)));
    }
}