package de.viadee.xai.framework.explanation_pipeline.exemplary_pipeline;

import de.viadee.xai.framework.adapter.black_box_classifier_adapter.TRSmileRFClassifier;
import de.viadee.xai.framework.adapter.data_source_adapter.NullDataAdapter;
import de.viadee.xai.framework.explanation_pipeline.AbstractStdExplanationPipeline;
import de.viadee.xai.framework.global_explanation_procedure_step.optimizer.KOptimalRuleExplanationOptimizer;
import de.viadee.xai.framework.global_explanation_procedure_step.optimizer.rule_optimizer.RuleExplanationGeneticAlgorithm;
import de.viadee.xai.framework.global_explanation_procedure_step.postprocessor.*;

/**
 * Pipeline directly performing association rule mining via a genetic algorithm.
 */
public class DirectMemeticMining extends AbstractStdExplanationPipeline {

    /**
     * Constructor for DirectMemeticMining.
     * @param nullDataAdapter The NullDataAdapter.
     * @param datasetName The name of the data set analyzed with this pipeline.
     */
    public DirectMemeticMining(NullDataAdapter nullDataAdapter, String datasetName) {
        super(
                nullDataAdapter,
                new TRSmileRFClassifier(),
                null,
                "DirectMemeticMining" + "_" + datasetName
        );
    }

    @Override
    protected void addSteps() {
        this.addRuleOptimizer(new RuleExplanationGeneticAlgorithm(0.05, 120), true)
                .addRuleOptimizer(new KOptimalRuleExplanationOptimizer(0.01, 1), true)
                .addWithTest(new BaselineRuleExplanationFilter(), true)
                .add(new RuleSetEnhancementFilter(), true)
                .add(new PairwiseRuleExplanationMetricFilter(), true)
                .add(new ObsoleteDisjunctionFilter(), false)
        ;
    }
}