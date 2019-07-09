package de.viadee.xai.framework.explanation_calculation.calculator;

import de.viadee.xai.framework.TestUtilityData;
import de.viadee.xai.framework.data.Feature.CategoricalFeature;
import de.viadee.xai.framework.data.index.RoaringBitmapIndex;
import de.viadee.xai.framework.data.tabular_data.LabelColumn;
import de.viadee.xai.framework.data.tabular_data.TabularDataset;
import de.viadee.xai.framework.utility.Utility;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * Class testing all current calculators.
 * Exemplary queries created in {@link TestUtilityData} are used to assess whether all
 * calculators function as expected.
 */
public class CategoricalCalculatorTest {
    protected TabularDataset<? extends LabelColumn, LabelColumn.CategoricalLabelColumn> dataset;
    protected RoaringBitmapIndex roaringBitmapIndex;
    protected Map<CategoricalFeature, Set<Integer>>[] queries;
    protected Set<Integer> overallInstances;
    protected Set<Integer>[] expectedCovered;
    protected Set<Integer>[] expectedCorrectlyCovered;
    protected Set<Integer>[] expectedCorrectlyNotCovered;
    protected Set<Integer>[] expectedIncorrectlyCovered;
    protected Set<Integer>[] expectedIncorrectlyNotCovered;
    @Before
    public void setup() {
        TestUtilityData data = new TestUtilityData();

        data.setUp();

        dataset = data.getDataset();
        roaringBitmapIndex = data.getRoaringBitmapIndex();
        queries = data.getQueries();
        overallInstances = data.getOverallInstances();
        expectedCovered = data.getExpectedCovered();
        expectedCorrectlyCovered = data.getExpectedCorrectlyCovered();
        expectedIncorrectlyCovered = data.getExpectedIncorrectlyCovered();
        expectedCorrectlyNotCovered = data.getExpectedCorrectlyNotCovered();
        expectedIncorrectlyNotCovered = data.getExpectedIncorrectlyNotCovered();
    }

    @Test
    public void testCoverCachedCalculator() {
        testRoaringBitmapCalculator(new CoverCachedCalculator(new SimpleRoaringBitmapCalculator(roaringBitmapIndex)));
    }

    @Test
    public void testCoverCacheReusingCalculator() {
        testRoaringBitmapCalculator(new CoverCacheReusingCalculator(new SimpleRoaringBitmapCalculator(roaringBitmapIndex)));
    }

    @Test
    public void testAmountCachedCalculator() {
        testRoaringBitmapCalculator(new RoaringBitmapNumberCachedCalculator(new SimpleRoaringBitmapCalculator(roaringBitmapIndex)));
    }

    @Test
    public void testSimpleRoaringBitmapCalculator() {
        testRoaringBitmapCalculator(new SimpleRoaringBitmapCalculator(roaringBitmapIndex));
    }

    @Test
    public void testAmountCachedRoaringBitmapCalculator() {
        testRoaringBitmapCalculator(new RoaringBitmapNumberCachedCalculator(new SimpleRoaringBitmapCalculator(roaringBitmapIndex)));
    }

    @Test
    public void testStdWrappingCalculator() {
        testRoaringBitmapCalculator(new SimpleRoaringBitmapCalculator(roaringBitmapIndex));
        testRoaringBitmapCalculator(new SimpleRoaringBitmapCalculator(roaringBitmapIndex));
    }

    protected void testRoaringBitmapCalculator(RoaringBitmapCalculator roaringBitmapCalculator) {
        assertCategoricalCalculator(roaringBitmapCalculator);
        assertCovered((x) -> (Utility.transformBitmapToSet(roaringBitmapCalculator.getCoveredAsBitmap(x))));
        assertCorrectlyCovered((x, y) -> (Utility.transformBitmapToSet(roaringBitmapCalculator.getCorrectlyCoveredAsBitmap(x, y))));
        assertIncorrectlyCovered((x, y) -> (Utility.transformBitmapToSet(roaringBitmapCalculator.getIncorrectlyCoveredAsBitmap(x, y))));
        assertCorrectlyNotCovered((x, y) -> (Utility.transformBitmapToSet(roaringBitmapCalculator.getCorrectlyNotCoveredAsBitmap(x, y))));
        assertIncorrectlyNotCovered((x, y) -> (Utility.transformBitmapToSet(roaringBitmapCalculator.getIncorrectlyNotCoveredAsBitmap(x, y))));
        assertMinimalAmountCovers(
                (x, y) -> Utility.transformBitmapsToSets(roaringBitmapCalculator.getMinimalNumberCoversAsBitmap(x, y)),
                (x) -> Utility.transformBitmapToSet(roaringBitmapCalculator.getCoveredAsBitmap(x)),
                (x, y) -> Utility.transformBitmapToSet(roaringBitmapCalculator.getCorrectlyCoveredAsBitmap(x, y)),
                (x, y) -> Utility.transformBitmapToSet(roaringBitmapCalculator.getIncorrectlyNotCoveredAsBitmap(x, y))
        );
    }

