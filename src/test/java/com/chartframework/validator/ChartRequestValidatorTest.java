package com.chartframework.validator;

import com.chartframework.enums.ExcelChartType;
import com.chartframework.exception.ChartValidationException;
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

    private ChartRequest.ChartRequestBuilder validBuilder() {
        return ChartRequest.builder()
                .inputFilePath("reports/dashboard.xlsx")
                .targetSheetName("Dashboard")
                .chartType(ExcelChartType.COLUMN)
                .placement(ChartPlacement.of(2, 0, 20, 8))
                .data(validData());
    }

    // ── Valid requests ────────────────────────────────────────────────────────

    @Nested
    @DisplayName("Valid requests")
    class ValidRequests {

        @Test @DisplayName("full request passes")
        void fullRequest_passes() {
            assertDoesNotThrow(() -> validator.validate(validBuilder().build()));
        }

        @Test @DisplayName("null config uses defaults — passes")
        void nullConfig_passes() {
            assertDoesNotThrow(() -> validator.validate(validBuilder().config(null).build()));
        }

        @Test @DisplayName("null outputFilePath is allowed (defaults to inputFilePath)")
        void nullOutputPath_passes() {
            assertDoesNotThrow(() -> validator.validate(
                    validBuilder().outputFilePath(null).build()));
        }
    }

    // ── Required field violations ─────────────────────────────────────────────

    @Nested
    @DisplayName("Required field violations")
    class RequiredViolations {

        @Test @DisplayName("null request throws immediately")
        void nullRequest_throws() {
            assertThrows(ChartValidationException.class, () -> validator.validate(null));
        }

        @Test @DisplayName("null inputFilePath fails")
        void nullFilePath_fails() {
            ChartRequest req = validBuilder().inputFilePath(null).build();
            assertThrows(ChartValidationException.class, () -> validator.validate(req));
        }

        @Test @DisplayName("blank inputFilePath fails")
        void blankFilePath_fails() {
            ChartRequest req = validBuilder().inputFilePath("   ").build();
            assertThrows(ChartValidationException.class, () -> validator.validate(req));
        }

        @Test @DisplayName("blank targetSheetName fails")
        void blankSheetName_fails() {
            assertThrows(ChartValidationException.class,
                    () -> validator.validate(validBuilder().targetSheetName("  ").build()));
        }

        @Test @DisplayName("null chartType fails")
        void nullChartType_fails() {
            assertThrows(ChartValidationException.class,
                    () -> validator.validate(validBuilder().chartType(null).build()));
        }

        @Test @DisplayName("null placement fails")
        void nullPlacement_fails() {
            assertThrows(ChartValidationException.class,
                    () -> validator.validate(validBuilder().placement(null).build()));
        }

        @Test @DisplayName("null data fails")
        void nullData_fails() {
            assertThrows(ChartValidationException.class,
                    () -> validator.validate(validBuilder().data(null).build()));
        }
    }

    // ── Placement violations ──────────────────────────────────────────────────

    @Nested
    @DisplayName("Placement violations")
    class PlacementViolations {

        @Test @DisplayName("endRow <= startRow fails")
        void endRowNotGreater_fails() {
            assertThrows(ChartValidationException.class, () -> validator.validate(
                    validBuilder().placement(ChartPlacement.of(10, 0, 10, 8)).build()));
        }

        @Test @DisplayName("endCol <= startCol fails")
        void endColNotGreater_fails() {
            assertThrows(ChartValidationException.class, () -> validator.validate(
                    validBuilder().placement(ChartPlacement.of(2, 5, 20, 5)).build()));
        }

        @Test @DisplayName("negative startRow fails")
        void negativeStartRow_fails() {
            assertThrows(ChartValidationException.class, () -> validator.validate(
                    validBuilder().placement(ChartPlacement.of(-1, 0, 20, 8)).build()));
        }
    }

    // ── Data violations ───────────────────────────────────────────────────────

    @Nested
    @DisplayName("Data violations")
    class DataViolations {

        @Test @DisplayName("only header row (no data rows) fails")
        void onlyHeader_fails() {
            assertThrows(ChartValidationException.class, () -> validator.validate(
                    validBuilder().data(List.of(List.of("Month", "Sales"))).build()));
        }

        @Test @DisplayName("single-column data fails")
        void singleColumn_fails() {
            assertThrows(ChartValidationException.class, () -> validator.validate(
                    validBuilder().data(List.of(
                            List.of("Month"), List.of("Jan"), List.of("Feb")
                    )).build()));
        }

        @Test @DisplayName("ragged rows fail")
        void raggedRows_fails() {
            assertThrows(ChartValidationException.class, () -> validator.validate(
                    validBuilder().data(List.of(
                            List.of("Month", "Sales", "Profit"),
                            List.of("Jan",    12000),          // missing Profit
                            List.of("Feb",    15000,   4500)
                    )).build()));
        }
    }

    // ── Error message quality ─────────────────────────────────────────────────

    @Nested
    @DisplayName("Error message quality")
    class ErrorMessages {

        @Test @DisplayName("multiple violations all reported in one throw")
        void multipleViolations_allReported() {
            ChartRequest req = ChartRequest.builder()
                    .inputFilePath(null)
                    .targetSheetName("")
                    .chartType(null)
                    .placement(null)
                    .data(null)
                    .build();

            ChartValidationException ex =
                    assertThrows(ChartValidationException.class, () -> validator.validate(req));

            String msg = ex.getMessage();
            assertTrue(msg.contains("inputFilePath"), "should mention inputFilePath");
            assertTrue(msg.contains("targetSheetName") || msg.contains("blank"),
                    "should mention sheet name");
            assertTrue(msg.contains("chartType"),  "should mention chartType");
            assertTrue(msg.contains("placement"),  "should mention placement");
            assertTrue(msg.contains("data"),       "should mention data");
        }
    }
}
