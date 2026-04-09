package com.chartframework.validator;

import com.chartframework.enums.ExcelChartType;
import com.chartframework.exception.ChartValidationException;
import com.chartframework.model.ChartBatchRequest;
import com.chartframework.model.ChartConfig;
import com.chartframework.model.ChartPlacement;
import com.chartframework.model.ChartRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ChartRequestValidator")
class ChartRequestValidatorTest {

    private ChartRequestValidator validator;

    @BeforeEach
    void setUp() {
        validator = new ChartRequestValidator();
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private List<List<Object>> validData() {
        return List.of(
                List.of("Month", "Sales", "Profit"),
                List.of("Jan",    12000,    3000),
                List.of("Feb",    15000,    4500)
        );
    }

    private ChartRequest validChart() {
        return ChartRequest.builder()
                .targetSheetName("Dashboard")
                .chartType(ExcelChartType.COLUMN)
                .placement(ChartPlacement.of(2, 0, 20, 8))
                .data(validData())
                .build();
    }

    private ChartBatchRequest.ChartBatchRequestBuilder validBatchBuilder() {
        return ChartBatchRequest.builder()
                .inputFilePath("reports/dashboard.xlsx")
                .charts(List.of(validChart()));
    }

    // ── Valid batch ───────────────────────────────────────────────────────────

    @Nested @DisplayName("Valid batches")
    class ValidBatches {

        @Test @DisplayName("single chart batch passes")
        void singleChart_passes() {
            assertDoesNotThrow(() -> validator.validate(validBatchBuilder().build()));
        }

        @Test @DisplayName("multiple charts in same batch pass")
        void multipleCharts_passes() {
            ChartBatchRequest batch = validBatchBuilder()
                    .charts(List.of(validChart(), validChart()))
                    .build();
            assertDoesNotThrow(() -> validator.validate(batch));
        }

        @Test @DisplayName("null outputFilePath is allowed")
        void nullOutputPath_passes() {
            assertDoesNotThrow(() -> validator.validate(
                    validBatchBuilder().outputFilePath(null).build()));
        }
    }

    // ── Batch-level violations ────────────────────────────────────────────────

    @Nested @DisplayName("Batch-level violations")
    class BatchViolations {

        @Test @DisplayName("null batch throws immediately")
        void nullBatch_throws() {
            assertThrows(ChartValidationException.class, () -> validator.validate(null));
        }

        @Test @DisplayName("null inputFilePath fails")
        void nullFilePath_fails() {
            assertThrows(ChartValidationException.class, () ->
                    validator.validate(validBatchBuilder().inputFilePath(null).build()));
        }

        @Test @DisplayName("blank inputFilePath fails")
        void blankFilePath_fails() {
            assertThrows(ChartValidationException.class, () ->
                    validator.validate(validBatchBuilder().inputFilePath("   ").build()));
        }

        @Test @DisplayName("null charts list fails")
        void nullCharts_fails() {
            assertThrows(ChartValidationException.class, () ->
                    validator.validate(validBatchBuilder().charts(null).build()));
        }

        @Test @DisplayName("empty charts list fails")
        void emptyCharts_fails() {
            assertThrows(ChartValidationException.class, () ->
                    validator.validate(validBatchBuilder().charts(List.of()).build()));
        }
    }

    // ── Per-chart violations ──────────────────────────────────────────────────

    @Nested @DisplayName("Per-chart violations")
    class PerChartViolations {

        @Test @DisplayName("null chart in list fails with index in message")
        void nullChart_failsWithIndex() {
            ChartBatchRequest batch = validBatchBuilder()
                    .charts(List.of(validChart(), null))
                    .build();
            ChartValidationException ex =
                    assertThrows(ChartValidationException.class, () -> validator.validate(batch));
            assertTrue(ex.getMessage().contains("charts[1]"),
                    "Error should reference chart index 1");
        }

        @Test @DisplayName("blank targetSheetName fails with index")
        void blankSheetName_failsWithIndex() {
            ChartRequest bad = ChartRequest.builder()
                    .targetSheetName("  ")
                    .chartType(ExcelChartType.COLUMN)
                    .placement(ChartPlacement.of(0, 0, 10, 5))
                    .data(validData())
                    .build();
            ChartBatchRequest batch = validBatchBuilder().charts(List.of(bad)).build();
            ChartValidationException ex =
                    assertThrows(ChartValidationException.class, () -> validator.validate(batch));
            assertTrue(ex.getMessage().contains("charts[0]"));
            assertTrue(ex.getMessage().contains("targetSheetName"));
        }

        @Test @DisplayName("invalid placement fails with index")
        void badPlacement_failsWithIndex() {
            ChartRequest bad = ChartRequest.builder()
                    .targetSheetName("Sheet1")
                    .chartType(ExcelChartType.PIE)
                    .placement(ChartPlacement.of(10, 0, 5, 8)) // endRow < startRow
                    .data(validData())
                    .build();
            ChartBatchRequest batch = validBatchBuilder().charts(List.of(bad)).build();
            ChartValidationException ex =
                    assertThrows(ChartValidationException.class, () -> validator.validate(batch));
            assertTrue(ex.getMessage().contains("charts[0]"));
            assertTrue(ex.getMessage().contains("endRow"));
        }

        @Test @DisplayName("errors from multiple charts all reported in one throw")
        void multipleChartErrors_allReported() {
            ChartRequest bad1 = ChartRequest.builder()
                    .targetSheetName("")         // invalid
                    .chartType(ExcelChartType.LINE)
                    .placement(ChartPlacement.of(0, 0, 10, 5))
                    .data(validData())
                    .build();
            ChartRequest bad2 = ChartRequest.builder()
                    .targetSheetName("Sheet2")
                    .chartType(null)             // invalid
                    .placement(ChartPlacement.of(0, 0, 10, 5))
                    .data(validData())
                    .build();
            ChartBatchRequest batch = validBatchBuilder()
                    .charts(List.of(bad1, bad2)).build();
            ChartValidationException ex =
                    assertThrows(ChartValidationException.class, () -> validator.validate(batch));
            assertTrue(ex.getMessage().contains("charts[0]"));
            assertTrue(ex.getMessage().contains("charts[1]"));
        }
    }
}