    protected void assertCategoricalCalculator(CategoricalCalculator categoricalCalculator) {
        assertNumberCovered(categoricalCalculator::getNumberCovered);
        assertNumberCorrectlyCovered(categoricalCalculator::getNumberCorrectlyCovered);
        assertNumberIncorrectlyCovered(categoricalCalculator::getNumberIncorrectlyCovered);
        assertNumberCorrectlyNotCovered(categoricalCalculator::getNumberCorrectlyNotCovered);
        assertNumberIncorrectlyNotCovered(categoricalCalculator::getNumberIncorrectlyNotCovered);
        assertNumberInMinimalAmountCovers(categoricalCalculator, 0);
        assertNumberInMinimalAmountCovers(categoricalCalculator, 1);
    }

    protected void assertCovered(Function<Map<CategoricalFeature, Set<Integer>>, Set<Integer>>
                                         calculatorFunction) {
        assertEquals(expectedCovered[0], calculatorFunction.apply(queries[0]));
        assertEquals(expectedCovered[1], calculatorFunction.apply(queries[1]));
        assertEquals(expectedCovered[2], calculatorFunction.apply(queries[2]));
        assertEquals(expectedCovered[3], calculatorFunction.apply(queries[3]));
        assertEquals(expectedCovered[4], calculatorFunction.apply(queries[4]));
        assertEquals(expectedCovered[5], calculatorFunction.apply(queries[5]));
        assertEquals(expectedCovered[6], calculatorFunction.apply(queries[6]));
        assertEquals(expectedCovered[7], calculatorFunction.apply(queries[7]));
        assertEquals(expectedCovered[8], calculatorFunction.apply(queries[8]));
        assertEquals(expectedCovered[9], calculatorFunction.apply(queries[9]));
        assertEquals(expectedCovered[10], calculatorFunction.apply(queries[10]));
        assertEquals(expectedCovered[11], calculatorFunction.apply(queries[11]));
        assertEquals(expectedCovered[12], calculatorFunction.apply(queries[12]));
        assertEquals(expectedCovered[13], calculatorFunction.apply(queries[13]));
    }

    protected void assertCorrectlyCovered(BiFunction<Map<CategoricalFeature, Set<Integer>>, Integer, Set<Integer>>
                                                  calculatorFunction) {
        assertEquals(expectedCorrectlyCovered[0],  calculatorFunction.apply(queries[0], 0));
        assertEquals(expectedCorrectlyCovered[1],  calculatorFunction.apply(queries[1], 0));
        assertEquals(expectedCorrectlyCovered[2],  calculatorFunction.apply(queries[2], 0));
        assertEquals(expectedCorrectlyCovered[3],  calculatorFunction.apply(queries[3], 0));
        assertEquals(expectedCorrectlyCovered[4],  calculatorFunction.apply(queries[4], 0));
        assertEquals(expectedCorrectlyCovered[5],  calculatorFunction.apply(queries[4], 1));
        assertEquals(expectedCorrectlyCovered[6],  calculatorFunction.apply(queries[5], 0));
        assertEquals(expectedCorrectlyCovered[7],  calculatorFunction.apply(queries[5], 1));
        assertEquals(expectedCorrectlyCovered[8],  calculatorFunction.apply(queries[7], 0));
        assertEquals(expectedCorrectlyCovered[9],  calculatorFunction.apply(queries[7], 1));
        assertEquals(expectedCorrectlyCovered[10], calculatorFunction.apply(queries[8], 0));
        assertEquals(expectedCorrectlyCovered[10], calculatorFunction.apply(queries[8], 1));
        assertEquals(expectedCorrectlyCovered[11], calculatorFunction.apply(queries[9], 0));
        assertEquals(expectedCorrectlyCovered[12], calculatorFunction.apply(queries[9], 1));
        assertEquals(expectedCorrectlyCovered[13], calculatorFunction.apply(queries[10], 0));
        assertEquals(expectedCorrectlyCovered[14], calculatorFunction.apply(queries[10], 1));
        assertEquals(expectedCorrectlyCovered[15], calculatorFunction.apply(queries[11], 0));
        assertEquals(expectedCorrectlyCovered[16], calculatorFunction.apply(queries[12], 0));
        assertEquals(expectedCorrectlyCovered[17], calculatorFunction.apply(queries[12], 1));
        assertEquals(expectedCorrectlyCovered[18], calculatorFunction.apply(queries[13], 0));
        assertEquals(expectedCorrectlyCovered[19], calculatorFunction.apply(queries[14], 0));
        assertEquals(expectedCorrectlyCovered[20], calculatorFunction.apply(queries[14], 1));
    }

