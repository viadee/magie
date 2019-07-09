package de.viadee.xai.framework.data.tabular_data;

import de.viadee.xai.anchor.adapter.tabular.AnchorTabular;
import de.viadee.xai.anchor.adapter.tabular.TabularInstance;
import de.viadee.xai.anchor.adapter.tabular.column.DoubleColumn;
import de.viadee.xai.anchor.adapter.tabular.column.GenericColumn;
import de.viadee.xai.anchor.adapter.tabular.column.IntegerColumn;
import de.viadee.xai.anchor.adapter.tabular.column.StringColumn;
import de.viadee.xai.framework.adapter.black_box_classifier_adapter.BlackBoxClassifierAdapter;
import de.viadee.xai.framework.data.Feature;
import de.viadee.xai.framework.data.Feature.CategoricalFeature;
import de.viadee.xai.framework.data.Feature.NumericFeature;
import de.viadee.xai.framework.data.FeatureValue;
import de.viadee.xai.framework.data.FeatureValue.CategoricalFeatureValue;
import de.viadee.xai.framework.data.FeatureValue.NumericFeatureValue;
import de.viadee.xai.framework.data.preprocessor.Discretizer;
import de.viadee.xai.framework.data.preprocessor.Preprocessor;
import de.viadee.xai.framework.exception.*;
import de.viadee.xai.framework.utility.Tuple;
import org.apache.commons.lang.NotImplementedException;

import java.io.Serializable;
import java.util.*;

/**
 * Implementation of a Dataset containing TabularRows. The TabularDataset is constructed in a column-wise fashion to
 * facilitate type-safety and efficiently conduct queries over the dataset. It includes shortcuts to access instances
 * with certain categorical feature values.
 * The data set is divided into two TabularDatasetPackages. The fist package is denoted as "original" data and
 * represents the data the black box model, if any uses. The second pacakge is denoted as "processed" data and
 * represents the data the explanation approaches work upon.
 * TabularDataset-instances can be created using their constructors or, as the suggested way: Using the
 * TabularDatasetBuilder {@link TabularDataset#newBuilderWithAnchorTabular(Class, Class, Class, AnchorTabular)}.
 * @param <OL> The type of label column used by the black box model adapter.
 * @param <PL> The type of label column used by the explanation procedures.
 * {@link TabularDatasetPackage}
 */
