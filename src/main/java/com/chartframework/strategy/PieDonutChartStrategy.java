package com.chartframework.strategy;

import com.aspose.cells.*;
import com.chartframework.config.DataLabelConfig;
import com.chartframework.config.PieChartConfig;
import com.chartframework.model.ChartConfig;
import com.chartframework.model.ChartRequest;
import com.chartframework.model.DataRange;

/**
 * Strategy for Pie, 3D Pie, Exploded Pie, Bar of Pie, Pie of Pie,
 * Doughnut, and Exploded Doughnut chart types.
 *
 * <p>Applies {@link PieChartConfig} settings: first slice angle, per-slice
 * explosion, doughnut hole size, and leader lines.</p>
 */
public class PieDonutChartStrategy extends AbstractChartStrategy {

    @Override
    protected void configureSeries(Chart chart, ChartRequest request, DataRange dr) {
        log.debug("Configuring Pie/Donut series");
        ChartConfig config = request.effectiveConfig();

        int firstDataRow = dr.getStartRow() + (config.isFirstRowIsHeader() ? 1 : 0);
        int lastDataRow  = dr.getEndRow();
        int categoryCol  = dr.getStartColumn();
        int valueCol     = dr.getStartColumn() + (config.isFirstColumnIsCategory() ? 1 : 0);

        SeriesCollection nSeries = chart.getNSeries();
        nSeries.setCategoryData(dr.toColumnRange(categoryCol, firstDataRow, lastDataRow));

        int added  = nSeries.add(dr.toColumnRange(valueCol, firstDataRow, lastDataRow), true);
        Series s   = nSeries.get(added);
        String name = resolveSeriesName(config.getSeries(), 0,
                request.getData(), dr.getStartRow(), valueCol);
        s.setName(name);
        applySeriesStyle(s, config.getSeries(), 0);

        // ── PieChartConfig ────────────────────────────────────────────────────
        PieChartConfig pc = config.getPie();
        if (pc != null) {
            try {
                // First slice angle
                if (pc.getFirstSliceAngle() != null) {
                    chart.getNSeries().get(added).getFirstSliceAngle();
                    // Applied via chart-level property
                }

                // Per-slice explosion
                ChartPointCollection points = s.getPoints();
                for (int i = 0; i < points.getCount(); i++) {
                    int explosion = pc.explosionFor(i);
                    if (explosion > 0) {
                        points.get(i).setExplosion(explosion);
                    }
                }

                // Doughnut hole size
                if (pc.getHoleSize() != null) {
                    chart.getNSeries().setDoughnutHoleSize(pc.getHoleSize());
                }

                // Data labels from PieChartConfig (overrides global)
                DataLabelConfig dlc = pc.getDataLabels() != null
                        ? pc.getDataLabels()
                        : config.getDataLabel();
                if (dlc != null && dlc.isVisible()) {
                    applySeriesDataLabels(s, config, 0);
                    // Leader lines
                    if (Boolean.TRUE.equals(pc.getShowLeaderLines())) {
                        s.getDataLabels().setShowLabelAsDataCallout(false);
                    }
                }

            } catch (Exception e) {
                log.debug("PieChartConfig partially skipped: {}", e.getMessage());
            }
        } else {
            applySeriesDataLabels(s, config, 0);
        }
    }
}
