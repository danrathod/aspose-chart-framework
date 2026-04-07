package com.chartframework;

import com.chartframework.enums.ExcelChartType;
import com.chartframework.model.ChartConfig;
import com.chartframework.model.ChartPlacement;
import com.chartframework.model.ChartRequest;
import com.chartframework.service.ChartService;

import java.util.List;

/**
 * Comprehensive demonstration of the Aspose Chart Generation Framework.
 *
 * <p>Consumers do NOT need Aspose.Cells on their classpath.
 * They only use: ChartService, ChartRequest, ChartConfig,
 * ChartPlacement, and ExcelChartType — all Aspose details are hidden.</p>
 *
 * <p>Run this class to produce {@code framework-demo-output.xlsx}.</p>
 */
public class ChartFrameworkDemo {

    public static void main(String[] args) {

        // ── Single service instance handles all chart requests ────────────────
        ChartService service = ChartService.create();

        String inputFile  = "framework-demo-output.xlsx"; // created fresh if absent
        String outputFile = "framework-demo-output.xlsx"; // same file, in-place

        // ── Chart 1: Clustered Column ─────────────────────────────────────────
        service.createChart(ChartRequest.builder()
                .inputFilePath(inputFile)
                .outputFilePath(outputFile)
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
                .build());
        System.out.println("✔ Chart 1 — Clustered Column");

        // ── Chart 2: Line with Markers ────────────────────────────────────────
        service.createChart(ChartRequest.builder()
                .inputFilePath(inputFile)
                .outputFilePath(outputFile)
                .targetSheetName("Dashboard")
                .chartType(ExcelChartType.LINE_WITH_DATA_MARKERS)
                .placement(ChartPlacement.of(0, 10, 18, 19))
                .data(monthlySalesData())
                .config(ChartConfig.builder()
                        .chartTitle("Sales Trend")
                        .showLegend(true)
                        .build())
                .build());
        System.out.println("✔ Chart 2 — Line with Markers");

        // ── Chart 3: Pie ──────────────────────────────────────────────────────
        service.createChart(ChartRequest.builder()
                .inputFilePath(inputFile)
                .outputFilePath(outputFile)
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
                .build());
        System.out.println("✔ Chart 3 — Pie");

        // ── Chart 4: Stacked Bar (Analysis sheet) ─────────────────────────────
        service.createChart(ChartRequest.builder()
                .inputFilePath(inputFile)
                .outputFilePath(outputFile)
                .targetSheetName("Analysis")   // sheet auto-created if absent
                .chartType(ExcelChartType.BAR_STACKED)
                .placement(ChartPlacement.of(0, 0, 18, 9))
                .data(productCategoryData())
                .config(ChartConfig.builder()
                        .chartTitle("Product Category Comparison")
                        .showLegend(true)
                        .build())
                .build());
        System.out.println("✔ Chart 4 — Stacked Bar (Analysis sheet)");

        // ── Chart 5: Radar ────────────────────────────────────────────────────
        service.createChart(ChartRequest.builder()
                .inputFilePath(inputFile)
                .outputFilePath(outputFile)
                .targetSheetName("Analysis")
                .chartType(ExcelChartType.RADAR_FILLED)
                .placement(ChartPlacement.of(19, 0, 37, 9))
                .data(radarData())
                .config(ChartConfig.builder()
                        .chartTitle("Team Performance Radar")
                        .showLegend(true)
                        .build())
                .build());
        System.out.println("✔ Chart 5 — Filled Radar");

        // ── Chart 6: Waterfall ────────────────────────────────────────────────
        service.createChart(ChartRequest.builder()
                .inputFilePath(inputFile)
                .outputFilePath(outputFile)
                .targetSheetName("Analysis")
                .chartType(ExcelChartType.WATERFALL)
                .placement(ChartPlacement.of(0, 10, 18, 19))
                .data(waterfallData())
                .config(ChartConfig.builder()
                        .chartTitle("P&L Waterfall")
                        .showDataLabels(true)
                        .build())
                .build());
        System.out.println("✔ Chart 6 — Waterfall");

        // ── Chart 7: Stock (High-Low-Close) ───────────────────────────────────
        service.createChart(ChartRequest.builder()
                .inputFilePath(inputFile)
                .outputFilePath(outputFile)
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
                .build());
        System.out.println("✔ Chart 7 — Stock (High-Low-Close)");

        System.out.println("\n✅ All charts created — workbook saved to: " + outputFile);
        System.out.println("   Hidden data sheets are prefixed '__chartdata_'.");
        System.out.println("   No Aspose.Cells import required in this class!");
    }

    // ── Sample datasets ───────────────────────────────────────────────────────

    static List<List<Object>> monthlySalesData() {
        return List.of(
                List.of("Month", "Sales",    "Profit",  "Units"),
                List.of("Jan",   120_000,    36_000,    450),
                List.of("Feb",   135_000,    40_500,    510),
                List.of("Mar",   118_000,    35_400,    420),
                List.of("Apr",   142_000,    42_600,    560),
                List.of("May",   158_000,    47_400,    620),
                List.of("Jun",   163_000,    48_900,    640)
        );
    }

    static List<List<Object>> regionalRevenueData() {
        return List.of(
                List.of("Region",  "Revenue"),
                List.of("North",   320_000),
                List.of("South",   280_000),
                List.of("East",    410_000),
                List.of("West",    195_000),
                List.of("Central", 230_000)
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

    static List<List<Object>> radarData() {
        return List.of(
                List.of("Attribute",     "Team A", "Team B"),
                List.of("Speed",          82,       65),
                List.of("Strength",       70,       90),
                List.of("Intelligence",   95,       75),
                List.of("Endurance",      60,       85),
                List.of("Teamwork",       88,       82)
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

    static List<List<Object>> stockData() {
        return List.of(
                List.of("Date",    "High",  "Low",   "Close"),
                List.of("Jan-01",  152.40,  140.20,  148.30),
                List.of("Jan-02",  155.80,  143.10,  152.60),
                List.of("Jan-03",  158.20,  149.50,  150.10),
                List.of("Jan-04",  153.00,  144.80,  151.90),
                List.of("Jan-05",  160.50,  152.20,  158.70),
                List.of("Jan-08",  163.10,  155.40,  161.20)
        );
    }
}
