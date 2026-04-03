package com.chartframework.strategy;

import com.aspose.cells.Chart;
import com.aspose.cells.Series;
import com.aspose.cells.SeriesCollection;
import com.chartframework.model.ChartConfig;
import com.chartframework.model.ChartRequest;
import com.chartframework.model.DataRange;

/**
 * Strategy for Funnel charts (Excel 2016+).
 *
 * <p>A funnel chart shows values across multiple stages in a process — for
 * example, prospects → qualified leads → proposals → closed deals in a sales
 * pipeline. Bars decrease in size from top to bottom, forming a funnel shape.</p>
 *
 * <h2>Data Layout</h2>
 * <pre>
 *   Stage             Count
 *   Awareness         10000
 *   Interest           7500
 *   Consideration      4200
 *   Intent             2100
 *   Purchase            850
 * </pre>
 *
 * <p>Only <b>one</b> value column is required. The category column provides
 * the stage labels on the Y-axis.</p>
 */
public class FunnelChartStrategy extends AbstractChartStrategy {

    @Override
    protected void configureSeries(Chart chart, ChartRequest request, DataRange dr) {
        log.debug("Configuring Funnel series");

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
        series.setName(config.getChartTitle() != null ? config.getChartTitle() : "Values");
    }
}