    protected void assertIncorrectlyCovered(BiFunction<Map<CategoricalFeature, Set<Integer>>, Integer, Set<Integer>>
                                                    calculatorFunction) {
        assertEquals(expectedIncorrectlyCovered[0], calculatorFunction.apply(queries[4], 0));
        assertEquals(expectedIncorrectlyCovered[1], calculatorFunction.apply(queries[4], 1));
        assertEquals(expectedIncorrectlyCovered[2], calculatorFunction.apply(queries[5], 0));
        assertEquals(expectedIncorrectlyCovered[3], calculatorFunction.apply(queries[5], 1));
        assertEquals(expectedIncorrectlyCovered[4], calculatorFunction.apply(queries[9], 0));
        assertEquals(expectedIncorrectlyCovered[5], calculatorFunction.apply(queries[9], 1));
        assertEquals(expectedIncorrectlyCovered[6], calculatorFunction.apply(queries[11],1));
        assertEquals(expectedIncorrectlyCovered[7], calculatorFunction.apply(queries[14],0));
        assertEquals(expectedIncorrectlyCovered[8], calculatorFunction.apply(queries[14],1));
    }

    protected void assertCorrectlyNotCovered(BiFunction<Map<CategoricalFeature, Set<Integer>>, Integer, Set<Integer>>
                                                     calculatorFunction) {
        assertEquals(expectedCorrectlyNotCovered[0], calculatorFunction.apply(queries[0], 0));
        assertEquals(expectedCorrectlyNotCovered[1], calculatorFunction.apply(queries[0], 1));
        assertEquals(expectedCorrectlyNotCovered[2], calculatorFunction.apply(queries[4], 0));
        assertEquals(expectedCorrectlyNotCovered[3], calculatorFunction.apply(queries[4], 1));
        assertEquals(expectedCorrectlyNotCovered[4], calculatorFunction.apply(queries[5], 0));
        assertEquals(expectedCorrectlyNotCovered[5], calculatorFunction.apply(queries[6], 0));
        assertEquals(expectedCorrectlyNotCovered[6], calculatorFunction.apply(queries[6], 1));
        assertEquals(expectedCorrectlyNotCovered[7], calculatorFunction.apply(queries[14],0));
        assertEquals(expectedCorrectlyNotCovered[8], calculatorFunction.apply(queries[14],1));
    }

    protected void assertIncorrectlyNotCovered(BiFunction<Map<CategoricalFeature, Set<Integer>>, Integer, Set<Integer>>
                                                       calculatorFunction) {
        assertEquals(expectedIncorrectlyNotCovered[0],  calculatorFunction.apply(queries[0], 0));
        assertEquals(expectedIncorrectlyNotCovered[1],  calculatorFunction.apply(queries[0], 1));
        assertEquals(expectedIncorrectlyNotCovered[2],  calculatorFunction.apply(queries[3], 0));
        assertEquals(expectedIncorrectlyNotCovered[3],  calculatorFunction.apply(queries[3], 1));
        assertEquals(expectedIncorrectlyNotCovered[4],  calculatorFunction.apply(queries[5], 0));
        assertEquals(expectedIncorrectlyNotCovered[5],  calculatorFunction.apply(queries[6], 0));
        assertEquals(expectedIncorrectlyNotCovered[6],  calculatorFunction.apply(queries[6], 1));
        assertEquals(expectedIncorrectlyNotCovered[7],  calculatorFunction.apply(queries[9], 0));
        assertEquals(expectedIncorrectlyNotCovered[8],  calculatorFunction.apply(queries[9], 1));
        assertEquals(expectedIncorrectlyNotCovered[9],  calculatorFunction.apply(queries[13],0));
        assertEquals(expectedIncorrectlyNotCovered[10], calculatorFunction.apply(queries[13],1));
    }


