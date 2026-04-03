package com.chartframework.builder;

import com.aspose.cells.*;
import com.chartframework.exception.ChartFrameworkException;
import com.chartframework.model.ChartPlacement;
import com.chartframework.model.ChartRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Builder Pattern implementation for constructing and positioning an Aspose
 * {@link Chart} object inside a target worksheet.
 *
 * <h2>Responsibilities</h2>
 * <ol>
 *   <li>Locate the target {@link Worksheet} in the workbook.</li>
 *   <li>Add a new chart of the correct type to that sheet's chart collection.</li>
 *   <li>Set the chart's bounding box (position + size) using the coordinates
 *       from {@link ChartPlacement}.</li>
 * </ol>
 *
 * <p>The builder is intentionally separated from the strategy layer so that
 * chart creation (Aspose API calls) and chart configuration (series/axes) are
 * independent concerns — each can be tested in isolation.</p>
 */
public class ChartBuilder {

    private static final Logger log = LoggerFactory.getLogger(ChartBuilder.class);

    /**
     * Creates, positions, and returns an unconfigured Aspose {@link Chart}
     * anchored at the coordinates specified in the request.
     *
     * @param request The chart request containing workbook, sheet name,
     *                chart type, and placement coordinates.
     * @return The newly created (but not yet data-configured) Chart.
     * @throws ChartFrameworkException if the target sheet does not exist or
     *                                  Aspose throws during chart creation.
     */
    public Chart buildChart(ChartRequest request) {
        Workbook  workbook   = request.getWorkbook();
        String    sheetName  = request.getTargetSheetName();
        int       asposeType = request.getChartType().getAsposeChartType();
        ChartPlacement p     = request.getPlacement();

        // ── Locate target sheet ───────────────────────────────────────────────
        Worksheet targetSheet = workbook.getWorksheets().get(sheetName);
        if (targetSheet == null) {
            throw new ChartFrameworkException(
                    "Target worksheet '" + sheetName + "' not found in the workbook. "
                            + "Please create the sheet before calling createChart().");
        }

        // ── Add chart to the sheet ────────────────────────────────────────────
        ChartCollection charts = targetSheet.getCharts();
        int chartIndex;
        try {
            chartIndex = charts.add(asposeType,
                    p.getStartRow(),
                    p.getStartColumn(),
                    p.getEndRow(),
                    p.getEndColumn());
        } catch (Exception e) {
            throw new ChartFrameworkException(
                    "Failed to create chart of type '" + request.getChartType().getDisplayName()
                            + "' on sheet '" + sheetName + "': " + e.getMessage(), e);
        }

        Chart chart = charts.get(chartIndex);

        log.info("Created chart [{}] '{}' on sheet '{}' at [{},{}]->[{},{}]",
                chartIndex,
                request.getChartType().getDisplayName(),
                sheetName,
                p.getStartRow(), p.getStartColumn(),
                p.getEndRow(),   p.getEndColumn());

        return chart;
    }
}
