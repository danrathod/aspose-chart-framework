package com.chartframework.builder;

import com.aspose.cells.*;
import com.chartframework.exception.ChartFrameworkException;
import com.chartframework.model.ChartPlacement;
import com.chartframework.model.ChartRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Builder Pattern implementation for creating and positioning an Aspose
 * {@link Chart} object inside a target worksheet.
 *
 * <h2>Responsibilities</h2>
 * <ol>
 *   <li>Locate the target {@link Worksheet} in the workbook.</li>
 *   <li>Add a new chart of the correct Aspose type to that sheet.</li>
 *   <li>Anchor the chart using the {@link ChartPlacement} coordinates.</li>
 * </ol>
 *
 * <p>The workbook is passed in directly by {@link com.chartframework.service.ChartService}
 * (which now owns the workbook lifecycle), keeping this builder free of file I/O concerns.</p>
 */
public class ChartBuilder {

    private static final Logger log = LoggerFactory.getLogger(ChartBuilder.class);

    /**
     * Creates, positions, and returns an unconfigured Aspose {@link Chart} anchored
     * at the coordinates specified in the request.
     *
     * @param request  The chart request (chart type, sheet name, placement).
     * @param workbook The live Aspose Workbook managed by ChartService.
     * @return The newly created (but not yet data-configured) Chart.
     * @throws ChartFrameworkException if the target sheet is missing or Aspose throws.
     */
    public Chart buildChart(ChartRequest request, Workbook workbook) {
        String         sheetName  = request.getTargetSheetName();
        int            asposeType = request.getChartType().getAsposeChartType();
        ChartPlacement p          = request.getPlacement();

        Worksheet targetSheet = workbook.getWorksheets().get(sheetName);
        if (targetSheet == null) {
            throw new ChartFrameworkException(
                    "Target worksheet '" + sheetName + "' not found in the workbook. " +
                    "This should not happen as ChartService ensures the sheet exists first.");
        }

        ChartCollection charts = targetSheet.getCharts();
        int chartIndex;
        try {
            chartIndex = charts.add(asposeType,
                    p.getStartRow(), p.getStartColumn(),
                    p.getEndRow(),   p.getEndColumn());
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
