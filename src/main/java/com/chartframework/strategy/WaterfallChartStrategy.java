package com.chartframework.strategy;

import com.aspose.cells.Chart;
import com.aspose.cells.DataLabels;
import com.aspose.cells.Series;
import com.aspose.cells.SeriesCollection;
import com.chartframework.model.ChartConfig;
import com.chartframework.model.ChartRequest;
import com.chartframework.model.DataRange;

/**
 * Strategy for Waterfall charts (Excel 2016+).
 *
 * <p>A waterfall chart shows a running total as values are added or subtracted —
 * ideal for illustrating how an initial value (e.g. opening balance) is affected
 * by a series of positive and negative incremental values to reach a final result.</p>
 *
 * <h2>Data Layout</h2>
 * <pre>
 *   Category          Amount
 *   Opening Balance   50000
 *   Revenue           30000
 *   Operating Costs  -15000
 *   Tax               -8000
 *   Other Income       5000
 *   Net Profit        62000   ← subtotal / total bar
 * </pre>
 *
 * <p>The category column labels each bar; the value column drives the running
 * total. Positive values add upward bars; negative values add downward bars.</p>
 *
 * <p><b>Tip:</b> To mark a bar as a subtotal/total (solid bar touching the axis),
 * after chart creation access {@code chart.getNSeries().get(0).getLayoutProperties()}
 * and call {@code setSubtotal(pointIndex, true)} for the relevant data points.
 * This advanced configuration is left to the caller post-creation.</p>
 */
public class WaterfallChartStrategy extends AbstractChartStrategy {

    @Override
    protected void configureSeries(Chart chart, ChartRequest request, DataRange dr) {
        log.debug("Configuring Waterfall series");

        ChartConfig config   = request.effectiveConfig();
        int firstDataRow     = dr.getStartRow() + (config.isFirstRowIsHeader() ? 1 : 0);
        int lastDataRow      = dr.getEndRow();
        int categoryCol      = dr.getStartColumn();
        int valueCol         = dr.getStartColumn() + (config.isFirstColumnIsCategory() ? 1 : 0);

        SeriesCollection nSeries = chart.getNSeries();
        nSeries.setCategoryData(dr.toColumnRange(categoryCol, firstDataRow, lastDataRow));

        String valuesRange = dr.toColumnRange(valueCol, firstDataRow, lastDataRow);
        int idx = nSeries.add(valuesRange, true);
        Series series = nSeries.get(idx);
        series.setName(config.getChartTitle() != null ? config.getChartTitle() : "Amount");

        // Waterfall charts commonly show data labels for readability
        if (config.isShowDataLabels()) {
            DataLabels labels = series.getDataLabels();
            labels.setShowValue(true);
        }
    }
}