    protected void assertNumberCovered(Function<Map<CategoricalFeature, Set<Integer>>, Integer>
                                         calculatorFunction) {
        assertEquals((Integer) expectedCovered[0].size(), calculatorFunction.apply(queries[0]));
        assertEquals((Integer) expectedCovered[1].size(), calculatorFunction.apply(queries[1]));
        assertEquals((Integer) expectedCovered[2].size(), calculatorFunction.apply(queries[2]));
        assertEquals((Integer) expectedCovered[3].size(), calculatorFunction.apply(queries[3]));
        assertEquals((Integer) expectedCovered[4].size(), calculatorFunction.apply(queries[4]));
        assertEquals((Integer) expectedCovered[5].size(), calculatorFunction.apply(queries[5]));
        assertEquals((Integer) expectedCovered[6].size(), calculatorFunction.apply(queries[6]));
        assertEquals((Integer) expectedCovered[7].size(), calculatorFunction.apply(queries[7]));
        assertEquals((Integer) expectedCovered[8].size(), calculatorFunction.apply(queries[8]));
        assertEquals((Integer) expectedCovered[9].size(), calculatorFunction.apply(queries[9]));
        assertEquals((Integer) expectedCovered[10].size(), calculatorFunction.apply(queries[10]));
        assertEquals((Integer) expectedCovered[11].size(), calculatorFunction.apply(queries[11]));
        assertEquals((Integer) expectedCovered[12].size(), calculatorFunction.apply(queries[12]));
        assertEquals((Integer) expectedCovered[13].size(), calculatorFunction.apply(queries[13]));
    }

    protected void assertNumberCorrectlyCovered(BiFunction<Map<CategoricalFeature, Set<Integer>>, Integer, Integer>
                                                  calculatorFunction) {
        assertEquals((Integer) expectedCorrectlyCovered[0].size(),  calculatorFunction.apply(queries[0], 0));
        assertEquals((Integer) expectedCorrectlyCovered[1].size(),  calculatorFunction.apply(queries[1], 0));
        assertEquals((Integer) expectedCorrectlyCovered[2].size(),  calculatorFunction.apply(queries[2], 0));
        assertEquals((Integer) expectedCorrectlyCovered[3].size(),  calculatorFunction.apply(queries[3], 0));
        assertEquals((Integer) expectedCorrectlyCovered[4].size(),  calculatorFunction.apply(queries[4], 0));
        assertEquals((Integer) expectedCorrectlyCovered[5].size(),  calculatorFunction.apply(queries[4], 1));
        assertEquals((Integer) expectedCorrectlyCovered[6].size(),  calculatorFunction.apply(queries[5], 0));
        assertEquals((Integer) expectedCorrectlyCovered[7].size(),  calculatorFunction.apply(queries[5], 1));
        assertEquals((Integer) expectedCorrectlyCovered[8].size(),  calculatorFunction.apply(queries[7], 0));
        assertEquals((Integer) expectedCorrectlyCovered[9].size(),  calculatorFunction.apply(queries[7], 1));
        assertEquals((Integer) expectedCorrectlyCovered[10].size(), calculatorFunction.apply(queries[8], 0));
        assertEquals((Integer) expectedCorrectlyCovered[10].size(), calculatorFunction.apply(queries[8], 1));
        assertEquals((Integer) expectedCorrectlyCovered[11].size(), calculatorFunction.apply(queries[9], 0));
        assertEquals((Integer) expectedCorrectlyCovered[12].size(), calculatorFunction.apply(queries[9], 1));
        assertEquals((Integer) expectedCorrectlyCovered[13].size(), calculatorFunction.apply(queries[10], 0));
        assertEquals((Integer) expectedCorrectlyCovered[14].size(), calculatorFunction.apply(queries[10], 1));
        assertEquals((Integer) expectedCorrectlyCovered[15].size(), calculatorFunction.apply(queries[11], 0));
        assertEquals((Integer) expectedCorrectlyCovered[16].size(), calculatorFunction.apply(queries[12], 0));
        assertEquals((Integer) expectedCorrectlyCovered[17].size(), calculatorFunction.apply(queries[12], 1));
        assertEquals((Integer) expectedCorrectlyCovered[18].size(), calculatorFunction.apply(queries[13], 0));
        assertEquals((Integer) expectedCorrectlyCovered[19].size(), calculatorFunction.apply(queries[14], 0));
        assertEquals((Integer) expectedCorrectlyCovered[20].size(), calculatorFunction.apply(queries[14], 1));
    }

