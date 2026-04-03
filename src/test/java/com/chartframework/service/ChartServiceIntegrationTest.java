package com.chartframework.service;

import com.aspose.cells.Workbook;
import com.aspose.cells.Worksheet;
import com.chartframework.enums.ExcelChartType;
import com.chartframework.exception.ChartValidationException;
import com.chartframework.model.ChartConfig;
import com.chartframework.model.ChartPlacement;
import com.chartframework.model.ChartRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for {@link ChartService}.
 *
 * These tests exercise the full pipeline end-to-end using real Aspose objects.
 * A valid Aspose licence is required for the charts to be fully rendered;
 * without a licence, Aspose operates in evaluation mode and chart creation
 * still succeeds (but watermarks may appear).
 */
@DisplayName("ChartService — Integration")
class ChartServiceIntegrationTest {

    private ChartService service;
    private Workbook     workbook;

    @BeforeEach
    void setUp() throws Exception {
        service  = ChartService.create();
        workbook = new Workbook();
        // Rename the default sheet for clarity
        workbook.getWorksheets().get(0).setName("Dashboard");
    }

    // ── Shared data ───────────────────────────────────────────────────────────

    private List<List<Object>> monthlySalesData() {
        return List.of(
                List.of("Month",  "Sales",  "Profit", "Units"),
                List.of("Jan",    120_000,  36_000,   450),
                List.of("Feb",    135_000,  40_500,   510),
                List.of("Mar",    118_000,  35_400,   420),
                List.of("Apr",    142_000,  42_600,   560),
                List.of("May",    158_000,  47_400,   620)
        );
    }

    private List<List<Object>> pieData() {
        return List.of(
                List.of("Region",    "Revenue"),
                List.of("North",     320_000),
                List.of("South",     280_000),
                List.of("East",      410_000),
                List.of("West",      195_000)
        );
    }

    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("creates a single clustered column chart without error")
    void singleColumnChart_succeeds() throws Exception {
        ChartRequest req = ChartRequest.builder()
                .workbook(workbook)
                .targetSheetName("Dashboard")
                .chartType(ExcelChartType.COLUMN_CLUSTERED)
                .placement(ChartPlacement.of(2, 0, 20, 8))
                .data(monthlySalesData())
                .config(ChartConfig.builder()
                        .chartTitle("Monthly Sales Performance")
                        .categoryAxisTitle("Month")
                        .valueAxisTitle("Amount (USD)")
                        .showLegend(true)
                        .showDataLabels(false)
                        .build())
                .build();

        assertDoesNotThrow(() -> service.createChart(req));

        // Verify chart was added to the target sheet
        Worksheet dashboard = workbook.getWorksheets().get("Dashboard");
        assertEquals(1, dashboard.getCharts().getCount(), "Dashboard should have 1 chart");
    }

    @Test
    @DisplayName("creates a single pie chart without error")
    void singlePieChart_succeeds() throws Exception {
        ChartRequest req = ChartRequest.builder()
                .workbook(workbook)
                .targetSheetName("Dashboard")
                .chartType(ExcelChartType.PIE)
                .placement(ChartPlacement.of(22, 0, 40, 8))
                .data(pieData())
                .config(ChartConfig.builder()
                        .chartTitle("Regional Revenue Distribution")
                        .showLegend(true)
                        .showDataLabels(true)
                        .build())
                .build();

        assertDoesNotThrow(() -> service.createChart(req));

        Worksheet dashboard = workbook.getWorksheets().get("Dashboard");
        assertEquals(1, dashboard.getCharts().getCount());
    }

