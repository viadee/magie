package de.viadee.xai.framework.global_explanation_procedure_step;

import de.viadee.xai.framework.explanation_calculation.explanation.RuleExplanationSetFactory;

/**
 * Interface for all {@link de.viadee.xai.framework.global_explanation_procedure_step.explanation_mapper.ExplanationMapper},
 * {@link de.viadee.xai.framework.global_explanation_procedure_step.optimizer.Optimizer}, {@link de.viadee.xai.framework.global_explanation_procedure_step.postprocessor.Postprocessor},
 * and {@link de.viadee.xai.framework.global_explanation_procedure_step.explanation_structurer.ExplanationStructurer} classes.
 * @param <F> The type of factory used to generate the regarded instances with, i.e., either a {@link de.viadee.xai.framework.explanation_calculation.explanation.RuleExplanationFactory},
 *           or a {@link RuleExplanationSetFactory}.
 */
public interface ExplanationProcedureStep<F> {
    /**
     * Injects the factories needed by the global explanation procedure steps to create the corresponding entities.
     * @param factory The factory needed to create the regarded entity.
     * @param ruleExplanationSetFactory The factory used to create the resulting
     * {@link de.viadee.xai.framework.explanation_calculation.explanation.RuleExplanationSet}.
     */
    void initialize(F factory, RuleExplanationSetFactory ruleExplanationSetFactory);
}
