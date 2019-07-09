package de.viadee.xai.framework.global_explanation_procedure_step.optimizer.trajectory_optimizer;

import de.viadee.xai.framework.explanation_calculation.explanation.RuleExplanation;
import de.viadee.xai.framework.explanation_calculation.explanation.RuleExplanationFactory;
import de.viadee.xai.framework.global_explanation_procedure_step.optimizer.ObjectiveFunction;
import de.viadee.xai.framework.global_explanation_procedure_step.optimizer.RepresentationTranslator;
import de.viadee.xai.framework.utility.Tuple;

/**
 * Class applying a k-optimal local search for each {@link RuleExplanation} given.
 */
public class KOptimalRuleLocalSearch implements TrajectoryOptimizer<boolean[], RuleExplanation> {

    protected final int k;
    protected final RepresentationTranslator<boolean[], RuleExplanation, RuleExplanationFactory> representationTranslator;
    protected final ObjectiveFunction<RuleExplanation, Double> objectiveFunction;

    /**
     * Constructor for KOptimalRuleLocalSearch.
     * @param k The number of bitflips which can at most be checked at once, defining the neighborhood of a {@link RuleExplanation}.
     * @param representationTranslator The translator to map a boolean[] to a {@link RuleExplanation}.
     * @param objectiveFunction The {@link ObjectiveFunction} evaluating the {@link RuleExplanation}.
     */
    public KOptimalRuleLocalSearch(int k,
                                   RepresentationTranslator<
                                           boolean[],
                                           RuleExplanation,
                                           RuleExplanationFactory> representationTranslator,
                                   ObjectiveFunction<RuleExplanation, Double> objectiveFunction) {
        if (k < 1) {
            throw new IllegalArgumentException("k must be >= 1.");
        }
        this.k = k;
        this.representationTranslator = representationTranslator;
        this.objectiveFunction = objectiveFunction;
    }

    @Override
    public RuleExplanation optimize(final boolean[] toOptimize) {
        boolean[] bestLocal = optimizeBoolAr(toOptimize);
        return representationTranslator.apply(bestLocal);
    }

    /**
     * Method which is called by {@link KOptimalRuleLocalSearch#optimize(boolean[])}. Is kept separately to facilitate
     * testing. {@link KOptimalRuleLocalSearch#optimize(boolean[])} should be called.
     * @param toOptimize The binary string-representation of the to-be-optimized entity.
     * @return The binary string-representation of a locally optimal entity.
     */
    public boolean[] optimizeBoolAr(final boolean[] toOptimize) {
        boolean[] currentBest = toOptimize;
        Double currentBestObjective = objectiveFunction.apply(representationTranslator.apply(toOptimize));
        boolean changed;
        boolean[] bestForCurrentIteration;
        Double bestObjectiveForCurrentIteration;

        do {
            changed = false;
            Tuple<boolean[], Double> bestFlips = checkFlips(currentBest, k, 0);
            bestForCurrentIteration = bestFlips.getFirstElement();
            bestObjectiveForCurrentIteration = bestFlips.getSecondElement();

            if (bestObjectiveForCurrentIteration > currentBestObjective) {
                currentBestObjective = bestObjectiveForCurrentIteration;
                currentBest = bestForCurrentIteration;
                changed = true;
            }
        } while (changed); // Reiterate, if rule could be improved

        return currentBest;
    }

    protected Tuple<boolean[], Double> checkFlips(boolean[] toOptimize, int remainingK, int from) {
        if (remainingK <= 0 || from >= (toOptimize.length)) {
            return new Tuple<>(
                    toOptimize,
                    objectiveFunction.apply(representationTranslator.apply(toOptimize))
            );
        }

        boolean[] bestForCurrentIteration = toOptimize;
        Double bestObjectiveForCurrentIteration = objectiveFunction.apply(representationTranslator.apply(toOptimize));
        for (int i = from; i < bestForCurrentIteration.length; i++) {
            boolean[] current = new boolean[toOptimize.length];
            System.arraycopy(toOptimize, 0, current, 0, toOptimize.length);
            current[i] = !current[i];
            Tuple<boolean[], Double> withMoreFlips = checkFlips(current, remainingK - 1, i+1);
            current = withMoreFlips.getFirstElement();
            Double currentObjective = withMoreFlips.getSecondElement();
            if (bestObjectiveForCurrentIteration < currentObjective) {
                bestObjectiveForCurrentIteration = currentObjective;
                bestForCurrentIteration = current;
            }
        }
        return new Tuple<>(bestForCurrentIteration, bestObjectiveForCurrentIteration);
    }
}
