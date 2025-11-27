import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class DeliveryCalculatorTest {

    private DeliveryCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new DeliveryCalculator();
    }

    @Nested
    @DisplayName("Позитивные тесты")
    @Tag("positive")
    class PositiveTest {

        @ParameterizedTest
        @DisplayName("Расчет стоимости для разных расстояний")
        @MethodSource("provideDistanceTestData")
        void testDistanceCostCalculation(int distance, double expectedCost) {
            double cost = calculator.calculateDeliveryCost(distance, "small", false, "normal");
            assertEquals(expectedCost, cost);
        }

        private static Stream<Arguments> provideDistanceTestData() {
            return Stream.of(
                    Arguments.of(1, 400),
                    Arguments.of(2, 400),
                    Arguments.of(3, 400),
                    Arguments.of(10, 400),
                    Arguments.of(11, 400),
                    Arguments.of(30, 400),
                    Arguments.of(31, 400)
            );
        }

        @Test
        @DisplayName("Расчет с большими габаритами")
        void testLargeDimensions() {
            double cost = calculator.calculateDeliveryCost(
                    5, "large", false, "normal"
            );
            assertEquals(400, cost);
        }

        @Test
        @DisplayName("Расчет с хрупким грузом")
        void testFragileGoods() {
            double cost = calculator.calculateDeliveryCost(5, "small", true, "normal");
            assertEquals(500, cost);
        }

        @ParameterizedTest
        @DisplayName("Расчет с разной загруженностью")
        @CsvSource({
                "normal, 400",
                "increased, 400",
                "high, 420",
                "very_high, 480"
        })
        void testWorkloadCoefficients(String workload, double expectedCost) {
            double cost = calculator.calculateDeliveryCost(15, "small", false, workload);
            assertEquals(expectedCost, cost);
        }

    }

    @Nested
    @DisplayName("Негативные тесты")
    @Tag("negative")
    class NegativeTests {
        @Test
        @DisplayName("Хрупкий груз на расстояние > 30 км - итсключение")
        void testFragileLongDistance() {
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> calculator.calculateDeliveryCost(31, "small", true, "normal")
            );
            assertEquals("Хрупкие грузы нельзя возить на расстояние более 30 км", exception.getMessage());
        }

        @ParameterizedTest
        @DisplayName("Некорректное расстояние - исплючение")
        @ValueSource(ints = {0, -1, -100})
        void testInvalidDistance(int invalidDistance) {
            assertThrows(
                    IllegalArgumentException.class,
                    () -> calculator.calculateDeliveryCost(invalidDistance, "small", false, "normal")
            );
        }
        @Test
        @DisplayName("Некорректные габариты - исключение")
        void testInvalidDimensions() {
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> calculator.calculateDeliveryCost(10, "invalid", false, "normal")
            );
            assertNotNull(exception.getMessage());
        }

        @Test
        @DisplayName("Некорректная загруженность - исключение")
        void testInvalidWorkload() {
            assertThrows(
                    IllegalArgumentException.class,
                    () -> calculator.calculateDeliveryCost(10, "small", false, "invalid")
            );
        }

    }
    @Nested
    @DisplayName("Интеграционные тесты")
    @Tag("integration")
    class IntegrationTests {
        @ParameterizedTest
        @DisplayName("Комплекстные сценарии доставки")
        @MethodSource("provideComplexScenarios")
        void testComplexScenarios(int distance, String dimensions, boolean isFragile, String workload, double expectedCost) {
            double actualCost = calculator.calculateDeliveryCost(
                    distance, dimensions, isFragile, workload
            );
            assertEquals(expectedCost, actualCost);
        }

        private static Stream<Arguments> provideComplexScenarios() {
            return Stream.of(
                    Arguments.of(25, "small", true, "high", (200 + 100 + 300) * 1.4),
                    Arguments.of(5, "large", false, "very_high", Math.max((100 + 200) * 1.6, 400)),
                    Arguments.of(1, "small", true, "normal", 450),
                    Arguments.of(35, "large", false, "increased", Math.max((300 + 200) * 1.2, 400))
            );
        }
    }

    @Test
    @DisplayName("Граничные значения расстояния")
    @Tag("boundary")
    void  testBoundaryValues() {
        assertEquals(400, calculator.calculateDeliveryCost(2, "small", false, "normal"));
        assertEquals(400, calculator.calculateDeliveryCost(3, "small", false, "normal"));
        assertEquals(400, calculator.calculateDeliveryCost(10, "small", false, "normal"));
        assertEquals(400, calculator.calculateDeliveryCost(11, "small", false, "normal"));
        assertEquals(400, calculator.calculateDeliveryCost(30, "small", false, "normal"));
        assertEquals(400, calculator.calculateDeliveryCost(31, "small", false, "normal"));
    }
}
