package de.viadee.xai.framework.explanation_calculation.calculator;

import de.viadee.xai.framework.data.Feature;
import org.roaringbitmap.buffer.ImmutableRoaringBitmap;

import java.util.Map;
import java.util.Set;

/**
 * Calculator utilizing a {@link RoaringBitmapCalculator} to calculate covers and caching the total number for the different
 * covers.
 */
public class RoaringBitmapNumberCachedCalculator
        extends NumberCachedCalculator<RoaringBitmapCalculator>
        implements RoaringBitmapCalculator {

    /**
     * Constructor for RoaringBitmapNumberCachedCalculator.
     * @param delegateTo The calculator to delegate the actual computation in case of a cache-miss to.
     */
    public RoaringBitmapNumberCachedCalculator(RoaringBitmapCalculator delegateTo) {
        super(delegateTo);
    }

    @Override
    public ImmutableRoaringBitmap getCoveredAsBitmap(Feature.CategoricalFeature categoricalFeature, Set<Integer> featureValues) {
        return delegateTo.getCoveredAsBitmap(categoricalFeature, featureValues);
    }

    @Override
    public ImmutableRoaringBitmap getCoveredAsBitmap(Feature.CategoricalFeature categoricalFeature, Integer featureValue) {
        return delegateTo.getCoveredAsBitmap(categoricalFeature, featureValue);
    }

    @Override
    public ImmutableRoaringBitmap getCoveredAsBitmap(Map<Feature.CategoricalFeature, Set<Integer>> conditions) {
        return delegateTo.getCoveredAsBitmap(conditions);
    }

    @Override
    public ImmutableRoaringBitmap getCorrectlyCoveredAsBitmap(Map<Feature.CategoricalFeature, Set<Integer>> conditions, int labelValue) {
        return delegateTo.getCorrectlyCoveredAsBitmap(conditions, labelValue);
    }

    @Override
    public ImmutableRoaringBitmap getIncorrectlyCoveredAsBitmap(Map<Feature.CategoricalFeature, Set<Integer>> conditions, int labelValue) {
        return delegateTo.getIncorrectlyCoveredAsBitmap(conditions, labelValue);
    }

    @Override
    public ImmutableRoaringBitmap getCorrectlyNotCoveredAsBitmap(Map<Feature.CategoricalFeature, Set<Integer>> conditions, int labelValue) {
        return delegateTo.getCorrectlyNotCoveredAsBitmap(conditions, labelValue);
    }

    @Override
    public ImmutableRoaringBitmap getIncorrectlyNotCoveredAsBitmap(Map<Feature.CategoricalFeature, Set<Integer>> conditions, int labelValue) {
        return delegateTo.getIncorrectlyNotCoveredAsBitmap(conditions, labelValue);
    }

    @Override
    public ImmutableRoaringBitmap[] getCoversAsBitmap(Map<Feature.CategoricalFeature, Set<Integer>> conditions, int labelValue) {
        return delegateTo.getCoversAsBitmap(conditions, labelValue);
    }

    @Override
    public ImmutableRoaringBitmap[] getMinimalNumberCoversAsBitmap(Map<Feature.CategoricalFeature, Set<Integer>> conditions, int labelValue) {
        return delegateTo.getMinimalNumberCoversAsBitmap(conditions, labelValue);
    }

    @Override
    public int[] getMinimalAmountNumberInCovers(Map<Feature.CategoricalFeature, Set<Integer>> conditions, int labelValue) {
        Integer numberCorrectlyCovered = correctlyCoveredCaches.get(labelValue).get(conditions);
        if (numberCorrectlyCovered != null) {
            // Assume that the other covers also were calculated.
            return new int[] {
                    getNumberCovered(conditions),
                    numberCorrectlyCovered,
                    getNumberIncorrectlyNotCovered(conditions, labelValue)
            };
        }
        else {
            ImmutableRoaringBitmap[] minimalNumberCovers = this.getMinimalNumberCoversAsBitmap(conditions, labelValue);
            coveredCache.put(conditions, minimalNumberCovers[0].getCardinality());
            correctlyCoveredCaches.get(labelValue).put(conditions, minimalNumberCovers[1].getCardinality());
            incorrectlyNotCoveredCaches.get(labelValue).put(conditions, minimalNumberCovers[2].getCardinality());
            return new int[] {
                    minimalNumberCovers[0].getCardinality(),
                    minimalNumberCovers[1].getCardinality(),
                    minimalNumberCovers[2].getCardinality()
            };
        }
    }
}
