package com.chartframework.strategy;

import com.aspose.cells.Chart;
import com.aspose.cells.Series;
import com.aspose.cells.SeriesCollection;
import com.chartframework.model.ChartConfig;
import com.chartframework.model.ChartRequest;
import com.chartframework.model.DataRange;

/**
 * Strategy for Pie, 3D Pie, Exploded Pie, Doughnut, and related chart types.
 *
 * <h2>Data Layout for Pie Charts</h2>
 * <pre>
 *   COL-0      COL-1
 *   (slice)    (value)
 *   Q1         30000
 *   Q2         45000
 *   Q3         38000
 *   Q4         52000
 * </pre>
 *
 * <p>Pie charts use a single series. The category column (COL-0) becomes
 * the slice labels; COL-1 becomes the slice values.</p>
 */
public class PieDonutChartStrategy extends AbstractChartStrategy {

    @Override
    protected void configureSeries(Chart chart, ChartRequest request, DataRange dr) {
        log.debug("Configuring Pie/Donut series");

        ChartConfig config = request.effectiveConfig();

        int firstDataRow = dr.getStartRow() + (config.isFirstRowIsHeader() ? 1 : 0);
        int lastDataRow  = dr.getEndRow();
        int categoryCol  = dr.getStartColumn();
        int valueCol     = dr.getStartColumn() + (config.isFirstColumnIsCategory() ? 1 : 0);

        String categoryRange = dr.toColumnRange(categoryCol, firstDataRow, lastDataRow);
        String valuesRange   = dr.toColumnRange(valueCol,    firstDataRow, lastDataRow);

        SeriesCollection nSeries = chart.getNSeries();
        nSeries.setCategoryData(categoryRange);

        int seriesIndex = nSeries.add(valuesRange, true);
        Series series   = nSeries.get(seriesIndex);
        series.setName(config.getChartTitle() != null ? config.getChartTitle() : "Values");
    }
}
