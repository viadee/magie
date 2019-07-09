package de.viadee.xai.framework.data.tabular_data;

import de.viadee.xai.anchor.algorithm.DataInstance;
import de.viadee.xai.framework.data.Feature;
import de.viadee.xai.framework.data.Feature.CategoricalFeature;
import de.viadee.xai.framework.data.Feature.NumericFeature;
import de.viadee.xai.framework.data.FeatureValue;
import de.viadee.xai.framework.data.FeatureValue.CategoricalFeatureValue;
import de.viadee.xai.framework.data.FeatureValue.NumericFeatureValue;
import de.viadee.xai.framework.exception.ColumnForFeatureNotFound;
import de.viadee.xai.framework.exception.FeatureNotFound;

import java.util.Arrays;

/**
 * Class representing one row of a TabularDataset. It contains separate arrays of NumericFeatureValues and
 * CategoricalFeatureValues for the "original" and "processed" data.
 * For an explanation on "original" and "processed" data, see {@link TabularDataset}
 * {@link FeatureValue}
 */
public class TabularRow implements DataInstance<FeatureValue[]> { // TODO Type parameters for both labels

    protected final NumericFeatureValue[] originalNumericFeatureValues;
    protected final CategoricalFeatureValue[] originalCategoricalFeatureValues;
    protected final NumericFeatureValue[] processedNumericFeatureValues;
    protected final CategoricalFeatureValue[] processedCategoricalFeatureValues;
    protected final FeatureValue originalLabelValue;
    protected final FeatureValue processedLabelValue;

    /**
     * Constructor for a TabularRow instance.
     * @param originalNumericFeatureValues The original numeric feature values.
     * @param originalCategoricalFeatureValues The original categorical feature values.
     * @param processedNumericFeatureValues The processed numeric feature values.
     * @param processedCategoricalFeatureValues The processed categorical feature values.
     * @param originalLabelValue The original label feature value.
     * @param processedLabelValue The processed label feature value.
     */
    public TabularRow(final NumericFeatureValue[] originalNumericFeatureValues,
                      final CategoricalFeatureValue[] originalCategoricalFeatureValues,
                      final NumericFeatureValue[] processedNumericFeatureValues,
                      final CategoricalFeatureValue[] processedCategoricalFeatureValues,
                      final FeatureValue originalLabelValue,
                      final FeatureValue processedLabelValue) {
        this.originalCategoricalFeatureValues = originalCategoricalFeatureValues;
        this.originalNumericFeatureValues = originalNumericFeatureValues;
        this.processedCategoricalFeatureValues = processedCategoricalFeatureValues;
        this.processedNumericFeatureValues = processedNumericFeatureValues;
        this.originalLabelValue = originalLabelValue;
        this.processedLabelValue = processedLabelValue;
    }

    // Help method to the double-value for a given numeric feature.
    private double getNumValue(final NumericFeature numericFeature,
                               final NumericFeatureValue[] findIn) {
        for (int i = 0; i < findIn.length; i++) {
            if (findIn[i].getFeature().equals(numericFeature)) {
                return findIn[i].getValue();
            }
        }
        throw new FeatureNotFound(numericFeature);
    }

    // Help method to the int working-representation for a given categorical feature.
    private int getCatValue(final CategoricalFeature categoricalFeature, final CategoricalFeatureValue[] findIn) {
        for (int i = 0; i < findIn.length; i++) {
            if (findIn[i].getFeature().equals(categoricalFeature)) {
                return findIn[i].getValue();
            }
        }
        throw new FeatureNotFound(categoricalFeature);
    }

    /**
     * Returns the double cell-value for a given numeric feature in the original data subset of this row.
     * @param numericFeature The numeric feature.
     * @return The double value of the cell of the row specified by the numeric feature.
     */
    public double getOriginalValue(final NumericFeature numericFeature) {
        return getNumValue(numericFeature, originalNumericFeatureValues);
    }

