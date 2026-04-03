package com.chartframework.strategy;

import com.aspose.cells.Chart;
import com.chartframework.model.ChartRequest;
import com.chartframework.model.DataRange;

/**
 * Strategy for all Surface chart variants:
 * 3D Surface, Wireframe 3D Surface, Contour, Wireframe Contour.
 *
 * <h2>Data Layout</h2>
 * <p>Surface charts treat the data as a grid where rows are the X-axis
 * positions and columns are the Y-axis positions. Each cell value is the
 * Z-axis (height). Uses the same standard category-series layout.</p>
 *
 * <pre>
 *   (header)  Y=1    Y=2    Y=3
 *   X=1       1.2    2.4    3.1
 *   X=2       2.1    3.6    4.8
 *   X=3       3.0    4.2    5.5
 * </pre>
 */
public class SurfaceChartStrategy extends AbstractChartStrategy {

    @Override
    protected void configureSeries(Chart chart, ChartRequest request, DataRange dataRange) {
        log.debug("Configuring Surface series");
        addStandardCategorySeries(chart, request, dataRange);
    }
}
