package de.viadee.xai.framework.explanation_calculation.explanation;

import de.viadee.xai.framework.data.Feature;
import de.viadee.xai.framework.data.tabular_data.LabelColumn;
import de.viadee.xai.framework.data.tabular_data.TabularDataset;
import de.viadee.xai.framework.explanation_calculation.calculator.RoaringBitmapCalculator;

import java.util.Map;
import java.util.Set;

/**
 * Factory for {@link MinimalBitmapCoversRuleExplanation}s.
 */
public class MinimalCoversRuleExplanationFactory implements RuleExplanationFactory {

    protected final RoaringBitmapCalculator calculator;


    /**
     * Constructor for MinimalCoversRuleExplanationFactory.
     * @param calculator The calculator which is injected into the corresponding {@link MinimalBitmapCoversRuleExplanation}.
     */
    public MinimalCoversRuleExplanationFactory(RoaringBitmapCalculator calculator) {
        this.calculator = calculator;
    }

    @Override
    public RuleExplanation initialize(Map<Feature.CategoricalFeature, Set<Integer>> conditions, Feature.CategoricalFeature labelFeature, int labelValue) {

        return new MinimalBitmapCoversRuleExplanation(conditions, labelFeature, labelValue, calculator);
    }

    @Override
    public RuleExplanation translateWithData(RuleExplanation toTranslate) {
        return new MinimalBitmapCoversRuleExplanation(toTranslate, calculator);
    }

    @Override
    public TabularDataset<?, LabelColumn.CategoricalLabelColumn> getDataset() {
        return calculator.getDataset();
    }


    @Override
    public RoaringBitmapCalculator getCalculator() {
        return calculator;
    }
}
