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
 * Standard implementation of a RuleExplanation. Holds the number of instances in the covered, correctly-covered, and
 * correctly-not-covered sets.
 */
public final class StdRuleExplanation implements RuleExplanation {

    // The Map's keys consist of different Features which are AND-connected.
    // The values of the mapExplanations correspond to OR-connected categorical feature values of the same Feature.
    protected final Map<CategoricalFeature, Set<Integer>> conditions;
    protected final CategoricalFeature labelFeature;
    protected final int labelValue;


    protected final TabularDataset<?, LabelColumn.CategoricalLabelColumn> dataset;
    protected final RoaringBitmapCalculator calculator;
    protected final int numberCoveredInstances;
    protected final int numberCorrectlyCoveredInstances;
    protected final int numberIncorrectlyNotCoveredInstances;

    /**
     * Constructor for a StdRuleExplanation instance.
     * @param conditions The conditions from which to generate the Rule.
     * @param labelFeature The label feature.
     * @param labelValue The label value.
     * @param calculator The calculator with which the StdRuleExplanation's metrics and covers are calculated per default.
     */
    protected StdRuleExplanation(final Map<CategoricalFeature, Set<Integer>> conditions,
                                 final CategoricalFeature labelFeature,
                                 final int labelValue,
                                 final RoaringBitmapCalculator calculator) {
        this.conditions = new HashMap<>(conditions);
        this.labelFeature = labelFeature;
        this.labelValue = labelValue;
        this.dataset = calculator.getDataset();
        this.calculator = calculator;
        int[] numberInCovers = calculator.getMinimalAmountNumberInCovers(conditions, labelValue);
        numberCoveredInstances = numberInCovers[0];
        numberCorrectlyCoveredInstances = numberInCovers[1];
        numberIncorrectlyNotCoveredInstances = numberInCovers[2];
    }

    /**
     * Constructor for a StdRuleExplanation instance.
     * Copies the given RuleExplanation and transforms it to a StdRuleExplanation using the given dataset.
     * @param copyFrom The to-be-copies RuleExplanation.
     * @param calculator The used calculator.
     */
    public StdRuleExplanation(final RuleExplanation copyFrom,
                              final RoaringBitmapCalculator calculator) {
        this.dataset = calculator.getDataset();
        this.calculator = calculator;
        conditions = new HashMap<>(copyFrom.getConditions());
        labelFeature = copyFrom.getLabelFeature();
        labelValue = copyFrom.getLabelValue();
        if (dataset.equals(copyFrom.getDataset())) {
            numberCoveredInstances = copyFrom.getNumberCovered();
            numberCorrectlyCoveredInstances = copyFrom.getNumberCorrectlyCovered();
            numberIncorrectlyNotCoveredInstances = copyFrom.getNumberIncorrectlyNotCovered();
        } else {
            int[] numberInCovers = calculator.getMinimalAmountNumberInCovers(conditions, labelValue);
            numberCoveredInstances = numberInCovers[0];
            numberCorrectlyCoveredInstances = numberInCovers[1];
            numberIncorrectlyNotCoveredInstances = numberInCovers[2];
        }
    }
    @Override
    public ImmutableRoaringBitmap getCoverAsBitmap() {
        return calculator.getCoveredAsBitmap(conditions);
    }

    @Override
    public ImmutableRoaringBitmap calculateCoverAsBitmap(RoaringBitmapCalculator calculator) {
        return calculator.getCoveredAsBitmap(conditions);
    }

    @Override
    public ImmutableRoaringBitmap getCorrectCoverAsBitmap() {
        return calculator.getCorrectlyCoveredAsBitmap(conditions, labelValue);
    }

    @Override
    public ImmutableRoaringBitmap calculateCorrectCoverAsBitmap(RoaringBitmapCalculator calculator) {
        return calculator.getCorrectlyCoveredAsBitmap(conditions, labelValue);
    }

    @Override
    public ImmutableRoaringBitmap getIncorrectCoverAsBitmap() {
        return calculator.getIncorrectlyCoveredAsBitmap(conditions, labelValue);
    }

    @Override
    public ImmutableRoaringBitmap calculateIncorrectCoverAsBitmap(RoaringBitmapCalculator calculator) {
        return calculator.getIncorrectlyCoveredAsBitmap(conditions, labelValue);
    }

    @Override
    public ImmutableRoaringBitmap calculateCorrectlyNotCoveredAsBitmap(RoaringBitmapCalculator calculator) {
        return calculator.getCorrectlyNotCoveredAsBitmap(conditions, labelValue);
    }

    @Override
    public ImmutableRoaringBitmap getCorrectlyNotCoveredAsBitmap() {
        return calculator.getCorrectlyNotCoveredAsBitmap(conditions, labelValue);
    }

    @Override
    public ImmutableRoaringBitmap getIncorrectlyNotCoveredAsBitmap() {
        return calculator.getIncorrectlyNotCoveredAsBitmap(conditions, labelValue);
    }

    @Override
    public ImmutableRoaringBitmap calculateIncorrectlyNotCoveredAsBitmap(RoaringBitmapCalculator calculator) {
        return calculator.getIncorrectlyNotCoveredAsBitmap(conditions, labelValue);
    }

    @Override
    public double getCoverage() {
        return calculateCoverage(numberCoveredInstances, dataset.getNumberRows());
    }

    @Override
    public double getPrecision() {
        return calculatePrecision(numberCorrectlyCoveredInstances, numberCoveredInstances);
    }

    @Override
    public double calculatePrecision(final CategoricalCalculator calculator) {
        TabularDataset<?, LabelColumn.CategoricalLabelColumn> newData = calculator.getDataset();
        if (dataset.equals(newData)) {
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
        return numberCorrectlyCoveredInstances;
    }

    @Override
    public int getNumberIncorrectlyCovered() {
        return getNumberCovered() - getNumberCorrectlyCovered();
    }

    @Override
    public int getNumberIncorrectlyNotCovered() {
        return numberIncorrectlyNotCoveredInstances;
    }

    @Override
    public int getNumberCorrectlyNotCovered() {
        return dataset.getNumberRows() - numberCoveredInstances - numberIncorrectlyNotCoveredInstances;
    }

    @Override
    public int getNumberCovered() {
        return numberCoveredInstances;
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
    public int getNumberConditions() {
        return conditions.size();
    }

    @Override
    public boolean equals(Object stdRuleExplanation) {
        if (!(stdRuleExplanation instanceof StdRuleExplanation)) {
            return false;
        } else {
            return conditions.equals(((StdRuleExplanation) stdRuleExplanation).conditions) &&
                    labelValue == ((StdRuleExplanation) stdRuleExplanation).labelValue &&
                    labelFeature.equals(((StdRuleExplanation) stdRuleExplanation).labelFeature);
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
    public RoaringBitmapCalculator getCalculator() {
        return calculator;
    }

    @Override
    public String toString() {
        return "StdRuleExplanation{" +
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
