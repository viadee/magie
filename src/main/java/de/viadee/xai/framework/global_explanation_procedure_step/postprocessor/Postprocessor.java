package de.viadee.xai.framework.global_explanation_procedure_step.postprocessor;

import de.viadee.xai.framework.explanation_calculation.explanation.RuleExplanationFactory;
import de.viadee.xai.framework.explanation_calculation.explanation.RuleExplanationSet;
import de.viadee.xai.framework.global_explanation_procedure_step.ExplanationProcedureStep;

/**
 * Interface for all postprocessors. A postprocessor can be used to filter
 * {@link de.viadee.xai.framework.explanation_calculation.explanation.RuleExplanation}s without using an objective
 * function.
 */
public interface Postprocessor extends ExplanationProcedureStep<RuleExplanationFactory> {
    /**
     * Postprocesses the given {@link RuleExplanationSet}.
     * @param toProcess The {@link RuleExplanationSet} to be filtered.
     * @return The processed RuleExplanationSet.
     */
    RuleExplanationSet postprocess(RuleExplanationSet toProcess);
}
