package com.chartframework.service;

import com.chartframework.enums.ExcelChartType;
import com.chartframework.exception.ChartValidationException;
import com.chartframework.model.ChartConfig;
import com.chartframework.model.ChartPlacement;
import com.chartframework.model.ChartRequest;
import org.junit.jupiter.api.*;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for {@link ChartService}.
 *
 * No Workbook is constructed by the test — the service creates/loads it
 * from the file path internally. A temp file is used for each test.
 */
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

    private List<List<Object>> monthlySalesData() {
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

    private ChartRequest.ChartRequestBuilder baseBuilder() {
        return ChartRequest.builder()
                .inputFilePath(tempFile.getAbsolutePath())
                .targetSheetName("Dashboard");
    }

    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("creates column chart and returns output path")
    void columnChart_createsFileAndReturnsPath() {
        String output = service.createChart(baseBuilder()
                .chartType(ExcelChartType.COLUMN)
                .placement(ChartPlacement.of(2, 0, 20, 8))
                .data(monthlySalesData())
                .config(ChartConfig.builder().chartTitle("Sales").showLegend(true).build())
                .build());

        assertNotNull(output);
        assertTrue(new File(output).exists(), "Output file must exist after createChart()");
    }

    @Test
    @DisplayName("creates pie chart without error")
    void pieChart_succeeds() {
        assertDoesNotThrow(() -> service.createChart(baseBuilder()
                .chartType(ExcelChartType.PIE)
                .placement(ChartPlacement.of(0, 0, 18, 8))
                .data(pieData())
                .build()));
    }

    @Test
    @DisplayName("target sheet is auto-created when it does not exist")
    void autoCreatesTargetSheet() {
        // tempFile is a fresh empty xlsx — 'MyNewSheet' does not exist
        assertDoesNotThrow(() -> service.createChart(
                ChartRequest.builder()
                        .inputFilePath(tempFile.getAbsolutePath())
                        .targetSheetName("MyNewSheet")
                        .chartType(ExcelChartType.LINE)
                        .placement(ChartPlacement.of(0, 0, 15, 8))
                        .data(monthlySalesData())
                        .build()));
    }

    @Test
    @DisplayName("multiple sequential calls update the same file")
    void multipleCharts_sameFile() {
        String path = tempFile.getAbsolutePath();

        service.createChart(baseBuilder()
                .chartType(ExcelChartType.COLUMN)
                .placement(ChartPlacement.of(0, 0, 18, 8))
                .data(monthlySalesData()).build());

        service.createChart(baseBuilder()
                .inputFilePath(path) // reload previously saved file
                .chartType(ExcelChartType.PIE)
                .placement(ChartPlacement.of(19, 0, 37, 8))
                .data(pieData()).build());

        assertTrue(new File(path).length() > 0, "File must not be empty");
    }

    @Test
    @DisplayName("separate inputFilePath and outputFilePath are respected")
    void separateInputOutput() throws Exception {
        File outFile = File.createTempFile("chart-out-", ".xlsx");
        outFile.deleteOnExit();
        try {
            service.createChart(baseBuilder()
                    .outputFilePath(outFile.getAbsolutePath())
                    .chartType(ExcelChartType.COLUMN)
                    .placement(ChartPlacement.of(0, 0, 18, 8))
                    .data(monthlySalesData()).build());

            assertTrue(outFile.exists() && outFile.length() > 0,
                    "Output file must be created at the specified path");
        } finally {
            outFile.delete();
        }
    }

    @Test
    @DisplayName("invalid request throws ChartValidationException")
    void invalidRequest_throwsValidation() {
        assertThrows(ChartValidationException.class, () ->
                service.createChart(ChartRequest.builder()
                        .inputFilePath(null)   // invalid
                        .targetSheetName("Dashboard")
                        .chartType(ExcelChartType.PIE)
                        .placement(ChartPlacement.of(0, 0, 10, 5))
                        .data(monthlySalesData())
                        .build()));
    }

    @Test
    @DisplayName("bar chart succeeds with same data layout as column")
    void barChart_succeeds() {
        assertDoesNotThrow(() -> service.createChart(baseBuilder()
                .chartType(ExcelChartType.BAR)
                .placement(ChartPlacement.of(0, 0, 18, 8))
                .data(monthlySalesData())
                .config(ChartConfig.builder().chartTitle("Bar Chart").build())
                .build()));
    }
}
