package de.viadee.xai.framework.global_explanation_procedure_step.optimizer.trajectory_optimizer;

import de.viadee.xai.framework.data.Feature;
import de.viadee.xai.framework.data.tabular_data.LabelColumn;
import de.viadee.xai.framework.data.tabular_data.TabularDataset;
import de.viadee.xai.framework.explanation_calculation.calculator.CategoricalCalculator;
import de.viadee.xai.framework.explanation_calculation.calculator.RoaringBitmapCalculator;
import de.viadee.xai.framework.explanation_calculation.explanation.RuleExplanation;
import org.roaringbitmap.buffer.ImmutableRoaringBitmap;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * Dummy {@link RuleExplanation}. Can solely be used to count the included conditions.
 */
public class DummyRuleExplanation implements RuleExplanation {

    public Map<Feature.CategoricalFeature, Set<Integer>> conditions;

    /**
     * Constructor for DummyRuleExplanation.
     * @param conditions The conditions to be included.
     */
    public DummyRuleExplanation(Map<Feature.CategoricalFeature, Set<Integer>> conditions) {
        this.conditions = conditions;
    }

    @Override
    public ImmutableRoaringBitmap getCoverAsBitmap() {
        return null;
    }

    @Override
    public ImmutableRoaringBitmap calculateCoverAsBitmap(RoaringBitmapCalculator calculator) {
        return null;
    }

    @Override
    public ImmutableRoaringBitmap getCorrectCoverAsBitmap() {
        return null;
    }

    @Override
    public ImmutableRoaringBitmap calculateCorrectCoverAsBitmap(RoaringBitmapCalculator calculator) {
        return null;
    }

    @Override
    public ImmutableRoaringBitmap getIncorrectCoverAsBitmap() {
        return null;
    }

    @Override
    public ImmutableRoaringBitmap calculateIncorrectCoverAsBitmap(RoaringBitmapCalculator calculator) {
        return null;
    }

    @Override
    public ImmutableRoaringBitmap calculateCorrectlyNotCoveredAsBitmap(RoaringBitmapCalculator calculator) {
        return null;
    }

    @Override
    public ImmutableRoaringBitmap getCorrectlyNotCoveredAsBitmap() {
        return null;
    }

    @Override
    public ImmutableRoaringBitmap getIncorrectlyNotCoveredAsBitmap() {
        return null;
    }

    @Override
    public ImmutableRoaringBitmap calculateIncorrectlyNotCoveredAsBitmap(RoaringBitmapCalculator calculator) {
        return null;
    }

    @Override
    public int getNumberCorrectlyCovered() {
        return 0;
    }

    @Override
    public int getNumberIncorrectlyCovered() {
        return 0;
    }

    @Override
    public int getNumberIncorrectlyNotCovered() {
        return 0;
    }

    @Override
    public int getNumberCorrectlyNotCovered() {
        return 0;
    }

    @Override
    public int getNumberCovered() {
        return 0;
    }

    @Override
    public Set<Feature.CategoricalFeature> getConditionFeatures() {
        return conditions.keySet();
    }

    @Override
    public int getNumberConditions() {
        return conditions.size();
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
    public Set<Integer> getConditionValues(Feature.CategoricalFeature condition) {
        return Collections.unmodifiableSet(conditions.get(condition));
    }

    @Override
    public double getCoverage() {
        return 0;
    }

    @Override
    public double getPrecision() {
        return 0;
    }

    @Override
    public double calculatePrecision(CategoricalCalculator calculator) {
        return 0;
    }

    @Override
    public double calculateCoverage(CategoricalCalculator calculator) {
        return 0;
    }

    @Override
    public TabularDataset<?, LabelColumn.CategoricalLabelColumn> getDataset() {
        return null;
    }

    @Override
    public Map<Feature.CategoricalFeature, Set<Integer>> getConditions() {
        return conditions;
    }

    @Override
    public Feature.CategoricalFeature getLabelFeature() {
        return null;
    }

    @Override
    public int getLabelValue() {
        return 0;
    }

    @Override
    public RoaringBitmapCalculator getCalculator() {
        return null;
    }
}
