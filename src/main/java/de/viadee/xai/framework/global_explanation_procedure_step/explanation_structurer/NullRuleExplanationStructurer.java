package de.viadee.xai.framework.global_explanation_procedure_step.explanation_structurer;

import de.viadee.xai.framework.explanation_calculation.explanation.RuleExplanation;
import de.viadee.xai.framework.explanation_calculation.explanation.RuleExplanationSet;

import java.util.*;

/**
 * Default ExplanationStructurer. Keeps the per-label partitioning of the given set of {@link RuleExplanationSet}s.
 */
public class NullRuleExplanationStructurer implements ExplanationStructurer {
    @Override
    public List<Map<Integer, Set<RuleExplanation>>> structure(Set<RuleExplanationSet> toStructure) {
        List<Map<Integer, Set<RuleExplanation>>> restructuredToVisualize = new ArrayList<>();
        Map<Integer, Set<RuleExplanation>> mapRepresentation = new HashMap<>();
        for (RuleExplanationSet explanationsForLabel : toStructure) {
            Set<RuleExplanation> explanations = explanationsForLabel.getExplanations();
            mapRepresentation.put(explanationsForLabel.getLabelValue(), explanations);
        }
        restructuredToVisualize.add(mapRepresentation);
        return restructuredToVisualize;
    }
}
