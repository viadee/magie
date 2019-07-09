package de.viadee.xai.framework.exception;

/**
 * RuntimeException to be thrown if in the {@link de.viadee.xai.framework.data.tabular_data.TabularDataset.TabularDatasetBuilder}
 * post-discretization preprocessing is specified, yet, no discretizer has been found.
 */
public class PostDiscretizationNotLegal extends RuntimeException {
    /**
     * Constructor for PostDiscretizationNotLegal.
     */
    public PostDiscretizationNotLegal() {
        super("In the build-process a preprocessing step for discretized data is demanded. However, no discretizer" +
                "has been found.");
    }
}
