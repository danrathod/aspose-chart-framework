package com.chartframework.strategy;

import com.aspose.cells.Chart;
import com.aspose.cells.Series;
import com.aspose.cells.SeriesCollection;
import com.chartframework.model.ChartConfig;
import com.chartframework.model.ChartRequest;
import com.chartframework.model.DataRange;

/**
 * Strategy for Map (Filled Map / Region Map) charts (Excel 2019+).
 *
 * <p>A map chart colours geographic regions (countries, states, provinces)
 * according to a numeric value — ideal for visualising regional sales,
 * population density, survey responses, etc.</p>
 *
 * <h2>Data Layout</h2>
 * <pre>
 *   Country          Revenue
 *   United States    350000
 *   United Kingdom   120000
 *   Germany          95000
 *   France           88000
 *   Japan            142000
 *   Australia        67000
 * </pre>
 *
 * <p>The category column must contain <b>recognised geographic names</b>
 * (country names, ISO codes, US state names, etc.) that Excel / Aspose can
 * resolve to map regions. The value column drives the colour gradient.</p>
 *
 * <p><b>Note:</b> Map charts require an internet connection in Excel to fetch
 * map tiles. In server-side Aspose.Cells rendering the map shape data is
 * embedded within the file.</p>
 */
public class MapChartStrategy extends AbstractChartStrategy {

    @Override
    protected void configureSeries(Chart chart, ChartRequest request, DataRange dr) {
        log.debug("Configuring Map (Region) series");

        ChartConfig config   = request.effectiveConfig();
        int firstDataRow     = dr.getStartRow() + (config.isFirstRowIsHeader() ? 1 : 0);
        int lastDataRow      = dr.getEndRow();
        int categoryCol      = dr.getStartColumn();
        int valueCol         = dr.getStartColumn() + (config.isFirstColumnIsCategory() ? 1 : 0);

        SeriesCollection nSeries = chart.getNSeries();
        nSeries.setCategoryData(dr.toColumnRange(categoryCol, firstDataRow, lastDataRow));

        String valuesRange = dr.toColumnRange(valueCol, firstDataRow, lastDataRow);
        int idx = nSeries.add(valuesRange, true);
        Series series = nSeries.get(idx);
        series.setName(config.getValueAxisTitle() != null ? config.getValueAxisTitle() : "Value");
    }
}
