package de.viadee.xai.framework.global_explanation_procedure_step.optimizer.trajectory_optimizer;

import de.viadee.xai.framework.data.Feature;
import de.viadee.xai.framework.data.FeatureValue;
import de.viadee.xai.framework.explanation_calculation.explanation.RuleExplanation;
import de.viadee.xai.framework.explanation_calculation.explanation.RuleExplanationSet;
import de.viadee.xai.framework.global_explanation_procedure_step.optimizer.ObjectiveFunction;

import java.util.Map;
import java.util.Set;

/**
 * Dummy objective function. Each value increases the value. Exceptional conditions can be defined for which
 * the objective function is decreased.
 */
public class CountObjectiveFunctionExcept implements ObjectiveFunction<RuleExplanation, Double> {

    protected final int[] positions;
    protected final FeatureValue.CategoricalFeatureValue[] valueSpace;

    /**
     * Constructor for CountObjectiveFunctionExcept.
     * @param positions The positions for which the objective value is decreased.
     * @param valueSpace The search space.
     */
    public CountObjectiveFunctionExcept(int[] positions, FeatureValue.CategoricalFeatureValue[] valueSpace) {
        this.positions = positions;
        this.valueSpace = valueSpace;
    }

    @Override
    public void initialize(RuleExplanationSet representationSpaceFoundation) {}

    @Override
    public Double apply(RuleExplanation ruleExplanation) {
        int shouldNotHaveConditionsAtPosition = 0;
        Map<Feature.CategoricalFeature, Set<Integer>> conditions = ruleExplanation.getConditions();
        for (Map.Entry<Feature.CategoricalFeature, Set<Integer>> condition : conditions.entrySet()) {
            for (int j = 0; j < positions.length; j++) {
                if (condition.getKey().equals(valueSpace[positions[j]].getFeature()) &&
                    condition.getValue().contains(valueSpace[positions[j]].getValue())) {
                    shouldNotHaveConditionsAtPosition++;
                }
            }
        }
        return ruleExplanation.getNumberConditionValues() - ((double) shouldNotHaveConditionsAtPosition * 2);
    }
}
