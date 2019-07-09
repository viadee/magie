package de.viadee.xai.framework.exception;

import de.viadee.xai.anchor.adapter.tabular.column.GenericColumn;
import de.viadee.xai.framework.data.tabular_data.TabularDataset;

/**
 * Subclass of RuntimeException. To be thrown if a column is to be transformed to a TabularDataset, yet its type could
 * not be identified.
 * {@link TabularDataset}
 */
public class ColumnTypeNotAccepted extends RuntimeException {
    /**
     * Constructor for a ColumnTypeNotAccepted instance.
     * @param genericColumn The GenericColumn which was tried to be used to generate a dataset from.
     */
    public ColumnTypeNotAccepted(final GenericColumn genericColumn) {
        super("The column of type " + genericColumn.getName() + " is not accepted as input.");
    }
}
