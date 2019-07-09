package de.viadee.xai.framework.explanation_calculation.explanation;

import de.viadee.xai.framework.data.Feature.CategoricalFeature;
import de.viadee.xai.framework.data.tabular_data.LabelColumn;
import de.viadee.xai.framework.data.tabular_data.TabularDataset;
import de.viadee.xai.framework.explanation_calculation.calculator.CategoricalCalculator;
import de.viadee.xai.framework.explanation_calculation.calculator.RoaringBitmapCalculator;
import org.roaringbitmap.buffer.ImmutableRoaringBitmap;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Standard implementation of a RuleExplanation. Caches the covers which are calculated on the dataset used for the
 * initial computation.
 */
public final class MinimalBitmapCoversRuleExplanation implements RuleExplanation {

    // The Map's keys consist of different Features which are AND-connected.
    // The values of the mapExplanations correspond to OR-connected categorical feature values of the same Feature.
    protected final Map<CategoricalFeature, Set<Integer>> conditions;
    protected final CategoricalFeature labelFeature;
    protected final int labelValue;


    protected final TabularDataset<?, LabelColumn.CategoricalLabelColumn> dataset;
    protected final RoaringBitmapCalculator calculator;
    protected final ImmutableRoaringBitmap coveredInstances;
    protected final ImmutableRoaringBitmap correctlyCoveredInstances;
    protected final ImmutableRoaringBitmap incorrectlyNotCoveredInstances;

    /**
     * Constructor for a MinimalBitmapCoversRuleExplanation instance.
     * @param conditions The conditions from which to generate the Rule.
     * @param labelFeature The label feature.
     * @param labelValue The label value.
     * @param calculator The calculator with which the MinimalBitmapCoversRuleExplanation's metrics and covers are calculated per default.
     */
    protected MinimalBitmapCoversRuleExplanation(final Map<CategoricalFeature, Set<Integer>> conditions,
                                                 final CategoricalFeature labelFeature,
                                                 final int labelValue,
                                                 final RoaringBitmapCalculator calculator) {
        this.conditions = new HashMap<>(conditions);
        this.labelFeature = labelFeature;
        this.labelValue = labelValue;
        this.dataset = calculator.getDataset();
        this.calculator = calculator;
        ImmutableRoaringBitmap[] covers = calculator.getMinimalNumberCoversAsBitmap(conditions, labelValue);
        coveredInstances = covers[0];
        correctlyCoveredInstances = covers[1];
        incorrectlyNotCoveredInstances = covers[2];
    }

    /**
     * Constructor for a MinimalBitmapCoversRuleExplanation instance.
     * Copies the given RuleExplanation and transforms it to a MinimalBitmapCoversRuleExplanation using the given dataset.
     * @param copyFrom The to-be-copied RuleExplanation.
     * @param calculator The used calculator.
     */
    public MinimalBitmapCoversRuleExplanation(final RuleExplanation copyFrom,
                                              final RoaringBitmapCalculator calculator) {
        this.dataset = calculator.getDataset();
        this.calculator = calculator;
        conditions = new HashMap<>(copyFrom.getConditions());
        labelFeature = copyFrom.getLabelFeature();
        labelValue = copyFrom.getLabelValue();
        if (dataset.equals(copyFrom.getDataset())) {
            coveredInstances = copyFrom.getCoverAsBitmap();
            correctlyCoveredInstances = copyFrom.getCorrectCoverAsBitmap();
            incorrectlyNotCoveredInstances = copyFrom.getIncorrectlyNotCoveredAsBitmap();
        } else {
            ImmutableRoaringBitmap[] covers = calculator.getMinimalNumberCoversAsBitmap(conditions, labelValue);
            coveredInstances = covers[0];
            correctlyCoveredInstances = covers[1];
            incorrectlyNotCoveredInstances = covers[2];
        }
    }

    @Override
    public ImmutableRoaringBitmap getCoverAsBitmap() {
        return coveredInstances;
    }

    @Override
    public ImmutableRoaringBitmap calculateCoverAsBitmap(RoaringBitmapCalculator calculator) {
        return calculator.getCoveredAsBitmap(conditions);
    }

    @Override
    public ImmutableRoaringBitmap getCorrectCoverAsBitmap() {
        return correctlyCoveredInstances;
    }

    @Override
    public ImmutableRoaringBitmap calculateCorrectCoverAsBitmap(RoaringBitmapCalculator calculator) {
        return calculator.getCorrectlyCoveredAsBitmap(conditions, labelValue);
    }

    @Override
    public ImmutableRoaringBitmap getIncorrectCoverAsBitmap() {
        return ImmutableRoaringBitmap.andNot(coveredInstances, correctlyCoveredInstances);
    }

    @Override
    public ImmutableRoaringBitmap calculateIncorrectCoverAsBitmap(RoaringBitmapCalculator calculator) {
        return calculator.getIncorrectlyCoveredAsBitmap(conditions, labelValue);
    }

    @Override
    public ImmutableRoaringBitmap getCorrectlyNotCoveredAsBitmap() {
        Set<Integer> labelValues = labelFeature.getUniqueNumberRepresentations();
        labelValues.remove(labelValue);
        ImmutableRoaringBitmap notLabelBitmap = calculator.getCoveredAsBitmap(labelFeature, labelValues);
        ImmutableRoaringBitmap correctlyNotCovered = ImmutableRoaringBitmap.andNot(notLabelBitmap, coveredInstances);
        return correctlyNotCovered;
    }