    @Test
    @DisplayName("multiple chart calls produce multiple charts in same sheet")
    void multipleCharts_addedToSameSheet() throws Exception {
        // Chart 1 — Column
        service.createChart(ChartRequest.builder()
                .workbook(workbook)
                .targetSheetName("Dashboard")
                .chartType(ExcelChartType.COLUMN_CLUSTERED)
                .placement(ChartPlacement.of(0, 0, 18, 8))
                .data(monthlySalesData())
                .build());

        // Chart 2 — Line
        service.createChart(ChartRequest.builder()
                .workbook(workbook)
                .targetSheetName("Dashboard")
                .chartType(ExcelChartType.LINE_WITH_DATA_MARKERS)
                .placement(ChartPlacement.of(0, 9, 18, 17))
                .data(monthlySalesData())
                .build());

        // Chart 3 — Pie
        service.createChart(ChartRequest.builder()
                .workbook(workbook)
                .targetSheetName("Dashboard")
                .chartType(ExcelChartType.PIE)
                .placement(ChartPlacement.of(19, 0, 37, 8))
                .data(pieData())
                .build());

        Worksheet dashboard = workbook.getWorksheets().get("Dashboard");
        assertEquals(3, dashboard.getCharts().getCount(),
                "All 3 charts should be in the Dashboard sheet");
    }

    @Test
    @DisplayName("multiple charts produce separate hidden sheets (data isolation)")
    void multipleCharts_dataIsolation() throws Exception {
        service.createChart(ChartRequest.builder()
                .workbook(workbook).targetSheetName("Dashboard")
                .chartType(ExcelChartType.COLUMN_CLUSTERED)
                .placement(ChartPlacement.of(0, 0, 18, 8))
                .data(monthlySalesData()).build());

        service.createChart(ChartRequest.builder()
                .workbook(workbook).targetSheetName("Dashboard")
                .chartType(ExcelChartType.PIE)
                .placement(ChartPlacement.of(19, 0, 37, 8))
                .data(pieData()).build());

        // Count hidden sheets — must be at least 2 (one per chart)
        long hiddenCount = 0;
        for (int i = 0; i < workbook.getWorksheets().getCount(); i++) {
            Worksheet ws = workbook.getWorksheets().get(i);
            if (!ws.isVisible()) hiddenCount++;
        }
        assertEquals(2, hiddenCount, "Two charts → two hidden data sheets");
    }

    @Test
    @DisplayName("hidden sheets are invisible to users")
    void hiddenSheets_areNotVisible() throws Exception {
        service.createChart(ChartRequest.builder()
                .workbook(workbook).targetSheetName("Dashboard")
                .chartType(ExcelChartType.COLUMN_CLUSTERED)
                .placement(ChartPlacement.of(0, 0, 18, 8))
                .data(monthlySalesData()).build());

        for (int i = 0; i < workbook.getWorksheets().getCount(); i++) {
            Worksheet ws = workbook.getWorksheets().get(i);
            if (ws.getName().startsWith("__chartdata_")) {
                assertFalse(ws.isVisible(),
                        "Data sheet '" + ws.getName() + "' must be hidden");
            }
        }
    }

    @Test
    @DisplayName("invalid request propagates ChartValidationException")
    void invalidRequest_throwsValidationException() {
        ChartRequest bad = ChartRequest.builder()
                .workbook(null)            // invalid
                .targetSheetName("Sheet")
                .chartType(ExcelChartType.PIE)
                .placement(ChartPlacement.of(0, 0, 10, 5))
                .data(monthlySalesData())
                .build();

        assertThrows(ChartValidationException.class, () -> service.createChart(bad));
    }

    @Test
    @DisplayName("target sheet does not exist → throws ChartFrameworkException")
    void nonExistentTargetSheet_throws() {
        ChartRequest req = ChartRequest.builder()
                .workbook(workbook)
                .targetSheetName("NonExistent")   // sheet not in workbook
                .chartType(ExcelChartType.COLUMN_CLUSTERED)
                .placement(ChartPlacement.of(0, 0, 10, 5))
                .data(monthlySalesData())
                .build();

        assertThrows(RuntimeException.class, () -> service.createChart(req));
    }

    @Test
    @DisplayName("bar chart succeeds with same data layout as column")
    void barChart_succeeds() {
        ChartRequest req = ChartRequest.builder()
                .workbook(workbook)
                .targetSheetName("Dashboard")
                .chartType(ExcelChartType.BAR_CLUSTERED)
                .placement(ChartPlacement.of(0, 0, 18, 8))
                .data(monthlySalesData())
                .config(ChartConfig.builder().chartTitle("Sales by Month (Bar)").build())
                .build();

        assertDoesNotThrow(() -> service.createChart(req));
    }
}
