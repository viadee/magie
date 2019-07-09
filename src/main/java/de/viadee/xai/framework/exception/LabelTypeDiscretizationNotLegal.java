package de.viadee.xai.framework.exception;

/**
 * RuntimeException to be thrown if an erroneous discretization sequence is specified
 * in the {@link de.viadee.xai.framework.data.tabular_data.TabularDataset.TabularDatasetBuilder}.
 */
public class LabelTypeDiscretizationNotLegal extends RuntimeException {
    /**
     * Constructor for LabelTypeDiscretizationNotLegal.
     */
    public LabelTypeDiscretizationNotLegal() {
        super("The label type which is to be discretized is not numeric.");
    }
}
