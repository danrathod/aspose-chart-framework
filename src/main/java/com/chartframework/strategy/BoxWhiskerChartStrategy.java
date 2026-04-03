package com.chartframework.strategy;

import com.aspose.cells.Chart;
import com.aspose.cells.Series;
import com.aspose.cells.SeriesCollection;
import com.chartframework.model.ChartConfig;
import com.chartframework.model.ChartRequest;
import com.chartframework.model.DataRange;

/**
 * Strategy for Box and Whisker charts (Excel 2016+).
 *
 * <p>A box and whisker chart (box plot) shows the distribution of a dataset
 * into quartiles, highlighting mean and outliers. The "box" spans Q1–Q3;
 * "whiskers" extend to min/max; outliers are plotted individually.</p>
 *
 * <h2>Data Layout — One column per group/series</h2>
 * <pre>
 *   Group A   Group B   Group C
 *   45        72        55
 *   52        68        60
 *   38        85        48
 *   61        79        67
 *   49        63        53
 *   57        91        71
 * </pre>
 *
 * <p>Each column is an independent series (group). The header row provides
 * the group name. Aspose.Cells calculates Q1, median, Q3, min, max, and
 * outliers automatically from the raw data values.</p>
 *
 * <p><b>Note:</b> Box and Whisker charts do not use a separate category column;
 * all data columns become independent series. Set
 * {@link com.chartframework.model.ChartConfig#isFirstColumnIsCategory()} to
 * {@code false} for this chart type.</p>
 */
public class BoxWhiskerChartStrategy extends AbstractChartStrategy {

    @Override
    protected void configureSeries(Chart chart, ChartRequest request, DataRange dr) {
        log.debug("Configuring Box and Whisker series");

        ChartConfig config   = request.effectiveConfig();
        int firstDataRow     = dr.getStartRow() + (config.isFirstRowIsHeader() ? 1 : 0);
        int lastDataRow      = dr.getEndRow();
        // Box & Whisker: every column is its own series; no separate category column
        int firstCol = dr.getStartColumn();
        int lastCol  = dr.getEndColumn();

        SeriesCollection nSeries = chart.getNSeries();

        for (int col = firstCol; col <= lastCol; col++) {
            String valuesRange = dr.toColumnRange(col, firstDataRow, lastDataRow);
            int idx = nSeries.add(valuesRange, true);
            Series series = nSeries.get(idx);

            // Derive series name from header row
            String seriesName = deriveSeriesName(request.getData(),
                    dr.getStartRow(), col, col - firstCol);
            series.setName(seriesName);
        }
    }

    private String deriveSeriesName(java.util.List<java.util.List<Object>> data,
                                    int headerRow, int col, int fallbackIndex) {
        if (data != null && headerRow < data.size()) {
            java.util.List<Object> header = data.get(headerRow);
            if (header != null && col < header.size() && header.get(col) != null) {
                return header.get(col).toString();
            }
        }
        return "Group " + (fallbackIndex + 1);
    }
}
