package de.viadee.xai.framework.global_explanation_procedure_step.optimizer.objective_function;

import de.viadee.xai.framework.explanation_calculation.explanation.RuleExplanation;
import de.viadee.xai.framework.explanation_calculation.explanation.RuleExplanationSet;
import de.viadee.xai.framework.global_explanation_procedure_step.optimizer.ObjectiveFunction;
import de.viadee.xai.framework.utility.RuleMetricCalculation;

/**
 * Scaled version of {@link EnhancedRMICalculator}. The output is normalized within the interval [-2;1].
 */
public class ScaledEnhancedRMICalculator implements ObjectiveFunction<RuleExplanation, Double> {

    protected final double counterWeightNumberConditionValues;
    protected double maximumMI;

    /**
     * Constructor for ScaledEnhancedRMICalculator.
     * @param counterWeightNumberConditionValues A weight for taking the number of conditions into account.
     *                                           Should be in [0;1].
     */
    public ScaledEnhancedRMICalculator(double counterWeightNumberConditionValues) {
        this.counterWeightNumberConditionValues = counterWeightNumberConditionValues;
    }

    @Override
    public Double apply(RuleExplanation ruleExplanation) {
        return ((RuleMetricCalculation.calculateRMI(ruleExplanation) / maximumMI) -
                (counterWeightNumberConditionValues * ruleExplanation.getNumberConditionValues()));
    }

    private double calculateMaximumMI(int overallAmountRows, int amountForLabelValue) {
        return RuleMetricCalculation.calculateRMI(amountForLabelValue, 0, 0, overallAmountRows - amountForLabelValue);
    }

    @Override
    public void initialize(RuleExplanationSet representationSpaceFoundation) {
        int amountForLabelValue = representationSpaceFoundation
                .getCalculator()
                .getNumberCovered(
                        representationSpaceFoundation.getLabelFeature(),
                        representationSpaceFoundation.getLabelValue()
                );
        this.maximumMI = calculateMaximumMI(representationSpaceFoundation.getDataset().getNumberRows(), amountForLabelValue);
    }
}
