package com.chartframework.strategy;

import com.aspose.cells.Chart;
import com.aspose.cells.Series;
import com.aspose.cells.SeriesCollection;
import com.chartframework.model.ChartConfig;
import com.chartframework.model.ChartRequest;
import com.chartframework.model.DataRange;

/**
 * Strategy for all Stock chart variants.
 *
 * <h2>Data Layouts by Stock Type</h2>
 *
 * <p><b>High-Low-Close (3 series columns):</b></p>
 * <pre>
 *   Date    High    Low     Close
 *   Jan-01  150.0   140.0   145.0
 *   Jan-02  155.0   142.0   152.0
 * </pre>
 *
 * <p><b>Open-High-Low-Close (4 series columns):</b></p>
 * <pre>
 *   Date    Open    High    Low     Close
 *   Jan-01  143.0   150.0   140.0   145.0
 * </pre>
 *
 * <p><b>Volume-High-Low-Close (4 series columns):</b></p>
 * <pre>
 *   Date    Volume  High    Low     Close
 *   Jan-01  1200000 150.0   140.0   145.0
 * </pre>
 *
 * <p><b>Volume-Open-High-Low-Close (5 series columns):</b></p>
 * <pre>
 *   Date    Volume  Open    High    Low     Close
 *   Jan-01  1200000 143.0   150.0   140.0   145.0
 * </pre>
 *
 * <p>The framework adds each column after the date/category column as a
 * separate series — Aspose.Cells maps them to the correct OHLC roles
 * automatically based on the chart type integer.</p>
 */
public class StockChartStrategy extends AbstractChartStrategy {

    private static final String[] OHLC_NAMES =
            {"Volume", "Open", "High", "Low", "Close"};

    @Override
    protected void configureSeries(Chart chart, ChartRequest request, DataRange dr) {
        log.debug("Configuring Stock series");

        ChartConfig config       = request.effectiveConfig();
        int firstDataRow         = dr.getStartRow() + (config.isFirstRowIsHeader() ? 1 : 0);
        int lastDataRow          = dr.getEndRow();
        int categoryCol          = dr.getStartColumn();
        int firstSeriesCol       = categoryCol + (config.isFirstColumnIsCategory() ? 1 : 0);

        SeriesCollection nSeries = chart.getNSeries();
        nSeries.setCategoryData(dr.toColumnRange(categoryCol, firstDataRow, lastDataRow));

        int nameIndex = 0;
        for (int col = firstSeriesCol; col <= dr.getEndColumn(); col++) {
            String valuesRange = dr.toColumnRange(col, firstDataRow, lastDataRow);
            int idx = nSeries.add(valuesRange, true);
            Series series = nSeries.get(idx);
            series.setName(nameIndex < OHLC_NAMES.length
                    ? OHLC_NAMES[nameIndex++] : "Series " + nameIndex++);
        }
    }
}
