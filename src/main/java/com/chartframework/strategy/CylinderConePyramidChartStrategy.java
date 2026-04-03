package com.chartframework.strategy;

import com.aspose.cells.Chart;
import com.chartframework.model.ChartRequest;
import com.chartframework.model.DataRange;

/**
 * Strategy for all Cylinder, Cone, and Pyramid chart variants.
 *
 * <p>These chart types are shape-decorated variants of Column and Bar charts.
 * They use vertical or horizontal markers shaped as cylinders, cones, or
 * pyramids but follow exactly the same standard category-series data layout.</p>
 *
 * <h2>Supported types</h2>
 * <ul>
 *   <li>CYLINDER, CYLINDER_STACKED, CYLINDER_100_STACKED, CYLINDER_BAR_*, CYLINDER_3D</li>
 *   <li>CONE, CONE_STACKED, CONE_100_STACKED, CONE_BAR_*, CONE_3D</li>
 *   <li>PYRAMID, PYRAMID_STACKED, PYRAMID_100_STACKED, PYRAMID_BAR_*, PYRAMID_3D</li>
 * </ul>
 *
 * <h2>Data Layout</h2>
 * <pre>
 *   Quarter    Electronics   Apparel   Home
 *   Q1         45000         30000     22000
 *   Q2         52000         34000     25000
 * </pre>
 */
public class CylinderConePyramidChartStrategy extends AbstractChartStrategy {

    @Override
    protected void configureSeries(Chart chart, ChartRequest request, DataRange dataRange) {
        log.debug("Configuring Cylinder/Cone/Pyramid series");
        addStandardCategorySeries(chart, request, dataRange);
    }
}
