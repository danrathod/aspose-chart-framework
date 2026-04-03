package com.chartframework.strategy;

import com.aspose.cells.Chart;
import com.aspose.cells.Series;
import com.aspose.cells.SeriesCollection;
import com.chartframework.model.ChartConfig;
import com.chartframework.model.ChartRequest;
import com.chartframework.model.DataRange;

/**
 * Strategy for Treemap and Sunburst charts (Excel 2016+).
 *
 * <p>Both chart types visualise <b>hierarchical</b> data:</p>
 * <ul>
 *   <li><b>Treemap</b> — nested rectangles; size encodes the leaf value.</li>
 *   <li><b>Sunburst</b> — concentric rings; each ring is a hierarchy level.</li>
 * </ul>
 *
 * <h2>Flat (single-level) Data Layout</h2>
 * <pre>
 *   Category     Value
 *   Electronics  45000
 *   Apparel      30000
 *   Home         22000
 *   Sports       18000
 * </pre>
 *
 * <h2>Hierarchical (multi-level) Data Layout</h2>
 * <pre>
 *   Level1    Level2       Value
 *   Tech      Hardware     25000
 *   Tech      Software     20000
 *   Retail    Apparel      30000
 *   Retail    Footwear     15000
 * </pre>
 *
 * <p>For hierarchical data, pass all hierarchy columns before the value column.
 * Aspose.Cells will interpret consecutive string columns as hierarchy levels
 * and the last numeric column as the leaf value.</p>
 */
public class TreemapSunburstChartStrategy extends AbstractChartStrategy {

    @Override
    protected void configureSeries(Chart chart, ChartRequest request, DataRange dr) {
        log.debug("Configuring Treemap/Sunburst series");

        ChartConfig config   = request.effectiveConfig();
        int firstDataRow     = dr.getStartRow() + (config.isFirstRowIsHeader() ? 1 : 0);
        int lastDataRow      = dr.getEndRow();
        int totalCols        = dr.getEndColumn() - dr.getStartColumn() + 1;

        SeriesCollection nSeries = chart.getNSeries();

        if (totalCols == 2) {
            // Simple flat layout: one category column + one value column
            int categoryCol = dr.getStartColumn();
            int valueCol    = dr.getStartColumn() + 1;

            nSeries.setCategoryData(dr.toColumnRange(categoryCol, firstDataRow, lastDataRow));
            int idx = nSeries.add(dr.toColumnRange(valueCol, firstDataRow, lastDataRow), true);
            nSeries.get(idx).setName("Values");

        } else {
            // Hierarchical layout: all columns are part of the series range.
            // Aspose reads the full block including hierarchy columns.
            String fullRange = dr.toAbsoluteRange(firstDataRow, dr.getStartColumn(),
                                                   lastDataRow, dr.getEndColumn());
            int idx = nSeries.add(fullRange, true);
            Series series = nSeries.get(idx);
            series.setName(config.getChartTitle() != null ? config.getChartTitle() : "Hierarchy");
        }
    }
}
