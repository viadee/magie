package de.viadee.xai.framework.adapter.local_explainer_adapter;

import de.viadee.xai.framework.adapter.black_box_classifier_adapter.BlackBoxClassifierAdapter;
import de.viadee.xai.framework.data.tabular_data.TabularRow;
import de.viadee.xai.framework.explanation_calculation.explanation.RuleExplanation;
import de.viadee.xai.framework.explanation_calculation.explanation.RuleExplanationFactory;

/**
 * Interface for a local explainer. A local explainer receives DataInstances of a dataset and explains them
 * using a black box classifier or a dataset.
 * @param <I> The type of data instance used for the local explainer. To be linked with {@link BlackBoxClassifierAdapter}
 */
public interface LocalExplainerAdapter<
        I
        > {

    /**
     * Explains a data instance from the dataset.
     * @param toExplain The data instance.
     * @return The generated explanation.
     */
    RuleExplanation explain(TabularRow toExplain);

    /**
     * Initializes the local explainer. Used within the executing ExplanationPipeline-instance.
     * @param blackBoxClassifierAdapter The adapter adjusting the black box model to the local explainer-functionality.
     * @param ruleExplanationFactory The factory generating a RuleExplanation based on the output.
     */
    void initialize(BlackBoxClassifierAdapter<I> blackBoxClassifierAdapter,
                    RuleExplanationFactory ruleExplanationFactory);
}
