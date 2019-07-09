package de.viadee.xai.framework.global_explanation_procedure_step.optimizer.optimization_initializer;

import de.viadee.xai.framework.data.Feature.CategoricalFeature;
import de.viadee.xai.framework.data.FeatureValue.CategoricalFeatureValue;
import de.viadee.xai.framework.explanation_calculation.explanation.RuleExplanation;
import de.viadee.xai.framework.explanation_calculation.explanation.RuleExplanationSet;
import de.viadee.xai.framework.global_explanation_procedure_step.optimizer.OptimizationInitializer;
import de.viadee.xai.framework.utility.RuleMetricCalculation;
import de.viadee.xai.framework.utility.Utility;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * Initializer initializing a number of binary strings representing passed {@link RuleExplanation}.
 * Can be used to forward generated {@link RuleExplanation}s as-are to a
 * {@link de.viadee.xai.framework.global_explanation_procedure_step.optimizer.KOptimalRuleExplanationOptimizer}.
 */
public class BinaryRuleExplanationInitializer implements OptimizationInitializer<boolean[][]> {

    protected RuleExplanationSet representationSpaceFoundation;

    /**
     * Constructor for BinaryRuleExplanationInitializer.
     */
    public BinaryRuleExplanationInitializer() {
    }

    @Override
    public boolean[][] apply(Integer populationSize, Integer representationLength) {
        CategoricalFeatureValue[] conditionFeatureValues =
                Utility.transformConditionsMapToArray(this.representationSpaceFoundation);

        boolean[][] result =
                new boolean[representationSpaceFoundation.getNumberExplanations()][representationSpaceFoundation.getNumberConditionValues()];

        int ruleCount = 0;
        Set<RuleExplanation> ruleExplanations = representationSpaceFoundation.getExplanations();
        for (RuleExplanation re : ruleExplanations) {
            int featureValueCount = 0;
            Map<CategoricalFeature, Set<Integer>> conditions = re.getConditions();
            for (CategoricalFeatureValue cfv : conditionFeatureValues) {
                Set<Integer> featureValues = conditions.get(cfv.getFeature());
                if (!(featureValues==null)) {
                    if (featureValues.contains(cfv.getValue())) {
                        result[ruleCount][featureValueCount] = true;
                    } else {
                        result[ruleCount][featureValueCount] = false;
                    }
                } else {
                    result[ruleCount][featureValueCount] = false;
                }
                featureValueCount++;
            }
            ruleCount++;
        }
        return result;
    }

    @Override
    public void initialize(RuleExplanationSet representationSpaceFoundation) {
        this.representationSpaceFoundation = representationSpaceFoundation;
    }
}
