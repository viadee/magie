package de.viadee.xai.framework.data.preprocessor;

import de.viadee.xai.framework.data.Feature.NumericFeature;
import de.viadee.xai.framework.data.tabular_data.LabelColumn;

import java.io.Serializable;
import java.util.Set;

/**
 * Basic interface for all discretizers.
 * @param <OL> The original label type of the passed data.
 * @param <PL> The label type of the discretized data set.
 */
public interface Discretizer<
        OL extends LabelColumn,
        PL extends LabelColumn>
        extends Serializable, Preprocessor<OL, PL> {

    default int[] apply(NumericFeature numericFeature, double[] values) {
        int[] result = new int[values.length];
        for (int i = 0; i < values.length; i++) {
            result[i] = apply(numericFeature, values[i]);
        }
        return result;
    }

    /**
     * Discretizes a concrete value for a given numeric feature.
     * @param numericFeature The numeric feature.
     * @param value The value of the numeric feature.
     * @return The integer representation of the interval representing the discretization.
     */
    int apply(NumericFeature numericFeature, double value);

    // Filter out NumericFeatures which should NOT be discretized.

    /**
     * Can be overridden to specify which numeric features should not be discretized.
     * @param numericFeatures The numeric features from which those, which should not be discretized can be removed.
     * @return The filtered set of numeric features.
     */ // TODO Should be configurable via lambda.
    default Set<NumericFeature> filterDiscretization(Set<NumericFeature> numericFeatures) {
        return numericFeatures; // Default is no filtering
    }
}
