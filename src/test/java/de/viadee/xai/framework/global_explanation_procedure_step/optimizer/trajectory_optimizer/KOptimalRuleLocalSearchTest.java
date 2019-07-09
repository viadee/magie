package de.viadee.xai.framework.global_explanation_procedure_step.optimizer.trajectory_optimizer;

import de.viadee.xai.framework.TestUtilityData;
import de.viadee.xai.framework.data.Feature;
import de.viadee.xai.framework.explanation_calculation.explanation.RuleExplanation;
import de.viadee.xai.framework.global_explanation_procedure_step.optimizer.ObjectiveFunction;
import de.viadee.xai.framework.global_explanation_procedure_step.optimizer.representation_translator.BinaryRepresentationToRuleExplanation;
import de.viadee.xai.framework.utility.Utility;
import org.junit.Test;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertArrayEquals;

/**
 * Tests the {@link KOptimalRuleLocalSearch}.
 */
public class KOptimalRuleLocalSearchTest {


    @Test
    public void optimize() {
        // Due to the required structure of optimizers, first, a search space must be created.
        // The characteristics of this search space does not matter, as dummy objective functions are used which
        // solely count bits.
        DummyRuleExplanationFactory factory = new DummyRuleExplanationFactory();
        TestUtilityData testData = new TestUtilityData();
        testData.setUp();
        Map<Feature.CategoricalFeature, Set<Integer>>[] queries = testData.getQueries();

        Set<RuleExplanation> ruleExplanations = new HashSet<>();

        // For each query, a dummy rule explanation is created.
        for (Map<Feature.CategoricalFeature, Set<Integer>> query : queries) {
            ruleExplanations.add(factory.initialize(query, null, 0));
        }

        DummyRuleExplanationSet dummyRepresentationSpace = new DummyRuleExplanationSet(ruleExplanations);

        BinaryRepresentationToRuleExplanation translator = new BinaryRepresentationToRuleExplanation();
        translator.initialize(dummyRepresentationSpace, factory);
        // Overall representation space is 18 condition values long. This does not matter for the subsequent tests,
        // as this objective function does not care for the features of the concrete rules.

        // The following binary strings represent rule explanations which will be tested.
        boolean[] binaryString0 = new boolean[] {false};
        boolean[] binaryString1 = new boolean[] {true};
        boolean[] binaryString2 = new boolean[] {false, false, false};
        boolean[] binaryString3 = new boolean[] {false, true, false};
        boolean[] binaryString4 = new boolean[] {true, false, true};
        boolean[] binaryString5 = new boolean[] {false, false, true};
        boolean[] binaryString6 = new boolean[] {true, true, true, true};
        boolean[] binaryString7 = new boolean[] {false, false, true, false, true, true, false};


        // Check with bit-count objective function for k = 1, and k = 2.
        ObjectiveFunction<RuleExplanation, Double> objectiveFunction0 = new CountObjectiveFunction();
        KOptimalRuleLocalSearch optimizer = new KOptimalRuleLocalSearch(1, translator, objectiveFunction0);
        boolean[] testBinaryString0_0 = optimizer.optimizeBoolAr(binaryString0);
        boolean[] testBinaryString1_0 = optimizer.optimizeBoolAr(binaryString1);
        boolean[] testBinaryString2_0 = optimizer.optimizeBoolAr(binaryString2);
        boolean[] testBinaryString3_0 = optimizer.optimizeBoolAr(binaryString3);
        boolean[] testBinaryString4_0 = optimizer.optimizeBoolAr(binaryString4);
        boolean[] testBinaryString5_0 = optimizer.optimizeBoolAr(binaryString5);
        boolean[] testBinaryString6_0 = optimizer.optimizeBoolAr(binaryString6);
        boolean[] testBinaryString7_0 = optimizer.optimizeBoolAr(binaryString7);
        assertArrayEquals(new boolean[] {true}, testBinaryString0_0);
        assertArrayEquals(new boolean[] {true}, testBinaryString1_0);
        assertArrayEquals(new boolean[] {true, true, true}, testBinaryString2_0);
        assertArrayEquals(new boolean[] {true, true, true}, testBinaryString3_0);
        assertArrayEquals(new boolean[] {true, true, true}, testBinaryString4_0);
        assertArrayEquals(new boolean[] {true, true, true}, testBinaryString5_0);
        assertArrayEquals(new boolean[] {true, true, true, true}, testBinaryString6_0);
        assertArrayEquals(new boolean[] {true, true, true, true, true, true, true}, testBinaryString7_0);

        optimizer = new KOptimalRuleLocalSearch(2, translator, objectiveFunction0);
        testBinaryString0_0 = optimizer.optimizeBoolAr(binaryString0);
        testBinaryString1_0 = optimizer.optimizeBoolAr(binaryString1);
        testBinaryString2_0 = optimizer.optimizeBoolAr(binaryString2);
        testBinaryString3_0 = optimizer.optimizeBoolAr(binaryString3);
        testBinaryString4_0 = optimizer.optimizeBoolAr(binaryString4);
        testBinaryString5_0 = optimizer.optimizeBoolAr(binaryString5);
        testBinaryString6_0 = optimizer.optimizeBoolAr(binaryString6);
        testBinaryString7_0 = optimizer.optimizeBoolAr(binaryString7);
        assertArrayEquals(new boolean[] {true}, testBinaryString0_0);
        assertArrayEquals(new boolean[] {true}, testBinaryString1_0);
        assertArrayEquals(new boolean[] {true, true, true}, testBinaryString2_0);
        assertArrayEquals(new boolean[] {true, true, true}, testBinaryString3_0);
        assertArrayEquals(new boolean[] {true, true, true}, testBinaryString4_0);
        assertArrayEquals(new boolean[] {true, true, true}, testBinaryString5_0);
        assertArrayEquals(new boolean[] {true, true, true, true}, testBinaryString6_0);
        assertArrayEquals(new boolean[] {true, true, true, true, true, true, true}, testBinaryString7_0);

        // Check with bit-count objective function which is better with first bit turned off.
        ObjectiveFunction<RuleExplanation, Double> objectiveFunction1 =
                new CountObjectiveFunctionExcept(
                        new int[] {0},
                        Utility.transformConditionsMapToArray(dummyRepresentationSpace)
                );
        KOptimalRuleLocalSearch optimizerExcept_0 = new KOptimalRuleLocalSearch(1, translator, objectiveFunction1);
        boolean[] testBinaryString0_1 = optimizerExcept_0.optimizeBoolAr(binaryString0);
        boolean[] testBinaryString1_1 = optimizerExcept_0.optimizeBoolAr(binaryString1);
        boolean[] testBinaryString2_1 = optimizerExcept_0.optimizeBoolAr(binaryString2);
        boolean[] testBinaryString3_1 = optimizerExcept_0.optimizeBoolAr(binaryString3);
        boolean[] testBinaryString4_1 = optimizerExcept_0.optimizeBoolAr(binaryString4);
        boolean[] testBinaryString5_1 = optimizerExcept_0.optimizeBoolAr(binaryString5);
        boolean[] testBinaryString6_1 = optimizerExcept_0.optimizeBoolAr(binaryString6);
        boolean[] testBinaryString7_1 = optimizerExcept_0.optimizeBoolAr(binaryString7);
        assertArrayEquals(new boolean[] {false}, testBinaryString0_1);
        assertArrayEquals(new boolean[] {false}, testBinaryString1_1);
        assertArrayEquals(new boolean[] {false, true, true}, testBinaryString2_1);
        assertArrayEquals(new boolean[] {false, true, true}, testBinaryString3_1);
        assertArrayEquals(new boolean[] {false, true, true}, testBinaryString4_1);
        assertArrayEquals(new boolean[] {false, true, true}, testBinaryString5_1);
        assertArrayEquals(new boolean[] {false, true, true, true}, testBinaryString6_1);
        assertArrayEquals(new boolean[] {false, true, true, true, true, true, true}, testBinaryString7_1);

        optimizerExcept_0 = new KOptimalRuleLocalSearch(2, translator, objectiveFunction1);
        testBinaryString0_1 = optimizerExcept_0.optimizeBoolAr(binaryString0);
        testBinaryString1_1 = optimizerExcept_0.optimizeBoolAr(binaryString1);
        testBinaryString2_1 = optimizerExcept_0.optimizeBoolAr(binaryString2);
        testBinaryString3_1 = optimizerExcept_0.optimizeBoolAr(binaryString3);
        testBinaryString4_1 = optimizerExcept_0.optimizeBoolAr(binaryString4);
        testBinaryString5_1 = optimizerExcept_0.optimizeBoolAr(binaryString5);
        testBinaryString6_1 = optimizerExcept_0.optimizeBoolAr(binaryString6);
        testBinaryString7_1 = optimizerExcept_0.optimizeBoolAr(binaryString7);
        assertArrayEquals(new boolean[] {false}, testBinaryString0_1);
        assertArrayEquals(new boolean[] {false}, testBinaryString1_1);
        assertArrayEquals(new boolean[] {false, true, true}, testBinaryString2_1);
        assertArrayEquals(new boolean[] {false, true, true}, testBinaryString3_1);
        assertArrayEquals(new boolean[] {false, true, true}, testBinaryString4_1);
        assertArrayEquals(new boolean[] {false, true, true}, testBinaryString5_1);
        assertArrayEquals(new boolean[] {false, true, true, true}, testBinaryString6_1);
        assertArrayEquals(new boolean[] {false, true, true, true, true, true, true}, testBinaryString7_1);


        // Check with bit-count objective function which is better with first, third, and forth bit turned off.
        ObjectiveFunction<RuleExplanation, Double> objectiveFunction2 =
                new CountObjectiveFunctionExcept(
                        new int[] {0,2,3},
                        Utility.transformConditionsMapToArray(dummyRepresentationSpace)
                );
        KOptimalRuleLocalSearch optimizerExcept_1 = new KOptimalRuleLocalSearch(1, translator, objectiveFunction2);
        boolean[] testBinaryString0_2 = optimizerExcept_1.optimizeBoolAr(binaryString0);
        boolean[] testBinaryString1_2 = optimizerExcept_1.optimizeBoolAr(binaryString1);
        boolean[] testBinaryString2_2 = optimizerExcept_1.optimizeBoolAr(binaryString2);
        boolean[] testBinaryString3_2 = optimizerExcept_1.optimizeBoolAr(binaryString3);
        boolean[] testBinaryString4_2 = optimizerExcept_1.optimizeBoolAr(binaryString4);
        boolean[] testBinaryString5_2 = optimizerExcept_1.optimizeBoolAr(binaryString5);
        boolean[] testBinaryString6_2 = optimizerExcept_1.optimizeBoolAr(binaryString6);
        boolean[] testBinaryString7_2 = optimizerExcept_1.optimizeBoolAr(binaryString7);
        assertArrayEquals(new boolean[] {false}, testBinaryString0_2);
        assertArrayEquals(new boolean[] {false}, testBinaryString1_2);
        assertArrayEquals(new boolean[] {false, true, false}, testBinaryString2_2);
        assertArrayEquals(new boolean[] {false, true, false}, testBinaryString3_2);
        assertArrayEquals(new boolean[] {false, true, false}, testBinaryString4_2);
        assertArrayEquals(new boolean[] {false, true, false}, testBinaryString5_2);
        assertArrayEquals(new boolean[] {false, true, false, false}, testBinaryString6_2);
        assertArrayEquals(new boolean[] {false, true, false, false, true, true, true}, testBinaryString7_2);

        optimizerExcept_1 = new KOptimalRuleLocalSearch(2, translator, objectiveFunction2);
        testBinaryString0_2 = optimizerExcept_1.optimizeBoolAr(binaryString0);
        testBinaryString1_2 = optimizerExcept_1.optimizeBoolAr(binaryString1);
        testBinaryString2_2 = optimizerExcept_1.optimizeBoolAr(binaryString2);
        testBinaryString3_2 = optimizerExcept_1.optimizeBoolAr(binaryString3);
        testBinaryString4_2 = optimizerExcept_1.optimizeBoolAr(binaryString4);
        testBinaryString5_2 = optimizerExcept_1.optimizeBoolAr(binaryString5);
        testBinaryString6_2 = optimizerExcept_1.optimizeBoolAr(binaryString6);
        testBinaryString7_2 = optimizerExcept_1.optimizeBoolAr(binaryString7);
        assertArrayEquals(new boolean[] {false}, testBinaryString0_2);
        assertArrayEquals(new boolean[] {false}, testBinaryString1_2);
        assertArrayEquals(new boolean[] {false, true, false}, testBinaryString2_2);
        assertArrayEquals(new boolean[] {false, true, false}, testBinaryString3_2);
        assertArrayEquals(new boolean[] {false, true, false}, testBinaryString4_2);
        assertArrayEquals(new boolean[] {false, true, false}, testBinaryString5_2);
        assertArrayEquals(new boolean[] {false, true, false, false}, testBinaryString6_2);
        assertArrayEquals(new boolean[] {false, true, false, false, true, true, true}, testBinaryString7_2);


        // Check with bit-count objective function which is better if neither the first and second, or the first and
        // third bit is turned on.
        ObjectiveFunction<RuleExplanation, Double> objectiveFunction3 =
                new CountObjectiveFunctionExceptSpecifiedCombination(
                        new int[][] {new int[]{0, 2}, new int[] {0,1}},
                        Utility.transformConditionsMapToArray(dummyRepresentationSpace)
                );
        KOptimalRuleLocalSearch optimizerExceptSpecifiedCombination_0 = new KOptimalRuleLocalSearch(1, translator, objectiveFunction3);
        boolean[] testBinaryString0_3 = optimizerExceptSpecifiedCombination_0.optimizeBoolAr(binaryString0);
        boolean[] testBinaryString1_3 = optimizerExceptSpecifiedCombination_0.optimizeBoolAr(binaryString1);
        boolean[] testBinaryString2_3 = optimizerExceptSpecifiedCombination_0.optimizeBoolAr(binaryString2);
        boolean[] testBinaryString3_3 = optimizerExceptSpecifiedCombination_0.optimizeBoolAr(binaryString3);
        boolean[] testBinaryString4_3 = optimizerExceptSpecifiedCombination_0.optimizeBoolAr(binaryString4);
        boolean[] testBinaryString5_3 = optimizerExceptSpecifiedCombination_0.optimizeBoolAr(binaryString5);
        boolean[] testBinaryString6_3 = optimizerExceptSpecifiedCombination_0.optimizeBoolAr(binaryString6);
        boolean[] testBinaryString7_3 = optimizerExceptSpecifiedCombination_0.optimizeBoolAr(binaryString7);
        assertArrayEquals(new boolean[] {true}, testBinaryString0_3);
        assertArrayEquals(new boolean[] {true}, testBinaryString1_3);
        assertArrayEquals(new boolean[] {true, false, false}, testBinaryString2_3); // Cannot leave local optimum.
        assertArrayEquals(new boolean[] {false, true, true}, testBinaryString3_3);
        assertArrayEquals(new boolean[] {false, true, true}, testBinaryString4_3);
        assertArrayEquals(new boolean[] {false, true, true}, testBinaryString5_3);
        assertArrayEquals(new boolean[] {false, true, true, true}, testBinaryString6_3);
        assertArrayEquals(new boolean[] {false, true, true, true, true, true, true}, testBinaryString7_3);

        optimizerExceptSpecifiedCombination_0 = new KOptimalRuleLocalSearch(2, translator, objectiveFunction3);
        testBinaryString0_3 = optimizerExceptSpecifiedCombination_0.optimizeBoolAr(binaryString0);
        testBinaryString1_3 = optimizerExceptSpecifiedCombination_0.optimizeBoolAr(binaryString1);
        testBinaryString2_3 = optimizerExceptSpecifiedCombination_0.optimizeBoolAr(binaryString2);
        testBinaryString3_3 = optimizerExceptSpecifiedCombination_0.optimizeBoolAr(binaryString3);
        testBinaryString4_3 = optimizerExceptSpecifiedCombination_0.optimizeBoolAr(binaryString4);
        testBinaryString5_3 = optimizerExceptSpecifiedCombination_0.optimizeBoolAr(binaryString5);
        testBinaryString6_3 = optimizerExceptSpecifiedCombination_0.optimizeBoolAr(binaryString6);
        testBinaryString7_3 = optimizerExceptSpecifiedCombination_0.optimizeBoolAr(binaryString7);
        assertArrayEquals(new boolean[] {true}, testBinaryString0_3);
        assertArrayEquals(new boolean[] {true}, testBinaryString1_3);
        assertArrayEquals(new boolean[] {false, true, true}, testBinaryString2_3); // This time can leave local optimum.
        assertArrayEquals(new boolean[] {false, true, true}, testBinaryString3_3);
        assertArrayEquals(new boolean[] {false, true, true}, testBinaryString4_3);
        assertArrayEquals(new boolean[] {false, true, true}, testBinaryString5_3);
        assertArrayEquals(new boolean[] {false, true, true, true}, testBinaryString6_3);
        assertArrayEquals(new boolean[] {false, true, true, true, true, true, true}, testBinaryString7_3);
    }



}