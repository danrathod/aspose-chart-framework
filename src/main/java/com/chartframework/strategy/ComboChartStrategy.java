package com.chartframework.strategy;

import com.aspose.cells.*;
import com.chartframework.model.ChartConfig;
import com.chartframework.model.ChartRequest;
import com.chartframework.model.DataRange;

/**
 * Strategy for Combo / Dual-axis charts (Column + Line).
 *
 * <h2>How Aspose.Cells Combo Charts Work</h2>
 * <p>Aspose.Cells for Java has <b>no dedicated {@code ChartType} constant</b>
 * for combo charts. The correct approach is:</p>
 * <ol>
 *   <li>Create the chart with a base type (e.g. {@code ChartType.COLUMN}).</li>
 *   <li>Add all series normally.</li>
 *   <li>Change the type of specific series via {@code series.setType(ChartType.LINE)}.</li>
 *   <li>Optionally plot the changed series on the secondary axis via
 *       {@code series.setPlotOnSecondAxis(true)}.</li>
 * </ol>
 *
 * <p>This strategy is therefore registered under {@link com.chartframework.enums.ExcelChartType#COLUMN}
 * when the caller passes that type — see {@link com.chartframework.service.ChartService}
 * usage note. To explicitly request a combo, callers should use
 * {@link com.chartframework.enums.ExcelChartType#COLUMN} as the chart type and set
 * {@link com.chartframework.model.ChartConfig#getSecondaryValueAxisTitle()} to
 * signal that the last series should become a secondary-axis line.</p>
 *
 * <h2>Data Layout</h2>
 * <pre>
 *   Month   Sales     Units     Growth%
 *   Jan     120000    450       12.5
 *   Feb     135000    520       14.2
 * </pre>
 * <p>All series except the <em>last</em> are rendered as clustered columns on
 * the primary Y-axis. The last series is rendered as a line on the secondary
 * Y-axis — ideal for percentage or ratio metrics with a different scale.</p>
 */
public class ComboChartStrategy extends AbstractChartStrategy {

    @Override
    protected void configureSeries(Chart chart, ChartRequest request, DataRange dr) {
        log.debug("Configuring Combo (Column + Line, dual-axis) series");

        ChartConfig config   = request.effectiveConfig();
        int firstDataRow     = dr.getStartRow() + (config.isFirstRowIsHeader() ? 1 : 0);
        int lastDataRow      = dr.getEndRow();
        int categoryCol      = dr.getStartColumn();
        int firstSeriesCol   = categoryCol + (config.isFirstColumnIsCategory() ? 1 : 0);
        int lastSeriesCol    = dr.getEndColumn();

        SeriesCollection nSeries = chart.getNSeries();
        nSeries.setCategoryData(dr.toColumnRange(categoryCol, firstDataRow, lastDataRow));

        for (int col = firstSeriesCol; col <= lastSeriesCol; col++) {
            String valuesRange  = dr.toColumnRange(col, firstDataRow, lastDataRow);
            int    seriesIndex  = nSeries.add(valuesRange, true);
            Series series       = nSeries.get(seriesIndex);
            boolean isLastSeries = (col == lastSeriesCol);

            if (isLastSeries && config.getSecondaryValueAxisTitle() != null) {
                // Render last series as a line on the secondary axis
                series.setType(ChartType.LINE);
                series.setPlotOnSecondAxis(true);
            } else {
                series.setType(ChartType.COLUMN);
                series.setPlotOnSecondAxis(false);
            }
        }

        // Activate secondary value axis when it has been used
        if (lastSeriesCol > firstSeriesCol && config.getSecondaryValueAxisTitle() != null) {
            try {
                chart.getSecondValueAxis().setVisible(true);
                chart.getSecondValueAxis().getTitle()
                        .setText(config.getSecondaryValueAxisTitle());
                chart.getSecondValueAxis().getTitle().setVisible(true);
            } catch (Exception e) {
                log.warn("Could not configure secondary axis: {}", e.getMessage());
            }
        }
    }
}
