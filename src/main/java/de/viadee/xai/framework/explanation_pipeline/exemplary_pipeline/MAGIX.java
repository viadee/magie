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
 * Procedure directly mimicking MAGIX with modified components.
 * Puri, N., Gupta, P., Agarwal, P., Verma, S., {@literal &} Krishnamurthy, B. (2017).
 * MAGIX: Model Agnostic Globally Interpretable Explanations. arXiv preprint arXiv:1706.07160.
 */
public class MAGIX extends AbstractStdExplanationPipeline {

    /**
     * Constructor for MAGIX.
     * @param nullDataAdapter The NullDataAdapter.
     * @param datasetName The name of the data set analyzed with this pipeline.
     */
    public MAGIX(NullDataAdapter nullDataAdapter, String datasetName) {
        super(
                nullDataAdapter,
                new TRSmileRFClassifier(),
                new AnchorLocalExplainerAdapter(true),
                "MAGIX" + "_" + datasetName
        );
    }

    @Override
    protected void addSteps() {
        this.add(new ModifiedMAGIXExplanationMapper(), true)
                .addRuleOptimizer(new RuleExplanationGeneticAlgorithm(), true)
                .add(new RuleSetEnhancementFilter(), true)
                .addWithTest(new BaselineRuleExplanationFilter(), true)
                .add(new PairwiseRuleExplanationMetricFilter(), true)
        ;
    }
}
