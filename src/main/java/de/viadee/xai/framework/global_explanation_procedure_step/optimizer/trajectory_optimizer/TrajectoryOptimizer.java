package de.viadee.xai.framework.global_explanation_procedure_step.optimizer.trajectory_optimizer;


/**
 * Interface for a TrajectoryOptimizer, i.e., an optimizer which optimizes one single instance.
 * @param <R> The working-representation of the optimization procedure.
 * @param <Res> The result type of the optimization procedure.
 */
public interface TrajectoryOptimizer<R, Res> {

    /**
     * Optimizes the given entity.
     * @param toOptimize The represented entity.
     * @return The optimized entity.
     */
    Res optimize(R toOptimize);
}
