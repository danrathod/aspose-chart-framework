package com.chartframework.strategy;

import com.aspose.cells.Chart;
import com.chartframework.model.ChartRequest;
import com.chartframework.model.DataRange;

/**
 * Strategy for all Scatter (XY) chart variants.
 *
 * <h2>Data Layout</h2>
 * <pre>
 *   COL-0   COL-1   COL-2   COL-3   COL-4
 *   Label   X1      Y1      X2      Y2
 *   ...     1.0     2.5     3.0     4.1
 *   ...     2.0     3.8     4.5     5.6
 * </pre>
 *
 * <p>Pairs of columns (X, Y) form individual series.
 * The first column (categories) is ignored for scatter charts.</p>
 */
public class ScatterChartStrategy extends AbstractChartStrategy {

    @Override
    protected void configureSeries(Chart chart, ChartRequest request, DataRange dataRange) {
        log.debug("Configuring Scatter series");
        addScatterSeries(chart, request, dataRange);
    }
}
