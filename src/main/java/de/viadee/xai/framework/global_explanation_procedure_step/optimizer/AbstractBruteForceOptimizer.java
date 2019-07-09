package de.viadee.xai.framework.global_explanation_procedure_step.optimizer;

import de.viadee.xai.framework.explanation_calculation.explanation.RuleExplanationSet;
import org.apache.commons.math3.util.CombinatoricsUtils;

/**
 * Abstract superclass for brute-force optimizers.
 * @param <E> The entity type to be optimized.
 * @param <EF> The factory creating the entity.
 */
public abstract class AbstractBruteForceOptimizer<
        E,
        EF
        >
        extends Optimizer<
        E,
        EF,
        boolean[],
        Double> {

    protected final int keepBest;
    protected final int maxLength;


    /**
     * Constructor for AbstractBruteForceOptimizer.
     * @param initializer The Initializer.
     * @param representationTranslator The RepresentationTranslator.
     * @param objectiveFunction The ObjectiveFunction.
     * @param maxLength The maximum checked length for the representation.
     * @param keepBest The number of best results to be kept.
     */
    public AbstractBruteForceOptimizer(
            OptimizationInitializer<boolean[][]> initializer,
            RepresentationTranslator<boolean[], E, EF> representationTranslator,
            ObjectiveFunction<E, Double> objectiveFunction,
            int maxLength,
            int keepBest
    ) {
        super(initializer,
                representationTranslator,
                objectiveFunction,
                0);
        this.keepBest = keepBest;
        this.maxLength = maxLength;
    }

    protected int calculatePopulationSize(RuleExplanationSet representationSpaceFoundation) {
        int representationLength = this.calculateRepresentationLength(representationSpaceFoundation);
        int maximalLength = maxLength;
        if (representationLength < maxLength) {
            maximalLength = representationLength;
        }
        long result = 0;
        for (int i = 1; i <= maxLength; i++) {
            result += CombinatoricsUtils.binomialCoefficient(representationLength, maximalLength - i);
        }
        if (Integer.MAX_VALUE < result) {
            return Integer.MAX_VALUE;
        } else {
            return (int) result;
        }
    }
}
