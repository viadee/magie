package de.viadee.xai.framework.data.index;

import de.viadee.xai.framework.data.Feature.CategoricalFeature;
import de.viadee.xai.framework.data.tabular_data.LabelColumn;
import de.viadee.xai.framework.data.tabular_data.TabularDataset;
import org.roaringbitmap.buffer.ImmutableRoaringBitmap;
import org.roaringbitmap.buffer.MutableRoaringBitmap;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Straight-forward implementation of a RoaringBitmapIndex only allowing for categorical features.
 */
public class SimpleRoaringBitmapIndex implements RoaringBitmapIndex {

    protected final TabularDataset<?, LabelColumn.CategoricalLabelColumn> dataset;
    protected final Map<CategoricalFeature, Map<Integer, ImmutableRoaringBitmap>> catIndexes;

    /**
     * Constructor for the SimpleRoaringBitmapIndex.
     * @param dataset The data set which should be indexed.
     */
    public SimpleRoaringBitmapIndex(TabularDataset<?, LabelColumn.CategoricalLabelColumn> dataset) {
        this.dataset = dataset;
        Map<CategoricalFeature, Map<Integer, MutableRoaringBitmap>> tempCatIndexes = new HashMap<>();
        for (CategoricalFeature currentCatFeature : dataset.getProcessedCatFeatures()) {
            tempCatIndexes.put(currentCatFeature, new HashMap<>());
            int[] valuesForCatFeature = dataset.getProcessedCol(currentCatFeature);
            for (int row = 0; row < valuesForCatFeature.length; row++) {
                tempCatIndexes.get(currentCatFeature).putIfAbsent(valuesForCatFeature[row], new MutableRoaringBitmap());
                tempCatIndexes.get(currentCatFeature).get(valuesForCatFeature[row]).add(row);
            }
        }
        int[] labelValues = dataset.getProcessedLabelCol().getValues();
        CategoricalFeature labelFeature = dataset.getProcessedLabelCol().getLabel();
        tempCatIndexes.put(labelFeature, new HashMap<>());
        for (int row = 0; row < labelValues.length; row++) {
            tempCatIndexes.get(labelFeature).putIfAbsent(labelValues[row], new MutableRoaringBitmap());
            tempCatIndexes.get(labelFeature).get(labelValues[row]).add(row);
        }

        // Simply copy the mutable roaring bitmap to the immutable declaration.
        catIndexes = new HashMap<>();
        for (Map.Entry<CategoricalFeature, Map<Integer, MutableRoaringBitmap>> outerEntry : tempCatIndexes.entrySet()) {
            catIndexes.put(outerEntry.getKey(), new HashMap<>());
            for (Map.Entry<Integer, MutableRoaringBitmap> innerEntry : outerEntry.getValue().entrySet()) {
                catIndexes.get(outerEntry.getKey()).put(innerEntry.getKey(), innerEntry.getValue());
            }
        }
    }

    @Override
    public ImmutableRoaringBitmap getInstancesAsBitmap(CategoricalFeature categoricalFeature, Integer featureValue) {
        ImmutableRoaringBitmap result = catIndexes.get(categoricalFeature).get(featureValue);
        if (result == null) {
            // This might occur if due to a split of the original dataset some feature values are not in either dataset.
            // In this case, a new RoaringBitmap is returned, indicating an empty set.
            return new MutableRoaringBitmap();
        } else {
            return result;
        }
    }

    @Override
    public MutableRoaringBitmap getInstancesAsBitmap(CategoricalFeature categoricalFeature,
                                                     Set<Integer> featureValues) {
        MutableRoaringBitmap resultRoaringBitmap = null;
        for (Integer i : featureValues) {
            if (resultRoaringBitmap == null) {
                resultRoaringBitmap = getInstancesAsBitmap(categoricalFeature, i).toMutableRoaringBitmap();
            } else {
                resultRoaringBitmap.or(getInstancesAsBitmap(categoricalFeature, i));
            }
        }
        if (resultRoaringBitmap == null) {
            return new MutableRoaringBitmap();
        } else {
            return resultRoaringBitmap;
        }
    }

    @Override
    public MutableRoaringBitmap getInstancesAsBitmap(Map<CategoricalFeature, Set<Integer>> conditions) {
        MutableRoaringBitmap resultRoaringBitmap = null;
        for (Map.Entry<? extends CategoricalFeature, Set<Integer>> condition : conditions.entrySet()) {
            if (resultRoaringBitmap == null) {
                resultRoaringBitmap = getInstancesAsBitmap(condition.getKey(), condition.getValue());
            } else {
                resultRoaringBitmap.and(getInstancesAsBitmap(condition.getKey(), condition.getValue()));
            }
        }
        if (resultRoaringBitmap == null) {
            return new MutableRoaringBitmap();
        } else {
            return resultRoaringBitmap;
        }
    }

    @Override
    public TabularDataset<?, LabelColumn.CategoricalLabelColumn> getDataset() {
        return dataset;
    }
}
