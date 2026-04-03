package com.chartframework.strategy;

import com.aspose.cells.Chart;
import com.chartframework.model.ChartRequest;
import com.chartframework.model.DataRange;

/**
 * Strategy for all Line and Area chart variants.
 *
 * <p>Supported types: LINE, LINE_STACKED, LINE_100_STACKED,
 * LINE_WITH_DATA_MARKERS, LINE_3D, AREA, AREA_STACKED,
 * AREA_100_STACKED, AREA_3D_*.</p>
 *
 * <p>Data layout: standard category-series (first row = headers,
 * first column = category labels).</p>
 */
public class LineAreaChartStrategy extends AbstractChartStrategy {

    @Override
    protected void configureSeries(Chart chart, ChartRequest request, DataRange dataRange) {
        log.debug("Configuring Line/Area series");
        addStandardCategorySeries(chart, request, dataRange);
    }
}
