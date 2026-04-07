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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("HiddenSheetManager")
class HiddenSheetManagerTest {

    private HiddenSheetManager manager;
    private Workbook            workbook;

    @BeforeEach
    void setUp() throws Exception {
        manager  = new HiddenSheetManager();
        workbook = new Workbook();
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
                .inputFilePath("test.xlsx")
                .targetSheetName("Sheet1")
                .chartType(ExcelChartType.COLUMN)
                .placement(ChartPlacement.of(2, 0, 20, 8))
                .data(threeRowData())
                .preferredHiddenSheetBaseName(preferredName)
                .build();
    }

    @Test
    @DisplayName("writeData creates a hidden sheet")
    void writeData_createsHiddenSheet() {
        DataRange range = manager.writeData(buildRequest(null), workbook);
        Worksheet ws = workbook.getWorksheets().get(range.getSheetName());
        assertNotNull(ws, "Hidden sheet must exist");
        assertFalse(ws.isVisible(), "Sheet must be hidden");
    }

    @Test
    @DisplayName("DataRange reflects correct row/column counts")
    void writeData_correctCounts() {
        DataRange range = manager.writeData(buildRequest(null), workbook);
        assertEquals(0,  range.getStartRow());
        assertEquals(3,  range.getEndRow());
        assertEquals(0,  range.getStartColumn());
        assertEquals(2,  range.getEndColumn());
        assertEquals(3,  range.getDataRowCount());
        assertEquals(2,  range.getSeriesCount());
    }

    @Test
    @DisplayName("two successive calls produce unique sheet names")
    void writeData_uniqueNames() {
        DataRange r1 = manager.writeData(buildRequest(null), workbook);
        DataRange r2 = manager.writeData(buildRequest(null), workbook);
        assertNotEquals(r1.getSheetName(), r2.getSheetName());
    }

    @Test
    @DisplayName("preferred base name is used as prefix")
    void writeData_usesPreferredBaseName() {
        DataRange range = manager.writeData(buildRequest("mydata_"), workbook);
        assertTrue(range.getSheetName().startsWith("mydata_"));
    }

    @Test
    @DisplayName("cell values are written correctly")
    void writeData_cellValuesCorrect() {
        DataRange range = manager.writeData(buildRequest(null), workbook);
        Worksheet ws = workbook.getWorksheets().get(range.getSheetName());
        assertEquals("Month",   ws.getCells().get(0, 0).getStringValue());
        assertEquals("Revenue", ws.getCells().get(0, 1).getStringValue());
        assertEquals("Jan",     ws.getCells().get(1, 0).getStringValue());
        assertEquals(10000.0,   ws.getCells().get(1, 1).getDoubleValue(), 0.001);
    }

    @Test
    @DisplayName("10 calls all produce unique names")
    void writeData_tenCallsAllUnique() {
        Set<String> names = new HashSet<>();
        for (int i = 0; i < 10; i++) {
            DataRange r = manager.writeData(buildRequest(null), workbook);
            assertTrue(names.add(r.getSheetName()), "Duplicate: " + r.getSheetName());
        }
        assertEquals(10, names.size());
    }
}
