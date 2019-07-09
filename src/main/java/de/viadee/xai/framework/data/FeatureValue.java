package de.viadee.xai.framework.data;

import de.viadee.xai.framework.data.Feature.CategoricalFeature;
import de.viadee.xai.framework.data.Feature.DiscretizedNumericFeature;
import de.viadee.xai.framework.data.Feature.NumericFeature;
import de.viadee.xai.framework.data.tabular_data.TabularDataset;
import de.viadee.xai.framework.data.tabular_data.TabularRow;

import java.io.Serializable;

/**
 * Abstract superclass for all FeatureValues. A FeatureValue represents one cell of a given dataset and is used to
 * form a row of the dataset. It should not be used to iterate over a TabularDataset, as this dataset is column-oriented.
 * {@link TabularDataset}
 * {@link TabularRow}
 */
public abstract class FeatureValue implements Serializable {

    private FeatureValue() {}

    /**
     * Returns the feature of the cell.
     * @return The Feature of the cell.
     */
    public abstract Feature getFeature();

    /**
     * Class representing cells containing a numeric value.
     */
    public static class NumericFeatureValue extends FeatureValue {
        private final NumericFeature feature;
        private final double value;

        /**
         * Constructor for a NumericFeatureValue instance.
         * @param numericFeature The numeric feature of the cell.
         * @param value The double-value of the cell.
         * {@link NumericFeature}
         */
        public NumericFeatureValue(final NumericFeature numericFeature,
                                   final double value) {
            super();
            this.value = value;
            this.feature = numericFeature;
        }

        /**
         * Returns the value of the cell.
         * @return The double value of the cell.
         */
        public double getValue() {
            return value;
        }

        @Override
        public NumericFeature getFeature() {
            return feature;
        }

        @Override
        public String toString() {
            return "NumericFeatureValue{" +
                    "feature=" + feature +
                    ", value=" + value +
                    "}";
        }
    }

    /**
     * Class representing cells containing a categorical value.
     */
    public static class CategoricalFeatureValue extends FeatureValue {
        protected final CategoricalFeature feature;
        protected final int value;

        /**
         * Constructor for CategoricalFeatureValueInstances.
         * @param categoricalFeature The categorical feature of the cell.
         * @param value The Integer-working representation of the CategoricalFeature.
         * {@link CategoricalFeature}
         * {@link DiscretizedNumericFeature}
         */
        public CategoricalFeatureValue(final CategoricalFeature categoricalFeature,
                                       final int value) {
            super();
            this.feature = categoricalFeature;
            this.value = value;
        }

        /**
         * Returns the int-working-representation of the cell.
         * @return The Integer-working representation of the cell's categorical value.
         */
        public int getValue() {
            return value;
        }

        @Override
        public CategoricalFeature getFeature() {
            return feature;
        }

        @Override
        public String toString() {
            return "CategoricalFeatureValue{" +
                    "\nfeature=" + feature.toString() +
                    ",\nvalue=" + value +
                    "}";
        }
    }
}
