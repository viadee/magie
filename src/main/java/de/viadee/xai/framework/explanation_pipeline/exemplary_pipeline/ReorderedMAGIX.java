package de.viadee.xai.framework.explanation_pipeline.exemplary_pipeline;

import de.viadee.xai.framework.adapter.black_box_classifier_adapter.TRSmileRFClassifier;
import de.viadee.xai.framework.adapter.data_source_adapter.NullDataAdapter;
import de.viadee.xai.framework.adapter.local_explainer_adapter.AnchorLocalExplainerAdapter;
import de.viadee.xai.framework.explanation_pipeline.AbstractStdExplanationPipeline;
import de.viadee.xai.framework.global_explanation_procedure_step.explanation_mapper.ModifiedMAGIXExplanationMapper;
import de.viadee.xai.framework.global_explanation_procedure_step.optimizer.rule_optimizer.RuleExplanationGeneticAlgorithm;
import de.viadee.xai.framework.global_explanation_procedure_step.postprocessor.BaselineRuleExplanationFilter;
import de.viadee.xai.framework.global_explanation_procedure_step.postprocessor.PairwiseRuleExplanationMetricFilter;
import de.viadee.xai.framework.global_explanation_procedure_step.postprocessor.RuleSetEnhancementFilter;

/**
 * Contains the same components as {@link MAGIX}, yet, reorders them.
 */
public class ReorderedMAGIX extends AbstractStdExplanationPipeline {

    /**
     * Constructor for ReorderedMAGIX.
     * @param nullDataAdapter The NullDataAdapter.
     * @param datasetName The name of the data set analyzed with this pipeline.
     */
    public ReorderedMAGIX(NullDataAdapter nullDataAdapter, String datasetName) {
        super(
                nullDataAdapter,
                new TRSmileRFClassifier(),
                new AnchorLocalExplainerAdapter(true),
                "ReorderedMAGIX" + "_" + datasetName
        );
    }

    @Override
    protected void addSteps() {
        this.add(new ModifiedMAGIXExplanationMapper(), true)
                .addRuleOptimizer(new RuleExplanationGeneticAlgorithm(), true)
                .addWithTest(new BaselineRuleExplanationFilter(), true)
                .add(new RuleSetEnhancementFilter(), true)
                .add(new PairwiseRuleExplanationMetricFilter(), true)
        ;
    }
}
