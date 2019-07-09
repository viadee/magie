package de.viadee.xai.framework.data.preprocessor;

import de.viadee.xai.framework.data.tabular_data.LabelColumn;
import de.viadee.xai.framework.data.tabular_data.TabularDatasetPackage;

/**
 * Interface for all Preprocessors, such as discretizers, feature selectors, etc.
 * Preprocessors are used by the TabularDatasetBuilder to construct a TabularDataset utilizing
 * an AnchorTabular-instance.
 * @param <OL> The type of the label column prior to the preprocessing step.
 * @param <PL> The type of the label column after the preprocessing step.
 * {@link de.viadee.xai.anchor.adapter.tabular.AnchorTabular}
 * {@link de.viadee.xai.framework.data.tabular_data.TabularDataset.TabularDatasetBuilder}
 * {@link de.viadee.xai.framework.data.tabular_data.TabularDataset}
 */
public interface Preprocessor<
        OL extends LabelColumn,
        PL extends LabelColumn> {

    /**
     * Preprocesses a training set of data.
     * @param toProcess The data which is to be processed.
     * @return The preprocessed data.
     */
    TabularDatasetPackage<PL> preprocess(TabularDatasetPackage<OL> toProcess);

    /**
     * After the training data has been preprocessed, if the transformations, e.g. discretizations,
     * have been learned based on the training data, these transformations are applied to new data.
     * @param toProcess The new data for which transformations are to be conducted.
     * @return The transformed new data.
     */
    TabularDatasetPackage<PL> preprocessTestData(TabularDatasetPackage<OL> toProcess);

}
