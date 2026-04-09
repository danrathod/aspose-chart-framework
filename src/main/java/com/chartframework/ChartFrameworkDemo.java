package com.chartframework;

import com.chartframework.config.*;
import com.chartframework.enums.ExcelChartType;
import com.chartframework.model.*;
import com.chartframework.service.ChartService;

import java.util.List;

/**
 * Demonstrates the new rich ChartConfig system with real-world examples.
 */
public class ChartFrameworkDemo {

    public static void main(String[] args) {
        ChartService service    = ChartService.create();
        String       outputFile = "framework-demo-output.xlsx";

        // ── BATCH 1: Dashboard — column, line, pie ────────────────────────────
        service.createCharts(ChartBatchRequest.builder()
                .inputFilePath(outputFile)
                .charts(List.of(

                        // ── Chart 1: Clustered Column — full config ──────────
                        ChartRequest.builder()
                                .targetSheetName("Dashboard")
                                .chartType(ExcelChartType.COLUMN)
                                .placement(ChartPlacement.of(0, 0, 18, 9))
                                .data(monthlySalesData())
                                .config(ChartConfig.builder()
                                        .title(TitleConfig.bold("Monthly Sales Performance", 14))
                                        .legend(LegendConfig.at(LegendConfig.Position.BOTTOM))
                                        .categoryAxis(AxisConfig.builder()
                                                .title(TitleConfig.of("Month"))
                                                .tickLabelFont(FontConfig.of("Calibri", 9))
                                                .majorGridlines(GridlineConfig.hidden())
                                                .build())
                                        .valueAxis(AxisConfig.builder()
                                                .title(TitleConfig.of("Amount (USD)"))
                                                .numberFormat("$#,##0")
                                                .minValue(0.0)
                                                .majorGridlines(GridlineConfig.solid("#E0E0E0", 0.5))
                                                .build())
                                        .series(SeriesConfig.builder()
                                                .name("Sales").name("Profit").name("Units")
                                                .style(SeriesStyleConfig.solidColor("#1565C0"))
                                                .style(SeriesStyleConfig.solidColor("#2E7D32"))
                                                .style(SeriesStyleConfig.solidColor("#E65100"))
                                                .build())
                                        .barColumn(BarColumnConfig.builder().gapWidth(120).build())
                                        .dataLabel(DataLabelConfig.hidden())
                                        .plotArea(PlotAreaConfig.clean())
                                        .build())
                                .build(),

                        // ── Chart 2: Smooth Line with Markers ───────────────
                        ChartRequest.builder()
                                .targetSheetName("Dashboard")
                                .chartType(ExcelChartType.LINE_WITH_DATA_MARKERS)
                                .placement(ChartPlacement.of(0, 10, 18, 19))
                                .data(monthlySalesData())
                                .config(ChartConfig.builder()
                                        .title(TitleConfig.of("Sales Trend"))
                                        .legend(LegendConfig.at(LegendConfig.Position.TOP))
                                        .line(LineChartConfig.smooth())
                                        .series(SeriesConfig.builder()
                                                .style(SeriesStyleConfig.smoothLine("#1565C0", 2.0))
                                                .style(SeriesStyleConfig.smoothLine("#2E7D32", 2.0))
                                                .style(SeriesStyleConfig.smoothLine("#E65100", 2.0))
                                                .globalDataLabel(DataLabelConfig.hidden())
                                                .build())
                                        .valueAxis(AxisConfig.builder()
                                                .numberFormat("$#,##0")
                                                .majorGridlines(GridlineConfig.solid("#EEEEEE", 0.5))
                                                .build())
                                        .build())
                                .build(),

                        // ── Chart 3: Pie — with explosion and percent labels ─
                        ChartRequest.builder()
                                .targetSheetName("Dashboard")
                                .chartType(ExcelChartType.PIE)
                                .placement(ChartPlacement.of(19, 0, 37, 9))
                                .data(regionalRevenueData())
                                .config(ChartConfig.builder()
                                        .title(TitleConfig.bold("Regional Revenue", 13))
                                        .legend(LegendConfig.builder()
                                                .visible(true)
                                                .position(LegendConfig.Position.RIGHT)
                                                .font(FontConfig.of("Calibri", 9))
                                                .build())
                                        .pie(PieChartConfig.builder()
                                                .firstSliceAngle(90)
                                                .explodeAllSlices(5)
                                                .showLeaderLines(true)
                                                .dataLabels(DataLabelConfig.builder()
                                                        .visible(true)
                                                        .showPercentage(true)
                                                        .showValue(false)
                                                        .position(DataLabelConfig.Position.OUTSIDE_END)
                                                        .font(FontConfig.of("Calibri", 9))
                                                        .build())
                                                .build())
                                        .build())
                                .build()
                ))
                .build());
        System.out.println("✔ Batch 1 complete (Column + Line + Pie)");

        // ── BATCH 2: Analysis — scatter, radar, waterfall ─────────────────────
        service.createCharts(ChartBatchRequest.builder()
                .inputFilePath(outputFile)
                .charts(List.of(

                        // ── Chart 4: Scatter with custom markers ─────────────
                        ChartRequest.builder()
                                .targetSheetName("Analysis")
                                .chartType(ExcelChartType.SCATTER)
                                .placement(ChartPlacement.of(0, 0, 18, 9))
                                .data(scatterData())
                                .config(ChartConfig.builder()
                                        .title(TitleConfig.of("Cost vs Revenue"))
                                        .categoryAxis(AxisConfig.builder()
                                                .title(TitleConfig.of("Cost ($)"))
                                                .numberFormat("$#,##0")
                                                .majorGridlines(GridlineConfig.solid("#EEEEEE", 0.5))
                                                .build())
                                        .valueAxis(AxisConfig.builder()
                                                .title(TitleConfig.of("Revenue ($)"))
                                                .numberFormat("$#,##0")
                                                .majorGridlines(GridlineConfig.solid("#EEEEEE", 0.5))
                                                .build())
                                        .series(SeriesConfig.builder()
                                                .style(SeriesStyleConfig.builder()
                                                        .marker(MarkerConfig.circle(8, "#1565C0"))
                                                        .build())
                                                .build())
                                        .scatter(ScatterBubbleConfig.defaults())
                                        .build())
                                .build(),

                        // ── Chart 5: Filled Radar ────────────────────────────
                        ChartRequest.builder()
                                .targetSheetName("Analysis")
                                .chartType(ExcelChartType.RADAR_FILLED)
                                .placement(ChartPlacement.of(19, 0, 37, 9))
                                .data(radarData())
                                .config(ChartConfig.builder()
                                        .title(TitleConfig.of("Team Performance"))
                                        .legend(LegendConfig.at(LegendConfig.Position.BOTTOM))
                                        .radar(RadarChartConfig.filled())
                                        .series(SeriesConfig.builder()
                                                .style(SeriesStyleConfig.builder()
                                                        .fillColor("#1565C0").fillOpacity(0.35)
                                                        .lineColor("#1565C0").lineWidthPt(1.5)
                                                        .build())
                                                .style(SeriesStyleConfig.builder()
                                                        .fillColor("#E65100").fillOpacity(0.35)
                                                        .lineColor("#E65100").lineWidthPt(1.5)
                                                        .build())
                                                .build())
                                        .build())
                                .build(),

                        // ── Chart 6: Waterfall ───────────────────────────────
                        ChartRequest.builder()
                                .targetSheetName("Analysis")
                                .chartType(ExcelChartType.WATERFALL)
                                .placement(ChartPlacement.of(0, 10, 18, 19))
                                .data(waterfallData())
                                .config(ChartConfig.builder()
                                        .title(TitleConfig.of("P&L Bridge"))
                                        .dataLabel(DataLabelConfig.builder()
                                                .visible(true)
                                                .showValue(true)
                                                .numberFormat("$#,##0")
                                                .font(FontConfig.of("Calibri", 9))
                                                .build())
                                        .categoryAxis(AxisConfig.builder()
                                                .tickLabelRotation(-30)
                                                .build())
                                        .build())
                                .build()
                ))
                .build());
        System.out.println("✔ Batch 2 complete (Scatter + Radar + Waterfall)");
        System.out.println("\n✅ Saved to: " + outputFile);
    }

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
                List.of("Region", "Revenue"),
                List.of("North",  320_000),
                List.of("South",  280_000),
                List.of("East",   410_000),
                List.of("West",   195_000)
        );
    }

    static List<List<Object>> scatterData() {
        return List.of(
                List.of("Product", "Cost",  "Revenue"),
                List.of("P01",     12_500,  28_300),
                List.of("P02",     15_000,  32_100),
                List.of("P03",     10_200,  24_800),
                List.of("P04",     18_700,  38_400),
                List.of("P05",     22_000,  42_000)
        );
    }

    static List<List<Object>> radarData() {
        return List.of(
                List.of("Attribute",   "Team A", "Team B"),
                List.of("Speed",        82,       65),
                List.of("Strength",     70,       90),
                List.of("Intelligence", 95,       75),
                List.of("Endurance",    60,       85),
                List.of("Teamwork",     88,       82)
        );
    }

    static List<List<Object>> waterfallData() {
        return List.of(
                List.of("Item",             "Amount"),
                List.of("Opening",          80_000),
                List.of("New Customers",    25_000),
                List.of("Upsells",          12_000),
                List.of("Churn",           -18_000),
                List.of("Refunds",          -5_000),
                List.of("Closing",          94_000)
        );
    }
}
