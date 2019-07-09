package de.viadee.xai.framework.global_explanation_procedure_step.optimizer;

import de.viadee.xai.framework.explanation_calculation.explanation.RuleExplanationSet;

import java.util.function.Function;

/**
 * Interface of all objective functions. An objective function maps an entity of type
 * &lt;E&gt; to an Comparable.
 * @param <E> The type of entity which is to-be-optimized.
 * @param <D> The type of comparable to-be-generated and which will be compared for
 *           different entities of type &lt;E&gt;.
 */
public interface ObjectiveFunction<E, D extends Comparable<D>> extends Function<E, D> {

    /**
     * Initializes the ObjectiveFunction given information stored in the {@link RuleExplanationSet}.
     * @param representationSpaceFoundation The foundation upon which the ObjectiveFunction is defined.
     */
    void initialize(RuleExplanationSet representationSpaceFoundation);

}
