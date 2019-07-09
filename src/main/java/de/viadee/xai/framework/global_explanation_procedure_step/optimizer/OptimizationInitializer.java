package de.viadee.xai.framework.global_explanation_procedure_step.optimizer;

import de.viadee.xai.framework.explanation_calculation.explanation.RuleExplanationSet;

import java.util.function.BiFunction;

/**
 * Represents the interface for an initializer. An initializer accepts two Integers, the first one representing
 * the population size, the second one representing the representation length. It generates ORep which in turn is used
 * to initialize an optimization algorithm.
 * @param <ORep> The type of working-representation used by the optimization algorithm.
 */
public interface OptimizationInitializer<ORep> extends BiFunction<Integer, Integer, ORep> {

    /**
     * Initializes the OptimizationInitializer given information stored in the {@link RuleExplanationSet}.
     * @param representationSpaceFoundation The foundation upon which the OptimizationInitializer is defined.
     */
    void initialize(RuleExplanationSet representationSpaceFoundation);

}
