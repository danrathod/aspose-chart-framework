package com.chartframework.manager;

import com.aspose.cells.Workbook;
import com.aspose.cells.Worksheet;
import com.chartframework.enums.ExcelChartType;
import com.chartframework.model.ChartBatchRequest;
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

    // ── Helpers ───────────────────────────────────────────────────────────────

    private List<List<Object>> threeRowData() {
        return List.of(
                List.of("Month", "Revenue", "Cost"),
                List.of("Jan", 10_000, 6_000),
                List.of("Feb", 12_000, 7_000),
                List.of("Mar", 11_000, 6_500)
        );
    }

    private ChartRequest chartRequest(String title) {
        return ChartRequest.builder()
                .targetSheetName("Sheet1")
                .chartType(ExcelChartType.COLUMN)
                .placement(ChartPlacement.of(0, 0, 18, 8))
                .data(threeRowData())
                .build();
    }

    private ChartBatchRequest batchOf(ChartRequest... charts) {
        return ChartBatchRequest.builder()
                .inputFilePath("test.xlsx")
                .charts(List.of(charts))
                .build();
    }

    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("single chart batch creates exactly one hidden sheet")
    void singleChart_createsOneHiddenSheet() {
        List<DataRange> ranges = manager.writeDataForBatch(
                batchOf(chartRequest("Sales")), workbook);

        assertEquals(1, ranges.size());
        Worksheet ws = workbook.getWorksheets().get(ranges.get(0).getSheetName());
        assertNotNull(ws, "Hidden sheet must exist");
        assertFalse(ws.isVisible(), "Sheet must be hidden");
    }

    @Test
    @DisplayName("two charts in same batch share the same hidden sheet")
    void twoCharts_shareOneHiddenSheet() {
        List<DataRange> ranges = manager.writeDataForBatch(
                batchOf(chartRequest("Sales"), chartRequest("Revenue")), workbook);

        assertEquals(2, ranges.size());
        assertEquals(ranges.get(0).getSheetName(), ranges.get(1).getSheetName(),
                "Both charts in the same batch must share the same hidden sheet");
    }

    @Test
    @DisplayName("DataRange for each chart points to correct rows")
    void dataRanges_haveCorrectRowBounds() {
        List<DataRange> ranges = manager.writeDataForBatch(
                batchOf(chartRequest("Chart1"), chartRequest("Chart2")), workbook);

        DataRange r1 = ranges.get(0);
        DataRange r2 = ranges.get(1);

        // Chart 1: title at row 0, data at rows 1-3 (3 data rows)
        assertEquals(1, r1.getStartRow(), "Chart1 data should start at row 1 (after title)");
        assertEquals(3, r1.getEndRow(),   "Chart1 data should end at row 3");

        // Chart 2: 2 blank rows after Chart1 (rows 4,5), title at row 6, data rows 7-9
        assertEquals(7, r2.getStartRow(), "Chart2 data should start at row 7");
        assertEquals(9, r2.getEndRow(),   "Chart2 data should end at row 9");
    }

    @Test
    @DisplayName("title label is written above each chart's data block")
    void titleRow_isWrittenAboveData() {
        List<DataRange> ranges = manager.writeDataForBatch(
                batchOf(chartRequest("MyChart")), workbook);

        String sheetName = ranges.get(0).getSheetName();
        Worksheet ws     = workbook.getWorksheets().get(sheetName);

        // Row 0 should be the title label (not the data header)
        String titleCellValue = ws.getCells().get(0, 0).getStringValue();
        assertTrue(titleCellValue.contains("COLUMN"),
                "Title row should contain the chart type name");
        // Row 1 should be the data header "Month"
        String headerCell = ws.getCells().get(1, 0).getStringValue();
        assertEquals("Month", headerCell, "Data header should be at row 1");
    }

    @Test
    @DisplayName("two blank separator rows exist between chart blocks")
    void separatorRows_arePresentBetweenBlocks() {
        List<DataRange> ranges = manager.writeDataForBatch(
                batchOf(chartRequest("A"), chartRequest("B")), workbook);

        String    sheetName = ranges.get(0).getSheetName();
        Worksheet ws        = workbook.getWorksheets().get(sheetName);

        // Chart1 data ends at row 3, so rows 4 and 5 should be blank,
        // and row 6 should be Chart2's title label.
        String row4 = ws.getCells().get(4, 0).getStringValue();
        String row5 = ws.getCells().get(5, 0).getStringValue();
        assertTrue(row4.isEmpty() || row4.isBlank(), "Row 4 must be blank separator");
        assertTrue(row5.isEmpty() || row5.isBlank(), "Row 5 must be blank separator");

        String chart2Title = ws.getCells().get(6, 0).getStringValue();
        assertTrue(chart2Title.startsWith("■"), "Chart2 title must start at row 6");
    }

    @Test
    @DisplayName("two separate batches get different hidden sheets")
    void separateBatches_getDifferentSheets() {
        List<DataRange> ranges1 = manager.writeDataForBatch(
                batchOf(chartRequest("Batch1Chart")), workbook);
        List<DataRange> ranges2 = manager.writeDataForBatch(
                batchOf(chartRequest("Batch2Chart")), workbook);

        assertNotEquals(ranges1.get(0).getSheetName(), ranges2.get(0).getSheetName(),
                "Different batches must use different hidden sheets");
    }

    @Test
    @DisplayName("DataRange returns correct row/column counts")
    void dataRange_correctCounts() {
        List<DataRange> ranges = manager.writeDataForBatch(
                batchOf(chartRequest("Test")), workbook);
        DataRange r = ranges.get(0);
        // 3 data rows (header excluded), 2 series cols (category excluded)
        assertEquals(3, r.getDataRowCount());
        assertEquals(2, r.getSeriesCount());
        assertEquals(0, r.getStartColumn());
        assertEquals(2, r.getEndColumn());
    }

    @Test
    @DisplayName("10 batches each get unique hidden sheet names")
    void tenBatches_allUniqueSheetNames() {
        Set<String> names = new HashSet<>();
        for (int i = 0; i < 10; i++) {
            List<DataRange> ranges = manager.writeDataForBatch(
                    batchOf(chartRequest("Chart" + i)), workbook);
            assertTrue(names.add(ranges.get(0).getSheetName()),
                    "Duplicate sheet name: " + ranges.get(0).getSheetName());
        }
        assertEquals(10, names.size());
    }
}