package com.chartframework.strategy;

import com.aspose.cells.Chart;
import com.chartframework.model.ChartRequest;
import com.chartframework.model.DataRange;

/**
 * Strategy for Bubble and 3D Bubble chart types.
 *
 * <h2>Data Layout</h2>
 * <pre>
 *   COL-0    COL-1   COL-2   COL-3
 *   Label    X       Y       Size
 *   Product  2.5     3.8     1200
 *   Service  5.0     7.2     800
 * </pre>
 *
 * <p>Groups of three columns (X, Y, Size) form individual bubble series.
 * The first column is treated as the category/label column and is skipped.</p>
 */
public class BubbleChartStrategy extends AbstractChartStrategy {

    @Override
    protected void configureSeries(Chart chart, ChartRequest request, DataRange dataRange) {
        log.debug("Configuring Bubble series");
        addBubbleSeries(chart, request, dataRange);
    }
}
