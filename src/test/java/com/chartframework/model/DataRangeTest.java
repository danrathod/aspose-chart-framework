package com.chartframework.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("DataRange — cell reference helpers")
class DataRangeTest {

    private DataRange range(String sheet) {
        return DataRange.builder()
                .sheetName(sheet)
                .startRow(0).endRow(4)
                .startColumn(0).endColumn(3)
                .dataRowCount(4).seriesCount(3)
                .build();
    }

    @Test
    @DisplayName("columnIndexToLetter: single letters A-Z")
    void singleLetters() {
        assertEquals("A", DataRange.columnIndexToLetter(0));
        assertEquals("B", DataRange.columnIndexToLetter(1));
        assertEquals("Z", DataRange.columnIndexToLetter(25));
    }

    @Test
    @DisplayName("columnIndexToLetter: two-letter columns AA, AB, AZ, BA")
    void twoLetterColumns() {
        assertEquals("AA", DataRange.columnIndexToLetter(26));
        assertEquals("AB", DataRange.columnIndexToLetter(27));
        assertEquals("AZ", DataRange.columnIndexToLetter(51));
        assertEquals("BA", DataRange.columnIndexToLetter(52));
    }

    @Test
    @DisplayName("toAbsoluteRange produces correct formula")
    void toAbsoluteRange() {
        DataRange dr = range("__chartdata_1");
        // rows 0-4, cols 0-3 → $A$1:$D$5
        String formula = dr.toAbsoluteRange(0, 0, 4, 3);
        assertEquals("'__chartdata_1'!$A$1:$D$5", formula);
    }

    @Test
    @DisplayName("toColumnRange produces correct single-column formula")
    void toColumnRange() {
        DataRange dr = range("Data");
        // column B (index 1), rows 1-4 → $B$2:$B$5
        String formula = dr.toColumnRange(1, 1, 4);
        assertEquals("'Data'!$B$2:$B$5", formula);
    }

    @Test
    @DisplayName("toRowRange produces correct single-row formula")
    void toRowRange() {
        DataRange dr = range("Data");
        // row 0, cols 1-3 → $B$1:$D$1
        String formula = dr.toRowRange(0, 1, 3);
        assertEquals("'Data'!$B$1:$D$1", formula);
    }

    @Test
    @DisplayName("sheet names with spaces are quoted correctly")
    void sheetNameWithSpaces() {
        DataRange dr = range("My Sheet");
        String formula = dr.toColumnRange(0, 0, 3);
        assertTrue(formula.startsWith("'My Sheet'!"),
                "Sheet names with spaces must be single-quoted");
    }
}