    /**
     * Returns a double-value of either the working representation of a categorical feature value, or of a numeric feature
     * value of the original data subset.
     * Should be used with care, as the int working-representation of the categorical feature is casted to double.
     * @param feature The feature for which the value of the row is to be retrieved.
     * @return The value of the row specified by the feature.
     */
    public double getOriginalValue(final Feature feature) {
        if (feature instanceof CategoricalFeature) {
            return getOriginalValue((CategoricalFeature) feature);
        } else if (feature instanceof NumericFeature) {
            return getOriginalValue((NumericFeature) feature);
        } else {
            throw new ColumnForFeatureNotFound(feature);
        }
    }

    /**
     * Returns the int working-representation value of the categorical feature from the features used in the original
     * data subset.
     * @param categoricalFeature The categorical feature.
     * @return The int working-representation of the categorical feature's value for this row.
     */
    public int getOriginalValue(final CategoricalFeature categoricalFeature) {
        return getCatValue(categoricalFeature, originalCategoricalFeatureValues);
    }

    /**
     * Returns a double-value of either the working representation of a categorical feature value, or of a numeric feature
     * value of the processed data subset.
     * Should be used with care, as the int working-representation of the categorical feature is casted to double.
     * @param feature The feature for which the value of the row is to be retrieved.
     * @return The value of the row specified by the feature.
     */
    public double getProcessedValue(final Feature feature) {
        if (feature instanceof CategoricalFeature) {
            return this.getProcessedValue((CategoricalFeature) feature);
        } else if (feature instanceof NumericFeature) {
            return this.getProcessedValue((NumericFeature) feature);
        } else {
            throw new ColumnForFeatureNotFound(feature);
        }
    }

    /**
     * Returns the double cell-value for a given numeric feature in the processed data subset of this row.
     * @param numericFeature The numeric feature.
     * @return The double value of the cell of the row specified by the numeric feature.
     */
    public double getProcessedValue(final NumericFeature numericFeature) {
        return getNumValue(numericFeature, processedNumericFeatureValues);
    }

    /**
     * Returns the int working-representation value of the categorical feature from the features used in the processed
     * data subset.
     * @param categoricalFeature The categorical feature.
     * @return The int working-representation of the categorical feature's value for this row.
     */
    public int getProcessedValue(final CategoricalFeature categoricalFeature) {
        return getCatValue(categoricalFeature, processedCategoricalFeatureValues);
    }

    /**
     * Returns the FeatureValue of the original label.
     * @return The label of the original data subset of this row.
     */
    public FeatureValue getOriginalLabelValue() {
        return originalLabelValue;
    }

    /**
     * Returns the FeatureValue of the processed label.
     * @return The label of the processed data subset of this row.
     */
    public FeatureValue getProcessedLabelValue() {
        return processedLabelValue;
    }

    /**
     * Returns the FeatureValues of the original data subset.
     * @return An array of FeatureValues of the original data subset.
     */
    public FeatureValue[] getOriginalValues() {
        return bundleFeatureValues(originalNumericFeatureValues, originalCategoricalFeatureValues);
    }

    /**
     * Returns the FeatureValues of the processed data subset.
     * @return An array of FeatureValues of the processed data subset.
     */
    public FeatureValue[] getProcessedFeatureValues() {
        return bundleFeatureValues(processedNumericFeatureValues, processedCategoricalFeatureValues);
    }

    /**
     * Returns the CategoricalFeatureValues of the processed data subset.
     * @return An array of CategoricalFeatureValues of the processed data subset.
     */
    public CategoricalFeatureValue[] getProcessedCatFeatureValues() {
        return processedCategoricalFeatureValues;
    }

    /**
     * Returns the CategoricalFeatureValues of the original data subset.
     * @return An array of CategoricalFeatureValues of the original data subset.
     */
    public CategoricalFeatureValue[] getOriginalCatFeatureValues() {
        return originalCategoricalFeatureValues;
    }

    @Override
    public FeatureValue[] getInstance() {
        return getProcessedFeatureValues();
    }

