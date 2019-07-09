package de.viadee.xai.framework.explanation_calculation.calculator;

import de.viadee.xai.framework.data.Feature;
import de.viadee.xai.framework.data.index.RoaringBitmapIndex;
import de.viadee.xai.framework.data.tabular_data.LabelColumn;
import de.viadee.xai.framework.data.tabular_data.TabularDataset;
import org.roaringbitmap.buffer.ImmutableRoaringBitmap;

import java.util.Map;
import java.util.Set;

/**
 * Simple calculator utilizing {@link ImmutableRoaringBitmap}s and a {@link RoaringBitmapIndex}.
 */
public class SimpleRoaringBitmapCalculator implements RoaringBitmapCalculator {
    protected final RoaringBitmapIndex roaringBitmapIndex;
    protected final TabularDataset<?, LabelColumn.CategoricalLabelColumn> dataset;
    protected final Feature.CategoricalFeature labelFeature;
    protected final int datasetSize;

    /**
     * Constructor for SimpleRoaringBitmapCalculator.
     * @param roaringBitmapIndex The {@link RoaringBitmapIndex} utilized by the calculator.
     */
    public SimpleRoaringBitmapCalculator(RoaringBitmapIndex roaringBitmapIndex) {
        this.dataset = roaringBitmapIndex.getDataset();
        this.labelFeature = dataset.getProcessedLabelCol().getLabel();
        datasetSize = dataset.getNumberRows();

        this.roaringBitmapIndex = roaringBitmapIndex;
    }


    @Override
    public ImmutableRoaringBitmap getCoveredAsBitmap(Feature.CategoricalFeature categoricalFeature, Set<Integer> featureValues) {
        return roaringBitmapIndex.getInstancesAsBitmap(categoricalFeature, featureValues);
    }

    @Override
    public ImmutableRoaringBitmap getCoveredAsBitmap(Feature.CategoricalFeature categoricalFeature, Integer featureValue) {
        return roaringBitmapIndex.getInstancesAsBitmap(categoricalFeature, featureValue);
    }

    @Override
    public ImmutableRoaringBitmap getCoveredAsBitmap(Map<Feature.CategoricalFeature, Set<Integer>> conditions) {
        return roaringBitmapIndex.getInstancesAsBitmap(conditions);
    }

    @Override
    public ImmutableRoaringBitmap getCorrectlyCoveredAsBitmap(Map<Feature.CategoricalFeature, Set<Integer>> conditions, int labelValue) {
        ImmutableRoaringBitmap covered = roaringBitmapIndex.getInstancesAsBitmap(conditions);
        ImmutableRoaringBitmap labelBitmap = roaringBitmapIndex.getInstancesAsBitmap(labelFeature, labelValue);
        return ImmutableRoaringBitmap.and(covered, labelBitmap);
    }

    @Override
    public ImmutableRoaringBitmap getIncorrectlyCoveredAsBitmap(Map<Feature.CategoricalFeature, Set<Integer>> conditions, int labelValue) {
        ImmutableRoaringBitmap covered = roaringBitmapIndex.getInstancesAsBitmap(conditions);
        ImmutableRoaringBitmap labelBitmap = roaringBitmapIndex.getInstancesAsBitmap(labelFeature, labelValue);
        return ImmutableRoaringBitmap.andNot(covered, labelBitmap);
    }

    @Override
    public ImmutableRoaringBitmap getCorrectlyNotCoveredAsBitmap(Map<Feature.CategoricalFeature, Set<Integer>> conditions, int labelValue) {
        ImmutableRoaringBitmap covered = roaringBitmapIndex.getInstancesAsBitmap(conditions);
        Set<Integer> labelValues = labelFeature.getUniqueNumberRepresentations();
        labelValues.remove(labelValue);
        ImmutableRoaringBitmap notLabelBitmap = roaringBitmapIndex.getInstancesAsBitmap(labelFeature, labelValues);
        return ImmutableRoaringBitmap.andNot(notLabelBitmap, covered);
    }

    @Override
    public ImmutableRoaringBitmap getIncorrectlyNotCoveredAsBitmap(Map<Feature.CategoricalFeature, Set<Integer>> conditions, int labelValue) {
        ImmutableRoaringBitmap covered = roaringBitmapIndex.getInstancesAsBitmap(conditions);
        ImmutableRoaringBitmap labelBitmap = roaringBitmapIndex.getInstancesAsBitmap(labelFeature, labelValue);
        return ImmutableRoaringBitmap.andNot(labelBitmap, covered);
    }