public class TabularDataset<
        OL extends LabelColumn,
        PL extends LabelColumn
        > {
    protected BlackBoxClassifierAdapter<?> blackBoxModel;

    // Data-subset used by the black-box model.
    protected TabularDatasetPackage<OL> originalData;
    // Data-subset used by the explaining algorithms.
    protected TabularDatasetPackage<PL> processedData;


    /**
     * Constructor of the TabularDataset. Used to construct a TabularDataset without a black box model adapter.
     * @param originalData The data prior to preparing it for the explanation/rule-mining procedures.
     * @param processedData The data used by the explanation algorithms.
     */
    public TabularDataset(TabularDatasetPackage<OL> originalData,
                          TabularDatasetPackage<PL> processedData) {
        this.originalData = originalData;
        this.processedData = processedData;
    }

    /**
     * Constructor for the TabularDataset.
     * @param originalData The data used by the black box model adapter.
     * @param processedData The data used by the explanation algorithms.
     * @param blackBoxModel The black box model adapter.
     */
    public TabularDataset(
            TabularDatasetPackage<OL> originalData,
            TabularDatasetPackage<PL> processedData,
            BlackBoxClassifierAdapter<?> blackBoxModel) {
        this.originalData = originalData;
        this.processedData = processedData;
        this.blackBoxModel = blackBoxModel;
    }

    // Returns the array storing the values for a feature.
    private Serializable getValuesForFeature(Feature feature,
                                             Map<? extends Feature, ? extends Serializable> featureValues) {
        Serializable result;
        if (!feature.equals(originalData.getLabelCol().getLabel())) {
            result = featureValues.get(feature);
            if (result == null) {
                throw new ColumnForFeatureNotFound(feature);
            } else {
                return result;
            }
        } else {
            return originalData.getLabelCol().getValues();
        }
    }

    /**
     * Returns an int[] or double[] depending on the type of feature for the data
     * which is not prepared for the explanation procedures.
     * (NumericFeature: double[], CategoricalFeature: int[]) storing the values of this feature.
     * @param feature The feature for which the values are to be retrieved.
     * @return The values.
     */
    public Serializable getOriginalCol(Feature feature) {
        if (feature instanceof CategoricalFeature) {
            return getOriginalCol((CategoricalFeature) feature);
        } else if (feature instanceof NumericFeature) {
            return getOriginalCol((NumericFeature) feature);
        } else {
            throw new ColumnForFeatureNotFound(feature);
        }
    }

    /**
     * Returns the values for a numeric feature for the data
     * which is not prepared for the explanation procedures.
     * @param numericFeature The feature.
     * @return The values.
     */
    public double[] getOriginalCol(NumericFeature numericFeature) {
        return (double[]) getValuesForFeature(numericFeature, originalData.getNumericData());
    }

    /**
     * Returns the values for a categorical feature for the data
     * which is not prepared for the explanation procedures.
     * @param categoricalFeature The feature.
     * @return The values.
     */
    public int[] getOriginalCol(CategoricalFeature categoricalFeature) {
        return (int[]) getValuesForFeature(categoricalFeature, originalData.getCategoricalData());
    }

    /**
     * Returns an int[] or double[] depending on the type of feature for the data
     * which IS prepared for the explanation procedures.
     * (NumericFeature: double[], CategoricalFeature: int[]) storing the values of this feature.
     * @param feature The feature for which the values are to be retrieved.
     * @return The values.
     */
    public Serializable getProcessedCol(Feature feature) {
        if (feature instanceof CategoricalFeature) {
            return getProcessedCol((CategoricalFeature) feature);
        } else if (feature instanceof NumericFeature) {
            return getProcessedCol((NumericFeature) feature);
        } else {
            throw new ColumnForFeatureNotFound(feature);
        }
    }

    /**
     * Returns the values for a numeric feature for the data
     * which IS prepared for the explanation procedures.
     * @param numericFeature The feature.
     * @return The values.
     */
    public double[] getProcessedCol(NumericFeature numericFeature) {
        return (double[]) getValuesForFeature(numericFeature, processedData.getNumericData());
    }

    /**
     * Returns the values for a categorical feature for the data
     * which IS prepared for the explanation procedures.
     * @param categoricalFeature The feature.
     * @return The values.
     */
    public int[] getProcessedCol(CategoricalFeature categoricalFeature) {
        return (int[]) getValuesForFeature(categoricalFeature, processedData.getCategoricalData());
    }

    /**
     * Returns the numeric feature for the data which is not prepared for the explanation approaches.
     * @return The corresponding set of numeric features.
     */
    public Set<NumericFeature> getOriginalNumFeatures() {
        return originalData.getNumericFeatures();
    }

    /**
     * Returns the categorical feature for the data which is not prepared for the explanation approaches.
     * @return The corresponding set of categorical features.
     */
    public Set<CategoricalFeature> getOriginalCatFeatures() {
        return originalData.getCatFeatures();
    }

    /**
     * Returns the numeric feature for the data which IS prepared for the explanation approaches.
     * @return The corresponding set of numeric features.
     */
    public Set<NumericFeature> getProcessedNumFeatures() {
        return processedData.getNumericFeatures();
    }

    /**
     * Returns the categorical feature for the data which IS prepared for the explanation approaches.
     * @return The corresponding set of categorical features.
     */
    public Set<CategoricalFeature> getProcessedCatFeatures() {
        return processedData.getCatFeatures();
    }

    /**
     * Returns all features for the data which is not prepared for the explanation approaches.
     * @return The corresponding set of features.
     */
    public Set<Feature> getOriginalFeatures() {
        Set<Feature> result =  new HashSet<>();
        result.addAll(originalData.getNumericFeatures());
        result.addAll(originalData.getCatFeatures());
        return result;
    }

    /**
     * Returns all features for the data which IS prepared for the explanation approaches.
     * @return The corresponding set of features.
     */
    public Set<Feature> getProcessedFeatures() {
        Set<Feature> result =  new HashSet<>();
        result.addAll(processedData.getCatFeatures());
        result.addAll(processedData.getNumericFeatures());
        return result;
    }

    /**
     * Returns the number of columns for the data set which is not prepared for the explanation approaches.
     * @return The corresponding number of columns.
     */
    public int getNumberOriginalCols() {
        return getNumberOriginalNumCols() + getNumberOriginalCatCols();
    }

    /**
     * Returns the number of numeric columns for the data set which is not prepared for the explanation approaches.
     * @return The corresponding number of columns.
     */
    public int getNumberOriginalNumCols() {
        return originalData.getNumericFeatures().size();
    }

    /**
     * Returns the number of categorical columns for the data set which is not prepared for the explanation approaches.
     * @return The corresponding number of columns.
     */
    public int getNumberOriginalCatCols() {
        return originalData.getCatFeatures().size();
    }

    /**
     * Returns the number of columns for the data set which IS prepared for the explanation approaches.
     * @return The corresponding number of columns.
     */
    public int getNumberProcessedCols() {
        return getNumberProcessedCatCols() + getNumberProcessedNumCols();
    }

    /**
     * Returns the number of numeric columns for the data set which IS prepared for the explanation approaches.
     * @return The corresponding number of columns.
     */
    public int getNumberProcessedNumCols() {
        return processedData.getNumericFeatures().size();
    }

    /**
     * Returns the number of categorical columns for the data set which IS prepared for the explanation approaches.
     * @return The corresponding number of columns.
     */
    public int getNumberProcessedCatCols() {
        return processedData.getCatFeatures().size();
    }

    /**
     * Returns boolean indicating whether label of data set which is not prepared for the explanation procedures
     * is numeric.
     * @return True, if the original data sub-set contains a numeric label.
     */
    public boolean originalLabelIsNumeric() {
        return (originalData.getLabelCol().getLabel() instanceof NumericFeature);
    }


    /**
     * Returns the label column of the data which IS prepared for the explanation approaches.
     * @return The corresponding label column.
     */
    public PL getProcessedLabelCol() {
        return processedData.getLabelCol();
    }

    /**
     * Returns the label column of the data which is not prepared for the explanation approaches.
     * @return The corresponding label column.
     */
    public OL getOriginalLabelCol() {
        return originalData.getLabelCol();
    }

    /**
     * Returns the CategoricalFeature with the name specified in the string.
     * @param find The specified name.
     * @return The CategoricalFeature of the data which IS prepared for the explanation procedures.
     */
    public CategoricalFeature getProcessedCatFeatureForName(String find) {
        return (CategoricalFeature) getFeatureForName(find, processedData.getCatFeatures());
    }

    /**
     * Returns the NumericFeature with the name specified in the string.
     * @param find The specified name.
     * @return The NumericFeature of the data which IS prepared for the explanation procedures.
     */
    public NumericFeature getProcessedNumFeatureForName(String find) {
        return (NumericFeature) getFeatureForName(find, processedData.getNumericFeatures());
    }

    /**
     * Returns the CategoricalFeature with the name specified in the string.
     * @param find The specified name.
     * @return The CategoricalFeature of the data which is not prepared for the explanation procedures.
     */
    public CategoricalFeature getOriginalCatFeatureForName(String find) {
        return (CategoricalFeature) getFeatureForName(find, originalData.getCatFeatures());
    }

    /**
     * Returns the NumericFeature with the name specified in the string.
     * @param find The specified name.
     * @return The NumericFeature of the data which is not prepared for the explanation procedures.
     */
    public NumericFeature getOriginalNumFeatureForName(String find) {
        return (NumericFeature) getFeatureForName(find, originalData.getNumericFeatures());
    }

    private Feature getFeatureForName(String find, Set<? extends Feature> searchIn) {
        for (Feature f : searchIn) {
            if (f.getName().equals(find)) {
                return f;
            }
        }
        throw new FeatureNotFound(find);
    }

    // Extracts a TabularRow from the TabularDataset given the row-identifier i.
    private static TabularRow getInstance(int i,
                                         TabularDatasetPackage<?> blackboxData,
                                         TabularDatasetPackage<?> explainerData) {
        NumericFeatureValue[] originalNumValues = getNumFeatureValuesFor(i, blackboxData.getNumericData());
        CategoricalFeatureValue[] originalCatValues = getCatFeatureValuesFor(i, blackboxData.getCategoricalData());
        NumericFeatureValue[] processedNumValues = getNumFeatureValuesFor(i, explainerData.getNumericData());
        CategoricalFeatureValue[] processedCatValues = getCatFeatureValuesFor(i, explainerData.getCategoricalData());
        FeatureValue originalLabelValue = getLabelFeatureValue(blackboxData.getLabelCol().getLabel(), blackboxData.getLabelCol().getValues(), i);
        FeatureValue processedLabelValue = getLabelFeatureValue(explainerData.getLabelCol().getLabel(), explainerData.getLabelCol().getValues(), i);

        return new TabularRow(
                originalNumValues,
                originalCatValues,
                processedNumValues,
                processedCatValues,
                originalLabelValue,
                processedLabelValue
        );
    }

    /**
     * Returns the TabularRow with the specified row-identifier.
     * @param i The row-identifier
     * @return The extracted TabularRow.
     */
    public TabularRow getInstance(int i) {
        return getInstance(i, originalData, processedData);
    }


    private static NumericFeatureValue[] getNumFeatureValuesFor(int i, Map<NumericFeature, double[]> values) {
        NumericFeatureValue[] result = new NumericFeatureValue[values.size()];
        int count = 0;
        for (Map.Entry<NumericFeature, double[]> entry : values.entrySet()) {
            result[count] = new NumericFeatureValue(entry.getKey(), entry.getValue()[i]);
            count++;
        }
        return result;
    }

    private static CategoricalFeatureValue[] getCatFeatureValuesFor(int i, Map<? extends CategoricalFeature, int[]> values) {
        CategoricalFeatureValue[] result = new CategoricalFeatureValue[values.size()];
        int count = 0;
        for (Map.Entry<? extends CategoricalFeature, int[]> entry : values.entrySet()) {
            result[count] = new CategoricalFeatureValue(entry.getKey(), entry.getValue()[i]);
            count++;
        }
        return result;
    }

    private static FeatureValue getLabelFeatureValue(Feature labelFeature, Serializable values, int i) {
        if (labelFeature instanceof NumericFeature) {
            return new NumericFeatureValue((NumericFeature) labelFeature, ((double[]) values)[i]);
        } else {
            return new CategoricalFeatureValue((CategoricalFeature) labelFeature, ((int[]) values)[i]);
        }
    }

    /**
     * Returns the number of rows. Is based on the length of the label column of the data prepared for the
     * explanation procedure.
     * @return The number of rows of the TabularDataset.
     */
    public int getNumberRows() {
        return processedData.getLabelCol().getLength();
    }

    /**
     * Returns the utilized black box model adapter, if any.
     * @return Null, or the black box model adapter.
     */
    public BlackBoxClassifierAdapter<?> getBlackBoxModel() {
        return blackBoxModel;
    }

    /**
     * Create a new builder executing the preprocessing pipeline on the TabularDataset.
     * @param labelFeatureTypeOfLoadedData The type of label column upon which the original data set is based.
     * @param labelFeatureTypeOfBlackBoxFunction The type of label column the black box model uses.
     * @param labelFeatureTypeOfExplainerData The type of label column used by explanation approaches.
     * @param anchorTabular The AnchorTabular-instance representing the initially loaded data.
     * @param <LL> The type of label column for the initially loaded data.
     * @param <OL> The type of label column for the data used by the black box model.
     * @param <PL> The type of label column for the data used by the explanation procedures.
     * @return The corresponding Builder.
     */
    public static <LL extends LabelColumn, OL extends LabelColumn, PL extends LabelColumn>
    TabularDatasetBuilder<LL, OL, PL> newBuilderWithAnchorTabular(
            Class<LL> labelFeatureTypeOfLoadedData,
            Class<OL> labelFeatureTypeOfBlackBoxFunction,
            Class<PL> labelFeatureTypeOfExplainerData,
            AnchorTabular anchorTabular) {

        return new TabularDatasetBuilder(
                labelFeatureTypeOfLoadedData,
                labelFeatureTypeOfBlackBoxFunction,
                labelFeatureTypeOfExplainerData,
                anchorTabular
        );
    }

    /**
     * The Builder-class for the TabularDataset.
     * @param <LL> The type of label column for the initially loaded data.
     * @param <OL> The type of label column for the data used by the black box model.
     * @param <PL> The type of label column for the data used by the explanation procedures.
     */
    @SuppressWarnings({"unchecked", "Duplicates"})
    public static class TabularDatasetBuilder<
            LL extends LabelColumn,
            OL extends LabelColumn,
            PL extends LabelColumn
            > {

        private final Class<LL> labelFeatureTypeOfLoadedData;
        private final Class<OL> labelFeatureTypeOfBlackBoxFunction;
        private final Class<PL> labelFeatureTypeOfExplainerData;

        private TabularDatasetPackage<LL> initialLoad;
        private TabularDatasetPackage<OL> trainingBlackboxData;
        private TabularDatasetPackage<PL> trainingExplainerData;

        private boolean trainingTestSplit;
        private double testsetPercentage;
        private int splitSeed;

        private TabularDatasetPackage<LL> rawTestData;
        private TabularDatasetPackage<OL> testBlackboxData;
        private TabularDatasetPackage<PL> testExplainerData;

        private BlackBoxClassifierAdapter<?> blackBoxModel;

        private final List<Preprocessor<LL, LL>> preDiscretizationBlackboxData;
        private Discretizer<LL, LabelColumn.CategoricalLabelColumn> discretizerForBlackboxData;
        private final List<Preprocessor<LabelColumn.CategoricalLabelColumn, LabelColumn.CategoricalLabelColumn>> postDiscretizationBlackboxData;

        private final List<Preprocessor<OL, OL>> preDiscretizationExplainerData;
        private Discretizer<OL, LabelColumn.CategoricalLabelColumn> discretizerForExplainerData;
        private final List<Preprocessor<LabelColumn.CategoricalLabelColumn, LabelColumn.CategoricalLabelColumn>> postDiscretizationExplainerData;

        private TabularDatasetBuilder(Class<LL> labelFeatureTypeOfLoadedData,
                                      Class<OL> labelFeatureTypeOfBlackBoxFunction,
                                      Class<PL> labelFeatureTypeOfExplainerData,
                                      AnchorTabular anchorTabular) {
            this.labelFeatureTypeOfLoadedData = labelFeatureTypeOfLoadedData;
            this.labelFeatureTypeOfBlackBoxFunction = labelFeatureTypeOfBlackBoxFunction;
            this.labelFeatureTypeOfExplainerData = labelFeatureTypeOfExplainerData;

            preDiscretizationBlackboxData = new LinkedList<>();
            postDiscretizationBlackboxData = new LinkedList<>();

            preDiscretizationExplainerData = new LinkedList<>();
            postDiscretizationExplainerData = new LinkedList<>();

            TabularInstance[] tabularInstances = anchorTabular.getTabularInstances();
            List<GenericColumn> genericColumns = anchorTabular.getColumns();
            GenericColumn labelColumn = anchorTabular.getTargetColumn();
            Tuple<Map<CategoricalFeature, int[]>, Map<NumericFeature, double[]>> originalData =
                    translateColumnsToFeatures(tabularInstances, genericColumns);
            LL loadedLabelColumn = translateLabelColumnToFeature(tabularInstances, labelColumn);

            this.initialLoad = new TabularDatasetPackage<>(
                    originalData.getFirstElement(),
                    originalData.getSecondElement(),
                    loadedLabelColumn
            );
        }

        /**
         * Adds a preprocessor which is executed before the black box-data is discretized.
         * @param preDiscretizationForBlackboxData The preprocessor.
         * @return The Builder for chaining.
         */
        public TabularDatasetBuilder<LL, OL, PL>
        withPreDiscretizationForBlackboxData(Preprocessor<LL, LL> preDiscretizationForBlackboxData) {
            this.preDiscretizationBlackboxData.add(preDiscretizationForBlackboxData);
            return this;
        }

        /**
         * Adds a discretizer for discretizing the data used by the black box model.
         * @param discretizerForBlackboxData The discretizer.
         * @return The Builder for chaining.
         */
        public TabularDatasetBuilder<
                LL,
                LabelColumn.CategoricalLabelColumn,
                PL> withBlackboxDataDiscretizer(Discretizer<LabelColumn.NumericLabelColumn, LabelColumn.CategoricalLabelColumn> discretizerForBlackboxData) {

            if (!((labelFeatureTypeOfLoadedData.equals(NumericFeature.class)) &&
                    (labelFeatureTypeOfBlackBoxFunction.equals(CategoricalFeature.class)))) {
                throw new LabelTypeDiscretizationNotLegal();
            } else {
                this.discretizerForBlackboxData = (Discretizer<LL, LabelColumn.CategoricalLabelColumn>) discretizerForBlackboxData;
                return (TabularDatasetBuilder<LL, LabelColumn.CategoricalLabelColumn, PL>) this;
            }
        }

        /**
         * Adds preprocessors which are executed after discretization has been applied to the data
         * used by the black box model. Only is executed if discretization is applied.
         * @param postDiscretizationForBlackboxData The preprocessor.
         * @return The Builder for chaining.
         */
        public TabularDatasetBuilder<LL, OL, PL>
        withPostDiscretizationForBlackboxData(Preprocessor<LabelColumn.CategoricalLabelColumn, LabelColumn.CategoricalLabelColumn>
                                                      postDiscretizationForBlackboxData) {
            if (discretizerForBlackboxData == null) {
                throw new PostDiscretizationNotLegal();
            }
            this.postDiscretizationBlackboxData.add(postDiscretizationForBlackboxData);
            return this;
        }

        /**
         * Adds preprocessors which are executed on the data used by the explainer
         * prior to the discretization procedure.
         * @param preDiscretizationForExplainerData The corresponding preprocessor.
         * @return The Builder for chaining.
         */
        public TabularDatasetBuilder<LL, OL, PL>
        withPreDiscretizationForExplainerData(Preprocessor<OL, OL> preDiscretizationForExplainerData) {
            this.preDiscretizationExplainerData.add(preDiscretizationForExplainerData);
            return this;
        }

        /**
         * Configures the Builder so that the data is split into a training- and a test data set.
         * @param trainingTestSplit Should a split be conducted?
         * @param testsetPercentage What percentage of the overall data set should be used by the test data?
         * @param splitSeed The seed for the sampling process.
         * @return The Builder for chaining.
         */
        public TabularDatasetBuilder<LL, OL, PL> withTrainingTestSplit(boolean trainingTestSplit,
                                                                       double testsetPercentage,
                                                                       int splitSeed) {
            if (0 >= testsetPercentage || testsetPercentage >= 1) {
                throw new BuildProcessNotLegal("The test set percentage must be in between ]0;1[.");
            }
            this.trainingTestSplit = trainingTestSplit;
            this.testsetPercentage = testsetPercentage;
            this.splitSeed = splitSeed;
            return this;
        }

        /**
         * Adds a discretizer for the data which is used by the explanation procedures.
         * @param discretizerForExplainerData The chosen discretizer for the explainer data.
         * @return The builder for chaining.
         */
        public TabularDatasetBuilder<
                LL,
                OL,
                LabelColumn.CategoricalLabelColumn>
        withExplainerDataDiscretizer(Discretizer<LabelColumn.NumericLabelColumn, LabelColumn.CategoricalLabelColumn>
                                             discretizerForExplainerData) {

            if (!(labelFeatureTypeOfExplainerData.equals(LabelColumn.CategoricalLabelColumn.class))) {
                throw new LabelTypeDiscretizationNotLegal();
            } else {
                this.discretizerForExplainerData = (Discretizer<OL, LabelColumn.CategoricalLabelColumn>) discretizerForExplainerData;
                return (TabularDatasetBuilder<LL, OL, LabelColumn.CategoricalLabelColumn>) this;
            }
        }

        /**
         * Sets a black box model adapter which will be trained on the data for the black box model and which
         * is used to label the data for the explanation procedure.
         * @param blackBoxModel The black box model adapter.
         * @return The Builder for chaining.
         */
        public TabularDatasetBuilder<LL, OL, PL> withBlackBoxModel(BlackBoxClassifierAdapter<?> blackBoxModel) {
            this.blackBoxModel = blackBoxModel;
            return this;
        }

        /**
         * Adds a preprocessor after discretization has been conducted on the data used for the explanation
         * approaches.
         * @param postDiscretizationForExplainerData The preprocessor.
         * @return The Builder for chaining.
         */
        public TabularDatasetBuilder<LL, OL, PL>
        withPostDiscretizationForExplainerData(Preprocessor<LabelColumn.CategoricalLabelColumn, LabelColumn.CategoricalLabelColumn>
                                                       postDiscretizationForExplainerData) {
            if (discretizerForExplainerData == null) {
                throw new PostDiscretizationNotLegal();
            }
            this.postDiscretizationExplainerData.add(postDiscretizationForExplainerData);
            return this;
        }

        /**
         * The TabularDataset is generated.
         * In a first step, the AnchorTabular is translated into a TabularDatasetPackage.
         * In a second step, this initial TabularDatasetPackage is preprocessed according to the
         * specified preprocessors for the data used for the black box model. The black box model, if specified
         * thereafter will be trained on this data.
         * In a third step, the further preprocessing steps to achieve the data used for the explanation approaches
         * is generated from the previous black box-data package. Prior to this, the data used by
         * the explanation approaches is labelled by the trained black box model.
         * @return The Builder for chaining.
         */
        public TabularDataset<OL, PL> buildTrainingData() {
            if (initialLoad == null) {
                throw new BuildProcessNotLegal("No initial data was loaded.");
            }

            if (trainingTestSplit) {
                Tuple<TabularDatasetPackage<LL>, TabularDatasetPackage<LL>> split =
                        trainingTestSplit(initialLoad, testsetPercentage, splitSeed);
                initialLoad = split.getFirstElement();
                rawTestData = split.getSecondElement();
            }

            TabularDatasetPackage<LL> translateToBlackboxData = new TabularDatasetPackage<>(initialLoad);
            for (Preprocessor<LL, LL> preDiscretizationForBlackboxData : preDiscretizationBlackboxData) {
                translateToBlackboxData = preDiscretizationForBlackboxData.preprocess(translateToBlackboxData); ////
            }
            if (discretizerForBlackboxData != null) {
                trainingBlackboxData = (TabularDatasetPackage<OL>)
                        discretizerForBlackboxData.preprocess(translateToBlackboxData);
                for (Preprocessor<LabelColumn.CategoricalLabelColumn, LabelColumn.CategoricalLabelColumn>
                        postDiscretizationForBlackboxData :
                        postDiscretizationBlackboxData) {
                    trainingBlackboxData =
                            (TabularDatasetPackage<OL>)
                                    postDiscretizationForBlackboxData.preprocess((TabularDatasetPackage<LabelColumn.CategoricalLabelColumn>)
                                            trainingBlackboxData);
                }
            } else {
                trainingBlackboxData = (TabularDatasetPackage<OL>) translateToBlackboxData;
            }

            TabularDatasetPackage<OL> translateToExplainerData = new TabularDatasetPackage<>(trainingBlackboxData);
            for (Preprocessor<OL, OL> preDiscretizationForExplainerData : preDiscretizationExplainerData) {
                translateToExplainerData = preDiscretizationForExplainerData.preprocess(translateToExplainerData);
            }


            if (discretizerForExplainerData != null) {
                translateToExplainerData = (TabularDatasetPackage<OL>)
                        discretizerForExplainerData.preprocess(translateToExplainerData);
                for (Preprocessor<LabelColumn.CategoricalLabelColumn, LabelColumn.CategoricalLabelColumn> postDiscretizationForExplainerData :
                        postDiscretizationExplainerData) {
                    translateToExplainerData =
                            (TabularDatasetPackage<OL>)
                                    postDiscretizationForExplainerData.preprocess((TabularDatasetPackage<LabelColumn.CategoricalLabelColumn>)
                                            translateToExplainerData);
                }
            }

            TabularDataset<OL, OL> preResult = new TabularDataset<>(trainingBlackboxData, translateToExplainerData);
            if (blackBoxModel != null) {
                Serializable labelCol = translateToExplainerData.getLabelCol().getValues();
                if (labelCol instanceof double[]) {
                    throw new NotImplementedException("Support for regression explanations is not yet implemented."); // TODO
                } else {
                    int[] predictedValues = new int[preResult.getNumberRows()];
                    blackBoxModel.train((TabularDataset<LabelColumn.CategoricalLabelColumn, ?>) preResult);
                    for (int i = 0; i < predictedValues.length; i++) {
                        predictedValues[i] = blackBoxModel.predict(TabularDataset.getInstance(i, trainingBlackboxData, translateToExplainerData));
                    }
                    LabelColumn.CategoricalLabelColumn newLabel = new LabelColumn.CategoricalLabelColumn(
                            new CategoricalFeature((CategoricalFeature) translateToExplainerData.getLabelCol().getLabel(),
                                    predictedValues),
                            predictedValues
                    );
                    trainingExplainerData = new TabularDatasetPackage<>(
                            translateToExplainerData.getCategoricalData(),
                            translateToExplainerData.getNumericData(),
                            (PL) newLabel
                    );
                }
                return new TabularDataset<>(trainingBlackboxData, trainingExplainerData, blackBoxModel);
            } else {
                return new TabularDataset<>(trainingBlackboxData, (TabularDatasetPackage<PL>) translateToExplainerData, null);
            }
        }

        /**
         * The TabularDataset representing the test data is generated.
         * The steps are analogous to the generation of the training data {@link TabularDatasetBuilder#buildTrainingData()}.
         * The difference is that preprocessing methods are used according to the patterns learned from the training data
         * and the training of the black box model is not conducted.
         * @return The Builder for chaining.
         */
        public TabularDataset<OL, PL> buildTestData() {
            if (rawTestData == null) {
                throw new BuildProcessNotLegal("The test data is to be preprocessed, yet no test data was found");
            }
            TabularDatasetPackage<LL> translateToBlackboxData = new TabularDatasetPackage<>(rawTestData);
            for (Preprocessor<LL, LL> preDiscretizationForBlackboxData : preDiscretizationBlackboxData) {
                translateToBlackboxData = preDiscretizationForBlackboxData.preprocessTestData(translateToBlackboxData);
            }

            if (discretizerForBlackboxData != null) {
                testBlackboxData = (TabularDatasetPackage<OL>)
                        discretizerForBlackboxData.preprocess(translateToBlackboxData);
                for (Preprocessor<LabelColumn.CategoricalLabelColumn, LabelColumn.CategoricalLabelColumn>
                        postDiscretizationForBlackboxData :
                        postDiscretizationBlackboxData) {
                    testBlackboxData =
                            (TabularDatasetPackage<OL>)
                                    postDiscretizationForBlackboxData.preprocessTestData((TabularDatasetPackage<LabelColumn.CategoricalLabelColumn>)
                                            testBlackboxData);
                }
            } else {
                testBlackboxData = (TabularDatasetPackage<OL>) translateToBlackboxData;
            }

            TabularDatasetPackage<OL> translateToExplainerData = new TabularDatasetPackage<>(testBlackboxData);
            for (Preprocessor<OL, OL> preDiscretizationForExplainerData : preDiscretizationExplainerData) {
                translateToExplainerData = preDiscretizationForExplainerData.preprocessTestData(translateToExplainerData);
            }

            if (discretizerForExplainerData != null) {
                translateToExplainerData = (TabularDatasetPackage<OL>)
                        discretizerForExplainerData.preprocessTestData(translateToExplainerData);
                for (Preprocessor<LabelColumn.CategoricalLabelColumn, LabelColumn.CategoricalLabelColumn> postDiscretizationForExplainerData :
                        postDiscretizationExplainerData) {
                    translateToExplainerData =
                            (TabularDatasetPackage<OL>)
                                    postDiscretizationForExplainerData.preprocessTestData((TabularDatasetPackage<LabelColumn.CategoricalLabelColumn>)
                                            translateToExplainerData);
                }
            }


            TabularDataset<OL, OL> preResult = new TabularDataset<>(testBlackboxData, translateToExplainerData);
            if (blackBoxModel != null) {
                Serializable labelCol = translateToExplainerData.getLabelCol().getValues();
                if (labelCol instanceof double[]) {
                    throw new NotImplementedException("Support for regression explanations is not yet implemented."); // TODO
                } else {
                    int[] predictedValues = new int[preResult.getNumberRows()];
                    for (int i = 0; i < predictedValues.length; i++) {
                        predictedValues[i] = blackBoxModel.predict(TabularDataset.getInstance(i, testBlackboxData, translateToExplainerData));
                    }
                    LabelColumn.CategoricalLabelColumn newLabel = new LabelColumn.CategoricalLabelColumn(
                            new CategoricalFeature((CategoricalFeature) translateToExplainerData.getLabelCol().getLabel(),
                                    predictedValues),
                            predictedValues
                    );
                    testExplainerData = new TabularDatasetPackage<>(
                            translateToExplainerData.getCategoricalData(),
                            translateToExplainerData.getNumericData(),
                            (PL) newLabel
                    );
                }
                return new TabularDataset<>(testBlackboxData, testExplainerData, blackBoxModel);
            } else {
                return new TabularDataset<>(testBlackboxData, (TabularDatasetPackage<PL>) translateToExplainerData, null);
            }

        }


        private Tuple<TabularDatasetPackage<LL>, TabularDatasetPackage<LL>>
        trainingTestSplit(TabularDatasetPackage<LL> toSplit, double testSetPercentage, int seed) {
            int size = toSplit.labelColumn.getLength();
            int testSize = (int) (testSetPercentage * size);
            if (testSize < 1) {
                throw new BuildProcessNotLegal("The size of the test set is smaller than 1");
            } else if (size - testSize < 1) {
                throw new BuildProcessNotLegal("The size of the training set is smaller than 1");
            }
            ArrayList<Integer> overallIndices = new ArrayList<>();

            for (Integer i = 0; i < size; i++) {
                overallIndices.add(i);
            }
            Collections.shuffle(overallIndices, new Random(seed));
            List<Integer> testIndicies = new ArrayList<>();

            for (int i = 0; i < testSize; i++) {
                testIndicies.add(overallIndices.get(overallIndices.size()-1));
                overallIndices.remove(overallIndices.size()-1);
            }

            TabularDatasetPackage<LL> trainingRawData =
                    generateTabularDatasetPackageFromIndices(toSplit, overallIndices);
            TabularDatasetPackage<LL> testRawData =
                    generateTabularDatasetPackageFromIndices(toSplit, testIndicies);


            return new Tuple<>(
                    new TabularDatasetPackage<>(trainingRawData),
                    new TabularDatasetPackage<>(testRawData)
            );
        }

        private TabularDatasetPackage<LL>
        generateTabularDatasetPackageFromIndices(TabularDatasetPackage<LL> getFrom, List<Integer> indices) {
            Map<CategoricalFeature, int[]> subsetCategoricalFeatures = new HashMap<>();
            Map<NumericFeature, double[]> subsetNumericFeatures = new HashMap<>();
            LL labelCol = getFrom.getLabelCol();
            LL subsetLabelColumn;
            if (getFrom.getLabelCol() instanceof LabelColumn.NumericLabelColumn) {
                double[] labelValues = (double[]) labelCol.getValues();
                double[] subsetLabelValues = new double[indices.size()];
                for (int i = 0; i < indices.size(); i++) {
                    subsetLabelValues[i] = labelValues[indices.get(i)];
                }
                subsetLabelColumn = (LL) new LabelColumn.NumericLabelColumn((NumericFeature) labelCol.getLabel(), subsetLabelValues);
            } else {
                int[] labelValues = (int[]) labelCol.getValues();
                int[] subsetLabelValues = new int[indices.size()];
                for (int i = 0; i < indices.size(); i++) {
                    subsetLabelValues[i] = labelValues[indices.get(i)];
                }
                subsetLabelColumn = (LL) new LabelColumn.CategoricalLabelColumn((CategoricalFeature) labelCol.getLabel(), subsetLabelValues);
            }

            Set<CategoricalFeature> categoricalFeatures = getFrom.getCatFeatures();
            Set<NumericFeature> numericFeatures = getFrom.getNumericFeatures();

            for (CategoricalFeature categoricalFeature : categoricalFeatures) { // TODO Performance
                int[] colValues = getFrom.getCatCol(categoricalFeature);
                subsetCategoricalFeatures.put(categoricalFeature, new int[indices.size()]);
                int[] values = subsetCategoricalFeatures.get(categoricalFeature);
                for (int i = 0; i < indices.size(); i++) {
                    values[i] = colValues[indices.get(i)];
                }
            }
            for (NumericFeature numericFeature : numericFeatures) {
                double[] colValues = getFrom.getNumericCol(numericFeature);
                subsetNumericFeatures.put(numericFeature, new double[indices.size()]);
                double[] values = subsetNumericFeatures.get(numericFeature);
                for (int i = 0; i < indices.size(); i++) {
                    values[i] = colValues[indices.get(i)];
                }
            }

            return new TabularDatasetPackage<>(subsetCategoricalFeatures, subsetNumericFeatures, subsetLabelColumn);
        }



        //#############################LOAD AND TRANSFORM FROM ANCHORTABULAR#############################//



        protected LL translateLabelColumnToFeature(TabularInstance[] tabularInstances, GenericColumn labelColumn) {
            Tuple<? extends Feature, ? extends Serializable> labelCombi = translateColumnToFeature(tabularInstances, labelColumn, true);
            if (labelFeatureTypeOfLoadedData.equals(LabelColumn.CategoricalLabelColumn.class) &&
                    !labelCombi.getFirstElement().getClass().equals(CategoricalFeature.class)) {
                throw new BuildProcessNotLegal("The initialization procedure expected a categorical label for the data to be loaded, " +
                        "yet, no categorical label was loaded");
            } else if (labelFeatureTypeOfLoadedData.equals(LabelColumn.NumericLabelColumn.class) &&
                    !labelCombi.getFirstElement().getClass().equals(NumericFeature.class)) {
                throw new BuildProcessNotLegal("The initialization procedure expected a numeric label for the data to be loaded, " +
                        "yet, no numeric label was loaded");
            }
            if (labelFeatureTypeOfLoadedData.equals(LabelColumn.CategoricalLabelColumn.class)) {
                return (LL) new LabelColumn.CategoricalLabelColumn(
                        (CategoricalFeature) labelCombi.getFirstElement(), (int[]) labelCombi.getSecondElement()
                );
            } else if (labelFeatureTypeOfLoadedData.equals(LabelColumn.NumericLabelColumn.class)) {
                return (LL) new LabelColumn.NumericLabelColumn(
                        (NumericFeature) labelCombi.getFirstElement(), (double[]) labelCombi.getSecondElement()
                );
            } else {
                throw new BuildProcessNotLegal("The loaded type of label is neither a NumericFeature, nor a CategoricalFeature");
            }
        }

        protected Tuple<Map<CategoricalFeature, int[]>, Map<NumericFeature, double[]>> translateColumnsToFeatures(TabularInstance[] tabularInstances, List<GenericColumn> genericColumns) {
            Map<CategoricalFeature, int[]> catResult = new HashMap<>();
            Map<NumericFeature, double[]> numericResult = new HashMap<>();
            for (GenericColumn genericColumn : genericColumns) {
                if (genericColumn instanceof DoubleColumn) {
                    Tuple<NumericFeature, double[]> current = translateDoubleColumn(tabularInstances, (DoubleColumn) genericColumn, false);
                    numericResult.put(current.getFirstElement(), current.getSecondElement());
                } else if (genericColumn instanceof StringColumn) {
                    Tuple<CategoricalFeature, int[]> current = translateCategoricalColumn(tabularInstances, genericColumn, false, false);
                    catResult.put(current.getFirstElement(), current.getSecondElement());
                } else if (genericColumn instanceof IntegerColumn) {
                    Tuple<CategoricalFeature, int[]> current = translateCategoricalColumn(tabularInstances, genericColumn, false, true);
                    catResult.put(current.getFirstElement(), current.getSecondElement());
                } else {
                    throw new ColumnTypeNotAccepted(genericColumn);
                }
            }
            return new Tuple<>(catResult, numericResult);
        }

        protected Tuple<? extends Feature, ? extends Serializable> translateColumnToFeature(TabularInstance[] tabularInstances, GenericColumn genericColumn, boolean isLabel) {
            if (genericColumn instanceof DoubleColumn) {
                return translateDoubleColumn(tabularInstances, (DoubleColumn) genericColumn, isLabel);
            } else if (genericColumn instanceof StringColumn) {
                return translateCategoricalColumn(tabularInstances, genericColumn, isLabel, false);
            } else if (genericColumn instanceof IntegerColumn) {
                return translateCategoricalColumn(tabularInstances, genericColumn, isLabel, true);
            } else {
                throw new ColumnTypeNotAccepted(genericColumn);
            }
        }

        protected Tuple<NumericFeature, double[]> translateDoubleColumn(TabularInstance[] tabularInstances, DoubleColumn doubleColumn, boolean isLabel) {
            double[] values = new double[tabularInstances.length];
            double minValue = Double.MAX_VALUE;
            double maxValue = Double.NEGATIVE_INFINITY;
            double currentValue;
            for (int i = 0; i < values.length; i++) {
                currentValue = (Double) getValueForColumn(tabularInstances[i], doubleColumn, isLabel);
                values[i] = currentValue;
                if (minValue > currentValue) {
                    minValue = currentValue;
                }
                if (maxValue < currentValue) {
                    maxValue = currentValue;
                }
            }
            NumericFeature nf = new NumericFeature(doubleColumn.getName(), minValue, maxValue);
            return new Tuple<>(nf, values);
        }

        protected Tuple<CategoricalFeature, int[]> translateCategoricalColumn(TabularInstance[] tabularInstances,
                                                                              GenericColumn categoricalColumn,
                                                                              boolean isLabel,
                                                                              boolean toString) {
            String[] currentValues = new String[tabularInstances.length];
            for (int i = 0; i < currentValues.length; i++) {
                if (toString) {
                    currentValues[i] = ((Integer) getValueForColumn(tabularInstances[i], categoricalColumn, isLabel)).toString();
                } else {
                    currentValues[i] = (String) getValueForColumn(tabularInstances[i], categoricalColumn, isLabel);
                }
            }
            CategoricalFeature cf = new CategoricalFeature(
                    categoricalColumn.getName(),
                    currentValues
            );
            return new Tuple<>(cf, CategoricalFeature.getIntegerizedValues(cf, currentValues));
        }

        protected Serializable getValueForColumn(TabularInstance tabularInstance, GenericColumn genericColumn, boolean isLabel) {
            Serializable result;
            if (!isLabel) {
                result = tabularInstance.getTransformedValue(genericColumn);
            } else {
                result = tabularInstance.getTransformedLabel();
            }
            return result;
        }
    }
}