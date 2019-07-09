package de.viadee.xai.framework.data.tabular_data;

import de.viadee.xai.framework.data.Feature;
import de.viadee.xai.framework.data.Feature.CategoricalFeature;
import de.viadee.xai.framework.data.Feature.NumericFeature;

import java.io.Serializable;

/**
 * Represents the label column of a data set. Is sealed. The relevant implementations are NumericLabelColumn
 * and CategoricalLabelColumn.
 * @param <F> The feature of the label. Either NumericFeature or CategoricalFeature.
 * @param <V> The type of value within the label column. Either double[] or int[].
 * {@link CategoricalLabelColumn}
 * {@link NumericLabelColumn}
 */
public abstract class LabelColumn<F extends Feature, V extends Serializable> {
    protected F labelFeature;
    protected V labelValues;

    private LabelColumn(F labelFeature,
                        V labelValues) {
        this.labelFeature = labelFeature;
        this.labelValues = labelValues;
    }

    /**
     * Returns the feature type of the label.
     * @return The Feature.
     */
    public F getLabel() {
        return labelFeature;
    }

    /**
     * Returns the values of the label column.
     * @return The vales.
     */
    public V getValues() {
        return labelValues;
    }

    /**
     * Returns the length of the column.
     * @return The length.
     */
    public abstract int getLength();

    /**
     * Implementation of a LabelColumn holding a categorical feature.
     */
    public static class CategoricalLabelColumn extends LabelColumn<CategoricalFeature, int[]> {

        /**
         * Constructor for the CategoricalLabelColumn.
         * @param labelFeature The feature.
         * @param labelValues The values of the column.
         */
        public CategoricalLabelColumn(CategoricalFeature labelFeature,
                                      int[] labelValues) {
            super(labelFeature, labelValues);
        }

        @Override
        public int getLength() {
            return labelValues.length;
        }
    }

    /**
     * Implementation of a LabelColumn holding a numeric feature.
     */
    public static class NumericLabelColumn extends LabelColumn<NumericFeature, double[]> {

        /**
         * Constructor for the NumericLabelColumn.
         * @param labelFeature The feature.
         * @param labelValues The values of the column.
         */
        public NumericLabelColumn(NumericFeature labelFeature,
                                  double[] labelValues) {
            super(labelFeature, labelValues);
        }

        @Override
        public int getLength() {
            return labelValues.length;
        }
    }

}
