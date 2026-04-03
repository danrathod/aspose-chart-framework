package com.chartframework.validator;

import com.aspose.cells.Workbook;
import com.chartframework.enums.ExcelChartType;
import com.chartframework.exception.ChartValidationException;
import com.chartframework.model.ChartPlacement;
import com.chartframework.model.ChartRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link ChartRequestValidator}.
 *
 * All tests are pure Java — no Aspose licence required for the validator
 * itself (it only inspects the request fields; no Aspose APIs are called).
 */
@DisplayName("ChartRequestValidator")
class ChartRequestValidatorTest {

    private ChartRequestValidator validator;
    private Workbook mockWorkbook;

    @BeforeEach
    void setUp() throws Exception {
        validator    = new ChartRequestValidator();
        mockWorkbook = new Workbook(); // Aspose creates a default workbook with "Sheet1"
    }

    // ── Helper: minimal valid request ────────────────────────────────────────

    private List<List<Object>> validData() {
        return List.of(
                List.of("Month", "Sales", "Profit"),
                List.of("Jan",    12000,    3000),
                List.of("Feb",    15000,    4500)
        );
    }

    private ChartRequest.ChartRequestBuilder validBuilder() {
        return ChartRequest.builder()
                .workbook(mockWorkbook)
                .targetSheetName("Sheet1")
                .chartType(ExcelChartType.COLUMN_CLUSTERED)
                .placement(ChartPlacement.of(2, 0, 20, 8))
                .data(validData());
    }

    // ─────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("Valid requests")
    class ValidRequests {

        @Test
        @DisplayName("should pass for a fully populated request")
        void fullRequest_passes() {
            assertDoesNotThrow(() -> validator.validate(validBuilder().build()));
        }

        @Test
        @DisplayName("should pass when config is null (uses defaults)")
        void nullConfig_passes() {
            ChartRequest req = validBuilder().config(null).build();
            assertDoesNotThrow(() -> validator.validate(req));
        }
    }

    // ─────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("Null / blank field violations")
    class NullViolations {

        @Test
        @DisplayName("null request → throws immediately")
        void nullRequest_throws() {
            assertThrows(ChartValidationException.class, () -> validator.validate(null));
        }

        @Test
        @DisplayName("null workbook → validation error")
        void nullWorkbook_fails() {
            ChartRequest req = validBuilder().workbook(null).build();
            assertThrows(ChartValidationException.class, () -> validator.validate(req));
        }

        @Test
        @DisplayName("blank targetSheetName → validation error")
        void blankSheetName_fails() {
            ChartRequest req = validBuilder().targetSheetName("  ").build();
            assertThrows(ChartValidationException.class, () -> validator.validate(req));
        }

        @Test
        @DisplayName("null chartType → validation error")
        void nullChartType_fails() {
            ChartRequest req = validBuilder().chartType(null).build();
            assertThrows(ChartValidationException.class, () -> validator.validate(req));
        }

        @Test
        @DisplayName("null placement → validation error")
        void nullPlacement_fails() {
            ChartRequest req = validBuilder().placement(null).build();
            assertThrows(ChartValidationException.class, () -> validator.validate(req));
        }

        @Test
        @DisplayName("null data → validation error")
        void nullData_fails() {
            ChartRequest req = validBuilder().data(null).build();
            assertThrows(ChartValidationException.class, () -> validator.validate(req));
        }
    }

    // ─────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("Placement coordinate violations")
    class PlacementViolations {

        @Test
        @DisplayName("endRow <= startRow → validation error")
        void endRowNotGreaterThanStart_fails() {
            ChartPlacement bad = ChartPlacement.of(10, 0, 10, 8); // endRow == startRow
            ChartRequest req = validBuilder().placement(bad).build();
            assertThrows(ChartValidationException.class, () -> validator.validate(req));
        }

        @Test
        @DisplayName("endColumn <= startColumn → validation error")
        void endColNotGreaterThanStart_fails() {
            ChartPlacement bad = ChartPlacement.of(2, 5, 20, 5); // endCol == startCol
            ChartRequest req = validBuilder().placement(bad).build();
            assertThrows(ChartValidationException.class, () -> validator.validate(req));
        }

        @Test
        @DisplayName("negative startRow → validation error")
        void negativeStartRow_fails() {
            ChartPlacement bad = ChartPlacement.of(-1, 0, 20, 8);
            ChartRequest req = validBuilder().placement(bad).build();
            assertThrows(ChartValidationException.class, () -> validator.validate(req));
        }
    }

    // ─────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("Data structure violations")
    class DataViolations {

        @Test
        @DisplayName("only header row, no data rows → validation error")
        void onlyHeaderRow_fails() {
            List<List<Object>> oneRow = List.of(List.of("Month", "Sales"));
            ChartRequest req = validBuilder().data(oneRow).build();
            assertThrows(ChartValidationException.class, () -> validator.validate(req));
        }

        @Test
        @DisplayName("single-column data (no series) → validation error")
        void singleColumnData_fails() {
            List<List<Object>> data = List.of(
                    List.of("Month"),
                    List.of("Jan"),
                    List.of("Feb")
            );
            ChartRequest req = validBuilder().data(data).build();
            assertThrows(ChartValidationException.class, () -> validator.validate(req));
        }

        @Test
        @DisplayName("ragged rows (inconsistent column count) → validation error")
        void raggedRows_fails() {
            List<List<Object>> data = List.of(
                    List.of("Month", "Sales", "Profit"),
                    List.of("Jan",    12000),           // missing Profit column
                    List.of("Feb",    15000,   4500)
            );
            ChartRequest req = validBuilder().data(data).build();
            assertThrows(ChartValidationException.class, () -> validator.validate(req));
        }
    }

    // ─────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("Error message quality")
    class ErrorMessages {

        @Test
        @DisplayName("error message lists all collected violations")
        void multipleViolations_allReported() {
            ChartRequest req = ChartRequest.builder()
                    .workbook(null)           // violation 1
                    .targetSheetName("")      // violation 2
                    .chartType(null)          // violation 3
                    .placement(null)          // violation 4
                    .data(null)               // violation 5
                    .build();

            ChartValidationException ex =
                    assertThrows(ChartValidationException.class, () -> validator.validate(req));

            String msg = ex.getMessage();
            assertTrue(msg.contains("workbook"),    "should mention workbook");
            assertTrue(msg.contains("targetSheetName") || msg.contains("blank"),
                    "should mention sheet name");
            assertTrue(msg.contains("chartType"),   "should mention chart type");
            assertTrue(msg.contains("placement"),   "should mention placement");
            assertTrue(msg.contains("data"),        "should mention data");
        }
    }
}
