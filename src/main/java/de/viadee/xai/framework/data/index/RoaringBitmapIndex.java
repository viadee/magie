package de.viadee.xai.framework.data.index;

import de.viadee.xai.framework.data.Feature;
import de.viadee.xai.framework.data.tabular_data.LabelColumn;
import de.viadee.xai.framework.data.tabular_data.TabularDataset;
import org.roaringbitmap.buffer.ImmutableRoaringBitmap;
import org.roaringbitmap.buffer.MutableRoaringBitmap;

import java.util.Map;
import java.util.Set;

/**
 * Interface for all indexes enabling queries for ImmutableRoaringBitmap- or MutableRoaringBitmap-instances.
 */
public interface RoaringBitmapIndex {

    /**
     * Returns a bitmap containing all instances which satisfy the condition represented by a CategoricalFeature
     * and a feature value, i.e., all returned instances, represented by the row-identifiers of the corresponding
     * data set will exhibit the feature and the corresponding categorical value.
     * @param categoricalFeature The CategoricalFeature for which a query is to be executed.
     * @param featureValue The required feature value.
     * @return A bitmap containing the row-identifiers of all instances satisfying the specified condition.
     */
    ImmutableRoaringBitmap getInstancesAsBitmap(Feature.CategoricalFeature categoricalFeature, Integer featureValue);

    /**
     * Returns a bitmap of row-identifiers for all instances which exhibit any of the given feature values
     * of the specified CategoricalFeature.
     * @param categoricalFeature The CategoricalFeature requested.
     * @param featureValues The allowed feature values for the CategoricalFeature.
     * @return All instances exhibiting any of the feature values for the given CategoricalFeature.
     */
    MutableRoaringBitmap getInstancesAsBitmap(Feature.CategoricalFeature categoricalFeature,
                                                     Set<Integer> featureValues);

    /**
     * Returns the row-identifiers of all data instances exhibiting any of the allowed feature values
     * for all given corresponding categorical features. This is equivalent to conjunctions between features
     * which's feature values are disjuncted.
     * @param conditions The conditions represented by a map CategoricalFeature {@literal -->} allowed values.
     * @return The {@link MutableRoaringBitmap}-representation of the cover.
     */
    MutableRoaringBitmap getInstancesAsBitmap(Map<Feature.CategoricalFeature, Set<Integer>> conditions);

    /**
     * Returns the data set the index was created upon. This is useful to, e.g., distinguish indexes created
     * on the training data set vs. instances created on the test data set.
     * @return The data set the index was created for.
     */
    TabularDataset<?, LabelColumn.CategoricalLabelColumn> getDataset();
}
