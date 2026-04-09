package com.chartframework.strategy;

import com.aspose.cells.Chart;
import com.chartframework.config.RadarChartConfig;
import com.chartframework.model.ChartConfig;
import com.chartframework.model.ChartRequest;
import com.chartframework.model.DataRange;

/**
 * Strategy for Radar, Radar with Data Markers, and Filled Radar chart types.
 * Applies {@link RadarChartConfig} settings.
 */
public class RadarChartStrategy extends AbstractChartStrategy {

    @Override
    protected void configureSeries(Chart chart, ChartRequest request, DataRange dr) {
        log.debug("Configuring Radar series");
        addStandardCategorySeries(chart, request, dr);
        applyRadarConfig(chart, request.effectiveConfig());
    }

    private void applyRadarConfig(Chart chart, ChartConfig config) {
        RadarChartConfig rc = config.getRadar();
        if (rc == null) return;
        try {
            // Fill opacity on series for FILLED style
            if (rc.getStyle() == RadarChartConfig.RadarStyle.FILLED
                    && rc.getFillOpacity() != null) {
                for (int i = 0; i < chart.getNSeries().getCount(); i++) {
                    chart.getNSeries().get(i).getArea()
                            .setTransparency(1.0 - rc.getFillOpacity());
                }
            }
        } catch (Exception e) {
            log.debug("RadarChartConfig skipped: {}", e.getMessage());
        }
    }
}
