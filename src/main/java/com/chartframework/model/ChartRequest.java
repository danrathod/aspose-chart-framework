package com.chartframework.model;

import com.chartframework.enums.ExcelChartType;
import lombok.Builder;
import lombok.Value;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Chart-specific request DTO — describes a single chart to be generated.
 * Always contained inside a {@link ChartBatchRequest}.
 */
@Value
@Builder
public class ChartRequest {

    String         targetSheetName;
    ExcelChartType chartType;
    ChartPlacement placement;
    List<List<Object>> data;
    ChartConfig    config;

    public ChartConfig effectiveConfig() {
        return config != null ? config : ChartConfig.withDefaults();
    }

    public String deriveLabel() {
        ChartConfig cfg = effectiveConfig();
        String titleText = cfg.getChartTitle();
        if (titleText != null && !titleText.isBlank()) {
            return "■ " + titleText + "  [" + chartType.getDisplayName() + "]";
        }
        return "■ " + chartType.getDisplayName();
    }

    public static List<List<Object>> toListData(Object[][] raw) {
        if (raw == null) return List.of();
        List<List<Object>> result = new ArrayList<>(raw.length);
        for (Object[] row : raw) {
            result.add(row != null ? Arrays.asList(row) : List.of());
        }
        return result;
    }
}
