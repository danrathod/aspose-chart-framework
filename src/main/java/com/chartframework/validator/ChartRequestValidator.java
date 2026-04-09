package com.chartframework.validator;

import com.chartframework.exception.ChartValidationException;
import com.chartframework.model.ChartBatchRequest;
import com.chartframework.model.ChartPlacement;
import com.chartframework.model.ChartRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Validates a {@link ChartBatchRequest} (and every {@link ChartRequest} it
 * contains) before any Aspose objects are created.
 *
 * <p>All violations across the entire batch are collected before throwing, so
 * callers see the complete list in one pass.</p>
 */
public class ChartRequestValidator {

    private static final Logger log = LoggerFactory.getLogger(ChartRequestValidator.class);

    private static final int MAX_EXCEL_ROWS = 1_048_576;
    private static final int MAX_EXCEL_COLS = 16_384;

    // ─────────────────────────────────────────────────────────────────────────
    // Public API
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Validates a {@link ChartBatchRequest}.
     * Collects all errors before throwing {@link ChartValidationException}.
     */
    public void validate(ChartBatchRequest batchRequest) {
        List<String> errors = new ArrayList<>();

        if (batchRequest == null) {
            throw new ChartValidationException("ChartBatchRequest must not be null.");
        }

        // ── File path ─────────────────────────────────────────────────────────
        if (isBlank(batchRequest.getInputFilePath())) {
            errors.add("inputFilePath must not be null or blank.");
        }

        // ── Charts list ───────────────────────────────────────────────────────
        List<ChartRequest> charts = batchRequest.getCharts();
        if (charts == null || charts.isEmpty()) {
            errors.add("charts list must not be null or empty. " +
                    "Provide at least one ChartRequest.");
        } else {
            for (int i = 0; i < charts.size(); i++) {
                validateSingleChart(charts.get(i), i, errors);
            }
        }

        if (!errors.isEmpty()) {
            String message = "ChartBatchRequest validation failed:\n  - " +
                    String.join("\n  - ", errors);
            log.error(message);
            throw new ChartValidationException(message);
        }

        log.debug("ChartBatchRequest validation passed — {} chart(s), file='{}'",
                charts != null ? charts.size() : 0,
                batchRequest.getInputFilePath());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Private helpers
    // ─────────────────────────────────────────────────────────────────────────

    private void validateSingleChart(ChartRequest req, int index, List<String> errors) {
        String prefix = "charts[" + index + "]: ";

        if (req == null) {
            errors.add(prefix + "ChartRequest must not be null.");
            return;
        }

        if (isBlank(req.getTargetSheetName())) {
            errors.add(prefix + "targetSheetName must not be blank.");
        }

        if (req.getChartType() == null) {
            errors.add(prefix + "chartType must not be null.");
        }

        ChartPlacement p = req.getPlacement();
        if (p == null) {
            errors.add(prefix + "placement must not be null.");
        } else {
            validatePlacement(p, prefix, errors);
        }

        List<List<Object>> data = req.getData();
        if (data == null || data.isEmpty()) {
            errors.add(prefix + "data must not be null or empty.");
        } else {
            validateData(data, prefix, errors);
        }
    }

    private void validatePlacement(ChartPlacement p, String prefix, List<String> errors) {
        if (p.getStartRow() < 0) {
            errors.add(prefix + "placement.startRow must be >= 0 (was " + p.getStartRow() + ").");
        }
        if (p.getStartColumn() < 0) {
            errors.add(prefix + "placement.startColumn must be >= 0 (was " + p.getStartColumn() + ").");
        }
        if (p.getEndRow() >= MAX_EXCEL_ROWS) {
            errors.add(prefix + "placement.endRow exceeds Excel maximum of " + (MAX_EXCEL_ROWS - 1) + ".");
        }
        if (p.getEndColumn() >= MAX_EXCEL_COLS) {
            errors.add(prefix + "placement.endColumn exceeds Excel maximum of " + (MAX_EXCEL_COLS - 1) + ".");
        }
        if (p.getEndRow() <= p.getStartRow()) {
            errors.add(prefix + "placement.endRow (" + p.getEndRow() +
                    ") must be greater than startRow (" + p.getStartRow() + ").");
        }
        if (p.getEndColumn() <= p.getStartColumn()) {
            errors.add(prefix + "placement.endColumn (" + p.getEndColumn() +
                    ") must be greater than startColumn (" + p.getStartColumn() + ").");
        }
    }

    private void validateData(List<List<Object>> data, String prefix, List<String> errors) {
        if (data.size() < 2) {
            errors.add(prefix + "data must contain at least 2 rows " +
                    "(1 header + 1 data row). Found: " + data.size() + ".");
            return;
        }

        int expectedCols = data.get(0) != null ? data.get(0).size() : 0;
        if (expectedCols < 2) {
            errors.add(prefix + "data header row must have at least 2 columns " +
                    "(1 category + 1 series). Found: " + expectedCols + ".");
        }

        for (int i = 1; i < data.size(); i++) {
            List<Object> row = data.get(i);
            if (row == null || row.isEmpty()) {
                errors.add(prefix + "data row " + i + " is null or empty.");
            } else if (row.size() != expectedCols) {
                errors.add(prefix + "data row " + i + " has " + row.size() +
                        " columns but header has " + expectedCols +
                        ". All rows must be consistent.");
            }
        }
    }

    private boolean isBlank(String s) {
        return s == null || s.isBlank();
    }
}
