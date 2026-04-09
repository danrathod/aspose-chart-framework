package com.chartframework.strategy;

import com.aspose.cells.Chart;
import com.chartframework.model.ChartRequest;
import com.chartframework.model.DataRange;

/**
 * Strategy for Bubble and 3D Bubble chart types.
 */
public class BubbleChartStrategy extends AbstractChartStrategy {

    @Override
    protected void configureSeries(Chart chart, ChartRequest request, DataRange dr) {
        log.debug("Configuring Bubble series");
        addBubbleSeries(chart, request, dr);
    }
}
