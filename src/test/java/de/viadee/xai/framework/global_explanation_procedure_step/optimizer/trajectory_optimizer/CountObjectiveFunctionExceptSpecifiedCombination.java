package de.viadee.xai.framework.global_explanation_procedure_step.optimizer.trajectory_optimizer;

import de.viadee.xai.framework.data.Feature;
import de.viadee.xai.framework.data.FeatureValue;
import de.viadee.xai.framework.explanation_calculation.explanation.RuleExplanation;
import de.viadee.xai.framework.explanation_calculation.explanation.RuleExplanationSet;
import de.viadee.xai.framework.global_explanation_procedure_step.optimizer.ObjectiveFunction;

import java.util.Map;
import java.util.Set;

/**
 * Dummy objective function. The number of active conditions increases the objective function. Combinations can
 * be specified, for which the objective value is decreased.
 */
public class CountObjectiveFunctionExceptSpecifiedCombination implements ObjectiveFunction<RuleExplanation, Double> {

    protected final int[][] positions;
    protected final FeatureValue.CategoricalFeatureValue[] valueSpace;

    /**
     * Constructor for CountObjectiveFunctionExceptSpecifiedCombination.
     * @param positions An array of combinations which, in union, decrease the objective function.
     * @param valueSpace The search space.
     */
    public CountObjectiveFunctionExceptSpecifiedCombination(int[][] positions, FeatureValue.CategoricalFeatureValue[] valueSpace) {
        this.positions = positions;
        this.valueSpace = valueSpace;
    }

    @Override
    public void initialize(RuleExplanationSet representationSpaceFoundation) {}

    @Override
    public Double apply(RuleExplanation ruleExplanation) {
        int shouldNotHaveConditionsAtPosition = 0;
        Map<Feature.CategoricalFeature, Set<Integer>> conditions = ruleExplanation.getConditions();
        for (int i = 0; i < positions.length; i++) {
            int numberContained = 0;
            for (int j = 0; j < positions[i].length; j++) {
                for (Map.Entry<Feature.CategoricalFeature, Set<Integer>> condition : conditions.entrySet()) {
                    if (condition.getKey().equals(valueSpace[positions[i][j]].getFeature()) &&
                            condition.getValue().contains(valueSpace[positions[i][j]].getValue())) {
                        numberContained++;
                    }
                }
            }
            if (numberContained == positions[i].length) {
                shouldNotHaveConditionsAtPosition += 1;
            }
        }
        return ruleExplanation.getNumberConditionValues() - ((double) 5 * shouldNotHaveConditionsAtPosition);
    }
}