    protected void assertNumberIncorrectlyCovered(BiFunction<Map<CategoricalFeature, Set<Integer>>, Integer, Integer>
                                                    calculatorFunction) {
        assertEquals((Integer) expectedIncorrectlyCovered[0].size(), calculatorFunction.apply(queries[4], 0));
        assertEquals((Integer) expectedIncorrectlyCovered[1].size(), calculatorFunction.apply(queries[4], 1));
        assertEquals((Integer) expectedIncorrectlyCovered[2].size(), calculatorFunction.apply(queries[5], 0));
        assertEquals((Integer) expectedIncorrectlyCovered[3].size(), calculatorFunction.apply(queries[5], 1));
        assertEquals((Integer) expectedIncorrectlyCovered[4].size(), calculatorFunction.apply(queries[9], 0));
        assertEquals((Integer) expectedIncorrectlyCovered[5].size(), calculatorFunction.apply(queries[9], 1));
        assertEquals((Integer) expectedIncorrectlyCovered[6].size(), calculatorFunction.apply(queries[11],1));
        assertEquals((Integer) expectedIncorrectlyCovered[7].size(), calculatorFunction.apply(queries[14],0));
        assertEquals((Integer) expectedIncorrectlyCovered[8].size(), calculatorFunction.apply(queries[14],1));
    }

    protected void assertNumberCorrectlyNotCovered(BiFunction<Map<CategoricalFeature, Set<Integer>>, Integer, Integer>
                                                     calculatorFunction) {
        assertEquals((Integer) expectedCorrectlyNotCovered[0].size(), calculatorFunction.apply(queries[0], 0));
        assertEquals((Integer) expectedCorrectlyNotCovered[1].size(), calculatorFunction.apply(queries[0], 1));
        assertEquals((Integer) expectedCorrectlyNotCovered[2].size(), calculatorFunction.apply(queries[4], 0));
        assertEquals((Integer) expectedCorrectlyNotCovered[3].size(), calculatorFunction.apply(queries[4], 1));
        assertEquals((Integer) expectedCorrectlyNotCovered[4].size(), calculatorFunction.apply(queries[5], 0));
        assertEquals((Integer) expectedCorrectlyNotCovered[5].size(), calculatorFunction.apply(queries[6], 0));
        assertEquals((Integer) expectedCorrectlyNotCovered[6].size(), calculatorFunction.apply(queries[6], 1));
        assertEquals((Integer) expectedCorrectlyNotCovered[7].size(), calculatorFunction.apply(queries[14],0));
        assertEquals((Integer) expectedCorrectlyNotCovered[8].size(), calculatorFunction.apply(queries[14],1));
    }

    protected void assertNumberIncorrectlyNotCovered(BiFunction<Map<CategoricalFeature, Set<Integer>>, Integer, Integer>
                                                       calculatorFunction) {
        assertEquals((Integer) expectedIncorrectlyNotCovered[0].size(),  calculatorFunction.apply(queries[0], 0));
        assertEquals((Integer) expectedIncorrectlyNotCovered[1].size(),  calculatorFunction.apply(queries[0], 1));
        assertEquals((Integer) expectedIncorrectlyNotCovered[2].size(),  calculatorFunction.apply(queries[3], 0));
        assertEquals((Integer) expectedIncorrectlyNotCovered[3].size(),  calculatorFunction.apply(queries[3], 1));
        assertEquals((Integer) expectedIncorrectlyNotCovered[4].size(),  calculatorFunction.apply(queries[5], 0));
        assertEquals((Integer) expectedIncorrectlyNotCovered[5].size(),  calculatorFunction.apply(queries[6], 0));
        assertEquals((Integer) expectedIncorrectlyNotCovered[6].size(),  calculatorFunction.apply(queries[6], 1));
        assertEquals((Integer) expectedIncorrectlyNotCovered[7].size(),  calculatorFunction.apply(queries[9], 0));
        assertEquals((Integer) expectedIncorrectlyNotCovered[8].size(),  calculatorFunction.apply(queries[9], 1));
        assertEquals((Integer) expectedIncorrectlyNotCovered[9].size(),  calculatorFunction.apply(queries[13],0));
        assertEquals((Integer) expectedIncorrectlyNotCovered[10].size(), calculatorFunction.apply(queries[13],1));
    }

