package de.viadee.xai.framework.adapter.black_box_classifier_adapter;

import de.viadee.xai.anchor.algorithm.ClassificationFunction;
import de.viadee.xai.framework.data.tabular_data.LabelColumn;
import de.viadee.xai.framework.data.tabular_data.TabularRow;

/**
 * Combination-Interface which all BlackBoxClassifiers must implement.
 * A BlackBoxClassifierAdapter must implement methods to predict rows of a Dataset and, if a perturbation approach is used,
 * to predict instances of the data format chosen of by the local explainer.
 * Is used to classify TabularRow-instances as well as a data format specific to the local explainer utilized.
 * @param <I> The type of data instance used by the local explainer.
 * {@link de.viadee.xai.framework.data.tabular_data.TabularDataset}
 * {@link TabularRow}
 */
public interface BlackBoxClassifierAdapter<I>
        extends
        ClassificationFunction<TabularRow>,
        BlackBoxModelAdapter<I, LabelColumn.CategoricalLabelColumn, Integer> {
}
