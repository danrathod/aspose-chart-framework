package com.chartframework;

import com.aspose.cells.Workbook;
import com.chartframework.enums.ExcelChartType;
import com.chartframework.model.ChartConfig;
import com.chartframework.model.ChartPlacement;
import com.chartframework.model.ChartRequest;
import com.chartframework.service.ChartService;

import java.util.List;

/**
 * Comprehensive demonstration of the Aspose Chart Generation Framework.
 *
 * <p>Run this class to produce {@code framework-demo-output.xlsx} in the
 * working directory. Open the file in Excel to see all charts.</p>
 *
 * <h2>What this demo produces</h2>
 * <ol>
 *   <li>Dashboard sheet — Clustered Column chart (Monthly Sales)</li>
 *   <li>Dashboard sheet — Line with Markers chart (Trend overlay)</li>
 *   <li>Dashboard sheet — Pie chart (Regional split)</li>
 *   <li>Analysis sheet  — Stacked Bar chart (Product comparison)</li>
 *   <li>Analysis sheet  — Combo / dual-axis chart (Revenue + Growth%)</li>
 *   <li>Analysis sheet  — Radar chart (Performance dimensions)</li>
 *   <li>Analysis sheet  — Scatter chart (Cost vs Revenue)</li>
 *   <li>Stocks sheet    — High-Low-Close stock chart</li>
 * </ol>
 *
 * <p>All chart data is written into hidden sheets with names like
 * {@code __chartdata_1}, {@code __chartdata_2}, etc. — invisible
 * to the end user.</p>
 */
public class ChartFrameworkDemo {

