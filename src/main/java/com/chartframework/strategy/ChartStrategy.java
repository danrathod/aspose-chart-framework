package com.chartframework.strategy;

import com.aspose.cells.Chart;
import com.chartframework.model.ChartRequest;
import com.chartframework.model.DataRange;

/**
 * Strategy Pattern interface for chart-type-specific configuration logic.
 *
 * <h2>Responsibilities</h2>
 * <p>Each implementation is responsible for <em>one</em> concern:
 * mapping the abstract data in a {@link DataRange} to concrete Aspose
 * chart series, axes, and formatting settings for a specific chart family.</p>
 *
 * <h2>Extensibility</h2>
 * <p>To support a new chart type, simply:</p>
 * <ol>
 *   <li>Implement this interface.</li>
 *   <li>Register the implementation in {@link com.chartframework.factory.ChartStrategyFactory}.</li>
 * </ol>
 * No other class needs to change — satisfying the Open/Closed Principle.
 *
 * <h2>Contract</h2>
 * <ul>
 *   <li>Implementations receive an already-created (but unconfigured) Aspose
 *       {@link Chart} object and must populate its series and formatting.</li>
 *   <li>The chart has already been positioned in the target worksheet by the
 *       time {@code configure} is called.</li>
 *   <li>Implementations must NOT save the workbook — the caller owns that.</li>
 * </ul>
 */
public interface ChartStrategy {

    /**
     * Configures the given chart by adding data series and applying
     * chart-type-specific formatting.
     *
     * @param chart     The Aspose chart to configure (already created and positioned).
     * @param request   The original chart request (config, data metadata, etc.).
     * @param dataRange The range descriptor pointing to data in the hidden sheet.
     */
    void configure(Chart chart, ChartRequest request, DataRange dataRange);
}
