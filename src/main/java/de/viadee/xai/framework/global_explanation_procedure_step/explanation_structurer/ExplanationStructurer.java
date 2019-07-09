package de.viadee.xai.framework.global_explanation_procedure_step.explanation_structurer;

import de.viadee.xai.framework.explanation_calculation.explanation.RuleExplanation;
import de.viadee.xai.framework.explanation_calculation.explanation.RuleExplanationSet;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Interface for all explanation structurers. Is used to merge and different {@link RuleExplanation}s.
 */
public interface ExplanationStructurer {
    /**
     * Structures multiple {@link RuleExplanation}s.
     * @param toStructure The set of to-be-structured {@link RuleExplanation}s.
     * @return A sequence of identifiers mapped to a set of {@link RuleExplanation}s. Might be used to, e.g., depict
     * a dendrogram.
     */
    List<Map<Integer, Set<RuleExplanation>>> structure(Set<RuleExplanationSet> toStructure);
}
