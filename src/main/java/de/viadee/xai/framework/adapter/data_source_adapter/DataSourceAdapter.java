package de.viadee.xai.framework.adapter.data_source_adapter;

import de.viadee.xai.anchor.adapter.tabular.AnchorTabular;

/**
 * Loads a data set from a given source.
 * The AnchorTabular-instance will be used by the Builder of the TabularDataset-class to construct
 * a TabularDataset-instance.
 * AnchorTabular is utilized as it represents a simple and flexible intermediate format and makes
 * adapters created for the Anchors-algorithm reusable.
 * {@link de.viadee.xai.framework.data.tabular_data.TabularDataset}
 * {@link de.viadee.xai.framework.data.tabular_data.TabularDataset.TabularDatasetBuilder}
 * @param <T> The data type of the data which is translated to an instance of TabularDataset.
 */
public interface DataSourceAdapter<T> {

    /**
     * Loads data from a specific data source and transforms the data into a AnchorTabular.
     * @return The constructed AnchorTabular.
     */
    T loadDataset();
}
