package de.viadee.xai.framework.explanation_calculation.explanation;

import de.viadee.xai.framework.data.Feature.CategoricalFeature;
import de.viadee.xai.framework.data.tabular_data.LabelColumn;
import de.viadee.xai.framework.data.tabular_data.TabularDataset;
import de.viadee.xai.framework.explanation_calculation.calculator.CategoricalCalculator;
import de.viadee.xai.framework.explanation_calculation.calculator.RoaringBitmapCalculator;
import org.roaringbitmap.buffer.ImmutableRoaringBitmap;

import java.util.Map;
import java.util.Set;

/**
 * The interface of all RuleExplanations. A RuleExplanation contains conditions, i.e. a CategoricalFeature and
 * a corresponding Set of Integers representing the allowed manifestations of this CategoricalFeature under
 * the RuleExplanation. Each RuleExplanation predicts one certain CategoricalFeature label with the associated value.
 * Each RuleExplanation "covers" a certain portion of the dataset, i.e., data instances for which
 * all conditions hold.
 */
public interface RuleExplanation {

    /**
     * Returns a bitmap representing the indices of the instances covered by the rule on the dataset, the
     * RuleExplanation was originally calculated on.
     * @return The bitmap.
     */
    ImmutableRoaringBitmap getCoverAsBitmap();

    /**
     * Returns a bitmap representing the indices of the instances covered by the rule.
     * @param calculator The calculator with which to calculate the covered instances.
     * @return The bitmap.
     */
    ImmutableRoaringBitmap calculateCoverAsBitmap(final RoaringBitmapCalculator calculator);

    /**
     * Returns a bitmap representing the indices of the instances correctly covered by the rule on the dataset,
     * the RuleExplanation was originally calculated on. An instance is correctly covered, if it is covered and the
     * label corresponds with the label of the RuleExplanation.
     * @return The bitmap.
     */
    ImmutableRoaringBitmap getCorrectCoverAsBitmap();

    /**
     * Returns a bitmap representing the indices of the instances covered by the rule.
     * @param calculator The calculator with which to calculate the correctly covered instances.
     * @return The bitmap.
     */
    ImmutableRoaringBitmap calculateCorrectCoverAsBitmap(final RoaringBitmapCalculator calculator);

    /**
     * Returns a bitmap representing the indices of the instances incorrectly covered by the rule on the dataset,
     * the RuleExplanation was originally calculated on. An instance is incorrectly covered, if it is covered but the
     * label does not correspond with the label of the RuleExplanation.
     * @return The bitmap.
     */
    ImmutableRoaringBitmap getIncorrectCoverAsBitmap();

    /**
     * Returns a set of Integers representing the indices of the instances incorrectly covered by the rule on the dataset.
     * An instance is incorrectly covered, if it is covered but the label does not correspond with the label of the
     * RuleExplanation.
     * @param calculator The calculator with which to calculate the cinorrectly covered instances.
     * @return The set of indices.
     */
    ImmutableRoaringBitmap calculateIncorrectCoverAsBitmap(final RoaringBitmapCalculator calculator);

    /**
     * Returns a bitmap representing the indices of the instances correctly not covered by the rule on the dataset.
     * An instance is correctly not covered, if it is not covered and the label does not correspond with the label of the
     * RuleExplanation.
     * @param calculator The calculator with which to calculate the correctly not covered instances.
     * @return The bitmap.
     */
    ImmutableRoaringBitmap calculateCorrectlyNotCoveredAsBitmap(final RoaringBitmapCalculator calculator);

    /**
     * Returns a bitmap representing the indices of the instances correctly not covered by the rule on the dataset,
     * the RuleExplanation was originally calculated on, i.e., the label of the instance does not correspond to the label
     * of the rule and the instance is not contained in the covered instances.
     * @return The set of indices.
     */
    ImmutableRoaringBitmap getCorrectlyNotCoveredAsBitmap();

