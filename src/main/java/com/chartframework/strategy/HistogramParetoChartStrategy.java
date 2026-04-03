package com.chartframework.strategy;

import com.aspose.cells.Chart;
import com.aspose.cells.Series;
import com.aspose.cells.SeriesCollection;
import com.chartframework.model.ChartConfig;
import com.chartframework.model.ChartRequest;
import com.chartframework.model.DataRange;

/**
 * Strategy for Histogram and Pareto charts (Excel 2016+).
 *
 * <h2>Histogram</h2>
 * <p>A histogram shows frequency distribution — how many data points fall into
 * each bin. Aspose.Cells automatically calculates bin ranges from raw data.</p>
 *
 * <h2>Pareto</h2>
 * <p>A Pareto chart combines a histogram (sorted descending) with a cumulative
 * percentage line — useful for identifying the most significant factors
 * (the 80/20 rule).</p>
 *
 * <h2>Data Layout — Raw Values (preferred)</h2>
 * <pre>
 *   Score
 *   72
 *   85
 *   91
 *   68
 *   77
 *   ...
 * </pre>
 *
 * <h2>Data Layout — Pre-binned / Category + Frequency (Pareto)</h2>
 * <pre>
 *   Defect Type     Count
 *   Scratches       42
 *   Dents           28
 *   Stains          17
 *   Cracks          9
 *   Other           4
 * </pre>
 */
public class HistogramParetoChartStrategy extends AbstractChartStrategy {

    @Override
    protected void configureSeries(Chart chart, ChartRequest request, DataRange dr) {
        log.debug("Configuring Histogram/Pareto series");

        ChartConfig config   = request.effectiveConfig();
        int firstDataRow     = dr.getStartRow() + (config.isFirstRowIsHeader() ? 1 : 0);
        int lastDataRow      = dr.getEndRow();
        int totalCols        = dr.getEndColumn() - dr.getStartColumn() + 1;

        SeriesCollection nSeries = chart.getNSeries();

        if (totalCols == 1) {
            // Single column of raw values — Aspose bins automatically
            String valuesRange = dr.toColumnRange(dr.getStartColumn(), firstDataRow, lastDataRow);
            int idx = nSeries.add(valuesRange, true);
            nSeries.get(idx).setName("Frequency");
        } else {
            // Pre-binned: category + frequency columns
            int categoryCol = dr.getStartColumn();
            int valueCol    = dr.getStartColumn() + 1;
            nSeries.setCategoryData(dr.toColumnRange(categoryCol, firstDataRow, lastDataRow));
            int idx = nSeries.add(dr.toColumnRange(valueCol, firstDataRow, lastDataRow), true);
            nSeries.get(idx).setName("Count");
        }
    }
}
