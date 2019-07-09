package de.viadee.xai.framework.global_explanation_procedure_step.optimizer.trajectory_optimizer;

import de.viadee.xai.framework.explanation_calculation.explanation.RuleExplanation;
import de.viadee.xai.framework.explanation_calculation.explanation.RuleExplanationSet;
import de.viadee.xai.framework.global_explanation_procedure_step.optimizer.ObjectiveFunction;

/**
 * Dummy objective function. Each included condition increases the objective value.
 */
public class CountObjectiveFunction implements ObjectiveFunction<RuleExplanation, Double> {

    @Override
    public void initialize(RuleExplanationSet representationSpaceFoundation) {}

    @Override
    public Double apply(RuleExplanation ruleExplanation) {
        return Integer.valueOf(ruleExplanation.getNumberConditionValues()).doubleValue();
    }
}
