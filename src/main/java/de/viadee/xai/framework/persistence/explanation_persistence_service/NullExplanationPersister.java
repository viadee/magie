package de.viadee.xai.framework.persistence.explanation_persistence_service;

import de.viadee.xai.framework.explanation_calculation.explanation.RuleExplanationSet;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class NullExplanationPersister implements ExplanationPersistenceService {
    @Override
    public Map<Integer, Integer> persistExplanations(List<RuleExplanationSet> ruleExplanationSet, int stepNumber) {
        return new HashMap<>();
    }

    @Override
    public RuleExplanationSet loadExplanations(int id) {
        return null;
    }

    @Override
    public Set<RuleExplanationSet> loadExplanationsForAllLabels(int[] ids) {
        return null;
    }

    @Override
    public void terminate() {}
}