    /**
     * Returns a bitmap representing the indices of the instances incorrectly not covered by the rule on the dataset,
     * the RuleExplanation was originally calculated on, i.e., the label of the instance corresponds to the label of the rule,
     * yet, the instance is not contained in the covered instances.
     * @return The bitmap.
     */
    ImmutableRoaringBitmap getIncorrectlyNotCoveredAsBitmap();

    /**
     * Returns a bitmap representing the indices of the instances incorrectly not covered by the rule on the dataset.
     * @param calculator The calculator with which to calculate the incorrectly covered instances.
     * @return The bitmap.
     */
    ImmutableRoaringBitmap calculateIncorrectlyNotCoveredAsBitmap(final RoaringBitmapCalculator calculator);

    /**
     * Returns the number of instances correctly covered by the rule.
     * @return The number of instances.
     */
    int getNumberCorrectlyCovered();

    /**
     * Returns the number of instances incorrectly covered by the rule.
     * @return The number of instances.
     */
    int getNumberIncorrectlyCovered();

    /**
     * Returns the number of instances incorrectly not covered by the rule.
     * @return The number of instances.
     */
    int getNumberIncorrectlyNotCovered();

    /**
     * Returns the number of instances correctly not covered by the rule.
     * @return The number of instances.
     */
    int getNumberCorrectlyNotCovered();

    /**
     * Returns the number of instances covered by the rule.
     * @return The number of instances.
     */
    int getNumberCovered();

    /**
     * Returns the set of CategoricalFeatures representing conditions.
     * @return The CategoricalFeatures.
     */
    Set<CategoricalFeature> getConditionFeatures();

    /**
     * Returns the number of categorical features.
     * @return The number of categorical features.
     */
    int getNumberConditions();

    /**
     * Returns the sum of number of values for all CategoricalFeatures.
     * @return The number of all values for all CategoricalFeatures contained in the RuleExplanation.
     */
    int getNumberConditionValues();

    /**
     * Returns the values for a CategoricalFeature. These values represent conditions.
     * @param condition The CategoricalFeature for which the values are to be retrieved.
     * @return The set of Integer working-representations of the CategoricalFeature representing conditions.
     */
    Set<Integer> getConditionValues(final CategoricalFeature condition);

    /**
     * Returns the coverage of the RuleExplanation on the dataset the RuleExplanation was originally calculated on.
     * @return The coverage.
     */
    double getCoverage();

    /**
     * Returns the roundPrecision of the RuleExplanation on the dataset the RuleExplanation was originally calculated on.
     * @return The roundPrecision.
     */
    double getPrecision();

    /**
     * Calculates the roundPrecision of the RuleExplanation on the dataset.
     * @param calculator The calculator with which to calculate the roundPrecision.
     * @return The roundPrecision.
     */
    double calculatePrecision(final CategoricalCalculator calculator);

    /**
     * Calculates the coverage of the RuleExplanation on the dataset.
     * @param calculator The calculator with which to calculate the coverage.
     * @return The coverage.
     */
    double calculateCoverage(final CategoricalCalculator calculator);

    /**
     * Returns the dataset the RuleExplanation was originally calculated on.
     * @return The dataset.
     */
    TabularDataset<?, LabelColumn.CategoricalLabelColumn> getDataset();

    /**
     * Returns a mapping from the categorical features and the integer-working representation of the taken values.
     * @return A representation of which categorical features can assume which values to be satisfied.
     */
    Map<CategoricalFeature, Set<Integer>> getConditions();

    /**
     * Returns the label feature.
     * @return The label feature.
     */
    CategoricalFeature getLabelFeature();

    /**
     * Returns the label value predicted by the RuleExplanation.
     * @return The categorical label value.
     */
    int getLabelValue();

    /**
     * Returns the calculator which was injected into the object to calculate the covers from.
     * @return The calculator.
     */
    RoaringBitmapCalculator getCalculator();

    @Override
    boolean equals(final Object object);

    @Override
    int hashCode();
}
