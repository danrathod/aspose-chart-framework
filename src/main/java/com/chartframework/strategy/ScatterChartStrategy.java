package com.chartframework.strategy;

import com.aspose.cells.Chart;
import com.chartframework.model.ChartRequest;
import com.chartframework.model.DataRange;

/**
 * Strategy for Scatter (XY) chart variants.
 */
public class ScatterChartStrategy extends AbstractChartStrategy {

    @Override
    protected void configureSeries(Chart chart, ChartRequest request, DataRange dr) {
        log.debug("Configuring Scatter series");
        addScatterSeries(chart, request, dr);
    }
}
