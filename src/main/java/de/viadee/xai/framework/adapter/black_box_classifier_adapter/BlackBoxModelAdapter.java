package de.viadee.xai.framework.adapter.black_box_classifier_adapter;

import de.viadee.xai.framework.data.tabular_data.LabelColumn;
import de.viadee.xai.framework.data.tabular_data.TabularDataset;

import java.io.Serializable;
import java.util.function.Function;

/**
 * General interface for all black box model adapters. Can be specified for the regression and classification problem.
 * @param <I> The data type of the local explainer.
 * @param <L> The label type (categorical or numeric) which is predicted.
 * @param <S> The value type of the label (most probably Double or Integer).
 */
public interface BlackBoxModelAdapter<I, L extends LabelColumn, S extends Serializable>
        extends Function<I, S> {

    /**
     * Trains the adapted black box model on the given TabularDataset instance.
     * @param dataset The data set.
     */
    void train(TabularDataset<L, ?> dataset);
}
