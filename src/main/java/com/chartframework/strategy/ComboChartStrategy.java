package com.chartframework.strategy;

import com.aspose.cells.*;
import com.chartframework.model.ChartConfig;
import com.chartframework.model.ChartRequest;
import com.chartframework.model.DataRange;

/**
 * Strategy for Combo / Custom Combination charts (dual-axis charts).
 *
 * <h2>Data Layout</h2>
 * <pre>
 *   Month   Sales     Units     Growth%
 *   Jan     120000    450       12.5
 *   Feb     135000    520       14.2
 *   Mar     118000    410       10.8
 * </pre>
 *
 * <h2>Combo Chart Behaviour</h2>
 * <ul>
 *   <li>All series except the <em>last</em> are plotted as clustered
 *       columns on the <b>primary</b> Y-axis.</li>
 *   <li>The <em>last</em> series is plotted as a line on the
 *       <b>secondary</b> Y-axis — ideal for percentage or ratio metrics
 *       that have a very different scale.</li>
 * </ul>
 *
 * <p>This rule-of-thumb works for the most common combo requirement.
 * For fully custom combos, callers should subclass and override.</p>
 */
public class ComboChartStrategy extends AbstractChartStrategy {

    @Override
    protected void configureSeries(Chart chart, ChartRequest request, DataRange dr) {
        log.debug("Configuring Combo series (last series → secondary axis line)");

        ChartConfig config   = request.effectiveConfig();
        int firstDataRow     = dr.getStartRow() + (config.isFirstRowIsHeader() ? 1 : 0);
        int lastDataRow      = dr.getEndRow();
        int categoryCol      = dr.getStartColumn();
        int firstSeriesCol   = categoryCol + (config.isFirstColumnIsCategory() ? 1 : 0);
        int lastSeriesCol    = dr.getEndColumn();

        SeriesCollection nSeries = chart.getNSeries();
        nSeries.setCategoryData(dr.toColumnRange(categoryCol, firstDataRow, lastDataRow));

        // Add all series
        for (int col = firstSeriesCol; col <= lastSeriesCol; col++) {
            String valuesRange = dr.toColumnRange(col, firstDataRow, lastDataRow);
            int idx = nSeries.add(valuesRange, true);
            Series series = nSeries.get(idx);

            boolean isLastSeries = (col == lastSeriesCol);

            if (isLastSeries) {
                // Plot last series as a line on the secondary axis
                series.setType(ChartType.LINE);
                series.setPlotOnSecondAxis(true);
            } else {
                series.setType(ChartType.COLUMN_CLUSTERED);
                series.setPlotOnSecondAxis(false);
            }
        }

        // Show secondary axis if we have at least 2 series columns
        if (lastSeriesCol > firstSeriesCol) {
            try {
                chart.getSecondValueAxis().setVisible(true);
                if (config.getSecondaryValueAxisTitle() != null) {
                    chart.getSecondValueAxis().getTitle()
                            .setText(config.getSecondaryValueAxisTitle());
                    chart.getSecondValueAxis().getTitle().setVisible(true);
                }
            } catch (Exception e) {
                log.warn("Could not configure secondary axis: {}", e.getMessage());
            }
        }
    }
}
