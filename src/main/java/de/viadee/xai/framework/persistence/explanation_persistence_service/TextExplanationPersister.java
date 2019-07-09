package de.viadee.xai.framework.persistence.explanation_persistence_service;

import de.viadee.xai.framework.explanation_calculation.explanation.RuleExplanation;
import de.viadee.xai.framework.explanation_calculation.explanation.RuleExplanationSet;
import de.viadee.xai.framework.explanation_visualizer.ExplanationVisualizer;
import de.viadee.xai.framework.explanation_visualizer.StringRuleExplanationVisualizer;
import de.viadee.xai.framework.global_explanation_procedure_step.explanation_structurer.ExplanationStructurer;
import de.viadee.xai.framework.global_explanation_procedure_step.explanation_structurer.NullRuleExplanationStructurer;
import org.apache.commons.lang.NotImplementedException;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;

public class TextExplanationPersister implements ExplanationPersistenceService {
    private ExplanationStructurer explanationStructurer = new NullRuleExplanationStructurer();
    private ExplanationVisualizer<String> explanationVisualizer = new StringRuleExplanationVisualizer();
    private PrintWriter out;
    private final int setSizeCap;

    public TextExplanationPersister(String explanationPersistenceDescriptor) {
        this.setSizeCap = 100;
        try {
            this.out = new PrintWriter(explanationPersistenceDescriptor + "_" + new Date().getTime() + ".txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    @Override
    public Map<Integer, Integer> persistExplanations(List<RuleExplanationSet> ruleExplanationSets, int stepNumber) {
        Set<RuleExplanationSet> ruleExplanationSetsSet = new HashSet<>(ruleExplanationSets);
        int maximumSize = -1;
        for (RuleExplanationSet ruleExplanationSet : ruleExplanationSetsSet) {
            maximumSize = Math.max(ruleExplanationSet.getNumberExplanations(), maximumSize);
        }
        if (maximumSize < setSizeCap) {
            List<Map<Integer, Set<RuleExplanation>>> structure = explanationStructurer.structure(ruleExplanationSetsSet);
            out.println("Step number: " + stepNumber);
            out.println(explanationVisualizer.visualize(structure));
        } else {
            out.println("Step number: " + stepNumber + ", sets not visualized");
            for (RuleExplanationSet ruleExplanationSet : ruleExplanationSetsSet) {
                out.println("Label: " +
                        ruleExplanationSet.getLabelFeature().getStringRepresentation(ruleExplanationSet.getLabelValue()) +
                        ", size: " + ruleExplanationSet.getNumberExplanations());
            }
        }
        out.println("\n\n||\n\n");
        out.flush();
        return new HashMap<>();
    }

    @Override
    public RuleExplanationSet loadExplanations(int id) {
        throw new NotImplementedException("It is currently not possible to load explanations from a text file.");
    }

    @Override
    public Set<RuleExplanationSet> loadExplanationsForAllLabels(int[] ids) {
        throw new NotImplementedException("It is currently not possible to load explanations from a text file.");
    }

    @Override
    public void terminate() {
        this.out.close();
    }
}
