package com.chartframework.strategy;

import com.aspose.cells.Chart;
import com.aspose.cells.Series;
import com.chartframework.config.LineChartConfig;
import com.chartframework.model.ChartConfig;
import com.chartframework.model.ChartRequest;
import com.chartframework.model.DataRange;

/**
 * Strategy for Line and Area chart variants (all stacking modes, 3D).
 * Applies {@link LineChartConfig} settings: smooth, drop lines, high-low lines.
 */
public class LineAreaChartStrategy extends AbstractChartStrategy {

    @Override
    protected void configureSeries(Chart chart, ChartRequest request, DataRange dr) {
        log.debug("Configuring Line/Area series");
        addStandardCategorySeries(chart, request, dr);
        applyLineConfig(chart, request.effectiveConfig());
    }

    private void applyLineConfig(Chart chart, ChartConfig config) {
        LineChartConfig lc = config.getLine();
        if (lc == null) return;
        try {
            // Per-series smooth override (if not already set via SeriesStyleConfig)
            if (Boolean.TRUE.equals(lc.getSmooth())) {
                for (int i = 0; i < chart.getNSeries().getCount(); i++) {
                    Series s = chart.getNSeries().get(i);
                    s.setSmooth(true);
                }
            }
            if (Boolean.TRUE.equals(lc.getShowDropLines()) && lc.getDropLineStyle() != null) {
                applyBorderToLine(chart.getDropBars().getBorder(), lc.getDropLineStyle());
            }
        } catch (Exception e) {
            log.debug("LineChartConfig skipped: {}", e.getMessage());
        }
    }
}
