package de.viadee.xai.framework.exception;

/**
 * RuntimeException to be thrown if the {@link de.viadee.xai.framework.explanation_pipeline.ExplanationPipeline}
 * fails to be executed.
 */
public class PipelineExecutionFailed extends RuntimeException {
    /**
     * Constructor for PipelineExecutionFailed.
     * @param reason The String containing the reason.
     */
    public PipelineExecutionFailed(String reason) {
        super("The execution of the pipeline failed. " + reason + ".");
    }
}
