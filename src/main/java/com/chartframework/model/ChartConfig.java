package com.chartframework.model;

import lombok.Builder;
import lombok.Value;

import java.util.List;

/**
 * Immutable configuration DTO capturing the visual / metadata settings
 * of an Excel chart — title, axis labels, legend visibility, etc.
 *
 * <p>All fields are optional (nullable). The framework applies sensible
 * defaults for any field left {@code null}.</p>
 */
@Value
@Builder
public class ChartConfig {

    // ── Titles ────────────────────────────────────────────────────────────────

    /** Main chart title displayed above the chart area. */
    String chartTitle;

    /** Label for the category (X) axis. */
    String categoryAxisTitle;

    /** Label for the value (Y) axis. */
    String valueAxisTitle;

    /** Label for the secondary Y axis (combo/dual-axis charts). */
    String secondaryValueAxisTitle;

    // ── Legend ────────────────────────────────────────────────────────────────

    /**
     * Whether the legend is shown.
     * Defaults to {@code true} when {@code null}.
     */
    Boolean showLegend;

    /**
     * Legend position: "BOTTOM", "TOP", "LEFT", "RIGHT", "CORNER".
     * Defaults to "BOTTOM" when {@code null}.
     */
    String legendPosition;

    // ── Data Labels ───────────────────────────────────────────────────────────

    /** Show data labels on chart series. Defaults to {@code false} when {@code null}. */
    Boolean showDataLabels;

    // ── Axes ─────────────────────────────────────────────────────────────────

    /** Show gridlines on the value axis. Defaults to {@code true} when {@code null}. */
    Boolean showMajorGridlines;

    /** Rotate category axis labels by this many degrees (0 = horizontal). */
    Integer categoryAxisRotation;

    // ── Series Names ─────────────────────────────────────────────────────────

    /**
     * Optional explicit series names. When supplied, these override names
     * derived from the first row of the data table.
     */
    List<String> seriesNames;

    // ── Misc ─────────────────────────────────────────────────────────────────

    /**
     * Whether the first row of the data table contains header/series names.
     * Defaults to {@code true} when {@code null}.
     */
    Boolean firstRowIsHeader;

    /**
     * Whether the first column of the data table contains category labels.
     * Defaults to {@code true} when {@code null}.
     */
    Boolean firstColumnIsCategory;

    // ── Convenience defaults ──────────────────────────────────────────────────

    public boolean isShowLegend() {
        return showLegend == null || showLegend;
    }

    public boolean isShowDataLabels() {
        return showDataLabels != null && showDataLabels;
    }

    public boolean isShowMajorGridlines() {
        return showMajorGridlines == null || showMajorGridlines;
    }

    public boolean isFirstRowIsHeader() {
        return firstRowIsHeader == null || firstRowIsHeader;
    }

    public boolean isFirstColumnIsCategory() {
        return firstColumnIsCategory == null || firstColumnIsCategory;
    }

    public String getEffectiveLegendPosition() {
        return (legendPosition != null) ? legendPosition.toUpperCase() : "BOTTOM";
    }

    // ── Builder preset for a minimal "sensible defaults" config ──────────────

    /** Returns a ChartConfig with all defaults applied and no titles set. */
    public static ChartConfig withDefaults() {
        return ChartConfig.builder()
                .showLegend(true)
                .showDataLabels(false)
                .showMajorGridlines(true)
                .firstRowIsHeader(true)
                .firstColumnIsCategory(true)
                .legendPosition("BOTTOM")
                .build();
    }
}
