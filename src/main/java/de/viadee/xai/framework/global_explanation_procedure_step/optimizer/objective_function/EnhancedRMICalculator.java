package de.viadee.xai.framework.global_explanation_procedure_step.optimizer.objective_function;

import de.viadee.xai.framework.explanation_calculation.explanation.RuleExplanation;
import de.viadee.xai.framework.explanation_calculation.explanation.RuleExplanationSet;
import de.viadee.xai.framework.global_explanation_procedure_step.optimizer.ObjectiveFunction;
import de.viadee.xai.framework.utility.RuleMetricCalculation;

/**
 * An objective function evaluated on RuleExplanations which calculates the
 * RuleMutualInformation according to the MAGIX procedure, and furthermore applying the suggested weight.
 * Puri, N., Gupta, P., Agarwal, P., Verma, S., {@literal &} Krishnamurthy, B. (2017).
 * MAGIX: Model Agnostic Globally Interpretable Explanations. arXiv preprint arXiv:1706.07160.
 */
public class EnhancedRMICalculator implements ObjectiveFunction<RuleExplanation, Double> {

    protected final double counterWeightNumberConditionValues;

    /**
     * Constructor for EnhancedRMICalculator.
     */
    public EnhancedRMICalculator() {
        counterWeightNumberConditionValues = 0;
    }

    /**
     * Constructor for EnhancedRMICalculator.
     * @param counterWeightNumberConditionValues The weight for taking the number of conditions into account.
     */
    public EnhancedRMICalculator(double counterWeightNumberConditionValues) {
        this.counterWeightNumberConditionValues = counterWeightNumberConditionValues;
    }

    @Override
    public Double apply(RuleExplanation ruleExplanation) {
        return (RuleMetricCalculation.calculateRMI(ruleExplanation) -
                (counterWeightNumberConditionValues * ruleExplanation.getNumberConditionValues()));
    }

    @Override
    public void initialize(RuleExplanationSet representationSpaceFoundation) {}
}
