package com.chartframework.strategy;

import com.aspose.cells.*;
import com.chartframework.model.ChartConfig;
import com.chartframework.model.ChartRequest;
import com.chartframework.model.DataRange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Base class providing shared utility methods for all {@link ChartStrategy}
 * implementations.
 *
 * <h2>Shared behaviour</h2>
 * <ul>
 *   <li>Applying chart title</li>
 *   <li>Configuring axis titles</li>
 *   <li>Setting legend position/visibility</li>
 *   <li>Enabling / disabling data labels</li>
 *   <li>Gridline toggle</li>
 *   <li>Standard category-series series-building loop</li>
 * </ul>
 *
 * <p>Subclasses override {@link #configureSeries} to handle
 * chart-type-specific series layout and may call any protected helper.</p>
 */
public abstract class AbstractChartStrategy implements ChartStrategy {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    // ─────────────────────────────────────────────────────────────────────────
    // ChartStrategy entry point
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    public final void configure(Chart chart, ChartRequest request, DataRange dataRange) {
        ChartConfig config = request.effectiveConfig();

        applyTitle(chart, config);
        configureSeries(chart, request, dataRange);  // ← delegate to subclass
        applyAxes(chart, config);
        applyLegend(chart, config);
        applyDataLabels(chart, config);
        applyGridlines(chart, config);

        log.debug("[{}] Chart configured with {} series",
                getClass().getSimpleName(), chart.getNSeries().getCount());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Abstract hook — subclasses must implement
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Add series (and optionally categories) to the chart.
     * All other configuration is handled by the base class.
     */
    protected abstract void configureSeries(Chart chart,
                                            ChartRequest request,
                                            DataRange dataRange);

    // ─────────────────────────────────────────────────────────────────────────
    // Shared series-building helpers
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Standard column/bar/line/area series layout:
     *
     * <pre>
     *   COL-0       COL-1    COL-2    COL-3
     *   (category)  Series1  Series2  Series3
     *   Jan         100      200      50
     *   Feb         120      210      60
     * </pre>
     *
     * <p>Row 0 is the header; column 0 is categories; columns 1..N are series.</p>
     */
    protected void addStandardCategorySeries(Chart chart,
                                             ChartRequest request,
                                             DataRange dr) {
        ChartConfig config = request.effectiveConfig();
        List<List<Object>> data = request.getData();

        int headerRow    = dr.getStartRow();
        int firstDataRow = dr.getStartRow() + (config.isFirstRowIsHeader() ? 1 : 0);
        int lastDataRow  = dr.getEndRow();
        int categoryCol  = dr.getStartColumn();
        int firstSeriesCol = dr.getStartColumn() + (config.isFirstColumnIsCategory() ? 1 : 0);
        int lastSeriesCol  = dr.getEndColumn();

        // Category (X-axis) range
        String categoryRange = dr.toColumnRange(categoryCol, firstDataRow, lastDataRow);

        SeriesCollection nSeries = chart.getNSeries();
        nSeries.setCategoryData(categoryRange);

        // Add one series per data column
        List<String> explicitNames = config.getSeriesNames();

        for (int col = firstSeriesCol; col <= lastSeriesCol; col++) {
            String valuesRange = dr.toColumnRange(col, firstDataRow, lastDataRow);
            int seriesIndex = nSeries.add(valuesRange, true);
            Series series = nSeries.get(seriesIndex);

            // Resolve series name: explicit list → header cell → "Series N"
            String seriesName = resolveSeriesName(explicitNames, col - firstSeriesCol, data, headerRow, col);
            series.setName(seriesName);
        }
    }

    /**
     * Adds series for Bubble charts:
     * Expects columns: [Category, X-values, Y-values, Bubble-size, ...]
     * repeated per series group.
     */
    protected void addBubbleSeries(Chart chart, ChartRequest request, DataRange dr) {
        ChartConfig config = request.effectiveConfig();
        int firstDataRow = dr.getStartRow() + (config.isFirstRowIsHeader() ? 1 : 0);
        int lastDataRow  = dr.getEndRow();
        int startCol     = dr.getStartColumn() + (config.isFirstColumnIsCategory() ? 1 : 0);

        SeriesCollection nSeries = chart.getNSeries();

        // Groups of 3 columns: X, Y, Size
        int col = startCol;
        int idx = 1;
        while (col + 1 <= dr.getEndColumn()) {
            int xCol   = col;
            int yCol   = col + 1;
            int sizeCol = (col + 2 <= dr.getEndColumn()) ? col + 2 : yCol;

            String xRange    = dr.toColumnRange(xCol,    firstDataRow, lastDataRow);
            String yRange    = dr.toColumnRange(yCol,    firstDataRow, lastDataRow);
            String sizeRange = dr.toColumnRange(sizeCol, firstDataRow, lastDataRow);

            int seriesIndex = nSeries.add(xRange, true);
            Series series = nSeries.get(seriesIndex);
            series.setXValues(xRange);
            series.setValues(yRange);
            series.setBubbleSizes(sizeRange);
            series.setName("Bubble Series " + idx++);

            col += 3;
        }
    }

    /**
     * Adds series for Scatter charts:
     * Expects columns: [Category, X-values, Y-values] per series pair.
     */
    protected void addScatterSeries(Chart chart, ChartRequest request, DataRange dr) {
        ChartConfig config = request.effectiveConfig();
        int firstDataRow = dr.getStartRow() + (config.isFirstRowIsHeader() ? 1 : 0);
        int lastDataRow  = dr.getEndRow();
        int startCol     = dr.getStartColumn() + (config.isFirstColumnIsCategory() ? 1 : 0);

        SeriesCollection nSeries = chart.getNSeries();

        int col = startCol;
        int idx = 1;
        while (col + 1 <= dr.getEndColumn()) {
            String xRange = dr.toColumnRange(col,     firstDataRow, lastDataRow);
            String yRange = dr.toColumnRange(col + 1, firstDataRow, lastDataRow);

            int seriesIndex = nSeries.add(yRange, true);
            Series series = nSeries.get(seriesIndex);
            series.setXValues(xRange);
            series.setName("Scatter Series " + idx++);

            col += 2;
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Common chart configuration helpers
    // ─────────────────────────────────────────────────────────────────────────

    protected void applyTitle(Chart chart, ChartConfig config) {
        if (config.getChartTitle() != null && !config.getChartTitle().isBlank()) {
            chart.setShowTitle(true);
            chart.getTitle().setText(config.getChartTitle());
        } else {
            chart.setShowTitle(false);
        }
    }

    protected void applyAxes(Chart chart, ChartConfig config) {
        try {
            // Category (X) axis
            if (chart.getCategoryAxis() != null
                    && config.getCategoryAxisTitle() != null) {
                chart.getCategoryAxis().getTitle().setText(config.getCategoryAxisTitle());
                chart.getCategoryAxis().getTitle().setVisible(true);
            }
            if (chart.getCategoryAxis() != null
                    && config.getCategoryAxisRotation() != null) {
                chart.getCategoryAxis().setTickLabelSpacing(1);
            }

            // Value (Y) axis
            if (chart.getValueAxis() != null
                    && config.getValueAxisTitle() != null) {
                chart.getValueAxis().getTitle().setText(config.getValueAxisTitle());
                chart.getValueAxis().getTitle().setVisible(true);
            }

            // Secondary value axis (for combo charts)
            if (chart.getSecondValueAxis() != null
                    && config.getSecondaryValueAxisTitle() != null) {
                chart.getSecondValueAxis().getTitle().setText(config.getSecondaryValueAxisTitle());
                chart.getSecondValueAxis().getTitle().setVisible(true);
            }
        } catch (Exception e) {
            // Some chart types don't have certain axes (e.g. Pie)
            log.debug("Axis configuration skipped for this chart type: {}", e.getMessage());
        }
    }

    protected void applyLegend(Chart chart, ChartConfig config) {
        chart.setShowLegend(config.isShowLegend());
        if (config.isShowLegend()) {
            int legendPos = resolveLegendPosition(config.getEffectiveLegendPosition());
            chart.getLegend().setPosition(legendPos);
        }
    }

    protected void applyDataLabels(Chart chart, ChartConfig config) {
        if (!config.isShowDataLabels()) return;
        for (int i = 0; i < chart.getNSeries().getCount(); i++) {
            DataLabels labels = chart.getNSeries().get(i).getDataLabels();
            labels.setShowValue(true);
        }
    }

    protected void applyGridlines(Chart chart, ChartConfig config) {
        try {
            if (chart.getValueAxis() != null) {
                chart.getValueAxis().setMajorGridlines(config.isShowMajorGridlines());
            }
        } catch (Exception e) {
            log.debug("Gridlines configuration skipped: {}", e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Utility helpers
    // ─────────────────────────────────────────────────────────────────────────

    private String resolveSeriesName(List<String> explicitNames,
                                     int seriesIndex,
                                     List<List<Object>> data,
                                     int headerRow,
                                     int col) {
        // 1. Explicit names list
        if (explicitNames != null && seriesIndex < explicitNames.size()) {
            return explicitNames.get(seriesIndex);
        }
        // 2. Header row of the data table
        if (!data.isEmpty() && headerRow < data.size()) {
            List<Object> header = data.get(headerRow);
            if (header != null && col < header.size() && header.get(col) != null) {
                return header.get(col).toString();
            }
        }
        // 3. Default fallback
        return "Series " + (seriesIndex + 1);
    }

    private int resolveLegendPosition(String position) {
        return switch (position) {
            case "TOP"    -> LegendPositionType.TOP;
            case "LEFT"   -> LegendPositionType.LEFT;
            case "RIGHT"  -> LegendPositionType.RIGHT;
            case "CORNER" -> LegendPositionType.CORNER;
            default       -> LegendPositionType.BOTTOM;
        };
    }
}
