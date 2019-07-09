package de.viadee.xai.framework.explanation_calculation.explanation;

import de.viadee.xai.framework.data.Feature.CategoricalFeature;
import de.viadee.xai.framework.data.tabular_data.LabelColumn;
import de.viadee.xai.framework.data.tabular_data.TabularDataset;
import de.viadee.xai.framework.explanation_calculation.calculator.RoaringBitmapCalculator;

import java.util.Map;
import java.util.Set;

/**
 * Standard implementation of the RuleExplanationFactory.
 */
public class StdRuleExplanationFactory implements RuleExplanationFactory {

    protected final RoaringBitmapCalculator calculator;


    /**
     * Constructor for StdRuleExplanationFactory.
     * @param calculator The calculator with which the generated RuleExplanations are evaluated, i.e.,
     *                for which covers will be calculated.
     */
    public StdRuleExplanationFactory(RoaringBitmapCalculator calculator) {
        this.calculator = calculator;
    }

    @Override
    public StdRuleExplanation initialize(final Map<CategoricalFeature, Set<Integer>> conditions,
                                         final CategoricalFeature labelFeature,
                                         final int labelValue) {
        return new StdRuleExplanation(
                conditions,
                labelFeature,
                labelValue,
                calculator
        );
    }

    @Override
    public StdRuleExplanation translateWithData(RuleExplanation toTranslate) {
        return new StdRuleExplanation(
                toTranslate,
                calculator
        );
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
