package de.viadee.xai.framework.adapter.black_box_classifier_adapter;

import de.viadee.xai.framework.data.tabular_data.TabularRow;

import java.util.function.Function;

/**
 * Interface for transformers which transform TabularRows to any data type
 * used by a local explainer.
 * @param <I> The type of data used by the local explainer.
 */
public interface TabularRowTransformer<I> extends Function<TabularRow, I> {
}
