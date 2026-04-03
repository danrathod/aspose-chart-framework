package com.chartframework.strategy;

import com.aspose.cells.Chart;
import com.chartframework.model.ChartRequest;
import com.chartframework.model.DataRange;

/**
 * Strategy for Radar, Radar with Data Markers, and Filled Radar chart types.
 *
 * <h2>Data Layout</h2>
 * <p>Same as standard category-series layout.  Each row represents an axis
 * of the radar; each series column is a "spoke" group.</p>
 *
 * <pre>
 *   COL-0          COL-1      COL-2
 *   Attribute      Team A     Team B
 *   Speed          80         65
 *   Strength       70         90
 *   Intelligence   90         75
 *   Endurance      60         80
 * </pre>
 */
public class RadarChartStrategy extends AbstractChartStrategy {

    @Override
    protected void configureSeries(Chart chart, ChartRequest request, DataRange dataRange) {
        log.debug("Configuring Radar series");
        addStandardCategorySeries(chart, request, dataRange);
    }
}
