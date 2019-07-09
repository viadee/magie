package de.viadee.xai.framework.data.preprocessor;


import de.viadee.xai.framework.data.Feature.DiscretizedNumericFeature;
import de.viadee.xai.framework.data.Feature.NumericFeature;
import de.viadee.xai.framework.data.tabular_data.LabelColumn;
import de.viadee.xai.framework.utility.Tuple;
import org.apache.commons.math3.stat.descriptive.rank.Percentile;

import java.util.HashMap;
import java.util.Map;

/**
 * Preprocessor which discretizes the given data set.
 */
public class PercentileDiscretizer<OL extends LabelColumn>
        implements IndependentColumnDiscretizier<OL> {

    protected double[] splitPoints;
    protected int[] percentiles;
    protected Map<NumericFeature, DiscretizedNumericFeature> numericToDiscretization;

    /**
     * Constructor for the PercentileDiscretizer. Sets default values and discretizes according to the
     * 20-, 40-, 60-, and 80-percentiles.
     */
    public PercentileDiscretizer() {
        int[] percentiles = {20, 40, 60, 80};
        this.percentiles = percentiles;
        numericToDiscretization = new HashMap<>();
    }

    /**
     * Enables the specification of the desired percentiles.
     * @param percentiles The percentiles which should be used to split the data.
     */
    public PercentileDiscretizer(int... percentiles) {
        // Make sure percentiles are given in sorted order
        int previous = 0;
        for (int p : percentiles) {
            if (previous > p) {
                throw new IllegalArgumentException("The percentiles must be given in sorted order.");
            }
            previous = p;
        }
        numericToDiscretization = new HashMap<>();
        this.percentiles = percentiles;
    }


    @Override
    public Tuple<DiscretizedNumericFeature, int[]> preprocess(NumericFeature numericFeature, double[] values) {
        if (numericToDiscretization.get(numericFeature) == null) {
            Percentile percentile = new Percentile();
            splitPoints = new double[percentiles.length];
            for (int i = 0; i < percentiles.length; i++) {
                splitPoints[i] = percentile.evaluate(values, percentiles[i]);
            }

            DiscretizedNumericFeature discretizedNumericFeature = new DiscretizedNumericFeature(
                    numericFeature,
                    values,
                    splitPoints
            );

            numericToDiscretization.put(numericFeature, discretizedNumericFeature);
            return new Tuple<>(discretizedNumericFeature, discretizedNumericFeature.getDiscretizationOfOriginal());
        } else {
            DiscretizedNumericFeature discretizedNumericFeature = numericToDiscretization.get(numericFeature);
            return new Tuple<>(discretizedNumericFeature, discretizedNumericFeature.getDiscretization(values));
        }
    }


    @Override
    public int apply(NumericFeature numericFeature, double value) {
        return numericToDiscretization.get(numericFeature).getDiscretization(value);
    }

}
