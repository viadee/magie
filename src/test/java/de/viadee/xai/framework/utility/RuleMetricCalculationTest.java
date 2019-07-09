package de.viadee.xai.framework.utility;

import de.viadee.xai.framework.TestUtilityData;
import de.viadee.xai.framework.data.Feature;
import de.viadee.xai.framework.explanation_calculation.calculator.RoaringBitmapCalculator;
import de.viadee.xai.framework.explanation_calculation.calculator.SimpleRoaringBitmapCalculator;
import de.viadee.xai.framework.explanation_calculation.explanation.RuleExplanation;
import de.viadee.xai.framework.explanation_calculation.explanation.RuleExplanationFactory;
import de.viadee.xai.framework.explanation_calculation.explanation.StdRuleExplanationFactory;
import org.junit.Before;
import org.junit.Test;
import org.roaringbitmap.buffer.MutableRoaringBitmap;

import static de.viadee.xai.framework.utility.RuleMetricCalculation.*;
import static org.junit.Assert.assertEquals;

/**
 * Checks some of the functions of {@link RuleMetricCalculation}.
 */
public class RuleMetricCalculationTest {

    protected final double delta = 10e-12;
    protected TestUtilityData testUtilityData;
    protected Feature.CategoricalFeature labelFeature;
    protected RoaringBitmapCalculator calculator;
    protected RuleExplanationFactory factory;

    @Before
    public void setup() {
        testUtilityData = new TestUtilityData();
        testUtilityData.setUp();
        labelFeature = testUtilityData.getDataset().getProcessedLabelCol().getLabel();
        calculator = new SimpleRoaringBitmapCalculator(testUtilityData.getRoaringBitmapIndex());
        factory = new StdRuleExplanationFactory(calculator);
    }

    @Test
    public void testCalculateJaccardSimilarityAndDissimilarity() {
        MutableRoaringBitmap bm1 = new MutableRoaringBitmap();
        bm1.add(1);
        bm1.add(2);
        bm1.add(3);
        bm1.add(4);
        bm1.add(6);
        bm1.add(8);
        bm1.add(10);
        bm1.add(12);

        MutableRoaringBitmap bm2 = new MutableRoaringBitmap();
        bm2.add(1);
        bm2.add(2);
        bm2.add(3);
        bm2.add(4);
        bm2.add(5);
        bm2.add(7);
        bm2.add(9);
        bm2.add(11);

        double jaccardSimilarity = calculateJaccardSimilarity(bm1, bm2);

        double jaccardDissimilarity = calculateJaccardDissimilarity(bm1, bm2);

        assertEquals(jaccardSimilarity, 1.0/3, delta);
        assertEquals(jaccardDissimilarity, 1 - (1.0/3), delta);
    }

    @Test
    public void testCalculateRecall() {
        RuleExplanation re0 = factory.initialize(testUtilityData.getQueries()[0], labelFeature, 0);
        RuleExplanation re1 = factory.initialize(testUtilityData.getQueries()[0], labelFeature, 1);
        assertEquals(0.2, calculateRecall(re0), delta);
        assertEquals(0, calculateRecall(re1), delta);


        RuleExplanation re2 = factory.initialize(testUtilityData.getQueries()[9], labelFeature, 1);
        RuleExplanation re3 = factory.initialize(testUtilityData.getQueries()[3], labelFeature, 1);
        assertEquals((2.0/15), calculateRecall(re2), delta);
        assertEquals(0, calculateRecall(re3), delta);
    }

    @Test
    public void testCalculateAccuracy() {
        RuleExplanation re0 = factory.initialize(testUtilityData.getQueries()[0], labelFeature, 0);
        assertEquals(0.8, calculateAccuracy(re0), delta);

        RuleExplanation re1 = factory.initialize(testUtilityData.getQueries()[0], labelFeature, 1);
        assertEquals(0.2, calculateAccuracy(re1), delta);

        RuleExplanation re2 = factory.initialize(testUtilityData.getQueries()[5], labelFeature, 0);
        assertEquals(0.25, calculateAccuracy(re2), delta);

        RuleExplanation re3 = factory.initialize(testUtilityData.getQueries()[9], labelFeature, 1);
        assertEquals(0.35, calculateAccuracy(re3), delta);
    }
}