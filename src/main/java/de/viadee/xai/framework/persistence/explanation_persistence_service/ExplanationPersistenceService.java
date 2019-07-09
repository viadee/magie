package de.viadee.xai.framework.persistence.explanation_persistence_service;

import de.viadee.xai.framework.explanation_calculation.explanation.RuleExplanationSet;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Interface for all classes persisting explanations.
 */
public interface ExplanationPersistenceService {

    /**
     * Persists the explanations for a given step.
     * @param ruleExplanationSet The RuleExplanationSets for each label value.
     * @param stepNumber The step number.
     * @return A mapping from the label value to the ID of the persisted {@link RuleExplanationSet}.
     */
    Map<Integer, Integer> persistExplanations(List<RuleExplanationSet> ruleExplanationSet, int stepNumber);

    /**
     * Loads a persisted {@link RuleExplanationSet}.
     * @param id The id of the persisted {@link RuleExplanationSet}.
     * @return The loaded {@link RuleExplanationSet}.
     */
    RuleExplanationSet loadExplanations(int id);

    /**
     * Loads the persisted {@link RuleExplanationSet}s for each persisted step.
     * @param ids The IDs for the {@link RuleExplanationSet} which are to be loaded.
     * @return The loaded {@link RuleExplanationSet}s.
     */
    Set<RuleExplanationSet> loadExplanationsForAllLabels(int[] ids);

    /**
     * Terminates, e.g., the database connection.
     */
    void terminate();
}
