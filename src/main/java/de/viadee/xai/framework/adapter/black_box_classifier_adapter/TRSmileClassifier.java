package de.viadee.xai.framework.adapter.black_box_classifier_adapter;

import de.viadee.xai.framework.data.Feature;
import de.viadee.xai.framework.data.Feature.CategoricalFeature;
import de.viadee.xai.framework.data.Feature.NumericFeature;
import de.viadee.xai.framework.data.tabular_data.LabelColumn;
import de.viadee.xai.framework.data.tabular_data.TabularDataset;
import de.viadee.xai.framework.data.tabular_data.TabularRow;
import de.viadee.xai.framework.utility.Tuple;
import smile.classification.RandomForest;
import smile.classification.SoftClassifier;
import smile.data.Attribute;
import smile.data.NominalAttribute;
import smile.data.NumericAttribute;

import java.text.ParseException;
import java.util.Set;

/**
 * Abstract superclass for a Smile random forest classifier using TabularRows as input.
 * @param <I> The type of data instance used by the local explainer.
 * {@link TabularRow}
 */
public abstract class TRSmileClassifier<I> implements BlackBoxClassifierAdapter<I> {
    protected SoftClassifier<double[]> classifier;
    // To get final the features in the same order
    protected Feature[] orderedFeatures;
    protected Attribute[] orderedAttributes;
    protected TabularDataset<LabelColumn.CategoricalLabelColumn, ?> dataset;
    protected int nrTrees;

    /**
     * Constructor for the TRSmileClassifier.
     * @param nrTrees The number of trees which are trained on the data set.
     */
    public TRSmileClassifier(int nrTrees) {
        this.nrTrees = nrTrees;
    }

    @Override
    public void train(TabularDataset<LabelColumn.CategoricalLabelColumn, ?> dataset) {
        Tuple<Attribute[], double[][]> attributesToData = transformAll(dataset);
        int[] y = dataset.getOriginalLabelCol().getValues();
        classifier = new RandomForest(
                attributesToData.getFirstElement(),
                attributesToData.getSecondElement(),
                y,
                nrTrees
        );
        //System.out.println("ERROR OF RF: ");
        //System.out.println(((RandomForest) classifier).error());
    }
    // Transforms the tabular dataset into a tuple of attributes and doubles[][]. This can then
    // be input into the random forest.
    protected Tuple<Attribute[], double[][]> transformAll(TabularDataset<LabelColumn.CategoricalLabelColumn, ?> dataset) {
        this.dataset = dataset;
        orderedFeatures = new Feature[dataset.getNumberOriginalCols()];
        double[][] values = new double[dataset.getNumberRows()][dataset.getNumberOriginalCols()];
        orderedAttributes = new Attribute[dataset.getNumberOriginalCols()];
        Set<CategoricalFeature> categoricalFeatures = dataset.getOriginalCatFeatures();
        Set<NumericFeature> numericFeatures = dataset.getOriginalNumFeatures();
        int featureCount = 0;
        // Create a mapping: each CategoricalFeature is placed in an array at the same position the corresponding
        // NominalAttribute is placed in.
        for (CategoricalFeature cf : categoricalFeatures) {
            orderedFeatures[featureCount] = cf;
            int[] categoricalValues = dataset.getOriginalCol(cf);
            NominalAttribute nominalAttribute = new NominalAttribute(cf.getName());
            nominalAttribute.setOpen(true);

            for (Integer distinctValue : cf.getUniqueNumberRepresentations()) {
                try {
                    nominalAttribute.valueOf(Integer.toString(distinctValue));
                } catch (ParseException pe) {
                    pe.printStackTrace();
                }
            }

            orderedAttributes[featureCount] = nominalAttribute;
            for (int i = 0; i < dataset.getNumberRows(); i++) {
                try {
                    values[i][featureCount] = nominalAttribute.valueOf(Integer.toString(categoricalValues[i]));
                } catch (ParseException pe) {
                    pe.printStackTrace();
                }
            }
            nominalAttribute.setOpen(false);
            featureCount++;
        }
        // Create a mapping: each NumericFeature is placed in an array at the same position the corresponding
        // NumericAttribute is placed in.
        for (NumericFeature nf : numericFeatures) {
            orderedFeatures[featureCount] = nf;
            double[] numericValues = dataset.getOriginalCol(nf);
            orderedAttributes[featureCount] = new NumericAttribute(nf.getName());
            for (int i = 0; i < dataset.getNumberRows(); i++) {
                values[i][featureCount] = numericValues[i];
            }
            featureCount++;
        }

        return new Tuple<>(orderedAttributes, values);
    }
}