    public static void main(String[] args) throws Exception {

        // ── 1. Prepare workbook & sheets ──────────────────────────────────────
        Workbook wb = new Workbook();
        wb.getWorksheets().get(0).setName("Dashboard");
        wb.getWorksheets().add("Analysis");
        wb.getWorksheets().add("Stocks");

        // ── 2. Create the service (single instance handles all charts) ────────
        ChartService service = ChartService.create();

        // ═════════════════════════════════════════════════════════════════════
        //  DASHBOARD SHEET — three charts side by side
        // ═════════════════════════════════════════════════════════════════════

        // ── Chart 1: Clustered Column — Monthly Sales ─────────────────────────
        service.createChart(ChartRequest.builder()
                .workbook(wb)
                .targetSheetName("Dashboard")
                .chartType(ExcelChartType.COLUMN_CLUSTERED)
                .placement(ChartPlacement.of(0, 0, 18, 9))
                .data(monthlySalesData())
                .config(ChartConfig.builder()
                        .chartTitle("Monthly Sales Performance")
                        .categoryAxisTitle("Month")
                        .valueAxisTitle("Amount (USD)")
                        .showLegend(true)
                        .legendPosition("BOTTOM")
                        .showDataLabels(false)
                        .showMajorGridlines(true)
                        .build())
                .build());
        System.out.println("✔ Chart 1 — Clustered Column created");

        // ── Chart 2: Line with Markers — Trend ───────────────────────────────
        service.createChart(ChartRequest.builder()
                .workbook(wb)
                .targetSheetName("Dashboard")
                .chartType(ExcelChartType.LINE_WITH_DATA_MARKERS)
                .placement(ChartPlacement.of(0, 10, 18, 19))
                .data(monthlySalesData())
                .config(ChartConfig.builder()
                        .chartTitle("Sales Trend")
                        .categoryAxisTitle("Month")
                        .valueAxisTitle("Amount")
                        .showLegend(true)
                        .build())
                .build());
        System.out.println("✔ Chart 2 — Line with Markers created");

        // ── Chart 3: Pie — Regional Revenue ───────────────────────────────────
        service.createChart(ChartRequest.builder()
                .workbook(wb)
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
        System.out.println("✔ Chart 3 — Pie created");

        // ── Chart 4: Doughnut — Market Share ─────────────────────────────────
        service.createChart(ChartRequest.builder()
                .workbook(wb)
                .targetSheetName("Dashboard")
                .chartType(ExcelChartType.DOUGHNUT)
                .placement(ChartPlacement.of(19, 10, 37, 19))
                .data(marketShareData())
                .config(ChartConfig.builder()
                        .chartTitle("Market Share")
                        .showLegend(true)
                        .legendPosition("BOTTOM")
                        .build())
                .build());
        System.out.println("✔ Chart 4 — Doughnut created");

        // ═════════════════════════════════════════════════════════════════════
        //  ANALYSIS SHEET — advanced chart types
        // ═════════════════════════════════════════════════════════════════════

        // ── Chart 5: Stacked Bar — Product Category Comparison ────────────────
        service.createChart(ChartRequest.builder()
                .workbook(wb)
                .targetSheetName("Analysis")
                .chartType(ExcelChartType.BAR_STACKED)
                .placement(ChartPlacement.of(0, 0, 18, 9))
                .data(productCategoryData())
                .config(ChartConfig.builder()
                        .chartTitle("Product Category Comparison")
                        .categoryAxisTitle("Category")
                        .valueAxisTitle("Units Sold")
                        .showLegend(true)
                        .build())
                .build());
        System.out.println("✔ Chart 5 — Stacked Bar created");

        // ── Chart 6: Combo (Column + Line, dual-axis) — Revenue & Growth ──────
        service.createChart(ChartRequest.builder()
                .workbook(wb)
                .targetSheetName("Analysis")
                .chartType(ExcelChartType.COMBO)
                .placement(ChartPlacement.of(0, 10, 18, 19))
                .data(revenueAndGrowthData())
                .config(ChartConfig.builder()
                        .chartTitle("Revenue & Growth Rate")
                        .categoryAxisTitle("Quarter")
                        .valueAxisTitle("Revenue (USD)")
                        .secondaryValueAxisTitle("Growth %")
                        .showLegend(true)
                        .build())
                .build());
        System.out.println("✔ Chart 6 — Combo (dual-axis) created");

        // ── Chart 7: Radar — Team Performance ────────────────────────────────
        service.createChart(ChartRequest.builder()
                .workbook(wb)
                .targetSheetName("Analysis")
                .chartType(ExcelChartType.RADAR_FILLED)
                .placement(ChartPlacement.of(19, 0, 37, 9))
                .data(radarData())
                .config(ChartConfig.builder()
                        .chartTitle("Team Performance Radar")
                        .showLegend(true)
                        .build())
                .build());
        System.out.println("✔ Chart 7 — Radar (Filled) created");

        // ── Chart 8: Scatter — Cost vs Revenue ───────────────────────────────
        service.createChart(ChartRequest.builder()
                .workbook(wb)
                .targetSheetName("Analysis")
                .chartType(ExcelChartType.SCATTER_CONNECTED_CURVES)
                .placement(ChartPlacement.of(19, 10, 37, 19))
                .data(scatterData())
                .config(ChartConfig.builder()
                        .chartTitle("Cost vs Revenue Scatter")
                        .categoryAxisTitle("Cost")
                        .valueAxisTitle("Revenue")
                        .showLegend(true)
                        .build())
                .build());
        System.out.println("✔ Chart 8 — Scatter created");

        // ── Chart 9: Area — Stacked Area ─────────────────────────────────────
        service.createChart(ChartRequest.builder()
                .workbook(wb)
                .targetSheetName("Analysis")
                .chartType(ExcelChartType.AREA_STACKED)
                .placement(ChartPlacement.of(38, 0, 56, 9))
                .data(monthlySalesData())
                .config(ChartConfig.builder()
                        .chartTitle("Stacked Area — Cumulative Sales")
                        .showLegend(true)
                        .build())
                .build());
        System.out.println("✔ Chart 9 — Stacked Area created");

        // ═════════════════════════════════════════════════════════════════════
        //  STOCKS SHEET
        // ═════════════════════════════════════════════════════════════════════

        // ── Chart 10: Stock — High-Low-Close ─────────────────────────────────
        service.createChart(ChartRequest.builder()
                .workbook(wb)
                .targetSheetName("Stocks")
                .chartType(ExcelChartType.STOCK_HIGH_LOW_CLOSE)
                .placement(ChartPlacement.of(0, 0, 20, 12))
                .data(stockData())
                .config(ChartConfig.builder()
                        .chartTitle("ACME Corp — Stock Price (High / Low / Close)")
                        .categoryAxisTitle("Date")
                        .valueAxisTitle("Price (USD)")
                        .showLegend(true)
                        .build())
                .build());
        System.out.println("✔ Chart 10 — Stock (High-Low-Close) created");

        // ── Save ──────────────────────────────────────────────────────────────
        String outputPath = "framework-demo-output.xlsx";
        wb.save(outputPath);
        System.out.println("\n✅ Workbook saved → " + outputPath);
        System.out.println("   Open the file in Excel to inspect all "
                + "10 charts across 3 sheets.");
        System.out.println("   Hidden data sheets are prefixed '__chartdata_'.");
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  Sample data sets
    // ═════════════════════════════════════════════════════════════════════════

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

    static List<List<Object>> marketShareData() {
        return List.of(
                List.of("Brand",     "Share"),
                List.of("Brand A",   35.0),
                List.of("Brand B",   28.0),
                List.of("Brand C",   20.0),
                List.of("Others",    17.0)
        );
    }

    static List<List<Object>> productCategoryData() {
        return List.of(
                List.of("Quarter", "Electronics", "Apparel", "Home & Garden", "Sports"),
                List.of("Q1",      45_000,        30_000,   22_000,           18_000),
                List.of("Q2",      52_000,        34_000,   25_000,           21_000),
                List.of("Q3",      49_000,        38_000,   28_000,           24_000),
                List.of("Q4",      68_000,        42_000,   31_000,           27_000)
        );
    }

    static List<List<Object>> revenueAndGrowthData() {
        return List.of(
                List.of("Quarter", "Revenue",   "Growth%"),
                List.of("Q1 22",   1_200_000,   8.2),
                List.of("Q2 22",   1_350_000,   12.5),
                List.of("Q3 22",   1_180_000,   -12.6),
                List.of("Q4 22",   1_520_000,   28.8),
                List.of("Q1 23",   1_390_000,   -8.6),
                List.of("Q2 23",   1_610_000,   15.8)
        );
    }

    static List<List<Object>> radarData() {
        return List.of(
                List.of("Attribute",     "Team A", "Team B", "Team C"),
                List.of("Speed",          82,       65,       74),
                List.of("Strength",       70,       90,       78),
                List.of("Intelligence",   95,       75,       88),
                List.of("Endurance",      60,       85,       72),
                List.of("Agility",        78,       70,       80),
                List.of("Teamwork",       88,       82,       91)
        );
    }

    /**
     * Scatter data: col 0 = label (skipped), then pairs of (X, Y) per series.
     */
    static List<List<Object>> scatterData() {
        return List.of(
                List.of("Product", "Cost_A", "Revenue_A", "Cost_B", "Revenue_B"),
                List.of("P01",     12.5,      28.3,        18.0,     35.0),
                List.of("P02",     15.0,      32.1,        22.5,     40.2),
                List.of("P03",     10.2,      24.8,        14.0,     29.5),
                List.of("P04",     18.7,      38.4,        26.0,     46.1),
                List.of("P05",     22.0,      42.0,        30.5,     52.3)
        );
    }

    /**
     * Stock data: Date | High | Low | Close
     */
    static List<List<Object>> stockData() {
        return List.of(
                List.of("Date",     "High",  "Low",   "Close"),
                List.of("Jan-01",   152.40,  140.20,  148.30),
                List.of("Jan-02",   155.80,  143.10,  152.60),
                List.of("Jan-03",   158.20,  149.50,  150.10),
                List.of("Jan-04",   153.00,  144.80,  151.90),
                List.of("Jan-05",   160.50,  152.20,  158.70),
                List.of("Jan-08",   163.10,  155.40,  161.20),
                List.of("Jan-09",   165.00,  157.30,  162.80),
                List.of("Jan-10",   162.40,  153.60,  158.90)
        );
    }
}

// NOTE: The static data methods below are additions for newly supported chart types.
// Add these methods to the ChartFrameworkDemo class body shown above.

class AdditionalDemoData {

    /** Funnel chart: sales pipeline stages */
    static java.util.List<java.util.List<Object>> funnelData() {
        return java.util.List.of(
            java.util.List.of("Stage",          "Leads"),
            java.util.List.of("Awareness",      10_000),
            java.util.List.of("Interest",        7_500),
            java.util.List.of("Consideration",   4_200),
            java.util.List.of("Intent",          2_100),
            java.util.List.of("Purchase",          850)
        );
    }

    /** Treemap: product revenue by category */
    static java.util.List<java.util.List<Object>> treemapData() {
        return java.util.List.of(
            java.util.List.of("Category",       "Revenue"),
            java.util.List.of("Electronics",    45_000),
            java.util.List.of("Apparel",        30_000),
            java.util.List.of("Home & Garden",  22_000),
            java.util.List.of("Sports",         18_000),
            java.util.List.of("Books",           9_500),
            java.util.List.of("Toys",           12_000)
        );
    }

    /** Histogram: raw exam scores (Aspose auto-bins) */
    static java.util.List<java.util.List<Object>> histogramData() {
        return java.util.List.of(
            java.util.List.of("Score"),
            java.util.List.of(72), java.util.List.of(85), java.util.List.of(91),
            java.util.List.of(68), java.util.List.of(77), java.util.List.of(83),
            java.util.List.of(55), java.util.List.of(90), java.util.List.of(78),
            java.util.List.of(62), java.util.List.of(88), java.util.List.of(74),
            java.util.List.of(95), java.util.List.of(69), java.util.List.of(81)
        );
    }

    /** Pareto: defect counts (pre-binned) */
    static java.util.List<java.util.List<Object>> paretoData() {
        return java.util.List.of(
            java.util.List.of("Defect Type",  "Count"),
            java.util.List.of("Scratches",    42),
            java.util.List.of("Dents",        28),
            java.util.List.of("Stains",       17),
            java.util.List.of("Cracks",        9),
            java.util.List.of("Other",         4)
        );
    }

    /** Box & Whisker: test scores by class (each column = one group) */
    static java.util.List<java.util.List<Object>> boxWhiskerData() {
        return java.util.List.of(
            java.util.List.of("Class A", "Class B", "Class C"),
            java.util.List.of(72, 85, 68),
            java.util.List.of(78, 91, 74),
            java.util.List.of(65, 79, 82),
            java.util.List.of(88, 63, 77),
            java.util.List.of(74, 88, 69),
            java.util.List.of(91, 72, 85),
            java.util.List.of(69, 94, 71)
        );
    }

    /** Waterfall: P&L bridge */
    static java.util.List<java.util.List<Object>> waterfallData() {
        return java.util.List.of(
            java.util.List.of("Item",                "Amount"),
            java.util.List.of("Opening Revenue",      80_000),
            java.util.List.of("New Customers",        25_000),
            java.util.List.of("Upsells",              12_000),
            java.util.List.of("Churn",               -18_000),
            java.util.List.of("Refunds",              -5_000),
            java.util.List.of("Closing Revenue",      94_000)
        );
    }

    /** Map: regional revenue by country */
    static java.util.List<java.util.List<Object>> mapData() {
        return java.util.List.of(
            java.util.List.of("Country",          "Revenue"),
            java.util.List.of("United States",    350_000),
            java.util.List.of("United Kingdom",   120_000),
            java.util.List.of("Germany",           95_000),
            java.util.List.of("France",            88_000),
            java.util.List.of("Japan",            142_000),
            java.util.List.of("Australia",         67_000),
            java.util.List.of("Canada",            78_000),
            java.util.List.of("India",             55_000)
        );
    }

    /** Cylinder chart: same data as column — just different marker shape */
    static java.util.List<java.util.List<Object>> cylinderData() {
        return java.util.List.of(
            java.util.List.of("Quarter", "North", "South", "East"),
            java.util.List.of("Q1",      45_000,  38_000,  52_000),
            java.util.List.of("Q2",      52_000,  41_000,  58_000),
            java.util.List.of("Q3",      48_000,  39_000,  55_000),
            java.util.List.of("Q4",      61_000,  47_000,  68_000)
        );
    }
}
