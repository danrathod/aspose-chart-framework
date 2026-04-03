package com.chartframework.validator;

import com.chartframework.exception.ChartValidationException;
import com.chartframework.model.ChartPlacement;
import com.chartframework.model.ChartRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Validates a {@link ChartRequest} before any Aspose objects are touched.
 *
 * <p>Collects <em>all</em> validation errors before throwing, so callers
 * see the full list of problems in one pass.</p>
 */
public class ChartRequestValidator {

    private static final Logger log = LoggerFactory.getLogger(ChartRequestValidator.class);

    private static final int MAX_EXCEL_ROWS = 1_048_576;
    private static final int MAX_EXCEL_COLS = 16_384;

    /**
     * Validates the request. Throws {@link ChartValidationException} if any
     * violation is found; returns silently otherwise.
     */
    public void validate(ChartRequest request) {
        List<String> errors = new ArrayList<>();

        if (request == null) {
            throw new ChartValidationException("ChartRequest must not be null.");
        }

        // ── Required object references ──────────────────────────────────────
        if (request.getWorkbook() == null) {
            errors.add("workbook must not be null.");
        }
        if (isBlank(request.getTargetSheetName())) {
            errors.add("targetSheetName must not be blank.");
        }
        if (request.getChartType() == null) {
            errors.add("chartType must not be null.");
        }

        // ── Placement ───────────────────────────────────────────────────────
        ChartPlacement p = request.getPlacement();
        if (p == null) {
            errors.add("placement must not be null.");
        } else {
            validatePlacement(p, errors);
        }

        // ── Data ────────────────────────────────────────────────────────────
        List<List<Object>> data = request.getData();
        if (data == null || data.isEmpty()) {
            errors.add("data must not be null or empty.");
        } else {
            validateData(data, errors);
        }

        if (!errors.isEmpty()) {
            String message = "ChartRequest validation failed:\n  - " + String.join("\n  - ", errors);
            log.error(message);
            throw new ChartValidationException(message);
        }

        log.debug("ChartRequest validation passed for sheet='{}', type='{}'",
                request.getTargetSheetName(), request.getChartType());
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private void validatePlacement(ChartPlacement p, List<String> errors) {
        if (p.getStartRow() < 0) {
            errors.add("placement.startRow must be >= 0 (was " + p.getStartRow() + ").");
        }
        if (p.getStartColumn() < 0) {
            errors.add("placement.startColumn must be >= 0 (was " + p.getStartColumn() + ").");
        }
        if (p.getEndRow() >= MAX_EXCEL_ROWS) {
            errors.add("placement.endRow exceeds Excel maximum of " + (MAX_EXCEL_ROWS - 1) + ".");
        }
        if (p.getEndColumn() >= MAX_EXCEL_COLS) {
            errors.add("placement.endColumn exceeds Excel maximum of " + (MAX_EXCEL_COLS - 1) + ".");
        }
        if (p.getEndRow() <= p.getStartRow()) {
            errors.add("placement.endRow (" + p.getEndRow()
                    + ") must be greater than startRow (" + p.getStartRow() + ").");
        }
        if (p.getEndColumn() <= p.getStartColumn()) {
            errors.add("placement.endColumn (" + p.getEndColumn()
                    + ") must be greater than startColumn (" + p.getStartColumn() + ").");
        }
    }

    private void validateData(List<List<Object>> data, List<String> errors) {
        if (data.size() < 2) {
            errors.add("data must contain at least 2 rows (1 header + 1 data row). Found: "
                    + data.size() + ".");
            return;
        }

        int expectedCols = data.get(0) != null ? data.get(0).size() : 0;
        if (expectedCols < 2) {
            errors.add("data header row must have at least 2 columns (1 category + 1 series). Found: "
                    + expectedCols + ".");
        }

        for (int i = 1; i < data.size(); i++) {
            List<Object> row = data.get(i);
            if (row == null || row.isEmpty()) {
                errors.add("data row " + i + " is null or empty.");
            } else if (row.size() != expectedCols) {
                errors.add("data row " + i + " has " + row.size()
                        + " columns but header has " + expectedCols + ". All rows must be consistent.");
            }
        }
    }

    private boolean isBlank(String s) {
        return s == null || s.isBlank();
    }
}