    protected void assertNumberInMinimalAmountCovers(CategoricalCalculator calculator, int labelValue) {
        int[] numberInMinimalAmountCovers0  = calculator.getMinimalAmountNumberInCovers(queries[0], labelValue);
        int[] numberInMinimalAmountCovers1  = calculator.getMinimalAmountNumberInCovers(queries[1], labelValue);
        int[] numberInMinimalAmountCovers2  = calculator.getMinimalAmountNumberInCovers(queries[2], labelValue);
        int[] numberInMinimalAmountCovers3  = calculator.getMinimalAmountNumberInCovers(queries[3], labelValue);
        int[] numberInMinimalAmountCovers4  = calculator.getMinimalAmountNumberInCovers(queries[4], labelValue);
        int[] numberInMinimalAmountCovers5  = calculator.getMinimalAmountNumberInCovers(queries[5], labelValue);
        int[] numberInMinimalAmountCovers6  = calculator.getMinimalAmountNumberInCovers(queries[6], labelValue);
        int[] numberInMinimalAmountCovers7  = calculator.getMinimalAmountNumberInCovers(queries[7], labelValue);
        int[] numberInMinimalAmountCovers8  = calculator.getMinimalAmountNumberInCovers(queries[8], labelValue);
        int[] numberInMinimalAmountCovers9  = calculator.getMinimalAmountNumberInCovers(queries[9], labelValue);
        int[] numberInMinimalAmountCovers10 = calculator.getMinimalAmountNumberInCovers(queries[10], labelValue);
        int[] numberInMinimalAmountCovers11 = calculator.getMinimalAmountNumberInCovers(queries[11], labelValue);
        int[] numberInMinimalAmountCovers12 = calculator.getMinimalAmountNumberInCovers(queries[12], labelValue);
        int[] numberInMinimalAmountCovers13 = calculator.getMinimalAmountNumberInCovers(queries[13], labelValue);
        int[] numberInMinimalAmountCovers14 = calculator.getMinimalAmountNumberInCovers(queries[14], labelValue);

        int[] numberInMinimalAmountCovers0_single = singleQueryMinimalCovers(calculator, queries[0], labelValue);
        int[] numberInMinimalAmountCovers1_single = singleQueryMinimalCovers(calculator, queries[1], labelValue);
        int[] numberInMinimalAmountCovers2_single = singleQueryMinimalCovers(calculator, queries[2], labelValue);
        int[] numberInMinimalAmountCovers3_single = singleQueryMinimalCovers(calculator, queries[3], labelValue);
        int[] numberInMinimalAmountCovers4_single = singleQueryMinimalCovers(calculator, queries[4], labelValue);
        int[] numberInMinimalAmountCovers5_single = singleQueryMinimalCovers(calculator, queries[5], labelValue);
        int[] numberInMinimalAmountCovers6_single = singleQueryMinimalCovers(calculator, queries[6], labelValue);
        int[] numberInMinimalAmountCovers7_single = singleQueryMinimalCovers(calculator, queries[7], labelValue);
        int[] numberInMinimalAmountCovers8_single = singleQueryMinimalCovers(calculator, queries[8], labelValue);
        int[] numberInMinimalAmountCovers9_single = singleQueryMinimalCovers(calculator, queries[9], labelValue);
        int[] numberInMinimalAmountCovers10_single = singleQueryMinimalCovers(calculator, queries[10], labelValue);
        int[] numberInMinimalAmountCovers11_single = singleQueryMinimalCovers(calculator, queries[11], labelValue);
        int[] numberInMinimalAmountCovers12_single = singleQueryMinimalCovers(calculator, queries[12], labelValue);
        int[] numberInMinimalAmountCovers13_single = singleQueryMinimalCovers(calculator, queries[13], labelValue);
        int[] numberInMinimalAmountCovers14_single = singleQueryMinimalCovers(calculator, queries[14], labelValue);

        assertArrayEquals(numberInMinimalAmountCovers0, numberInMinimalAmountCovers0_single);
        assertArrayEquals(numberInMinimalAmountCovers1, numberInMinimalAmountCovers1_single);
        assertArrayEquals(numberInMinimalAmountCovers2, numberInMinimalAmountCovers2_single);
        assertArrayEquals(numberInMinimalAmountCovers3, numberInMinimalAmountCovers3_single);
        assertArrayEquals(numberInMinimalAmountCovers4, numberInMinimalAmountCovers4_single);
        assertArrayEquals(numberInMinimalAmountCovers5, numberInMinimalAmountCovers5_single);
        assertArrayEquals(numberInMinimalAmountCovers6, numberInMinimalAmountCovers6_single);
        assertArrayEquals(numberInMinimalAmountCovers7, numberInMinimalAmountCovers7_single);
        assertArrayEquals(numberInMinimalAmountCovers8, numberInMinimalAmountCovers8_single);
        assertArrayEquals(numberInMinimalAmountCovers9, numberInMinimalAmountCovers9_single);
        assertArrayEquals(numberInMinimalAmountCovers10, numberInMinimalAmountCovers10_single);
        assertArrayEquals(numberInMinimalAmountCovers11, numberInMinimalAmountCovers11_single);
        assertArrayEquals(numberInMinimalAmountCovers12, numberInMinimalAmountCovers12_single);
        assertArrayEquals(numberInMinimalAmountCovers13, numberInMinimalAmountCovers13_single);
        assertArrayEquals(numberInMinimalAmountCovers14, numberInMinimalAmountCovers14_single);
    }



