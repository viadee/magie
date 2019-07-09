package de.viadee.xai.framework.explanation_pipeline.exemplary_pipeline;

import de.viadee.xai.framework.adapter.black_box_classifier_adapter.TRSmileRFClassifier;
import de.viadee.xai.framework.adapter.data_source_adapter.NullDataAdapter;
import de.viadee.xai.framework.adapter.local_explainer_adapter.AnchorLocalExplainerAdapter;
import de.viadee.xai.framework.explanation_pipeline.AbstractStdExplanationPipeline;
import de.viadee.xai.framework.global_explanation_procedure_step.explanation_mapper.ModifiedMAGIXExplanationMapper;
import de.viadee.xai.framework.global_explanation_procedure_step.optimizer.KOptimalRuleExplanationOptimizer;
import de.viadee.xai.framework.global_explanation_procedure_step.optimizer.rule_optimizer.RuleExplanationGeneticAlgorithm;
import de.viadee.xai.framework.global_explanation_procedure_step.postprocessor.BaselineRuleExplanationFilter;
import de.viadee.xai.framework.global_explanation_procedure_step.postprocessor.ObsoleteDisjunctionFilter;
import de.viadee.xai.framework.global_explanation_procedure_step.postprocessor.PairwiseRuleExplanationMetricFilter;
import de.viadee.xai.framework.global_explanation_procedure_step.postprocessor.RuleSetEnhancementFilter;

/**
 * Pipeline similar to the {@link MAGIX}-pipeline, yet, containing a {@link KOptimalRuleExplanationOptimizer}.
 */
public class ReorderedKOptMAGIX extends AbstractStdExplanationPipeline {

    /**
     * Constructor for ReorderedKOptMAGIX.
     * @param nullDataAdapter The NullDataAdapter.
     * @param datasetName The name of the data set analyzed with this pipeline.
     */
    public ReorderedKOptMAGIX(NullDataAdapter nullDataAdapter, String datasetName) {
        super(
                nullDataAdapter,
                new TRSmileRFClassifier(),
                new AnchorLocalExplainerAdapter(true),
                "ReorderedKOptMAGIX" + "_" + datasetName
        );
    }

    @Override
    protected void addSteps() {
        this.add(new ModifiedMAGIXExplanationMapper(), true)
                .addRuleOptimizer(new RuleExplanationGeneticAlgorithm(), true)
                .addRuleOptimizer(new KOptimalRuleExplanationOptimizer(0.0001, 1), true)
                .addWithTest(new BaselineRuleExplanationFilter(), true)
                .add(new RuleSetEnhancementFilter(), true)
                .add(new PairwiseRuleExplanationMetricFilter(), true)
                .add(new ObsoleteDisjunctionFilter(), false)
        ;
    }
}
