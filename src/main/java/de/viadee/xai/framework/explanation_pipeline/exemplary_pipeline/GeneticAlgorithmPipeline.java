package de.viadee.xai.framework.explanation_pipeline.exemplary_pipeline;

import de.viadee.xai.framework.adapter.black_box_classifier_adapter.TRSmileRFClassifier;
import de.viadee.xai.framework.adapter.data_source_adapter.NullDataAdapter;
import de.viadee.xai.framework.adapter.local_explainer_adapter.AnchorLocalExplainerAdapter;
import de.viadee.xai.framework.explanation_pipeline.AbstractStdExplanationPipeline;
import de.viadee.xai.framework.global_explanation_procedure_step.optimizer.rule_optimizer.RuleExplanationGeneticAlgorithm;

/**
 * Pipeline solely conducting a {@link RuleExplanationGeneticAlgorithm}.
 */
public class GeneticAlgorithmPipeline extends AbstractStdExplanationPipeline {

    /**
     * Constructor for GeneticAlgorithmPipeline.
     * @param nullDataAdapter The NullDataAdapter.
     * @param datasetName The name of the data set analyzed with this pipeline.
     */
    public GeneticAlgorithmPipeline(NullDataAdapter nullDataAdapter, String datasetName) {
        super(
                nullDataAdapter,
                new TRSmileRFClassifier(),
                new AnchorLocalExplainerAdapter(true),
                "GeneticAlgorithmPipeline" + "_" + datasetName
        );
    }

    @Override
    protected void addSteps() {
        this.addRuleOptimizer(new RuleExplanationGeneticAlgorithm(), true)
        ;
    }
}