    @Override
    public ImmutableRoaringBitmap calculateCorrectlyNotCoveredAsBitmap(RoaringBitmapCalculator calculator) {
        return calculator.getCorrectlyNotCoveredAsBitmap(conditions, labelValue);
    }

    @Override
    public ImmutableRoaringBitmap getIncorrectlyNotCoveredAsBitmap() {
        return incorrectlyNotCoveredInstances;
    }

    @Override
    public ImmutableRoaringBitmap calculateIncorrectlyNotCoveredAsBitmap(RoaringBitmapCalculator calculator) {
        return calculator.getIncorrectlyNotCoveredAsBitmap(conditions, labelValue);
    }

    @Override
    public double getCoverage() {
        return calculateCoverage(
                coveredInstances.getCardinality(),
                dataset.getNumberRows());
    }

    @Override
    public double getPrecision() {
        return calculatePrecision(
                correctlyCoveredInstances.getCardinality(),
                coveredInstances.getCardinality());
    }

    @Override
    public double calculatePrecision(final CategoricalCalculator calculator) {
        if (dataset.equals(calculator.getDataset())) {
            return getPrecision();
        }
        int numberCoveredInstances = calculator.getNumberCovered(conditions);
        int correctlyCoveredInstances = calculator.getNumberCorrectlyCovered(conditions, labelValue);
        return calculatePrecision(correctlyCoveredInstances, numberCoveredInstances);
    }

    @Override
    public double calculateCoverage(final CategoricalCalculator calculator) {
        if (dataset.equals(calculator.getDataset())) {
            return getCoverage();
        }
        return calculateCoverage(calculator.getNumberCovered(conditions), calculator.getDataset().getNumberRows());
    }

    protected double calculatePrecision(int numberCorrectlyCoveredInstances, int numberCoveredInstances) {
        if (numberCoveredInstances == 0) {
            return 0;
        } else {
            return ((double) numberCorrectlyCoveredInstances) / numberCoveredInstances;
        }
    }

    protected double calculateCoverage(int numberCoveredInstances, int datasetSize) {
        return ((double) numberCoveredInstances) / datasetSize;
    }

    @Override
    public int getNumberCorrectlyCovered() {
        return correctlyCoveredInstances.getCardinality();
    }

    @Override
    public int getNumberIncorrectlyCovered() {
        return coveredInstances.getCardinality() -
                correctlyCoveredInstances.getCardinality();
    }

    @Override
    public int getNumberIncorrectlyNotCovered() {
        return incorrectlyNotCoveredInstances.getCardinality();
    }

    @Override
    public int getNumberCorrectlyNotCovered() {
        return dataset.getNumberRows() - getNumberCovered() - getNumberIncorrectlyNotCovered();
    }

    @Override
    public int getNumberCovered() {
        return coveredInstances.getCardinality();
    }

    @Override
    public CategoricalFeature getLabelFeature() {
        return labelFeature;
    }

    @Override
    public int getLabelValue() {
        return labelValue;
    }

    @Override
    public Set<CategoricalFeature> getConditionFeatures() {
        return Collections.unmodifiableSet(conditions.keySet());
    }

    @Override
    public Set<Integer> getConditionValues(CategoricalFeature condition) {
        return Collections.unmodifiableSet(conditions.get(condition));
    }

    @Override
    public Map<CategoricalFeature, Set<Integer>> getConditions() {
        return Collections.unmodifiableMap(conditions);
    }

    @Override
    public int getNumberConditionValues() {
        int result = 0;
        for (Set<Integer> condition : conditions.values()) {
            result += condition.size();
        }
        return result;
    }

    @Override
    public RoaringBitmapCalculator getCalculator() {
        return calculator;
    }

    @Override
    public int getNumberConditions() {
        return conditions.size();
    }


    @Override
    public boolean equals(Object allCoversRuleExplanation) {
        if (!(allCoversRuleExplanation instanceof MinimalBitmapCoversRuleExplanation)) {
            return false;
        } else {
            return conditions.equals(((MinimalBitmapCoversRuleExplanation) allCoversRuleExplanation).conditions) &&
                    labelValue == ((MinimalBitmapCoversRuleExplanation) allCoversRuleExplanation).labelValue &&
                    labelFeature.equals(((MinimalBitmapCoversRuleExplanation) allCoversRuleExplanation).labelFeature);
        }
    }

    @Override
    public int hashCode() {
        int result = labelValue + labelFeature.hashCode();
        for (CategoricalFeature cf : conditions.keySet()) {
            result += cf.hashCode();
        }
        return result;
    }

    @Override
    public TabularDataset<?, LabelColumn.CategoricalLabelColumn> getDataset() {
        return dataset;
    }

    @Override
    public String toString() {
        return "MinimalBitmapCoversRuleExplanation{" +
                "\n\tconditions=[\n\t\t" +
                getConditions()
                        .keySet()
                        .stream()
                        .map(k -> "{name=" + k.getName() + ", values=" +
                                getConditions().get(k)
                                        .stream()
                                        .map(v -> k.getStringRepresentation(v)).collect(Collectors.joining(", "))).collect(Collectors.joining("},\n\t\t")) +
                "}\n\t],\n\tlabelFeature=" + getLabelFeature() +
                ",\n\tlabelValue=" + getLabelFeature().getStringRepresentation(getLabelValue()) +
                ",\n\tcoverage=" + getCoverage() +
                ",\n\troundPrecision=" + getPrecision() +
                "\n}";
    }
}