    // Assumes all other tests passed.
    protected void assertMinimalAmountCovers(BiFunction<Map<CategoricalFeature, Set<Integer>>, Integer, Set<Integer>[]>
                                                     calculatorFunction,
                                             Function<Map<CategoricalFeature, Set<Integer>>, Set<Integer>>
                                                     calculatorFunction0,
                                             BiFunction<Map<CategoricalFeature, Set<Integer>>, Integer, Set<Integer>>
                                                     calculatorFunction1,
                                             BiFunction<Map<CategoricalFeature, Set<Integer>>, Integer, Set<Integer>>
                                                     calculatorFunction2) {
        assertMinimalCoversForLabelValue(calculatorFunction, calculatorFunction0, calculatorFunction1, calculatorFunction2,  0);
        assertMinimalCoversForLabelValue(calculatorFunction, calculatorFunction0, calculatorFunction1, calculatorFunction2, 1);
    }

    protected void assertMinimalCoversForLabelValue(BiFunction<Map<CategoricalFeature, Set<Integer>>, Integer, Set<Integer>[]>
                                                            calculatorFunction,
                                                    Function<Map<CategoricalFeature, Set<Integer>>, Set<Integer>>
                                                            calculatorFunction0,
                                                    BiFunction<Map<CategoricalFeature, Set<Integer>>, Integer, Set<Integer>>
                                                            calculatorFunction1,
                                                    BiFunction<Map<CategoricalFeature, Set<Integer>>, Integer, Set<Integer>>
                                                            calculatorFunction2,
                                                    int labelValue) {
        Set<Integer>[]  minimalSetOfCovers0  = calculatorFunction.apply(queries[0], labelValue);
        Set<Integer>[]  minimalSetOfCovers1  = calculatorFunction.apply(queries[1], labelValue);
        Set<Integer>[]  minimalSetOfCovers2  = calculatorFunction.apply(queries[2], labelValue);
        Set<Integer>[]  minimalSetOfCovers3  = calculatorFunction.apply(queries[3], labelValue);
        Set<Integer>[]  minimalSetOfCovers4  = calculatorFunction.apply(queries[4], labelValue);
        Set<Integer>[]  minimalSetOfCovers5  = calculatorFunction.apply(queries[5], labelValue);
        Set<Integer>[]  minimalSetOfCovers6  = calculatorFunction.apply(queries[6], labelValue);
        Set<Integer>[]  minimalSetOfCovers7  = calculatorFunction.apply(queries[7], labelValue);
        Set<Integer>[]  minimalSetOfCovers8  = calculatorFunction.apply(queries[8], labelValue);
        Set<Integer>[]  minimalSetOfCovers9  = calculatorFunction.apply(queries[9], labelValue);
        Set<Integer>[] minimalSetOfCovers10 = calculatorFunction.apply(queries[10], labelValue);
        Set<Integer>[] minimalSetOfCovers11 = calculatorFunction.apply(queries[11], labelValue);
        Set<Integer>[] minimalSetOfCovers12 = calculatorFunction.apply(queries[12], labelValue);
        Set<Integer>[] minimalSetOfCovers13 = calculatorFunction.apply(queries[13], labelValue);
        Set<Integer>[] minimalSetOfCovers14 = calculatorFunction.apply(queries[14], labelValue);

        Set<Integer>[]  minimalSetOfCovers0_single = singleQueryMinimalCovers(
                calculatorFunction0,
                calculatorFunction1,
                calculatorFunction2,
                queries[0],
                labelValue
        );
        Set<Integer>[]  minimalSetOfCovers1_single = singleQueryMinimalCovers(
                calculatorFunction0,
                calculatorFunction1,
                calculatorFunction2,
                queries[1],
                labelValue
        );
        Set<Integer>[]  minimalSetOfCovers2_single = singleQueryMinimalCovers(
                calculatorFunction0,
                calculatorFunction1,
                calculatorFunction2,
                queries[2],
                labelValue
        );
        Set<Integer>[]  minimalSetOfCovers3_single = singleQueryMinimalCovers(
                calculatorFunction0,
                calculatorFunction1,
                calculatorFunction2,
                queries[3],
                labelValue
        );
        Set<Integer>[]  minimalSetOfCovers4_single = singleQueryMinimalCovers(
                calculatorFunction0,
                calculatorFunction1,
                calculatorFunction2,
                queries[4],
                labelValue
        );
        Set<Integer>[]  minimalSetOfCovers5_single = singleQueryMinimalCovers(
                calculatorFunction0,
                calculatorFunction1,
                calculatorFunction2,
                queries[5],
                labelValue
        );
        Set<Integer>[]  minimalSetOfCovers6_single = singleQueryMinimalCovers(
                calculatorFunction0,
                calculatorFunction1,
                calculatorFunction2,
                queries[6],
                labelValue
        );
        Set<Integer>[]  minimalSetOfCovers7_single = singleQueryMinimalCovers(
                calculatorFunction0,
                calculatorFunction1,
                calculatorFunction2,
                queries[7],
                labelValue
        );
        Set<Integer>[]  minimalSetOfCovers8_single = singleQueryMinimalCovers(
                calculatorFunction0,
                calculatorFunction1,
                calculatorFunction2,
                queries[8],
                labelValue
        );
        Set<Integer>[]  minimalSetOfCovers9_single = singleQueryMinimalCovers(
                calculatorFunction0,
                calculatorFunction1,
                calculatorFunction2,
                queries[9],
                labelValue
        );
        Set<Integer>[] minimalSetOfCovers10_single = singleQueryMinimalCovers(
                calculatorFunction0,
                calculatorFunction1,
                calculatorFunction2,
                queries[10],
                labelValue
        );
        Set<Integer>[] minimalSetOfCovers11_single = singleQueryMinimalCovers(
                calculatorFunction0,
                calculatorFunction1,
                calculatorFunction2,
                queries[11],
                labelValue
        );
        Set<Integer>[] minimalSetOfCovers12_single = singleQueryMinimalCovers(
                calculatorFunction0,
                calculatorFunction1,
                calculatorFunction2,
                queries[12],
                labelValue
        );
        Set<Integer>[] minimalSetOfCovers13_single = singleQueryMinimalCovers(
                calculatorFunction0,
                calculatorFunction1,
                calculatorFunction2,
                queries[13],
                labelValue
        );
        Set<Integer>[] minimalSetOfCovers14_single = singleQueryMinimalCovers(
                calculatorFunction0,
                calculatorFunction1,
                calculatorFunction2,
                queries[14],
                labelValue
        );

        assertArrayEquals( minimalSetOfCovers0,   minimalSetOfCovers0_single);
        assertArrayEquals( minimalSetOfCovers1,   minimalSetOfCovers1_single);
        assertArrayEquals( minimalSetOfCovers2,   minimalSetOfCovers2_single);
        assertArrayEquals( minimalSetOfCovers3,   minimalSetOfCovers3_single);
        assertArrayEquals( minimalSetOfCovers4,   minimalSetOfCovers4_single);
        assertArrayEquals( minimalSetOfCovers5,   minimalSetOfCovers5_single);
        assertArrayEquals( minimalSetOfCovers6,   minimalSetOfCovers6_single);
        assertArrayEquals( minimalSetOfCovers7,   minimalSetOfCovers7_single);
        assertArrayEquals( minimalSetOfCovers8,   minimalSetOfCovers8_single);
        assertArrayEquals( minimalSetOfCovers9,   minimalSetOfCovers9_single);
        assertArrayEquals(minimalSetOfCovers10,  minimalSetOfCovers10_single);
        assertArrayEquals(minimalSetOfCovers11,  minimalSetOfCovers11_single);
        assertArrayEquals(minimalSetOfCovers12,  minimalSetOfCovers12_single);
        assertArrayEquals(minimalSetOfCovers13,  minimalSetOfCovers13_single);
        assertArrayEquals(minimalSetOfCovers14,  minimalSetOfCovers14_single);
    }

