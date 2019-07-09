package de.viadee.xai.framework.global_explanation_procedure_step.postprocessor;

import de.viadee.xai.framework.data.Feature;
import de.viadee.xai.framework.explanation_calculation.explanation.RuleExplanation;
import de.viadee.xai.framework.explanation_calculation.explanation.RuleExplanationFactory;
import de.viadee.xai.framework.explanation_calculation.explanation.RuleExplanationSet;
import de.viadee.xai.framework.explanation_calculation.explanation.RuleExplanationSetFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Class filtering out those disjunctions from a {@link RuleExplanation} which contain all possible feature values.
 */
public class ObsoleteDisjunctionFilter implements Postprocessor {
    protected RuleExplanationFactory ruleExplanationFactory;
    protected RuleExplanationSetFactory ruleExplanationSetFactory;

    @Override
    public RuleExplanationSet postprocess(RuleExplanationSet toProcess) {
        Set<RuleExplanation> ruleExplanations = toProcess.getExplanations();
        Set<RuleExplanation> newRuleExplanations = new HashSet<>();
        for (RuleExplanation rule : ruleExplanations) {
            Map<Feature.CategoricalFeature, Set<Integer>> conditions = rule.getConditions();
            Map<Feature.CategoricalFeature, Set<Integer>> newConditions = new HashMap<>();
            for (Map.Entry<Feature.CategoricalFeature, Set<Integer>> entry : conditions.entrySet()) {
                if (entry.getValue().size() != entry.getKey().getUniqueNumberRepresentations().size()) {
                    newConditions.put(entry.getKey(), entry.getValue());
                }
            }
            RuleExplanation newRuleExplanation =
                    ruleExplanationFactory.initialize(newConditions, toProcess.getLabelFeature(), toProcess.getLabelValue());
            newRuleExplanations.add(newRuleExplanation);
        }

        return ruleExplanationSetFactory
                .newWithCollection(toProcess.getLabelFeature(), toProcess.getLabelValue(), newRuleExplanations);
    }

    @Override
    public void initialize(RuleExplanationFactory ruleExplanationFactory, RuleExplanationSetFactory ruleExplanationSetFactory) {
        this.ruleExplanationFactory = ruleExplanationFactory;
        this.ruleExplanationSetFactory = ruleExplanationSetFactory;
    }
}
