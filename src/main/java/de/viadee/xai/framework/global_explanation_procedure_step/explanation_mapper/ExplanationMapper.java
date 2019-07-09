package de.viadee.xai.framework.global_explanation_procedure_step.explanation_mapper;

import de.viadee.xai.framework.adapter.local_explainer_adapter.LocalExplainerAdapter;
import de.viadee.xai.framework.explanation_calculation.explanation.RuleExplanationFactory;
import de.viadee.xai.framework.explanation_calculation.explanation.RuleExplanationSet;
import de.viadee.xai.framework.global_explanation_procedure_step.ExplanationProcedureStep;

/**
 * Interface for all explanation mappers. An explanation mapper generates an initial set of rules and conditions.
 */
public interface ExplanationMapper extends ExplanationProcedureStep<RuleExplanationFactory> {
    /**
     * For the given label value, create initial {@link de.viadee.xai.framework.explanation_calculation.explanation.RuleExplanation}s.
     * @param labelValue The label value of the {@link de.viadee.xai.framework.explanation_calculation.explanation.RuleExplanation}.
     * @return An initial set of {@link de.viadee.xai.framework.explanation_calculation.explanation.RuleExplanation}s.
     */
    RuleExplanationSet mapExplanations(int labelValue);

    /**
     * Set the adapted local explainer for the ExplanationMapper.
     * @param localExplainerAdapter The LocalExplainerAdapter.
     */
    void setLocalExplainer(LocalExplainerAdapter<?> localExplainerAdapter);
}
