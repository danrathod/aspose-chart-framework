package com.chartframework.manager;

import com.aspose.cells.Workbook;
import com.aspose.cells.Worksheet;
import com.chartframework.enums.ExcelChartType;
import com.chartframework.model.ChartPlacement;
import com.chartframework.model.ChartRequest;
import com.chartframework.model.DataRange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("HiddenSheetManager")
class HiddenSheetManagerTest {

    private HiddenSheetManager manager;
    private Workbook workbook;

    @BeforeEach
    void setUp() throws Exception {
        manager  = new HiddenSheetManager();
        workbook = new Workbook();  // fresh workbook for each test
    }

    private List<List<Object>> threeRowData() {
        return List.of(
                List.of("Month", "Revenue", "Cost"),
                List.of("Jan", 10000, 6000),
                List.of("Feb", 12000, 7000),
                List.of("Mar", 11000, 6500)
        );
    }

    private ChartRequest buildRequest(String preferredName) {
        return ChartRequest.builder()
                .workbook(workbook)
                .targetSheetName("Sheet1")
                .chartType(ExcelChartType.COLUMN_CLUSTERED)
                .placement(ChartPlacement.of(2, 0, 20, 8))
                .data(threeRowData())
                .preferredHiddenSheetBaseName(preferredName)
                .build();
    }

    @Test
    @DisplayName("writeData creates a hidden sheet in the workbook")
    void writeData_createsHiddenSheet() {
        DataRange range = manager.writeData(buildRequest(null));

        Worksheet ws = workbook.getWorksheets().get(range.getSheetName());
        assertNotNull(ws, "Hidden sheet should exist in workbook");
        assertFalse(ws.isVisible(), "Sheet must be hidden");
    }

    @Test
    @DisplayName("DataRange reflects correct row/column counts")
    void writeData_correctRowAndColumnCounts() {
        DataRange range = manager.writeData(buildRequest(null));

        // 4 total rows (header + 3 data), 3 total cols (Month + 2 series)
        assertEquals(0, range.getStartRow());
        assertEquals(3, range.getEndRow());   // 4 rows, 0-indexed → endRow = 3
        assertEquals(0, range.getStartColumn());
        assertEquals(2, range.getEndColumn()); // 3 cols, 0-indexed → endCol = 2
        assertEquals(3, range.getDataRowCount()); // 3 data rows (header excluded)
        assertEquals(2, range.getSeriesCount());   // 2 series columns
    }

    @Test
    @DisplayName("two successive calls produce uniquely named sheets")
    void writeData_uniqueSheetNamesPerCall() {
        DataRange r1 = manager.writeData(buildRequest(null));
        DataRange r2 = manager.writeData(buildRequest(null));

        assertNotEquals(r1.getSheetName(), r2.getSheetName(),
                "Each chart should get its own hidden sheet");
    }

    @Test
    @DisplayName("preferred base name is used as sheet name prefix")
    void writeData_usesPreferredBaseName() {
        DataRange range = manager.writeData(buildRequest("mydata_"));

        assertTrue(range.getSheetName().startsWith("mydata_"),
                "Sheet name should start with preferred base name");
    }

    @Test
    @DisplayName("data values are written correctly into the hidden sheet")
    void writeData_cellValuesAreCorrect() {
        DataRange range = manager.writeData(buildRequest(null));
        Worksheet ws    = workbook.getWorksheets().get(range.getSheetName());

        // Header row
        assertEquals("Month",   ws.getCells().get(0, 0).getStringValue());
        assertEquals("Revenue", ws.getCells().get(0, 1).getStringValue());
        assertEquals("Cost",    ws.getCells().get(0, 2).getStringValue());

        // First data row: Jan, 10000, 6000
        assertEquals("Jan",  ws.getCells().get(1, 0).getStringValue());
        assertEquals(10000.0, ws.getCells().get(1, 1).getDoubleValue(), 0.001);
        assertEquals(6000.0,  ws.getCells().get(1, 2).getDoubleValue(), 0.001);
    }

    @Test
    @DisplayName("10 successive calls all produce unique sheet names")
    void writeData_tenCallsAllUnique() {
        var names = new java.util.HashSet<String>();
        for (int i = 0; i < 10; i++) {
            DataRange r = manager.writeData(buildRequest(null));
            assertTrue(names.add(r.getSheetName()),
                    "Duplicate sheet name detected: " + r.getSheetName());
        }
        assertEquals(10, names.size());
    }
}
