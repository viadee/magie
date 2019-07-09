package de.viadee.xai.framework.exception;

import de.viadee.xai.framework.data.Feature;
import de.viadee.xai.framework.data.tabular_data.TabularDataset;
import de.viadee.xai.framework.data.tabular_data.TabularRow;

/**
 * Subclass of RuntimeException. To be thrown if a column is to be retrieved from a dataset or a TabularRow,
 * but the Feature this column is to be retrieved for is not found.
 * {@link TabularDataset}
 * {@link TabularRow}
 */
public class ColumnForFeatureNotFound extends RuntimeException {
    /**
     * Constructor for a ColumnForFeatureNotFound instance.
     * @param feature The feature for which no data entries could be found.
     */
    public ColumnForFeatureNotFound(final Feature feature) {
        super("No column was found for the given feature: " + feature.getName() + ".");
    }
}
