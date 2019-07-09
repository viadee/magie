package de.viadee.xai.framework.global_explanation_procedure_step.optimizer.trajectory_optimizer;

import de.viadee.xai.framework.data.Feature;
import de.viadee.xai.framework.data.tabular_data.LabelColumn;
import de.viadee.xai.framework.data.tabular_data.TabularDataset;
import de.viadee.xai.framework.explanation_calculation.calculator.RoaringBitmapCalculator;
import de.viadee.xai.framework.explanation_calculation.explanation.RuleExplanation;
import de.viadee.xai.framework.explanation_calculation.explanation.RuleExplanationFactory;

import java.util.Map;
import java.util.Set;

/**
 * Factory for {@link DummyRuleExplanation}.
 */
public class DummyRuleExplanationFactory implements RuleExplanationFactory {
    @Override
    public RuleExplanation initialize(Map<Feature.CategoricalFeature, Set<Integer>> conditions, Feature.CategoricalFeature labelFeature, int labelValue) {
        return new DummyRuleExplanation(conditions);
    }

    @Override
    public RuleExplanation translateWithData(RuleExplanation toTranslate) {
        return null;
    }

    @Override
    public TabularDataset<?, LabelColumn.CategoricalLabelColumn> getDataset() {
        return null;
    }

    @Override
    public RoaringBitmapCalculator getCalculator() {
        return null;
    }
}