    protected Set<Integer>[] singleQueryMinimalCovers(Function<Map<CategoricalFeature, Set<Integer>>, Set<Integer>>
                                                              calculatorFunction0,
                                                      BiFunction<Map<CategoricalFeature, Set<Integer>>, Integer, Set<Integer>>
                                                              calculatorFunction1,
                                                      BiFunction<Map<CategoricalFeature, Set<Integer>>, Integer, Set<Integer>>
                                                              calculatorFunction2,
                                                      Map<CategoricalFeature, Set<Integer>> query,
                                                      int labelValue) {
        Set<Integer>[] minimalCovers = new HashSet[3];
        minimalCovers[0] = calculatorFunction0.apply(query);
        minimalCovers[1] = calculatorFunction1.apply(query, labelValue);
        minimalCovers[2] = calculatorFunction2.apply(query, labelValue);
        return minimalCovers;
    }

    protected int[] singleQueryMinimalCovers(CategoricalCalculator categoricalCalculator,
                                             Map<CategoricalFeature, Set<Integer>> query,
                                             int labelValue) {
        return new int[] {
                categoricalCalculator.getNumberCovered(query),
                categoricalCalculator.getNumberCorrectlyCovered(query, labelValue),
                categoricalCalculator.getNumberIncorrectlyNotCovered(query, labelValue)
        };
    }
}