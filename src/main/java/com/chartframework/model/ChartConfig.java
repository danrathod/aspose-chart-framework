package com.chartframework.model;

import com.chartframework.config.*;
import lombok.Builder;
import lombok.Value;

/**
 * Root chart configuration DTO — composed of modular sub-configs, one per
 * concern area. Every field is optional; strategies apply only what is set.
 *
 * <h2>Design: Composition over Inheritance</h2>
 * <p>{@code ChartConfig} is a pure composition root. It holds no raw styling
 * strings itself — all configuration is delegated to focused sub-config
 * classes in the {@code com.chartframework.config} package:</p>
 *
 * <pre>
 *  ChartConfig
 *  ├── TitleConfig          title, subtitle
 *  ├── LegendConfig         visibility, position, font, fill, border
 *  ├── AxisConfig           categoryAxis, valueAxis, secondaryValueAxis
 *  │     └── GridlineConfig   major / minor gridlines
 *  ├── DataLabelConfig      global data label defaults
 *  ├── SeriesConfig         names, per-series styles, per-series labels
 *  │     ├── SeriesStyleConfig  fill, line, smooth, marker
 *  │     └── MarkerConfig       shape, size, colors
 *  ├── PlotAreaConfig       chart area and plot area fill / border
 *  ├── ThreeDConfig         rotation, perspective, depth (3D charts)
 *  └── [Chart-type specific extensions]
 *        ├── PieChartConfig       explosion, holeSize, firstSliceAngle
 *        ├── BarColumnConfig      gapWidth, overlap, shape
 *        ├── LineChartConfig      smooth, dropLines, highLowLines
 *        ├── ScatterBubbleConfig  bubbleScale, sizeRepresentation
 *        └── RadarChartConfig     radarStyle, fillOpacity
 * </pre>
 *
 * <h2>Usage Examples</h2>
 *
 * <b>Minimal (title only):</b>
 * <pre>{@code
 * ChartConfig.builder()
 *     .title(TitleConfig.of("Monthly Sales"))
 *     .build()
 * }</pre>
 *
 * <b>Column chart with full axis config:</b>
 * <pre>{@code
 * ChartConfig.builder()
 *     .title(TitleConfig.bold("Q1 Sales Performance", 14))
 *     .legend(LegendConfig.at(LegendConfig.Position.BOTTOM))
 *     .categoryAxis(AxisConfig.withTitle("Month"))
 *     .valueAxis(AxisConfig.builder()
 *         .title(TitleConfig.of("Revenue (USD)"))
 *         .numberFormat("$#,##0")
 *         .minValue(0.0)
 *         .majorGridlines(GridlineConfig.solid("#E0E0E0", 0.5))
 *         .build())
 *     .series(SeriesConfig.builder()
 *         .name("Revenue").name("Cost").name("Profit")
 *         .style(SeriesStyleConfig.solidColor("#1565C0"))
 *         .style(SeriesStyleConfig.solidColor("#E65100"))
 *         .style(SeriesStyleConfig.solidColor("#2E7D32"))
 *         .build())
 *     .barColumn(BarColumnConfig.builder().gapWidth(120).build())
 *     .dataLabel(DataLabelConfig.hidden())
 *     .plotArea(PlotAreaConfig.clean())
 *     .build()
 * }</pre>
 *
 * <b>Pie chart:</b>
 * <pre>{@code
 * ChartConfig.builder()
 *     .title(TitleConfig.of("Regional Revenue"))
 *     .legend(LegendConfig.at(LegendConfig.Position.RIGHT))
 *     .pie(PieChartConfig.builder()
 *         .firstSliceAngle(90)
 *         .explodeAllSlices(5)
 *         .dataLabels(DataLabelConfig.valueAndPercent())
 *         .build())
 *     .build()
 * }</pre>
 *
 * <b>Scatter chart:</b>
 * <pre>{@code
 * ChartConfig.builder()
 *     .title(TitleConfig.of("Cost vs Revenue"))
 *     .categoryAxis(AxisConfig.builder()
 *         .title(TitleConfig.of("Cost"))
 *         .numberFormat("$#,##0")
 *         .build())
 *     .valueAxis(AxisConfig.builder()
 *         .title(TitleConfig.of("Revenue"))
 *         .numberFormat("$#,##0")
 *         .build())
 *     .series(SeriesConfig.builder()
 *         .style(SeriesStyleConfig.builder()
 *             .marker(MarkerConfig.circle(8, "#1565C0"))
 *             .build())
 *         .build())
 *     .scatter(ScatterBubbleConfig.defaults())
 *     .build()
 * }</pre>
 */
@Value
@Builder
public class ChartConfig {

    // ── Universal visual elements ─────────────────────────────────────────────

    /** Main chart title configuration. Null = no title. */
    TitleConfig title;

    /** Legend configuration. Null = shown at bottom with defaults. */
    LegendConfig legend;

    /** Global data label defaults applied to all series. Null = no labels. */
    DataLabelConfig dataLabel;

    /** Series-level names, styles, and per-series label overrides. */
    SeriesConfig series;

    /** Chart area and plot area fill/border styling. */
    PlotAreaConfig plotArea;

    // ── Axes ─────────────────────────────────────────────────────────────────

    /** Category (X) axis configuration. */
    AxisConfig categoryAxis;

    /** Primary value (Y) axis configuration. */
    AxisConfig valueAxis;

    /** Secondary value axis (right Y axis — combo / dual-axis charts). */
    AxisConfig secondaryValueAxis;

    // ── 3D ───────────────────────────────────────────────────────────────────

    /** 3D rotation and perspective settings. Null = Aspose defaults. */
    ThreeDConfig threeD;

    // ── Data layout flags ─────────────────────────────────────────────────────

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

    // ── Chart-type specific extensions ────────────────────────────────────────

    /** Pie / Doughnut specific settings. Null = Aspose defaults. */
    PieChartConfig pie;

    /** Bar / Column specific settings. Null = Aspose defaults. */
    BarColumnConfig barColumn;

    /** Line chart specific settings. Null = Aspose defaults. */
    LineChartConfig line;

    /** Scatter / Bubble chart specific settings. Null = Aspose defaults. */
    ScatterBubbleConfig scatter;

    /** Radar chart specific settings. Null = Aspose defaults. */
    RadarChartConfig radar;

    // ── Convenience accessors with defaults ───────────────────────────────────

    public boolean isFirstRowIsHeader() {
        // Also delegate to SeriesConfig if set (it takes precedence)
        if (series != null) return series.isFirstRowIsHeader();
        return firstRowIsHeader == null || firstRowIsHeader;
    }

    public boolean isFirstColumnIsCategory() {
        if (series != null) return series.isFirstColumnIsCategory();
        return firstColumnIsCategory == null || firstColumnIsCategory;
    }

    /** Convenience: get chart title text, or null. */
    public String getChartTitle() {
        return title != null ? title.getText() : null;
    }

    // ── Static factory: sensible defaults ─────────────────────────────────────

    /**
     * Returns a minimal ChartConfig with legend shown at bottom,
     * no data labels, default gridlines.
     */
    public static ChartConfig withDefaults() {
        return ChartConfig.builder()
                .legend(LegendConfig.at(LegendConfig.Position.BOTTOM))
                .dataLabel(DataLabelConfig.hidden())
                .build();
    }
}
