package com.chartframework.config;

import lombok.Builder;
import lombok.Singular;
import lombok.Value;

import java.util.List;

/**
 * Full series configuration — names, per-series styling, and data label overrides.
 *
 * <p>Series are addressed by their zero-based index in the data table.
 * Any series index that does not have an explicit style entry uses the
 * chart's default palette.</p>
 *
 * <pre>{@code
 * SeriesConfig series = SeriesConfig.builder()
 *     // Override series names (instead of reading from header row)
 *     .name("Revenue")
 *     .name("Cost")
 *     .name("Profit")
 *     // Per-series styling (index-aligned with names)
 *     .style(SeriesStyleConfig.solidColor("#1565C0"))  // Revenue = blue
 *     .style(SeriesStyleConfig.solidColor("#E65100"))  // Cost    = orange
 *     .style(SeriesStyleConfig.solidColor("#2E7D32"))  // Profit  = green
 *     // Data labels applied to all series
 *     .globalDataLabel(DataLabelConfig.valueOnly())
 *     // Whether first data row is a header
 *     .firstRowIsHeader(true)
 *     .firstColumnIsCategory(true)
 *     .build();
 * }</pre>
 */
@Value
@Builder
public class SeriesConfig {

    /**
     * Explicit series names in order.
     * When non-empty, these override names read from the header row.
     */
    @Singular
    List<String> names;

    /**
     * Per-series style configs, indexed to match {@link #names}.
     * Series with no corresponding style entry use the auto palette.
     */
    @Singular
    List<SeriesStyleConfig> styles;

    /**
     * Data label config applied to <em>all</em> series uniformly.
     * Overridden per-series by {@link #perSeriesDataLabels} if provided.
     * Null = use chart-level {@link DataLabelConfig}.
     */
    DataLabelConfig globalDataLabel;

    /**
     * Per-series data label overrides (index-aligned with {@link #names}).
     * Null entries inherit {@link #globalDataLabel}.
     */
    @Singular("perSeriesDataLabel")
    List<DataLabelConfig> perSeriesDataLabels;

    /**
     * Whether the first data row is a header row containing series names.
     * Defaults to true.
     */
    @Builder.Default
    Boolean firstRowIsHeader = true;

    /**
     * Whether the first data column contains category labels.
     * Defaults to true.
     */
    @Builder.Default
    Boolean firstColumnIsCategory = true;

    // ── Convenience helpers ───────────────────────────────────────────────────

    /** Returns the style for a given series index, or null if not configured. */
    public SeriesStyleConfig styleFor(int seriesIndex) {
        if (styles == null || seriesIndex >= styles.size()) return null;
        return styles.get(seriesIndex);
    }

    /** Returns the explicit name for a series index, or null if not configured. */
    public String nameFor(int seriesIndex) {
        if (names == null || seriesIndex >= names.size()) return null;
        return names.get(seriesIndex);
    }

    /** Returns the data label config for a specific series (falls back to global). */
    public DataLabelConfig dataLabelFor(int seriesIndex) {
        if (perSeriesDataLabels != null && seriesIndex < perSeriesDataLabels.size()) {
            DataLabelConfig perSeries = perSeriesDataLabels.get(seriesIndex);
            if (perSeries != null) return perSeries;
        }
        return globalDataLabel;
    }

    public boolean isFirstRowIsHeader() {
        return firstRowIsHeader == null || firstRowIsHeader;
    }

    public boolean isFirstColumnIsCategory() {
        return firstColumnIsCategory == null || firstColumnIsCategory;
    }
}
