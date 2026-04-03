package com.chartframework.factory;

import com.chartframework.enums.ExcelChartType;
import com.chartframework.exception.ChartFrameworkException;
import com.chartframework.strategy.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ChartStrategyFactory")
class ChartStrategyFactoryTest {

    private ChartStrategyFactory factory;

    @BeforeEach
    void setUp() {
        factory = new ChartStrategyFactory();
    }

    @ParameterizedTest(name = "{0} → strategy resolved")
    @EnumSource(ExcelChartType.class)
    @DisplayName("every ExcelChartType resolves to a non-null strategy")
    void everyChartType_hasStrategy(ExcelChartType type) {
        ChartStrategy strategy = assertDoesNotThrow(() -> factory.getStrategy(type),
                () -> "No strategy registered for " + type);
        assertNotNull(strategy, "Strategy must not be null for " + type);
    }

    @Test
    @DisplayName("Column types resolve to ColumnBarChartStrategy")
    void columnTypes_resolveToColumnBar() {
        assertInstanceOf(ColumnBarChartStrategy.class,
                factory.getStrategy(ExcelChartType.COLUMN_CLUSTERED));
        assertInstanceOf(ColumnBarChartStrategy.class,
                factory.getStrategy(ExcelChartType.BAR_STACKED));
    }

    @Test
    @DisplayName("Pie types resolve to PieDonutChartStrategy")
    void pieTypes_resolveToPie() {
        assertInstanceOf(PieDonutChartStrategy.class,
                factory.getStrategy(ExcelChartType.PIE));
        assertInstanceOf(PieDonutChartStrategy.class,
                factory.getStrategy(ExcelChartType.DOUGHNUT));
    }

    @Test
    @DisplayName("Scatter types resolve to ScatterChartStrategy")
    void scatterTypes_resolveToScatter() {
        assertInstanceOf(ScatterChartStrategy.class,
                factory.getStrategy(ExcelChartType.SCATTER));
    }

    @Test
    @DisplayName("Bubble types resolve to BubbleChartStrategy")
    void bubbleTypes_resolveToBubble() {
        assertInstanceOf(BubbleChartStrategy.class,
                factory.getStrategy(ExcelChartType.BUBBLE));
        assertInstanceOf(BubbleChartStrategy.class,
                factory.getStrategy(ExcelChartType.BUBBLE_3D));
    }

    @Test
    @DisplayName("Combo type resolves to ComboChartStrategy")
    void comboType_resolvesToCombo() {
        assertInstanceOf(ComboChartStrategy.class,
                factory.getStrategy(ExcelChartType.COMBO));
    }

    @Test
    @DisplayName("registerStrategy allows runtime override")
    void registerStrategy_overridesExisting() {
        // Use a lambda as a custom strategy stub
        ChartStrategy custom = (chart, request, dataRange) -> { /* no-op stub */ };
        factory.registerStrategy(ExcelChartType.PIE, custom);

        assertSame(custom, factory.getStrategy(ExcelChartType.PIE),
                "Custom-registered strategy should be returned");
    }
}
