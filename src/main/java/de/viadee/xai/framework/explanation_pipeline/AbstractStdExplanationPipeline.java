package de.viadee.xai.framework.explanation_pipeline;

import de.viadee.xai.anchor.adapter.tabular.AnchorTabular;
import de.viadee.xai.anchor.adapter.tabular.TabularInstance;
import de.viadee.xai.framework.adapter.black_box_classifier_adapter.BlackBoxClassifierAdapter;
import de.viadee.xai.framework.adapter.data_source_adapter.DataSourceAdapter;
import de.viadee.xai.framework.adapter.local_explainer_adapter.LocalExplainerAdapter;
import de.viadee.xai.framework.data.preprocessor.PercentileDiscretizer;
import de.viadee.xai.framework.data.tabular_data.LabelColumn.CategoricalLabelColumn;
import de.viadee.xai.framework.data.tabular_data.TabularDataset;
import de.viadee.xai.framework.explanation_visualizer.StringRuleExplanationVisualizer;
import de.viadee.xai.framework.persistence.PersistenceService;
import de.viadee.xai.framework.persistence.explanation_persistence_service.TextExplanationPersister;

/**
 * Standard abstract superclass of all pipelines used in the thesis.
 */
public abstract class AbstractStdExplanationPipeline
        extends ExplanationPipeline<AnchorTabular, TabularInstance, String> {

    /**
     * Constructor for AbstractStdExplanationPipeline.
     * @param dataSourceAdapter The DataSourceAdapter.
     * @param blackBoxClassifierAdapter The BlackBoxClassifierAdapter.
     * @param localExplainer The LocalExplainer.
     * @param explanationPersistenceDescriptor The data set name.
     */
    public AbstractStdExplanationPipeline(DataSourceAdapter dataSourceAdapter,
                                          BlackBoxClassifierAdapter<TabularInstance> blackBoxClassifierAdapter,
                                          LocalExplainerAdapter<TabularInstance> localExplainer,
                                          String explanationPersistenceDescriptor) {
        super(
                new NullPipelineContext(),
                dataSourceAdapter,
                blackBoxClassifierAdapter,
                localExplainer,
                new StringRuleExplanationVisualizer(),
                new PersistenceService(
                        new TextExplanationPersister(explanationPersistenceDescriptor),
                        null,
                        null
                )
        );
    }

    @Override
    protected void prepareDataset(AnchorTabular anchorTabular) {
        TabularDataset.TabularDatasetBuilder<?, CategoricalLabelColumn, CategoricalLabelColumn> builder = TabularDataset
                .newBuilderWithAnchorTabular(
                        CategoricalLabelColumn.class,
                        CategoricalLabelColumn.class,
                        CategoricalLabelColumn.class,
                        anchorTabular)
                .withTrainingTestSplit(true, 0.2, 42)
                .withExplainerDataDiscretizer(new PercentileDiscretizer<>(new int[] {17, 33, 50, 67, 83}))
                .withBlackBoxModel(blackBoxClassifierAdapter);
        dataset = builder.buildTrainingData();
        testData = builder.buildTestData();
    }
}
