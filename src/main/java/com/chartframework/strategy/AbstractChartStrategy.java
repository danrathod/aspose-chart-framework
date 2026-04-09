package com.chartframework.strategy;

import com.aspose.cells.*;
import com.chartframework.config.*;
import com.chartframework.model.ChartConfig;
import com.chartframework.model.ChartRequest;
import com.chartframework.model.DataRange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Base strategy class providing full Aspose.Cells chart configuration coverage.
 *
 * <h2>Template Method Pattern</h2>
 * <p>The public entry point {@link #configure} orchestrates all configuration phases
 * in a fixed order. Subclasses implement only {@link #configureSeries}, which handles
 * chart-type-specific data wiring.</p>
 *
 * <h2>Configuration Phases</h2>
 * <ol>
 *   <li>Chart area and plot area (fill, border)</li>
 *   <li>Title</li>
 *   <li>Series (delegated to subclass)</li>
 *   <li>Category axis</li>
 *   <li>Value axis</li>
 *   <li>Secondary value axis</li>
 *   <li>Legend</li>
 *   <li>Global data labels</li>
 *   <li>3D settings</li>
 * </ol>
 *
 * <p>Every apply* method is guarded — unsupported configurations for a given chart
 * type are silently skipped (with a DEBUG log), never throwing to the caller.</p>
 */
public abstract class AbstractChartStrategy implements ChartStrategy {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    // ─────────────────────────────────────────────────────────────────────────
    // Entry point — template method
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    public final void configure(Chart chart, ChartRequest request, DataRange dataRange) {
        ChartConfig config = request.effectiveConfig();

        applyPlotArea(chart, config);
        applyTitle(chart, config);
        configureSeries(chart, request, dataRange);
        applyAxis(chart.getCategoryAxis(),   config.getCategoryAxis(),      "CategoryAxis");
        applyAxis(chart.getValueAxis(),      config.getValueAxis(),         "ValueAxis");
        applyAxis(chart.getSecondValueAxis(),config.getSecondaryValueAxis(),"SecondaryValueAxis");
        applyLegend(chart, config);
        applyGlobalDataLabels(chart, config);
        applyThreeD(chart, config);

        log.debug("[{}] configured — {} series", getClass().getSimpleName(),
                chart.getNSeries().getCount());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Abstract hook
    // ─────────────────────────────────────────────────────────────────────────

    protected abstract void configureSeries(Chart chart, ChartRequest request, DataRange dr);

    // ─────────────────────────────────────────────────────────────────────────
    // Series builder helpers (called by subclasses)
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Standard category-series layout — one category column, N value columns.
     * Applies names and styles from {@link SeriesConfig}.
     */
    protected void addStandardCategorySeries(Chart chart, ChartRequest request, DataRange dr) {
        ChartConfig config   = request.effectiveConfig();
        SeriesConfig sCfg    = config.getSeries();

        int firstDataRow   = dr.getStartRow()    + (config.isFirstRowIsHeader()       ? 1 : 0);
        int lastDataRow    = dr.getEndRow();
        int categoryCol    = dr.getStartColumn();
        int firstSeriesCol = dr.getStartColumn() + (config.isFirstColumnIsCategory()   ? 1 : 0);
        int lastSeriesCol  = dr.getEndColumn();
        int headerRow      = dr.getStartRow();

        SeriesCollection nSeries = chart.getNSeries();
        nSeries.setCategoryData(dr.toColumnRange(categoryCol, firstDataRow, lastDataRow));

        for (int col = firstSeriesCol; col <= lastSeriesCol; col++) {
            int    seriesIdx   = col - firstSeriesCol;
            String valuesRange = dr.toColumnRange(col, firstDataRow, lastDataRow);
            int    addedIdx    = nSeries.add(valuesRange, true);
            Series series      = nSeries.get(addedIdx);

            // Name resolution: SeriesConfig > header row > fallback
            series.setName(resolveSeriesName(sCfg, seriesIdx,
                    request.getData(), headerRow, col));

            // Per-series style
            applySeriesStyle(series, sCfg, seriesIdx);

            // Per-series data labels
            applySeriesDataLabels(series, config, seriesIdx);
        }
    }

    /** Scatter XY series — pairs of (X, Y) columns. */
    protected void addScatterSeries(Chart chart, ChartRequest request, DataRange dr) {
        ChartConfig  config      = request.effectiveConfig();
        SeriesConfig sCfg        = config.getSeries();
        int firstDataRow = dr.getStartRow() + (config.isFirstRowIsHeader() ? 1 : 0);
        int lastDataRow  = dr.getEndRow();
        int startCol     = dr.getStartColumn() + (config.isFirstColumnIsCategory() ? 1 : 0);

        SeriesCollection nSeries = chart.getNSeries();
        int idx = 0;
        for (int col = startCol; col + 1 <= dr.getEndColumn(); col += 2, idx++) {
            String xRange = dr.toColumnRange(col,     firstDataRow, lastDataRow);
            String yRange = dr.toColumnRange(col + 1, firstDataRow, lastDataRow);
            int    added  = nSeries.add(yRange, true);
            Series series = nSeries.get(added);
            series.setXValues(xRange);
            series.setName(resolveSeriesName(sCfg, idx, request.getData(),
                    dr.getStartRow(), col + 1));
            applySeriesStyle(series, sCfg, idx);
            applySeriesDataLabels(series, config, idx);
        }
    }

    /** Bubble series — triples of (X, Y, Size) columns. */
    protected void addBubbleSeries(Chart chart, ChartRequest request, DataRange dr) {
        ChartConfig  config      = request.effectiveConfig();
        SeriesConfig sCfg        = config.getSeries();
        int firstDataRow = dr.getStartRow() + (config.isFirstRowIsHeader() ? 1 : 0);
        int lastDataRow  = dr.getEndRow();
        int startCol     = dr.getStartColumn() + (config.isFirstColumnIsCategory() ? 1 : 0);

        SeriesCollection nSeries = chart.getNSeries();
        int idx = 0;
        for (int col = startCol; col + 1 <= dr.getEndColumn(); col += 3, idx++) {
            String xRange    = dr.toColumnRange(col,     firstDataRow, lastDataRow);
            String yRange    = dr.toColumnRange(col + 1, firstDataRow, lastDataRow);
            String sizeRange = (col + 2 <= dr.getEndColumn())
                    ? dr.toColumnRange(col + 2, firstDataRow, lastDataRow) : yRange;

            int    added  = nSeries.add(xRange, true);
            Series series = nSeries.get(added);
            series.setXValues(xRange);
            series.setValues(yRange);
            series.setBubbleSizes(sizeRange);
            series.setName(resolveSeriesName(sCfg, idx, request.getData(),
                    dr.getStartRow(), col));
            applySeriesStyle(series, sCfg, idx);

            // Bubble-specific scale
            ScatterBubbleConfig bc = config.getScatter();
            if (bc != null) {
                try { series.getBubbleScale(); } catch (Exception ignored) {}
            }
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Phase appliers — called from configure()
    // ─────────────────────────────────────────────────────────────────────────

    private void applyPlotArea(Chart chart, ChartConfig config) {
        PlotAreaConfig pa = config.getPlotArea();
        if (pa == null) return;
        try {
            // Chart area (outer frame)
            if (pa.getChartAreaFill() != null) {
                applyFillToArea(chart.getChartObject().getArea(), pa.getChartAreaFill());
            }
            if (pa.getChartAreaBorder() != null) {
                applyBorderToLine(chart.getChartObject().getBorder(), pa.getChartAreaBorder());
            }
            if (Boolean.TRUE.equals(pa.getRoundedCorners())) {
                chart.getChartObject().setRoundedCorners(true);
            }
            // Plot area (inner data region)
            if (pa.getPlotAreaFill() != null) {
                applyFillToArea(chart.getPlotArea().getArea(), pa.getPlotAreaFill());
            }
            if (pa.getPlotAreaBorder() != null) {
                applyBorderToLine(chart.getPlotArea().getBorder(), pa.getPlotAreaBorder());
            }
        } catch (Exception e) {
            log.debug("PlotArea config skipped: {}", e.getMessage());
        }
    }

    private void applyTitle(Chart chart, ChartConfig config) {
        TitleConfig tc = config.getTitle();
        if (tc == null || !tc.isVisible()) {
            chart.setShowTitle(false);
            return;
        }
        chart.setShowTitle(true);
        Title t = chart.getTitle();
        t.setText(tc.getText());
        applyFontToText(t.getFont(), tc.getFontName(), tc.getFontSize(),
                tc.getBold(), tc.getItalic(), tc.getFontColor());
    }

    private void applyAxis(Axis axis, AxisConfig cfg, String axisName) {
        if (axis == null || cfg == null) return;
        try {
            axis.setVisible(cfg.isVisible() ? 1 : 0);
            if (!cfg.isVisible()) return;

            // Title
            if (cfg.getTitle() != null && cfg.getTitle().isVisible()) {
                axis.getTitle().setText(cfg.getTitle().getText());
                axis.getTitle().setVisible(true);
                applyFontToText(axis.getTitle().getFont(),
                        cfg.getTitle().getFontName(), cfg.getTitle().getFontSize(),
                        cfg.getTitle().getBold(), cfg.getTitle().getItalic(),
                        cfg.getTitle().getFontColor());
            }

            // Scale
            if (cfg.getMinValue()   != null) axis.setMinValue(cfg.getMinValue());
            if (cfg.getMaxValue()   != null) axis.setMaxValue(cfg.getMaxValue());
            if (cfg.getMajorUnit()  != null) axis.setMajorUnit(cfg.getMajorUnit());
            if (cfg.getMinorUnit()  != null) axis.setMinorUnit(cfg.getMinorUnit());
            if (Boolean.TRUE.equals(cfg.getReversed())) axis.setPlotOrderReversed(true);
            if (Boolean.TRUE.equals(cfg.getLogScale())) {
                axis.setLogScale(true);
                if (cfg.getLogBase() != null) axis.setLogBase(cfg.getLogBase());
            }

            // Tick labels
            if (cfg.getTickLabelFont() != null) {
                applyFontToText(axis.getTickLabels().getFont(),
                        cfg.getTickLabelFont().getName(), cfg.getTickLabelFont().getSize(),
                        cfg.getTickLabelFont().getBold(), cfg.getTickLabelFont().getItalic(),
                        cfg.getTickLabelFont().getColor());
            }
            if (cfg.getTickLabelRotation() != null) {
                axis.getTickLabels().setRotationAngle(cfg.getTickLabelRotation());
            }
            if (cfg.getNumberFormat() != null && !cfg.getNumberFormat().isBlank()) {
                axis.getTickLabels().setNumberFormat(cfg.getNumberFormat());
                axis.getTickLabels().setLinkedToSource(false);
            }
            if (cfg.getTickLabelSpacing() != null) {
                axis.setTickLabelSpacing(cfg.getTickLabelSpacing());
            }

            // Gridlines
            applyGridline(axis.getMajorGridLines(), cfg.getMajorGridlines());
            applyGridline(axis.getMinorGridLines(), cfg.getMinorGridlines());

            // Axis line
            if (cfg.getAxisLine() != null) {
                applyBorderToLine(axis.getAxisLine(), cfg.getAxisLine());
            }

            // Crossing
            if (cfg.getCrossesAt() != null) {
                axis.setCrossAt(cfg.getCrossesAt());
            }
        } catch (Exception e) {
            log.debug("{} config partially skipped for this chart type: {}", axisName, e.getMessage());
        }
    }

    private void applyGridline(Line gridLine, GridlineConfig cfg) {
        if (gridLine == null || cfg == null) return;
        try {
            gridLine.setVisible(cfg.isVisible());
            if (!cfg.isVisible()) return;
            if (cfg.getColor() != null) {
                gridLine.setForegroundColor(parseColor(cfg.getColor()));
            }
            if (cfg.getWidthPt() != null) {
                gridLine.setWeight(cfg.getWidthPt().floatValue());
            }
            if (cfg.getLineStyle() != null) {
                gridLine.setStyle(resolveLineStyle(cfg.getLineStyle()));
            }
        } catch (Exception e) {
            log.debug("Gridline config skipped: {}", e.getMessage());
        }
    }

    private void applyLegend(Chart chart, ChartConfig config) {
        LegendConfig lc = config.getLegend();
        if (lc == null) {
            // Default: show legend at bottom
            chart.setShowLegend(true);
            chart.getLegend().setPosition(LegendPositionType.BOTTOM);
            return;
        }
        chart.setShowLegend(lc.isVisible());
        if (!lc.isVisible()) return;

        Legend legend = chart.getLegend();
        legend.setPosition(resolveLegendPosition(lc.getPosition()));
        if (Boolean.TRUE.equals(lc.getOverlay())) legend.setOverlay(true);
        if (lc.getFont() != null) {
            applyFontToText(legend.getFont(),
                    lc.getFont().getName(), lc.getFont().getSize(),
                    lc.getFont().getBold(), lc.getFont().getItalic(),
                    lc.getFont().getColor());
        }
        if (lc.getBackground() != null) {
            applyFillToArea(legend.getArea(), lc.getBackground());
        }
        if (lc.getBorder() != null) {
            applyBorderToLine(legend.getBorder(), lc.getBorder());
        }
    }

    private void applyGlobalDataLabels(Chart chart, ChartConfig config) {
        DataLabelConfig dlc = config.getDataLabel();
        if (dlc == null || !dlc.isVisible()) return;
        for (int i = 0; i < chart.getNSeries().getCount(); i++) {
            applyDataLabelsToSeries(chart.getNSeries().get(i), dlc);
        }
    }

    private void applyThreeD(Chart chart, ChartConfig config) {
        ThreeDConfig tc = config.getThreeD();
        if (tc == null) return;
        try {
            if (tc.getRotationX()    != null) chart.setRotationAngle(tc.getRotationX());
            if (tc.getRotationY()    != null) chart.setElevation(tc.getRotationY());
            if (tc.getPerspective()  != null) chart.setPerspective((short)(int)tc.getPerspective());
            if (tc.getHeightPercent()!= null) chart.setHeightPercent(tc.getHeightPercent());
            if (tc.getDepthPercent() != null) chart.setDepthPercent(tc.getDepthPercent());
            if (Boolean.TRUE.equals(tc.getRightAngleAxes())) chart.setRightAngleAxes(true);
        } catch (Exception e) {
            log.debug("3D config skipped for this chart type: {}", e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Per-series helpers
    // ─────────────────────────────────────────────────────────────────────────

    protected void applySeriesStyle(Series series, SeriesConfig sCfg, int seriesIdx) {
        if (sCfg == null) return;
        SeriesStyleConfig style = sCfg.styleFor(seriesIdx);
        if (style == null) return;

        try {
            // Fill
            if (style.getFillColor() != null) {
                series.getArea().setForegroundColor(parseColor(style.getFillColor()));
                series.getArea().setBackgroundColor(parseColor(style.getFillColor()));
                if (style.getFillOpacity() != null) {
                    series.getArea().setTransparency(1.0 - style.getFillOpacity());
                }
            }

            // Line / border
            if (style.getLineColor() != null) {
                series.getBorder().setForegroundColor(parseColor(style.getLineColor()));
                series.getBorder().setVisible(true);
            }
            if (style.getLineWidthPt() != null) {
                series.getBorder().setWeight(style.getLineWidthPt().floatValue());
            }
            if (style.getLineDashStyle() != null) {
                series.getBorder().setStyle(resolveLineStyle(style.getLineDashStyle()));
            }

            // Smooth
            if (Boolean.TRUE.equals(style.getSmooth())) {
                series.setSmooth(true);
            }

            // Marker
            applyMarker(series, style.getMarker());

        } catch (Exception e) {
            log.debug("Series[{}] style partially skipped: {}", seriesIdx, e.getMessage());
        }
    }

    private void applyMarker(Series series, MarkerConfig mc) {
        if (mc == null) return;
        try {
            Marker marker = series.getMarker();
            if (mc.getStyle() != null) {
                marker.setMarkerStyle(resolveMarkerStyle(mc.getStyle()));
            }
            if (mc.getSize() != null) {
                marker.setMarkerSize(mc.getSize());
            }
            if (mc.getForegroundColor() != null) {
                marker.setForegroundColor(parseColor(mc.getForegroundColor()));
            }
            if (mc.getBackgroundColor() != null) {
                marker.setBackgroundColor(parseColor(mc.getBackgroundColor()));
            }
        } catch (Exception e) {
            log.debug("Marker config skipped: {}", e.getMessage());
        }
    }

    protected void applySeriesDataLabels(Series series, ChartConfig config, int seriesIdx) {
        SeriesConfig sCfg = config.getSeries();
        DataLabelConfig dlc = (sCfg != null)
                ? sCfg.dataLabelFor(seriesIdx)
                : config.getDataLabel();
        if (dlc == null || !dlc.isVisible()) return;
        applyDataLabelsToSeries(series, dlc);
    }

    private void applyDataLabelsToSeries(Series series, DataLabelConfig dlc) {
        try {
            DataLabels labels = series.getDataLabels();
            labels.setShowValue(          Boolean.TRUE.equals(dlc.getShowValue()));
            labels.setShowPercentage(     Boolean.TRUE.equals(dlc.getShowPercentage()));
            labels.setShowCategoryName(   Boolean.TRUE.equals(dlc.getShowCategoryName()));
            labels.setShowSeriesName(     Boolean.TRUE.equals(dlc.getShowSeriesName()));
            labels.setShowBubbleSize(     Boolean.TRUE.equals(dlc.getShowBubbleSize()));
            if (dlc.getSeparator() != null) {
                labels.setSeparator(dlc.getSeparator());
            }
            if (dlc.getPosition() != null) {
                labels.setPosition(resolveLabelPosition(dlc.getPosition()));
            }
            if (dlc.getNumberFormat() != null && !dlc.getNumberFormat().isBlank()) {
                labels.setNumberFormat(dlc.getNumberFormat());
                labels.setLinkedToSource(false);
            }
            if (dlc.getFont() != null) {
                applyFontToText(labels.getFont(),
                        dlc.getFont().getName(), dlc.getFont().getSize(),
                        dlc.getFont().getBold(), dlc.getFont().getItalic(),
                        dlc.getFont().getColor());
            }
            if (dlc.getBackground() != null) {
                applyFillToArea(labels.getArea(), dlc.getBackground());
            }
            if (dlc.getBorder() != null) {
                applyBorderToLine(labels.getBorder(), dlc.getBorder());
            }
        } catch (Exception e) {
            log.debug("DataLabel config skipped: {}", e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Low-level Aspose helpers
    // ─────────────────────────────────────────────────────────────────────────

    protected void applyFillToArea(Area area, FillConfig fill) {
        if (area == null || fill == null) return;
        try {
            switch (fill.getFillType() != null ? fill.getFillType() : FillConfig.FillType.SOLID) {
                case NONE -> {
                    area.setForegroundColor(java.awt.Color.WHITE);
                    area.setTransparency(1.0); // fully transparent
                }
                case SOLID -> {
                    if (fill.getSolidColor() != null) {
                        area.setForegroundColor(parseColor(fill.getSolidColor()));
                        area.setBackgroundColor(parseColor(fill.getSolidColor()));
                    }
                    if (fill.getOpacity() != null) {
                        area.setTransparency(1.0 - fill.getOpacity());
                    }
                }
                case GRADIENT -> {
                    // Gradient requires FillFormat — fall back to solid primary color
                    if (fill.getSolidColor() != null) {
                        area.setForegroundColor(parseColor(fill.getSolidColor()));
                    }
                }
                default -> {} // PATTERN — not applied via Area API
            }
        } catch (Exception e) {
            log.debug("Fill config skipped: {}", e.getMessage());
        }
    }

    protected void applyBorderToLine(Line line, BorderConfig border) {
        if (line == null || border == null) return;
        try {
            if (Boolean.FALSE.equals(border.getVisible())
                    || border.getLineStyle() == BorderConfig.LineStyle.NONE) {
                line.setVisible(false);
                return;
            }
            line.setVisible(true);
            if (border.getColor() != null) {
                line.setForegroundColor(parseColor(border.getColor()));
            }
            if (border.getWidthPt() != null) {
                line.setWeight(border.getWidthPt().floatValue());
            }
            if (border.getLineStyle() != null) {
                line.setStyle(resolveLineStyle(border.getLineStyle()));
            }
        } catch (Exception e) {
            log.debug("Border config skipped: {}", e.getMessage());
        }
    }

    private void applyFontToText(Font font, String name, Integer size,
                                  Boolean bold, Boolean italic, String colorHex) {
        if (font == null) return;
        try {
            if (name  != null) font.setName(name);
            if (size  != null) font.setSize(size);
            if (bold  != null) font.setBold(bold);
            if (italic!= null) font.setItalic(italic);
            if (colorHex != null) font.setColor(parseColor(colorHex));
        } catch (Exception e) {
            log.debug("Font config skipped: {}", e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Name resolution
    // ─────────────────────────────────────────────────────────────────────────

    protected String resolveSeriesName(SeriesConfig sCfg, int seriesIdx,
                                       List<List<Object>> data, int headerRow, int col) {
        if (sCfg != null) {
            String explicit = sCfg.nameFor(seriesIdx);
            if (explicit != null) return explicit;
        }
        if (!data.isEmpty() && headerRow < data.size()) {
            List<Object> header = data.get(headerRow);
            if (header != null && col < header.size() && header.get(col) != null) {
                return header.get(col).toString();
            }
        }
        return "Series " + (seriesIdx + 1);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Enum / constant resolvers
    // ─────────────────────────────────────────────────────────────────────────

    private int resolveLegendPosition(LegendConfig.Position pos) {
        if (pos == null) return LegendPositionType.BOTTOM;
        return switch (pos) {
            case TOP    -> LegendPositionType.TOP;
            case LEFT   -> LegendPositionType.LEFT;
            case RIGHT  -> LegendPositionType.RIGHT;
            case CORNER -> LegendPositionType.CORNER;
            default     -> LegendPositionType.BOTTOM;
        };
    }

    private int resolveLabelPosition(DataLabelConfig.Position pos) {
        if (pos == null) return LabelPositionType.AUTO;
        return switch (pos) {
            case ABOVE        -> LabelPositionType.ABOVE;
            case BELOW        -> LabelPositionType.BELOW;
            case CENTER       -> LabelPositionType.CENTER;
            case INSIDE_BASE  -> LabelPositionType.INSIDE_BASE;
            case INSIDE_END   -> LabelPositionType.INSIDE_END;
            case OUTSIDE_END  -> LabelPositionType.OUTSIDE_END;
            case LEFT         -> LabelPositionType.LEFT;
            case RIGHT        -> LabelPositionType.RIGHT;
            default           -> LabelPositionType.AUTO;
        };
    }

    private int resolveLineStyle(BorderConfig.LineStyle style) {
        if (style == null) return LineType.SOLID;
        return switch (style) {
            case DASH         -> LineType.DASH;
            case DOT          -> LineType.DOT;
            case DASH_DOT     -> LineType.DASH_DOT;
            case DASH_DOT_DOT -> LineType.DASH_DOT_DOT;
            case NONE         -> LineType.NONE;
            default           -> LineType.SOLID;
        };
    }

    private int resolveMarkerStyle(MarkerConfig.MarkerStyle style) {
        if (style == null) return ChartMarkerType.AUTOMATIC;
        return switch (style) {
            case NONE      -> ChartMarkerType.NONE;
            case SQUARE    -> ChartMarkerType.SQUARE;
            case DIAMOND   -> ChartMarkerType.DIAMOND;
            case TRIANGLE  -> ChartMarkerType.TRIANGLE;
            case X         -> ChartMarkerType.X;
            case STAR      -> ChartMarkerType.STAR;
            case CIRCLE    -> ChartMarkerType.CIRCLE;
            case PLUS      -> ChartMarkerType.PLUS;
            case DASH      -> ChartMarkerType.DASH;
            default        -> ChartMarkerType.AUTOMATIC;
        };
    }

    /** Parses a hex colour string like {@code "#1565C0"} or {@code "1565C0"}. */
    protected java.awt.Color parseColor(String hex) {
        if (hex == null) return java.awt.Color.BLACK;
        String clean = hex.startsWith("#") ? hex.substring(1) : hex;
        try {
            int r = Integer.parseInt(clean.substring(0, 2), 16);
            int g = Integer.parseInt(clean.substring(2, 4), 16);
            int b = Integer.parseInt(clean.substring(4, 6), 16);
            return new java.awt.Color(r, g, b);
        } catch (Exception e) {
            log.debug("Could not parse colour '{}', using black.", hex);
            return java.awt.Color.BLACK;
        }
    }
}
