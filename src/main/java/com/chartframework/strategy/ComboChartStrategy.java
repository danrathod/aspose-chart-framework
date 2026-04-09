package com.chartframework.strategy;

import com.aspose.cells.*;
import com.chartframework.config.AxisConfig;
import com.chartframework.model.ChartConfig;
import com.chartframework.model.ChartRequest;
import com.chartframework.model.DataRange;

/**
 * Strategy for Combo / Dual-axis charts (Column + Line).
 *
 * <p>Detects combo intent when {@link ChartConfig#getSecondaryValueAxis()} is set.
 * The last series is rendered as a line on the secondary axis; all others as columns.</p>
 */
public class ComboChartStrategy extends AbstractChartStrategy {

    @Override
    protected void configureSeries(Chart chart, ChartRequest request, DataRange dr) {
        log.debug("Configuring Combo (Column + Line, dual-axis) series");

        ChartConfig config       = request.effectiveConfig();
        boolean     hasSecondary = config.getSecondaryValueAxis() != null;

        int firstDataRow   = dr.getStartRow() + (config.isFirstRowIsHeader() ? 1 : 0);
        int lastDataRow    = dr.getEndRow();
        int categoryCol    = dr.getStartColumn();
        int firstSeriesCol = categoryCol + (config.isFirstColumnIsCategory() ? 1 : 0);
        int lastSeriesCol  = dr.getEndColumn();

        SeriesCollection nSeries = chart.getNSeries();
        nSeries.setCategoryData(dr.toColumnRange(categoryCol, firstDataRow, lastDataRow));

        for (int col = firstSeriesCol; col <= lastSeriesCol; col++) {
            int    seriesIdx   = col - firstSeriesCol;
            String valuesRange = dr.toColumnRange(col, firstDataRow, lastDataRow);
            int    addedIdx    = nSeries.add(valuesRange, true);
            Series series      = nSeries.get(addedIdx);

            series.setName(resolveSeriesName(config.getSeries(), seriesIdx,
                    request.getData(), dr.getStartRow(), col));
            applySeriesStyle(series, config.getSeries(), seriesIdx);
            applySeriesDataLabels(series, config, seriesIdx);

            boolean isLastSeries = (col == lastSeriesCol);
            if (isLastSeries && hasSecondary) {
                series.setType(ChartType.LINE);
                series.setPlotOnSecondAxis(true);
            } else {
                series.setType(ChartType.COLUMN);
                series.setPlotOnSecondAxis(false);
            }
        }

        // Activate secondary axis (AxisConfig application handled by base configure())
        if (hasSecondary && lastSeriesCol > firstSeriesCol) {
            try {
                chart.getSecondValueAxis().setVisible(1);
            } catch (Exception e) {
                log.warn("Could not activate secondary axis: {}", e.getMessage());
            }
        }
    }
}
