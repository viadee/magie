package de.viadee.xai.framework.global_explanation_procedure_step.postprocessor;

import de.viadee.xai.framework.explanation_calculation.explanation.*;
import de.viadee.xai.framework.utility.Utility;

import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Class which iteratively constructs a RuleExplanationSet.
 * First, the RuleExplanations are sorted in descending order according to some metric.
 * Thereafter, the highest-scoring RuleExplanation is added to the result set.
 * Now iteratively, new RuleExplanations are added to the result, each one having a lower (or equal)
 * metric value than all RuleExplanations before. If the addition of the RuleExplanation to the
 * RuleExplanationSet does not meet a certain condition, e.g., the cover of the RuleExplanationSet does
 * not grow by adding a RuleExplanation, this RuleExplanation is discarded.
 */
public class RuleSetEnhancementFilter implements Postprocessor {

    protected RuleExplanationFactory ruleExplanationFactory;
    protected RuleExplanationSetFactory ruleExplanationSetFactory;
    protected final Function<RuleExplanation, Double> sortByMetricCalculation;
    protected final BiFunction<RuleExplanationSet, RuleExplanationSet, Boolean> preferNewSubsetEvaluation;

    /**
     * Default constructor for RuleSetEnhancementFilter.
     */
    public RuleSetEnhancementFilter() {
        this(
                RuleExplanation::getPrecision,
                (wouldBeResult, momentaryResult) ->
                        (wouldBeResult.getNumberCoveredInstances() > momentaryResult.getNumberCoveredInstances())
        );
    }

    /**
     * Constructor for RuleSetEnhancementFilter.
     * @param sortByMetricCalculation The function calculating the metric for the sorting-process.
     * @param preferNewSubsetEvaluation Function which's first parameter is a RuleExplanationSet with
     *                                  a newly added RuleExplanation. The second parameter is the "old"
     *                                  RuleExplanationSet. If true, the new RuleExplanationSet is used
     *                                  in the further computation.
     */
    public RuleSetEnhancementFilter(Function<RuleExplanation, Double> sortByMetricCalculation,
                                    BiFunction<RuleExplanationSet, RuleExplanationSet, Boolean> preferNewSubsetEvaluation) {
        this.sortByMetricCalculation = sortByMetricCalculation;
        this.preferNewSubsetEvaluation = preferNewSubsetEvaluation;
    }

    @Override
    public RuleExplanationSet postprocess(RuleExplanationSet toProcess) {
        Set<RuleExplanation> ruleExplanations = toProcess.getExplanations();
        Set<RuleExplanation> adaptedRuleExplanations =
                Utility.translateRuleExplanationsWithFactory(ruleExplanations, ruleExplanationFactory);

        List<ExplanationMetricAssignation> rulesWithMetrics =
                Utility.sortExplanationsViaMetric(adaptedRuleExplanations, sortByMetricCalculation);
        RuleExplanationSet result = ruleExplanationSetFactory
                .newEmpty(toProcess.getLabelFeature(),
                        toProcess.getLabelValue());
        RuleExplanationSet wouldBeResult;
        for (ExplanationMetricAssignation ema : rulesWithMetrics) {
            wouldBeResult = ruleExplanationSetFactory.newCopyWith(result, ema.getExplanation());
            if (preferNewSubsetEvaluation.apply(wouldBeResult, result)) {
                result = wouldBeResult;
            }
        }
        return result;
    }

    @Override
    public void initialize(RuleExplanationFactory ruleExplanationFactory, RuleExplanationSetFactory ruleExplanationSetFactory) {
        this.ruleExplanationFactory = ruleExplanationFactory;
        this.ruleExplanationSetFactory = ruleExplanationSetFactory;
    }
}
