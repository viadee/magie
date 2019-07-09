package de.viadee.xai.framework.explanation_pipeline.exemplary_pipeline;

import de.viadee.xai.framework.adapter.black_box_classifier_adapter.TRSmileRFClassifier;
import de.viadee.xai.framework.adapter.data_source_adapter.NullDataAdapter;
import de.viadee.xai.framework.explanation_pipeline.AbstractStdExplanationPipeline;
import de.viadee.xai.framework.global_explanation_procedure_step.optimizer.KOptimalRuleExplanationOptimizer;
import de.viadee.xai.framework.global_explanation_procedure_step.optimizer.optimization_initializer.BinaryAscendingInitializer;
import de.viadee.xai.framework.global_explanation_procedure_step.postprocessor.BaselineRuleExplanationFilter;
import de.viadee.xai.framework.global_explanation_procedure_step.postprocessor.ObsoleteDisjunctionFilter;
import de.viadee.xai.framework.global_explanation_procedure_step.postprocessor.PairwiseRuleExplanationMetricFilter;
import de.viadee.xai.framework.global_explanation_procedure_step.postprocessor.RuleSetEnhancementFilter;

/**
 * Pipeline directly optimizing an initial population locally.
 */
public class DirectKOptMining extends AbstractStdExplanationPipeline {

    /**
     * Constructor for DirectKOptMining.
     * @param nullDataAdapter The NullDataAdapter.
     * @param datasetName The name of the data set analyzed with this pipeline.
     */
    public DirectKOptMining(NullDataAdapter nullDataAdapter, String datasetName) {
        super(
                nullDataAdapter,
                new TRSmileRFClassifier(),
                null,
                "DirectKOptMining" + "_" + datasetName
        );
    }

    @Override
    protected void addSteps() {
        this.addRuleOptimizer(
                new KOptimalRuleExplanationOptimizer(
                        0.0001,
                        1,
                        new BinaryAscendingInitializer(),
                        240
                ),
                true)
                .addWithTest(new BaselineRuleExplanationFilter(), true)
                .add(new RuleSetEnhancementFilter(), true)
                .add(new PairwiseRuleExplanationMetricFilter(), true)
                .add(new ObsoleteDisjunctionFilter(), false)
        ;
    }
}
