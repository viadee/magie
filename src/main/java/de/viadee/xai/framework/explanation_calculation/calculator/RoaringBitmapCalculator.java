package de.viadee.xai.framework.explanation_calculation.calculator;

import de.viadee.xai.framework.data.Feature;
import org.roaringbitmap.buffer.ImmutableRoaringBitmap;

import java.util.Map;
import java.util.Set;

/**
 * Interface for all calculators returning a {@link ImmutableRoaringBitmap} as a cover-representation.
 */
public interface RoaringBitmapCalculator extends CategoricalCalculator {
    /**
     * Returns the covered instances.
     * @param categoricalFeature The categorical feature.
     * @param featureValues The allowed integerized value.
     * @return The covered instances.
     */
    ImmutableRoaringBitmap getCoveredAsBitmap(Feature.CategoricalFeature categoricalFeature,
                                              Set<Integer> featureValues);

    /**
     * Returns the covered instances.
     * @param categoricalFeature The categorical feature.
     * @param featureValue The integerized value.
     * @return The number of covered instances.
     */
    ImmutableRoaringBitmap getCoveredAsBitmap(Feature.CategoricalFeature categoricalFeature,
                                              Integer featureValue);

    /**
     * Returns the covered instances.
     * @param conditions A map from the features to the allowed/necessary feature values.
     * @return The covered instances.
     */
    ImmutableRoaringBitmap getCoveredAsBitmap(Map<Feature.CategoricalFeature, Set<Integer>> conditions);

    /**
     * Returns the correctly covered instances.
     * @param conditions A map from the features to the allowed/necessary feature values.
     * @param labelValue The label value.
     * @return The correctly covered instances (true positives).
     */
    ImmutableRoaringBitmap getCorrectlyCoveredAsBitmap(Map<Feature.CategoricalFeature, Set<Integer>> conditions, int labelValue);

    /**
     * Returns the incorrectly covered instances.
     * @param conditions A map from the features to the allowed/necessary feature values.
     * @param labelValue The label value.
     * @return The correctly covered instances (false positives).
     */
    ImmutableRoaringBitmap getIncorrectlyCoveredAsBitmap(Map<Feature.CategoricalFeature, Set<Integer>> conditions, int labelValue);

    /**
     * Returns the correctly not covered instances.
     * @param conditions A map from the features to the allowed/necessary feature values.
     * @param labelValue The label value.
     * @return The correctly not covered instances (true negatives).
     */
    ImmutableRoaringBitmap getCorrectlyNotCoveredAsBitmap(Map<Feature.CategoricalFeature, Set<Integer>> conditions, int labelValue);

    /**
     * Returns the incorrectly not covered instances.
     * @param conditions A map from the features to the allowed/necessary feature values.
     * @param labelValue The label value.
     * @return The incorrectly not covered instances (false negatives).
     */
    ImmutableRoaringBitmap getIncorrectlyNotCoveredAsBitmap(Map<Feature.CategoricalFeature, Set<Integer>> conditions, int labelValue);

    /**
     * Returns an array where at position 0, the cover of true positives, at position 1, the covered false positives,
     * at position 2, the covered true negatives, and at position 3, the covered false negatives.
     * @param conditions A map from the features to the allowed/necessary feature values.
     * @param labelValue The label value.
     * @return An array containing the instances for each cover.
     */
    ImmutableRoaringBitmap[] getCoversAsBitmap(Map<Feature.CategoricalFeature, Set<Integer>> conditions, int labelValue);

    /**
     * Returns an array where at position 0, the covered instances, at position 1, the covered true positives, and
     * at position 2 the covered true negatives.
     * @param conditions A map from the features to the allowed/necessary feature values.
     * @param labelValue The label value.
     * @return An array containing the number of instances for aforementioned covers..
     */
    ImmutableRoaringBitmap[] getMinimalNumberCoversAsBitmap(Map<Feature.CategoricalFeature, Set<Integer>> conditions, int labelValue);
}
