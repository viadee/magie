package de.viadee.xai.framework.data.preprocessor;

import de.viadee.xai.framework.data.Feature;
import de.viadee.xai.framework.data.Feature.CategoricalFeature;
import de.viadee.xai.framework.data.Feature.DiscretizedNumericFeature;
import de.viadee.xai.framework.data.Feature.NumericFeature;
import de.viadee.xai.framework.data.tabular_data.LabelColumn;
import de.viadee.xai.framework.data.tabular_data.TabularDatasetPackage;
import de.viadee.xai.framework.utility.Tuple;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Interface for all discretizers which do not regard interactions between different feature columns and
 * hence perform the discretization process on a per-column basis.
 */
public interface IndependentColumnDiscretizier<OL extends LabelColumn>
        extends Discretizer<OL, LabelColumn.CategoricalLabelColumn> {

    @Override
    default TabularDatasetPackage<LabelColumn.CategoricalLabelColumn> preprocess(TabularDatasetPackage<OL> toProcess) {
        double[] currentNumCol;
        Set<NumericFeature> toDiscretize = new HashSet<>(toProcess.getNumericFeatures());
        if (toProcess.getLabelCol().getLabel() instanceof NumericFeature) {
            toDiscretize.add((NumericFeature) toProcess.getLabelCol().getLabel());
        }

        toDiscretize = filterDiscretization(toDiscretize);

        Set<NumericFeature> notToDiscretize = new HashSet<>(toProcess.getNumericFeatures());
        notToDiscretize.removeAll(toDiscretize);

        Map<CategoricalFeature, int[]> discretizedCols = new HashMap<>();
        for (NumericFeature numericFeature : toDiscretize) {
            Tuple<? extends CategoricalFeature, int[]> featureDiscretization
                    = preprocess(numericFeature, toProcess.getNumericCol(numericFeature));
            discretizedCols.put(featureDiscretization.getFirstElement(),
                    featureDiscretization.getSecondElement());
        }
        LabelColumn labelCol = toProcess.getLabelCol();
        Feature labelFeature = labelCol.getLabel();
        Serializable labelValues = labelCol.getValues();
        CategoricalFeature discreteLabelFeature;
        int[] discreteLabelValues;
        if (labelFeature instanceof NumericFeature) {
            Tuple<DiscretizedNumericFeature, int[]> labelDiscretization
                    = preprocess((NumericFeature) labelFeature, (double[]) labelValues);
            discreteLabelFeature = labelDiscretization.getFirstElement();
            discreteLabelValues = labelDiscretization.getSecondElement();
        } else {
            discreteLabelFeature = (CategoricalFeature) labelFeature;
            discreteLabelValues = (int[]) labelValues;
        }

        Map<NumericFeature, double[]> notDiscretizedCols = new HashMap<>();
        for (NumericFeature numericFeature : notToDiscretize) {
            currentNumCol = toProcess.getNumericCol(numericFeature);
            notDiscretizedCols.put(numericFeature, currentNumCol);
        }

        for (CategoricalFeature categoricalFeature : toProcess.getCatFeatures()) {
            discretizedCols.put(categoricalFeature, toProcess.getCatCol(categoricalFeature));
        }

        TabularDatasetPackage<LabelColumn.CategoricalLabelColumn> result =
                new TabularDatasetPackage<>(
                        discretizedCols,
                        notDiscretizedCols,
                        new LabelColumn.CategoricalLabelColumn(discreteLabelFeature, discreteLabelValues));
        return result;
    }

    @Override
    default TabularDatasetPackage<LabelColumn.CategoricalLabelColumn> preprocessTestData(TabularDatasetPackage<OL> toProcess) {
        // Default is to treat the learned discretization in the preprocess(NumericFeature, double[])-method.
        return preprocess(toProcess);
    }

    /**
     * Performs the basic preprocessing-process and returns the discretizations alongside the new values.
     * @param numericFeature The original numeric feature.
     * @param values The values of the numeric feature.
     * @return The discretized feature and integer-representation of the intervals.
     */
    Tuple<DiscretizedNumericFeature, int[]> preprocess(NumericFeature numericFeature, double[] values);
}
