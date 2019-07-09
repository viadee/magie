package de.viadee.xai.framework.global_explanation_procedure_step.optimizer;

import de.viadee.xai.framework.explanation_calculation.explanation.RuleExplanationSet;

import java.util.function.Function;

/**
 * A representation translator for transforming the working-representation of an optimization algorithm to a
 * domain entity class, e.g., a RuleExplanation or a RuleExplanationSet. Prior to usage, initialize MUST be called.
 * @param <R> The type of working-representation used by the optimization algorithm.
 * @param <E> The type of object on which the objective function is to be evaluated, i.e., an ExplanationSet or an Explanation.
 * @param <EF> The type of Factory used to generate the result. For example, a RuleExplanationFactory or a
 *            RuleExplanationSetFactory can be used.
 */
public interface RepresentationTranslator<R, E, EF> extends Function<R, E> {

    /**
     * Sets up the mapping from which optimization algorithm working-representations are transformed into the wanted
     * entity.
     * @param representationSpaceFoundation The data from which the space of working-representation is derived.
     * @param factory The factory for generating the mapped-to representation.
     */
    void initialize(RuleExplanationSet representationSpaceFoundation, EF factory);
}
