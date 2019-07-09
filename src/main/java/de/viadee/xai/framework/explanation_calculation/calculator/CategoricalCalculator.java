package de.viadee.xai.framework.explanation_calculation.calculator;

import de.viadee.xai.framework.data.Feature.CategoricalFeature;
import de.viadee.xai.framework.data.tabular_data.LabelColumn;
import de.viadee.xai.framework.data.tabular_data.TabularDataset;

import java.util.Map;
import java.util.Set;

/**
 * The interface for all calculators deriving the cover of a query, most often to evaluate a
 * {@link de.viadee.xai.framework.explanation_calculation.explanation.RuleExplanation}.
 */
public interface CategoricalCalculator {

    /**
     * Returns the number of instances exhibiting a categorical feature values.
     * @param categoricalFeature The categorical feature.
     * @param featureValue The integerized value.
     * @return The number of covered instances.
     */
    int getNumberCovered(CategoricalFeature categoricalFeature, int featureValue);

    /**
     * Returns the number of instances exhibiting any of  the given feature value for categorical features.
     * @param conditions A map from the features to the allowed/necessary feature values.
     * @return The number of covered instances.
     */
    int getNumberCovered(Map<CategoricalFeature, Set<Integer>> conditions);

    /**
     * Returns the number of instances exhibiting any of the given feature value for categorical features and exhibiting
     * the specified label value.
     * @param conditions A map from the features to the allowed/necessary feature values.
     * @param labelValue The label value.
     * @return The number of correctly covered instances (true positives).
     */
    int getNumberCorrectlyCovered(Map<CategoricalFeature, Set<Integer>> conditions, int labelValue);

    /**
     * Returns the number of instances exhibiting any of the given feature value for categorical features and not
     * exhibiting the specified label value.
     * @param conditions A map from the features to the allowed/necessary feature values.
     * @param labelValue The label value.
     * @return The number of incorrectly covered instances (false positives).
     */
    int getNumberIncorrectlyCovered(Map<CategoricalFeature, Set<Integer>> conditions, int labelValue);

    /**
     * Returns the number of instances not exhibiting any of the given feature value for at least one categorical feature
     * and furthermore not exhibiting the specified label value.
     * @param conditions A map from the features to the allowed/necessary feature values.
     * @param labelValue The label value.
     * @return The number of correctly not covered instances (true negatives).
     */
    int getNumberCorrectlyNotCovered(Map<CategoricalFeature, Set<Integer>> conditions, int labelValue);

    /**
     * Returns the number of instances not exhibiting any of the given feature value for at least one categorical feature
     * and furthermore exhibiting the specified label value.
     * @param conditions A map from the features to the allowed/necessary feature values.
     * @param labelValue The label value.
     * @return The number of incorrectly not covered instances (false negatives).
     */
    int getNumberIncorrectlyNotCovered(Map<CategoricalFeature, Set<Integer>> conditions, int labelValue);

    /**
     * Returns an array where at position 0, the number of true positives, at position 1, the false positives, at position 2,
     * the true negatives, and at position 3, the false negatives.
     * @param conditions A map from the features to the allowed/necessary feature values.
     * @param labelValue The label value.
     * @return An array containing the number of instances for each cover.
     */
    int[] getNumberInCovers(Map<CategoricalFeature, Set<Integer>> conditions, int labelValue);

    /**
     * Returns an array where at position 0, the number of covered instances, at position 1, the true positives, and
     * at position 2 the true negatives.
     * @param conditions A map from the features to the allowed/necessary feature values.
     * @param labelValue The label value.
     * @return An array containing the number of instances for aforementioned covers..
     */
    int[] getMinimalAmountNumberInCovers(Map<CategoricalFeature, Set<Integer>> conditions, int labelValue);

    /**
     * Returns the data set which the calculator uses.
     * @return The data set.
     */
    TabularDataset<?, LabelColumn.CategoricalLabelColumn> getDataset();
}