    // Helper method to unify the numericc and categorical feature values in the FeatureValue-superclass array.
    private FeatureValue[] bundleFeatureValues(final NumericFeatureValue[] numericFeatureValues,
                                               final CategoricalFeatureValue[] categoricalFeatureValues) {
        // Concatenate arrays
        int numLength = numericFeatureValues.length;
        int catLength = categoricalFeatureValues.length;
        FeatureValue[] result = new FeatureValue[numLength + catLength];
        System.arraycopy(numericFeatureValues, 0, result, 0, numLength);
        System.arraycopy(categoricalFeatureValues, 0, result, numLength, catLength);
        return result;
    }

    /**
     * Returns the CategoricalFeature of the processed data subset with a given name.
     * @param find The name of the searched-for CategoricalFeature.
     * @return The CategoricalFeature with the specified name.
     */
    public CategoricalFeature getProcessedCatFeatureForName(final String find) {
        return (CategoricalFeature) getFeatureForName(find, processedCategoricalFeatureValues);
    }

    /**
     * Returns the NumericFeature of the processed data subset with a given name.
     * @param find The name of the searched-for NumericFeature.
     * @return The NumericFeature with the specified name.
     */
    public NumericFeature getProcessedNumFeatureForName(final String find) {
        return (NumericFeature) getFeatureForName(find, processedNumericFeatureValues);
    }

    /**
     * Returns the CategoricalFeature of the original data subset with a given name.
     * @param find The name of the searched-for CategoricalFeature.
     * @return The CategoricalFeature with the specified name.
     */
    public CategoricalFeature getOriginalCatFeatureForName(final String find) {
        return (CategoricalFeature) getFeatureForName(find, originalCategoricalFeatureValues);
    }

    /**
     * Returns the NumericFeature of the original data subset with a given name.
     * @param find The name of the searched-for NumericFeature.
     * @return The NumericFeature with the specified name.
     */
    public NumericFeature getOriginalNumFeatureForName(final String find) {
        return (NumericFeature) getFeatureForName(find, originalNumericFeatureValues);
    }

    /**
     * Returns the number of categorical feature values of the original data subset in this row.
     * @return The number of original categorical feature values.
     */
    public int getNumberOriginalCatFeatureValues() {
        return originalCategoricalFeatureValues.length;
    }

    /**
     * Returns the number of numeric feature values of the original data subset in this row.
     * @return The number of original numeric feature values.
     */
    public int getNumberOriginalNumFeatureValues() {
        return originalNumericFeatureValues.length;
    }

    /**
     * Returns the number of categorical feature values of the processed data subset in this row.
     * @return The number of processed categorical feature values.
     */
    public int getNumberProcessedCatFeatureValues() {
        return processedCategoricalFeatureValues.length;
    }

    /**
     * Returns the number of numeric feature values of the processed data subset in this row.
     * @return The number of processed numeric feature values.
     */
    public int getNumberProcessedNumFeatureValues() {
        return processedNumericFeatureValues.length;
    }

    // Helper method, find a feature in the array of feature values given its name.
    private Feature getFeatureForName(final String find,
                                      final FeatureValue[] featureValues) {
        for (FeatureValue fv : featureValues) {
            if (fv.getFeature().getName().equals(find)) {
                return fv.getFeature();
            }
        }
        throw new FeatureNotFound(find);
    }

    @Override
    public int getFeatureCount() {
        return processedCategoricalFeatureValues.length + processedNumericFeatureValues.length;
    }

    @Override
    public String toString() {
        return "TabularRow{" +
                "\noriginalNumericFeatureValues=" + Arrays.toString(originalNumericFeatureValues) +
                ",\noriginalCategoricalFeatureValues=" + Arrays.toString(originalCategoricalFeatureValues) +
                ",\nprocessedNumericFeatureValues=" + Arrays.toString(processedNumericFeatureValues) +
                ",\nprocessedCategoricalFeatureValues=" + Arrays.toString(processedCategoricalFeatureValues) +
                "}";
    }
}
