package de.viadee.xai.framework.global_explanation_procedure_step.postprocessor;

import de.viadee.xai.framework.explanation_calculation.explanation.*;
import de.viadee.xai.framework.utility.RuleMetricCalculation;
import de.viadee.xai.framework.utility.Utility;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Sorts the rules according to some metric. Therafter, iteratively adds new RuleExplanations
 * to the result, starting with the highest-scoring RuleExplanations. For each to-be-added RuleExplanation
 * it is evaluated, if it suffices a comparison with each RuleExplanation already added.
 */
public class PairwiseRuleExplanationMetricFilter implements Postprocessor {
    protected RuleExplanationFactory ruleExplanationFactory;
    protected RuleExplanationSetFactory ruleExplanationSetFactory;
    protected final Function<RuleExplanation, Double> sortByMetricCalculation;
    protected final BiFunction<RuleExplanation, RuleExplanation, Boolean> ruleExplanationComparator;


    /**
     * Constructor for PairwiseRuleExplanationMetricFilter.
     * @param sortByMetricCalculation The function calculating the metric for the sorting-process.
     * @param ruleExplanationComparator The function comparing two RuleExplanations in aforementioned manner.
     */
    public PairwiseRuleExplanationMetricFilter(Function<RuleExplanation, Double> sortByMetricCalculation,
                                               BiFunction<RuleExplanation, RuleExplanation, Boolean> ruleExplanationComparator) {
        this.sortByMetricCalculation = sortByMetricCalculation;
        this.ruleExplanationComparator = ruleExplanationComparator;
    }

    /**
     * Constructor for PairwiseRuleExplanationMetricFilter.
     * Evaluates the Jaccard similarity between two rules following aforementioned procedure. If
     * the similarity is larger than 0.5, discards the rule.
     */
    public PairwiseRuleExplanationMetricFilter() {
        sortByMetricCalculation = RuleMetricCalculation::calculateRMI;
        ruleExplanationComparator =
                (re1, re2) -> (RuleMetricCalculation.calculateJaccardSimilarity(re1.getCoverAsBitmap(), re2.getCoverAsBitmap()) > 0.5);
    }

    @Override
    public RuleExplanationSet postprocess(RuleExplanationSet toProcess) {
        Set<RuleExplanation> ruleExplanations = toProcess.getExplanations();
        Set<RuleExplanation> adaptedRuleExplanations =
                Utility.translateRuleExplanationsWithFactory(ruleExplanations, ruleExplanationFactory);
        List<ExplanationMetricAssignation> rulesWithMetrics =
                Utility.sortExplanationsViaMetric(adaptedRuleExplanations, sortByMetricCalculation);
        List<RuleExplanation> resultingExplanations = new ArrayList<>();
        for (ExplanationMetricAssignation ema : rulesWithMetrics) {
            RuleExplanation currentExplanation = ema.getExplanation();
            boolean isDifferentEnough = true;
            for (RuleExplanation re : resultingExplanations) {
                if (ruleExplanationComparator.apply(re, currentExplanation)) {
                    isDifferentEnough = false;
                    break;
                }
            }
            if (isDifferentEnough) {
                resultingExplanations.add(currentExplanation);
            }
        }
        return ruleExplanationSetFactory.newWithCollection(
                toProcess.getLabelFeature(),
                toProcess.getLabelValue(),
                resultingExplanations);
    }

    @Override
    public void initialize(RuleExplanationFactory ruleExplanationFactory, RuleExplanationSetFactory ruleExplanationSetFactory) {
        this.ruleExplanationFactory = ruleExplanationFactory;
        this.ruleExplanationSetFactory = ruleExplanationSetFactory;
    }
}
