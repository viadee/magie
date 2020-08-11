package de.viadee.xai.framework.global_explanation_procedure_step.postprocessor;

import de.viadee.xai.framework.explanation_calculation.calculator.RoaringBitmapCalculator;
import de.viadee.xai.framework.explanation_calculation.explanation.*;
import de.viadee.xai.framework.utility.Utility;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

/**
 * Filters out rules which do not satisfy certain criteria.
 */
public class MetricRuleExplanationFilter implements Postprocessor {

    protected final Function<RuleExplanation, Boolean> filterFunction;
    protected RuleExplanationFactory ruleExplanationFactory;
    protected RuleExplanationSetFactory ruleExplanationSetFactory;

    /**
     * Constructor for MetricRuleExplanationFilter. Filters out rules which have less coverage
     * and roundPrecision than 0.5.
     * @param calculator The calculator with which to calculate the roundPrecision and coverage.
     */
    public MetricRuleExplanationFilter(RoaringBitmapCalculator calculator) {
        this.filterFunction = (ruleExplanation) ->
                ruleExplanation.getCoverage() < 0.5 &&
                        ruleExplanation.getPrecision() < 0.5;
        this.ruleExplanationSetFactory = new StdRuleExplanationSetFactory(calculator);
        this.ruleExplanationFactory = new MinimalCoversRuleExplanationFactory(calculator);
    }

    /**
     * Constructor for MetricRuleExplanationFilter.
     * @param filterFunction The filter function. If it evaluates to 'true', the corresponding
     *                       RuleExplanation will be filtered.
     */
    public MetricRuleExplanationFilter(Function<RuleExplanation, Boolean> filterFunction) {
        this.filterFunction = filterFunction;
    }

    public MetricRuleExplanationFilter() {
        this.filterFunction = (ruleExplanation) ->
                ruleExplanation.getCoverage() < 0.5 &&
                        ruleExplanation.getPrecision() < 0.5;
    }

    @Override
    public RuleExplanationSet postprocess(RuleExplanationSet toProcess) {
        Set<RuleExplanation> ruleExplanations = toProcess.getExplanations();

        ruleExplanations = Utility.translateRuleExplanationsWithFactory(ruleExplanations, ruleExplanationFactory);

        Set<RuleExplanation> ruleExplanationSet = new HashSet<>();
        for (RuleExplanation ruleExplanation : ruleExplanations) {
            if (filterFunction.apply(ruleExplanation)) {
                ruleExplanationSet.add(ruleExplanation);
            }
        }


        return ruleExplanationSetFactory
                .newWithCollection(
                        toProcess.getLabelFeature(),
                        toProcess.getLabelValue(),
                        ruleExplanationSet
                );
    }

    @Override
    public void initialize(RuleExplanationFactory ruleExplanationFactory, RuleExplanationSetFactory ruleExplanationSetFactory) {
        this.ruleExplanationFactory = ruleExplanationFactory;
        this.ruleExplanationSetFactory = ruleExplanationSetFactory;
    }
}
