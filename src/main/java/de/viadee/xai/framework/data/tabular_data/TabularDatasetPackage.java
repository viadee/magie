package de.viadee.xai.framework.data.tabular_data;

import de.viadee.xai.framework.data.Feature.CategoricalFeature;
import de.viadee.xai.framework.data.Feature.NumericFeature;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Contains either the original data set used for the {@link de.viadee.xai.framework.adapter.black_box_classifier_adapter.BlackBoxClassifierAdapter}
 * or the data set on which the {@link de.viadee.xai.framework.global_explanation_procedure_step.ExplanationProcedureStep}s
 * work on.
 * @param <L> The type of label column. Either a {@link de.viadee.xai.framework.data.tabular_data.LabelColumn.CategoricalLabelColumn}
 *           or a {@link de.viadee.xai.framework.data.tabular_data.LabelColumn.NumericLabelColumn}.
 */
public class TabularDatasetPackage<L extends LabelColumn> {

    protected final Map<CategoricalFeature, int[]> categoricalColumns;
    protected final Map<NumericFeature, double[]> numericColumns;
    protected final L labelColumn;

    /**
     * Constructor for creating a copy of the given {@link TabularDatasetPackage}.
     * @param copyFrom The to-be-copied instance.
     */
    public TabularDatasetPackage(TabularDatasetPackage<L> copyFrom) {
        categoricalColumns = new HashMap<>(copyFrom.categoricalColumns);
        numericColumns = new HashMap<>(copyFrom.numericColumns);
        labelColumn = copyFrom.labelColumn;
    }

    /**
     * Given numeric and categorical columns, as well as a label column, create a new T
     * {@link TabularDatasetPackage}.
     * @param categoricalColumns The categorical columns.
     * @param numericColumns The numeric columns.
     * @param labelColumn The label column.
     */
    public TabularDatasetPackage(
            Map<CategoricalFeature, int[]> categoricalColumns,
            Map<NumericFeature, double[]> numericColumns,
            L labelColumn) {
        this.categoricalColumns = categoricalColumns;
        this.numericColumns = numericColumns;
        this.labelColumn = labelColumn;
    }

    protected Map<NumericFeature, double[]> getNumericData() {
        return Collections.unmodifiableMap(numericColumns);
    }

    /**
     * Return the feature values of the given numeric feature.
     * @param numericFeature The numeric feature.
     * @return The feature values.
     */
    public double[] getNumericCol(NumericFeature numericFeature) {
        return numericColumns.get(numericFeature);
    }

    protected Map<CategoricalFeature, int[]> getCategoricalData() {
        return Collections.unmodifiableMap(categoricalColumns);
    }

    /**
     * Return the integerized feature values of the given  categorical feature.
     * @param categoricalFeature The categorical feature.
     * @return The integerized feature values.
     */
    public int[] getCatCol(CategoricalFeature categoricalFeature) {
        return categoricalColumns.get(categoricalFeature);
    }

    /**
     * Returns the categorical features.
     * @return The categorical features.
     */
    public Set<CategoricalFeature> getCatFeatures() {
        return categoricalColumns.keySet();
    }

    /**
     * Returns the numeric features.
     * @return The numeric features.
     */
    public Set<NumericFeature> getNumericFeatures() {
        return numericColumns.keySet();
    }

    /**
     * Returns the label column.
     * @return The label column.
     */
    public L getLabelCol() {
        return labelColumn;
    }

    /**
     * Returns the number of stored instances.
     * @return The number of instances in the package.
     */
    public int getLength() {
        return labelColumn.getLength();
    }

}