    @Override
    public ImmutableRoaringBitmap[] getCoversAsBitmap(Map<Feature.CategoricalFeature, Set<Integer>> conditions, int labelValue) {
        ImmutableRoaringBitmap covered = getCoveredAsBitmap(conditions);
        ImmutableRoaringBitmap labelBitmap = roaringBitmapIndex.getInstancesAsBitmap(labelFeature, labelValue);
        ImmutableRoaringBitmap correctlyCovered = ImmutableRoaringBitmap.and(covered, labelBitmap);
        ImmutableRoaringBitmap incorrectlyCovered = ImmutableRoaringBitmap.andNot(covered, labelBitmap);
        Set<Integer> labelValues = labelFeature.getUniqueNumberRepresentations();
        labelValues.remove(labelValue);
        ImmutableRoaringBitmap notLabelBitmap = roaringBitmapIndex.getInstancesAsBitmap(labelFeature, labelValues);
        ImmutableRoaringBitmap correctlyNotCovered = ImmutableRoaringBitmap.andNot(notLabelBitmap, covered);
        ImmutableRoaringBitmap incorrectlyNotCovered = ImmutableRoaringBitmap.andNot(labelBitmap, covered);
        return new ImmutableRoaringBitmap[]{
                correctlyCovered,
                incorrectlyCovered,
                correctlyNotCovered,
                incorrectlyNotCovered
        };
    }

    @Override
    public ImmutableRoaringBitmap[] getMinimalNumberCoversAsBitmap(Map<Feature.CategoricalFeature, Set<Integer>> conditions, int labelValue) {
        ImmutableRoaringBitmap covered = getCoveredAsBitmap(conditions);
        ImmutableRoaringBitmap labelBitmap = roaringBitmapIndex.getInstancesAsBitmap(labelFeature, labelValue);
        ImmutableRoaringBitmap correctlyCovered = ImmutableRoaringBitmap.and(covered, labelBitmap);
        ImmutableRoaringBitmap incorrectlyNotCovered = ImmutableRoaringBitmap.andNot(labelBitmap, covered);
        return new ImmutableRoaringBitmap[]{
                covered,
                correctlyCovered,
                incorrectlyNotCovered
        };
    }

    @Override
    public int getNumberCovered(Feature.CategoricalFeature categoricalFeature, int featureValue) {
        return getCoveredAsBitmap(categoricalFeature, featureValue).getCardinality();
    }

    @Override
    public int getNumberCovered(final Map<Feature.CategoricalFeature, Set<Integer>> conditions) {
        return getCoveredAsBitmap(conditions).getCardinality();
    }

    @Override
    public int getNumberCorrectlyCovered(final Map<Feature.CategoricalFeature, Set<Integer>> conditions,
                                         final int labelValue) {
        return getCorrectlyCoveredAsBitmap(conditions, labelValue).getCardinality();
    }

    @Override
    public int getNumberIncorrectlyCovered(final Map<Feature.CategoricalFeature, Set<Integer>> conditions,
                                           final int labelValue) {
        return getIncorrectlyCoveredAsBitmap(conditions, labelValue).getCardinality();
    }

    @Override
    public int getNumberCorrectlyNotCovered(final Map<Feature.CategoricalFeature, Set<Integer>> conditions,
                                            final int labelValue) {
        return getCorrectlyNotCoveredAsBitmap(conditions, labelValue).getCardinality();
    }

    @Override
    public int getNumberIncorrectlyNotCovered(final Map<Feature.CategoricalFeature, Set<Integer>> conditions,
                                              final int labelValue) {
        return getIncorrectlyNotCoveredAsBitmap(conditions, labelValue).getCardinality();
    }

    public int[] getNumberInCovers(final Map<Feature.CategoricalFeature, Set<Integer>> conditions,
                                   final int labelValue) {
        ImmutableRoaringBitmap[] covers = getCoversAsBitmap(conditions, labelValue);
        return new int[]{
                covers[0].getCardinality(),
                covers[1].getCardinality(),
                covers[2].getCardinality(),
                covers[3].getCardinality()
        };

    }

    @Override
    public int[] getMinimalAmountNumberInCovers(Map<Feature.CategoricalFeature, Set<Integer>> conditions, int labelValue) {
        ImmutableRoaringBitmap[] covers = getMinimalNumberCoversAsBitmap(conditions, labelValue);
        return new int[]{
                covers[0].getCardinality(),
                covers[1].getCardinality(),
                covers[2].getCardinality(),
        };

    }

    @Override
    public TabularDataset<?, LabelColumn.CategoricalLabelColumn> getDataset() {
        return dataset;
    }
}