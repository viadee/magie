package de.viadee.xai.framework.explanation_pipeline;

import java.util.Map;

/**
 * The container carrying the result of invoking {@link StdExplanationPipeline#executePipeline()}.
 * @param <I> The chosen format for the generated global explanation.
 */
public class GlobalExplanationContainer<I> {
    protected I visualizedResult;
    protected Map<Integer, Map<Integer, Integer>> storedResultIds;

    /**
     * Constructor for GlobalExplanationContainer.
     * @param visualizedResult The visualized result.
     * @param storedResultIds A mapping from the step number to another mapping from the label value to the ID of the
     *                        {@link de.viadee.xai.framework.explanation_calculation.explanation.RuleExplanationSet} which
     *                        was persisted.
     */
    public GlobalExplanationContainer(I visualizedResult,
                                      Map<Integer, Map<Integer, Integer>> storedResultIds) {
        this.visualizedResult = visualizedResult;
        this.storedResultIds = storedResultIds;
    }

    /**
     * Returns the visualized result.
     * @return The visualized result.
     */
    public I getVisualizedResult() {
        return visualizedResult;
    }

    /**
     * Returns the identifiers for the different step for each generated {@link de.viadee.xai.framework.explanation_calculation.explanation.RuleExplanationSet}.
     * @return The identifiers.
     */
    public Map<Integer, Map<Integer, Integer>> getStoredResultIds() {
        return storedResultIds;
    }
}
