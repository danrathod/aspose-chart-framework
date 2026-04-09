package com.chartframework.service;

import com.chartframework.config.*;
import com.chartframework.enums.ExcelChartType;
import com.chartframework.exception.ChartValidationException;
import com.chartframework.model.*;
import org.junit.jupiter.api.*;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ChartService — Integration")
class ChartServiceIntegrationTest {

    private ChartService service;
    private File         tempFile;

    @BeforeEach void setUp() throws Exception {
        service  = ChartService.create();
        tempFile = File.createTempFile("chart-test-", ".xlsx");
        tempFile.deleteOnExit();
    }
    @AfterEach void tearDown() { if (tempFile != null) tempFile.delete(); }

    private List<List<Object>> salesData() {
        return List.of(
                List.of("Month", "Sales", "Profit"),
                List.of("Jan",   120_000, 36_000),
                List.of("Feb",   135_000, 40_500)
        );
    }

    private List<List<Object>> pieData() {
        return List.of(
                List.of("Region", "Revenue"),
                List.of("North",  320_000),
                List.of("South",  280_000)
        );
    }

    private ChartRequest columnChart() {
        return ChartRequest.builder()
                .targetSheetName("Dashboard")
                .chartType(ExcelChartType.COLUMN)
                .placement(ChartPlacement.of(0, 0, 18, 8))
                .data(salesData())
                .config(ChartConfig.builder()
                        .title(TitleConfig.of("Sales"))
                        .legend(LegendConfig.at(LegendConfig.Position.BOTTOM))
                        .valueAxis(AxisConfig.builder()
                                .numberFormat("$#,##0")
                                .majorGridlines(GridlineConfig.solid("#E0E0E0", 0.5))
                                .build())
                        .series(SeriesConfig.builder()
                                .style(SeriesStyleConfig.solidColor("#1565C0"))
                                .style(SeriesStyleConfig.solidColor("#2E7D32"))
                                .build())
                        .barColumn(BarColumnConfig.defaults())
                        .build())
                .build();
    }

    private ChartRequest pieChart() {
        return ChartRequest.builder()
                .targetSheetName("Dashboard")
                .chartType(ExcelChartType.PIE)
                .placement(ChartPlacement.of(19, 0, 37, 8))
                .data(pieData())
                .config(ChartConfig.builder()
                        .title(TitleConfig.of("Regional Revenue"))
                        .pie(PieChartConfig.builder()
                                .firstSliceAngle(90)
                                .explodeAllSlices(5)
                                .dataLabels(DataLabelConfig.percentOnly())
                                .build())
                        .build())
                .build();
    }

    private ChartBatchRequest batchOf(ChartRequest... charts) {
        return ChartBatchRequest.builder()
                .inputFilePath(tempFile.getAbsolutePath())
                .charts(List.of(charts))
                .build();
    }

    @Test @DisplayName("column chart with full config succeeds")
    void fullColumnConfig_succeeds() {
        String path = service.createCharts(batchOf(columnChart()));
        assertTrue(new File(path).exists());
    }

    @Test @DisplayName("pie chart with PieChartConfig succeeds")
    void pieWithConfig_succeeds() {
        assertDoesNotThrow(() -> service.createCharts(batchOf(pieChart())));
    }

    @Test @DisplayName("multiple charts in one batch with rich configs succeed")
    void multipleCarts_richConfigs() {
        assertDoesNotThrow(() -> service.createCharts(batchOf(columnChart(), pieChart())));
    }

    @Test @DisplayName("null config uses withDefaults — still succeeds")
    void nullConfig_usesDefaults() {
        ChartRequest req = ChartRequest.builder()
                .targetSheetName("Dashboard")
                .chartType(ExcelChartType.LINE)
                .placement(ChartPlacement.of(0, 0, 15, 8))
                .data(salesData())
                .config(null)
                .build();
        assertDoesNotThrow(() -> service.createCharts(batchOf(req)));
    }

    @Test @DisplayName("scatter chart with SeriesConfig marker succeeds")
    void scatterWithMarker_succeeds() {
        ChartRequest req = ChartRequest.builder()
                .targetSheetName("Dashboard")
                .chartType(ExcelChartType.SCATTER)
                .placement(ChartPlacement.of(0, 0, 18, 8))
                .data(List.of(
                        List.of("Product", "X",    "Y"),
                        List.of("P1",       12.5,  28.3),
                        List.of("P2",       15.0,  32.1)
                ))
                .config(ChartConfig.builder()
                        .title(TitleConfig.of("Scatter Test"))
                        .series(SeriesConfig.builder()
                                .style(SeriesStyleConfig.builder()
                                        .marker(MarkerConfig.circle(8, "#1565C0"))
                                        .build())
                                .build())
                        .scatter(ScatterBubbleConfig.defaults())
                        .build())
                .build();
        assertDoesNotThrow(() -> service.createCharts(batchOf(req)));
    }

    @Test @DisplayName("invalid batch throws validation exception")
    void invalidBatch_throws() {
        assertThrows(ChartValidationException.class, () ->
                service.createCharts(ChartBatchRequest.builder()
                        .inputFilePath(null).charts(List.of(columnChart())).build()));
    }

    @Test @DisplayName("separate outputFilePath is respected")
    void separateOutputPath() throws Exception {
        File out = File.createTempFile("out-", ".xlsx");
        out.deleteOnExit();
        try {
            service.createCharts(ChartBatchRequest.builder()
                    .inputFilePath(tempFile.getAbsolutePath())
                    .outputFilePath(out.getAbsolutePath())
                    .charts(List.of(columnChart()))
                    .build());
            assertTrue(out.length() > 0);
        } finally { out.delete(); }
    }

    @Test @DisplayName("line chart with LineChartConfig smooth succeeds")
    void lineWithSmoothConfig_succeeds() {
        ChartRequest req = ChartRequest.builder()
                .targetSheetName("Dashboard")
                .chartType(ExcelChartType.LINE)
                .placement(ChartPlacement.of(0, 0, 15, 8))
                .data(salesData())
                .config(ChartConfig.builder()
                        .title(TitleConfig.of("Smooth Line"))
                        .line(LineChartConfig.smooth())
                        .build())
                .build();
        assertDoesNotThrow(() -> service.createCharts(batchOf(req)));
    }
}
