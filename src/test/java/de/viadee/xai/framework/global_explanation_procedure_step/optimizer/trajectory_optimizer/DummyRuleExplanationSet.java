package de.viadee.xai.framework.global_explanation_procedure_step.optimizer.trajectory_optimizer;

import de.viadee.xai.framework.data.Feature;
import de.viadee.xai.framework.data.tabular_data.LabelColumn;
import de.viadee.xai.framework.data.tabular_data.TabularDataset;
import de.viadee.xai.framework.explanation_calculation.calculator.RoaringBitmapCalculator;
import de.viadee.xai.framework.explanation_calculation.explanation.RuleExplanation;
import de.viadee.xai.framework.explanation_calculation.explanation.RuleExplanationSet;
import org.roaringbitmap.buffer.ImmutableRoaringBitmap;

import java.util.*;
@SuppressWarnings("Duplicates")
/**
 * Dummy {@link RuleExplanationSet}.
 */
public class DummyRuleExplanationSet implements RuleExplanationSet {

    protected Set<RuleExplanation> ruleExplanations;

    /**
     * Constructor for DummyRuleExplanationSet.
     * @param ruleExplanations The included {@link RuleExplanation}s, most likely {@link DummyRuleExplanation}s.
     */
    public DummyRuleExplanationSet(Set<RuleExplanation> ruleExplanations) {
        this.ruleExplanations = ruleExplanations;
    }

    @Override
    public ImmutableRoaringBitmap getCoverAsBitmap() {
        return null;
    }

    @Override
    public double getCoverage() {
        return 0;
    }

    @Override
    public int getNumberCoveredInstances() {
        return 0;
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
    public Map<Feature.CategoricalFeature, Set<Integer>> getConditions() {
        Map<Feature.CategoricalFeature, Set<Integer>> features = new HashMap<>();
        for (RuleExplanation re : ruleExplanations) {
            for (Feature.CategoricalFeature cf : re.getConditionFeatures()) {
                features.putIfAbsent(cf, new HashSet<>());
                features.get(cf).addAll(re.getConditionValues(cf));
            }
        }
        return Collections.unmodifiableMap(features);
    }


    @Override
    public TabularDataset<?, LabelColumn.CategoricalLabelColumn> getDataset() {
        return null;
    }

    @Override
    public RoaringBitmapCalculator getCalculator() {
        return null;
    }

    @Override
    public Set<RuleExplanation> getExplanations() {
        return ruleExplanations;
    }

    @Override
    public int getNumberExplanations() {
        return ruleExplanations.size();
    }

    @Override
    public int getNumberConditionValues() {
        int result = 0;
        for (Set<Integer> re : getConditions().values()) {
            result += re.size();
        }
        return result;
    }
}
