package de.viadee.xai.framework.explanation_calculation.explanation;

import de.viadee.xai.framework.data.Feature.CategoricalFeature;
import de.viadee.xai.framework.data.tabular_data.LabelColumn;
import de.viadee.xai.framework.data.tabular_data.TabularDataset;
import de.viadee.xai.framework.explanation_calculation.calculator.RoaringBitmapCalculator;

import java.util.Collection;

/**
 * Standard implementation of a RuleExplanationSetFactory.
 */
public class StdRuleExplanationSetFactory implements RuleExplanationSetFactory {

    protected final RoaringBitmapCalculator calculator;

    /**
     * Constructor for StdRuleExplanationSetFactory.
     * @param calculator The calculator with which the created StdRuleExplanationSets will be
     *                       evaluated on.
     */
    public StdRuleExplanationSetFactory(RoaringBitmapCalculator calculator) {

        this.calculator = calculator;
    }

    @Override
    public RuleExplanationSet newEmpty(CategoricalFeature labelFeature, int labelValue) {
        return new BitmapRuleExplanationSet(labelFeature, labelValue, calculator);
    }

    @Override
    public RuleExplanationSet newWithCollection(CategoricalFeature labelFeature,
                                                   int labelValue,
                                                   Collection<RuleExplanation> ruleExplanations) {
        return new BitmapRuleExplanationSet(labelFeature, labelValue, ruleExplanations, calculator);
    }

    @Override
    public RuleExplanationSet newCopyWith(RuleExplanationSet copyFrom,
                                             RuleExplanation ruleExplanation) {
        return new BitmapRuleExplanationSet(copyFrom, ruleExplanation, calculator);
    }

    @Override
    public RuleExplanationSet translateWithData(RuleExplanationSet copyFrom) {
        return new BitmapRuleExplanationSet(copyFrom, calculator);
    }

    @Override
    public TabularDataset<?, LabelColumn.CategoricalLabelColumn> getDataset() {
        return calculator.getDataset();
    }
}
