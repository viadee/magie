package de.viadee.xai.framework.global_explanation_procedure_step.explanation_mapper;

import de.viadee.xai.framework.adapter.local_explainer_adapter.LocalExplainerAdapter;
import de.viadee.xai.framework.data.Feature.CategoricalFeature;
import de.viadee.xai.framework.data.tabular_data.LabelColumn;
import de.viadee.xai.framework.data.tabular_data.TabularDataset;
import de.viadee.xai.framework.explanation_calculation.explanation.RuleExplanationFactory;
import de.viadee.xai.framework.explanation_calculation.explanation.RuleExplanationSet;
import de.viadee.xai.framework.explanation_calculation.explanation.RuleExplanationSetFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class AllConditionsMapper implements ExplanationMapper {

    protected TabularDataset<?, LabelColumn.CategoricalLabelColumn> dataset;
    protected Map<CategoricalFeature, Set<Integer>> allConditions;
    protected RuleExplanationSetFactory<?> ruleExplanationSetFactory;
    protected RuleExplanationFactory ruleExplanationFactory;

    public RuleExplanationSet mapExplanations(int labelValue) {
        RuleExplanationSet ruleExplanationSet =
                ruleExplanationSetFactory.newEmpty(dataset.getProcessedLabelCol().getLabel(), labelValue);
        return ruleExplanationSetFactory.newCopyWith(
                ruleExplanationSet,
                ruleExplanationFactory.initialize(
                        allConditions,
                        dataset.getProcessedLabelCol().getLabel(),
                        labelValue));
    }

    @Override
    public void setLocalExplainer(LocalExplainerAdapter<?> localExplainerAdapter) {
        throw new IllegalArgumentException("An AllConditionsMapper cannot utilize a LocalExplainerAdapter.");
    }

    @Override
    public void initialize(RuleExplanationFactory ruleExplanationFactory, RuleExplanationSetFactory ruleExplanationSetFactory) {
        this.ruleExplanationFactory = ruleExplanationFactory;
        this.ruleExplanationSetFactory = ruleExplanationSetFactory;
        this.dataset = ruleExplanationFactory.getDataset();
        Set<? extends CategoricalFeature> categoricalFeatures = this.dataset.getProcessedCatFeatures();
        allConditions = new HashMap<>();
        for (CategoricalFeature cf : categoricalFeatures) {
            allConditions
                    .put(cf,
                            Arrays
                                    .stream(dataset.getProcessedCol(cf))
                                    .mapToObj(i -> i).collect(Collectors.toSet()));
        }
    }
}
