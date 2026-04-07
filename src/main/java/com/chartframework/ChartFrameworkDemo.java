package com.chartframework;

import com.chartframework.enums.ExcelChartType;
import com.chartframework.model.ChartBatchRequest;
import com.chartframework.model.ChartConfig;
import com.chartframework.model.ChartPlacement;
import com.chartframework.model.ChartRequest;
import com.chartframework.service.ChartService;

import java.util.List;

/**
 * Demonstrates the Aspose Chart Generation Framework's multi-chart batch API.
 *
 * <p>Two separate batch requests are shown:</p>
 * <ul>
 *   <li><b>Batch 1</b> — 4 charts on the "Dashboard" &amp; "Analysis" sheets.
 *       All 4 charts' data is written to a single hidden sheet
 *       (e.g. {@code __chartdata_1}).</li>
 *   <li><b>Batch 2</b> — 2 charts on the "Stocks" sheet.
 *       Data goes to a separate hidden sheet (e.g. {@code __chartdata_2}).</li>
 * </ul>
 *
 * <p><b>No Aspose.Cells import is needed here.</b>
 * Consumers only depend on the framework's own model classes.</p>
 */
public class ChartFrameworkDemo {

    public static void main(String[] args) {

        ChartService service    = ChartService.create();
        String       outputFile = "framework-demo-output.xlsx";

        // ═══════════════════════════════════════════════════════════════════
        // BATCH 1 — Dashboard + Analysis charts (4 charts, 1 hidden sheet)
        // ═══════════════════════════════════════════════════════════════════
        String savedPath = service.createCharts(ChartBatchRequest.builder()
                .inputFilePath(outputFile)
                .outputFilePath(outputFile)
                .charts(List.of(

                        // Chart 1: Clustered Column — Dashboard
                        ChartRequest.builder()
                                .targetSheetName("Dashboard")
                                .chartType(ExcelChartType.COLUMN)
                                .placement(ChartPlacement.of(0, 0, 18, 9))
                                .data(monthlySalesData())
                                .config(ChartConfig.builder()
                                        .chartTitle("Monthly Sales Performance")
                                        .categoryAxisTitle("Month")
                                        .valueAxisTitle("Amount (USD)")
                                        .showLegend(true)
                                        .build())
                                .build(),

                        // Chart 2: Line with Markers — Dashboard
                        ChartRequest.builder()
                                .targetSheetName("Dashboard")
                                .chartType(ExcelChartType.LINE_WITH_DATA_MARKERS)
                                .placement(ChartPlacement.of(0, 10, 18, 19))
                                .data(monthlySalesData())
                                .config(ChartConfig.builder()
                                        .chartTitle("Sales Trend")
                                        .showLegend(true)
                                        .build())
                                .build(),

                        // Chart 3: Pie — Dashboard
                        ChartRequest.builder()
                                .targetSheetName("Dashboard")
                                .chartType(ExcelChartType.PIE)
                                .placement(ChartPlacement.of(19, 0, 37, 9))
                                .data(regionalRevenueData())
                                .config(ChartConfig.builder()
                                        .chartTitle("Regional Revenue Distribution")
                                        .showLegend(true)
                                        .legendPosition("RIGHT")
                                        .showDataLabels(true)
                                        .build())
                                .build(),

                        // Chart 4: Stacked Bar — Analysis sheet (auto-created)
                        ChartRequest.builder()
                                .targetSheetName("Analysis")
                                .chartType(ExcelChartType.BAR_STACKED)
                                .placement(ChartPlacement.of(0, 0, 18, 9))
                                .data(productCategoryData())
                                .config(ChartConfig.builder()
                                        .chartTitle("Product Category Comparison")
                                        .showLegend(true)
                                        .build())
                                .build()
                ))
                .build());

        System.out.println("✔ Batch 1 complete (4 charts) → " + savedPath);
        System.out.println("  All 4 charts' data written to a single hidden sheet.");

        // ═══════════════════════════════════════════════════════════════════
        // BATCH 2 — Stocks charts (separate batch → separate hidden sheet)
        // ═══════════════════════════════════════════════════════════════════
        service.createCharts(ChartBatchRequest.builder()
                .inputFilePath(outputFile)
                .outputFilePath(outputFile)
                .charts(List.of(

                        // Chart 5: Stock HLC
                        ChartRequest.builder()
                                .targetSheetName("Stocks")
                                .chartType(ExcelChartType.STOCK_HIGH_LOW_CLOSE)
                                .placement(ChartPlacement.of(0, 0, 20, 12))
                                .data(stockData())
                                .config(ChartConfig.builder()
                                        .chartTitle("ACME Corp — High / Low / Close")
                                        .categoryAxisTitle("Date")
                                        .valueAxisTitle("Price (USD)")
                                        .showLegend(true)
                                        .build())
                                .build(),

                        // Chart 6: Waterfall
                        ChartRequest.builder()
                                .targetSheetName("Stocks")
                                .chartType(ExcelChartType.WATERFALL)
                                .placement(ChartPlacement.of(21, 0, 40, 12))
                                .data(waterfallData())
                                .config(ChartConfig.builder()
                                        .chartTitle("P&L Bridge")
                                        .showDataLabels(true)
                                        .build())
                                .build()
                ))
                .build());

        System.out.println("✔ Batch 2 complete (2 charts) → " + outputFile);
        System.out.println("  Batch 2 data is on a separate hidden sheet from Batch 1.");
        System.out.println("\n✅ Done. Open '" + outputFile + "' to inspect the result.");
        System.out.println("   Hidden sheets are prefixed '__chartdata_' and marked invisible.");
    }

    // ── Sample datasets ───────────────────────────────────────────────────────

    static List<List<Object>> monthlySalesData() {
        return List.of(
                List.of("Month", "Sales",    "Profit",  "Units"),
                List.of("Jan",   120_000,    36_000,    450),
                List.of("Feb",   135_000,    40_500,    510),
                List.of("Mar",   118_000,    35_400,    420),
                List.of("Apr",   142_000,    42_600,    560),
                List.of("May",   158_000,    47_400,    620)
        );
    }

    static List<List<Object>> regionalRevenueData() {
        return List.of(
                List.of("Region",  "Revenue"),
                List.of("North",   320_000),
                List.of("South",   280_000),
                List.of("East",    410_000),
                List.of("West",    195_000)
        );
    }

    static List<List<Object>> productCategoryData() {
        return List.of(
                List.of("Quarter", "Electronics", "Apparel", "Home", "Sports"),
                List.of("Q1",      45_000,        30_000,   22_000, 18_000),
                List.of("Q2",      52_000,        34_000,   25_000, 21_000),
                List.of("Q3",      49_000,        38_000,   28_000, 24_000),
                List.of("Q4",      68_000,        42_000,   31_000, 27_000)
        );
    }

    static List<List<Object>> stockData() {
        return List.of(
                List.of("Date",    "High",  "Low",   "Close"),
                List.of("Jan-01",  152.40,  140.20,  148.30),
                List.of("Jan-02",  155.80,  143.10,  152.60),
                List.of("Jan-03",  158.20,  149.50,  150.10),
                List.of("Jan-04",  153.00,  144.80,  151.90),
                List.of("Jan-05",  160.50,  152.20,  158.70)
        );
    }

    static List<List<Object>> waterfallData() {
        return List.of(
                List.of("Item",               "Amount"),
                List.of("Opening Revenue",     80_000),
                List.of("New Customers",       25_000),
                List.of("Upsells",             12_000),
                List.of("Churn",              -18_000),
                List.of("Refunds",             -5_000),
                List.of("Closing Revenue",     94_000)
        );
    }
}