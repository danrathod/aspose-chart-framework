package com.chartframework.strategy;

import com.aspose.cells.Chart;
import com.chartframework.model.ChartRequest;
import com.chartframework.model.DataRange;

/**
 * Strategy for all Column and Bar chart variants.
 *
 * <p>Supported types: COLUMN_CLUSTERED, COLUMN_STACKED, COLUMN_100_STACKED,
 * COLUMN_3D_*, BAR_CLUSTERED, BAR_STACKED, BAR_100_STACKED, BAR_3D_*.</p>
 *
 * <p>Data layout: standard category-series (first row = headers,
 * first column = category labels).</p>
 */
public class ColumnBarChartStrategy extends AbstractChartStrategy {

    @Override
    protected void configureSeries(Chart chart, ChartRequest request, DataRange dataRange) {
        log.debug("Configuring Column/Bar series");
        addStandardCategorySeries(chart, request, dataRange);
    }
}
