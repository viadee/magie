package de.viadee.xai.framework.explanation_visualizer;

import de.viadee.xai.framework.explanation_calculation.explanation.RuleExplanation;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Interface for all explanation visualizers.
 * @param <F> The visualization of the previously structured {@link RuleExplanation}s.
 */
public interface ExplanationVisualizer<F> {
    /**
     * Visualizes the given structure of {@link RuleExplanation}s.
     * @param toVisualize The structure of {@link RuleExplanation}s.
     * @return The visualization.
     */
    F visualize(List<Map<Integer, Set<RuleExplanation>>> toVisualize);
}
