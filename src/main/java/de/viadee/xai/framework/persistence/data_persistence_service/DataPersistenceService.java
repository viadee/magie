package de.viadee.xai.framework.persistence.data_persistence_service;

import de.viadee.xai.framework.data.index.RoaringBitmapIndex;
import de.viadee.xai.framework.data.tabular_data.LabelColumn.CategoricalLabelColumn;
import de.viadee.xai.framework.data.tabular_data.TabularDataset;
import de.viadee.xai.framework.utility.Tuple;

public interface DataPersistenceService {

    /**
     * Persists the data set and index.
     * @param data The data set.
     * @param index The index.
     * @return The id of the data set and index.
     */
    int persistData(TabularDataset<CategoricalLabelColumn, CategoricalLabelColumn> data,
                        RoaringBitmapIndex index);

    /**
     * Loads the data set and index.
     * @param id The ID of the data set and index.
     * @return The tuple of data set and index.
     */
    Tuple<TabularDataset<CategoricalLabelColumn, CategoricalLabelColumn>, RoaringBitmapIndex>
    loadData(int id);

    /**
     * Terminates, e.g., the database connection.
     */
    void terminate();

}
