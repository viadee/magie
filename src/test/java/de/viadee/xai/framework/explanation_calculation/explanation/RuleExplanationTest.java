package de.viadee.xai.framework.explanation_calculation.explanation;

import de.viadee.xai.framework.TestUtilityData;
import de.viadee.xai.framework.data.Feature;
import de.viadee.xai.framework.data.index.RoaringBitmapIndex;
import de.viadee.xai.framework.explanation_calculation.calculator.RoaringBitmapCalculator;
import de.viadee.xai.framework.explanation_calculation.calculator.SimpleRoaringBitmapCalculator;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;

/**
 * Class testing {@link StdRuleExplanation} and {@link MinimalBitmapCoversRuleExplanation}.
 * Again, the data of {@link TestUtilityData} is utilized.
 * The calculator-classes are used as the ground truth.
 */
public class RuleExplanationTest {
    protected RoaringBitmapIndex index;
    protected Map<Feature.CategoricalFeature, Set<Integer>>[] queries;
    protected RoaringBitmapCalculator calculator;
    protected Feature.CategoricalFeature labelFeature;

    @Before
    public void setup() {
        TestUtilityData data = new TestUtilityData();

        data.setUp();

        index = data.getRoaringBitmapIndex();
        queries = data.getQueries();
        calculator = new SimpleRoaringBitmapCalculator(index);
        labelFeature = calculator.getDataset().getProcessedLabelCol().getLabel();
    }

    @Test
    public void testStdRuleExplanation() {
        RuleExplanationFactory numberInstancesFactory = new StdRuleExplanationFactory(calculator);
        checkAllQueries(numberInstancesFactory, 0);
        checkAllQueries(numberInstancesFactory, 1);
    }

    @Test
    public void testMinimalBitmapCoversRuleExplanation() {
        RuleExplanationFactory minimalCoversFactory = new MinimalCoversRuleExplanationFactory(calculator);
        checkAllQueries(minimalCoversFactory, 0);
        checkAllQueries(minimalCoversFactory, 1);
    }

    // For all queries specified in TestUtilityData, evaluates if the created rule has the same
    // representation as the calculators.
    protected void checkAllQueries(RuleExplanationFactory factory,
                                 int labelValue) {
        for (Map<Feature.CategoricalFeature, Set<Integer>> currentQuery : queries) {
            checkCovers(factory, currentQuery, labelFeature, labelValue);
        }
        for (Map<Feature.CategoricalFeature, Set<Integer>> currentQuery : queries) {
            checkNumbers(factory, currentQuery, labelFeature, labelValue);
        }
    }


    // Check whether the overall covers are correctly calculated.
    protected void checkCovers(RuleExplanationFactory factory,
                             Map<Feature.CategoricalFeature, Set<Integer>> conditions,
                             Feature.CategoricalFeature labelFeature,
                             int labelValue) {
        RuleExplanation explanation = factory.initialize(new HashMap<>(conditions), labelFeature, labelValue);
        assertEquals(calculator.getCoveredAsBitmap(conditions), explanation.getCoverAsBitmap());
        assertEquals(
                calculator.getCorrectlyCoveredAsBitmap(conditions, labelValue),
                explanation.getCorrectCoverAsBitmap()
        );
        assertEquals(
                calculator.getIncorrectlyCoveredAsBitmap(conditions, labelValue),
                explanation.getIncorrectCoverAsBitmap()
        );
        assertEquals(
                calculator.getCorrectlyNotCoveredAsBitmap(conditions, labelValue),
                explanation.getCorrectlyNotCoveredAsBitmap()
        );
        assertEquals(
                calculator.getIncorrectlyNotCoveredAsBitmap(conditions, labelValue),
                explanation.getIncorrectlyNotCoveredAsBitmap()
        );
    }

    // Check whether the number of instances within each cover is correct.
    protected void checkNumbers(RuleExplanationFactory factory,
                              Map<Feature.CategoricalFeature, Set<Integer>> conditions,
                              Feature.CategoricalFeature labelFeature,
                              int labelValue) {
        RuleExplanation explanation = factory.initialize(new HashMap<>(conditions), labelFeature, labelValue);
        assertEquals(calculator.getNumberCovered(conditions), explanation.getNumberCovered());
        assertEquals(
                calculator.getNumberCorrectlyCovered(conditions, labelValue),
                explanation.getNumberCorrectlyCovered()
        );
        assertEquals(
                calculator.getNumberIncorrectlyCovered(conditions, labelValue),
                explanation.getNumberIncorrectlyCovered()
        );
        assertEquals(
                calculator.getNumberCorrectlyNotCovered(conditions, labelValue),
                explanation.getNumberCorrectlyNotCovered()
        );
        assertEquals(
                calculator.getNumberIncorrectlyNotCovered(conditions, labelValue),
                explanation.getNumberIncorrectlyNotCovered()
        );
    }
}
