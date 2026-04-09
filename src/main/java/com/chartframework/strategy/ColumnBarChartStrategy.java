package com.chartframework.strategy;

import com.aspose.cells.Chart;
import com.chartframework.config.BarColumnConfig;
import com.chartframework.model.ChartConfig;
import com.chartframework.model.ChartRequest;
import com.chartframework.model.DataRange;

/**
 * Strategy for Column and Bar chart variants (all stacking modes, all 3D modes).
 * Applies {@link BarColumnConfig} settings: gap width, overlap, gap depth.
 */
public class ColumnBarChartStrategy extends AbstractChartStrategy {

    @Override
    protected void configureSeries(Chart chart, ChartRequest request, DataRange dr) {
        log.debug("Configuring Column/Bar series");
        addStandardCategorySeries(chart, request, dr);
        applyBarColumnConfig(chart, request.effectiveConfig());
    }

    private void applyBarColumnConfig(Chart chart, ChartConfig config) {
        BarColumnConfig bc = config.getBarColumn();
        if (bc == null) return;
        try {
            if (bc.getGapWidth() != null) chart.setGapWidth(bc.getGapWidth());
            if (bc.getOverlap()  != null) chart.setSeriesAxis(bc.getOverlap());
            if (bc.getGapDepth() != null) chart.setGapDepth(bc.getGapDepth());
        } catch (Exception e) {
            log.debug("BarColumnConfig skipped: {}", e.getMessage());
        }
    }
}
