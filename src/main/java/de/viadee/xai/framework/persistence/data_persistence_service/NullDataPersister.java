package de.viadee.xai.framework.persistence.data_persistence_service;

import de.viadee.xai.framework.data.index.RoaringBitmapIndex;
import de.viadee.xai.framework.data.tabular_data.LabelColumn;
import de.viadee.xai.framework.data.tabular_data.TabularDataset;
import de.viadee.xai.framework.utility.Tuple;

public class NullDataPersister implements DataPersistenceService {
    @Override
    public int persistData(TabularDataset<LabelColumn.CategoricalLabelColumn, LabelColumn.CategoricalLabelColumn> data, RoaringBitmapIndex index) {
        return -1;
    }

    @Override
    public Tuple<TabularDataset<LabelColumn.CategoricalLabelColumn, LabelColumn.CategoricalLabelColumn>, RoaringBitmapIndex> loadData(int id) {
        return null;
    }

    @Override
    public void terminate() {}
}
