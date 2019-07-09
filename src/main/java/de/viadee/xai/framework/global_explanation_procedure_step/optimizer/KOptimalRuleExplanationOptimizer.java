package de.viadee.xai.framework.global_explanation_procedure_step.optimizer;

import de.viadee.xai.framework.explanation_calculation.explanation.RuleExplanation;
import de.viadee.xai.framework.explanation_calculation.explanation.RuleExplanationFactory;
import de.viadee.xai.framework.explanation_calculation.explanation.RuleExplanationSet;
import de.viadee.xai.framework.global_explanation_procedure_step.optimizer.objective_function.ScaledEnhancedRMICalculator;
import de.viadee.xai.framework.global_explanation_procedure_step.optimizer.optimization_initializer.BinaryRuleExplanationInitializer;
import de.viadee.xai.framework.global_explanation_procedure_step.optimizer.representation_translator.BinaryRepresentationToRuleExplanation;
import de.viadee.xai.framework.global_explanation_procedure_step.optimizer.trajectory_optimizer.KOptimalRuleLocalSearch;
import de.viadee.xai.framework.global_explanation_procedure_step.optimizer.trajectory_optimizer.TrajectoryOptimizer;

import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Applies an instance of {@link KOptimalRuleLocalSearch} to each given {@link RuleExplanation}.
 */
public class KOptimalRuleExplanationOptimizer extends Optimizer<
        RuleExplanation,
        RuleExplanationFactory,
        boolean[],
        Double> {

    boolean usesPopulationSizeParameter = false;

    protected final int k;

    protected TrajectoryOptimizer<boolean[], RuleExplanation> trajectoryOptimizer;

    /**
     * Constructor for KOptimalRuleExplanationOptimizer.
     */
    public KOptimalRuleExplanationOptimizer() {
        super(
                new BinaryRuleExplanationInitializer(),
                new BinaryRepresentationToRuleExplanation(),
                new ScaledEnhancedRMICalculator(0.0001),
                0
        );
        this.k = 1;
    }

    /**
     * Constructor for KOptimalRuleExplanationOptimizer.
     * @param counterWeightNumberConditionValues The weight to be used in the {@link ScaledEnhancedRMICalculator}.
     * @param k The neighborhood-defining optimality-factor as defined in {@link KOptimalRuleLocalSearch}.
     */
    public KOptimalRuleExplanationOptimizer(double counterWeightNumberConditionValues,
                                            int k) {
        super(
                new BinaryRuleExplanationInitializer(),
                new BinaryRepresentationToRuleExplanation(),
                new ScaledEnhancedRMICalculator(counterWeightNumberConditionValues),
                0
        );
        this.k = k;
    }

    /**
     * Constructor for KOptimalRuleExplanationOptimizer. Utilizes a generated population instead of refining
     * received {@link RuleExplanation}s directly.
     * @param counterWeightNumberConditionValues The weight to be used in the {@link ScaledEnhancedRMICalculator}.
     * @param k The neighborhood-defining optimality-factor as defined in {@link KOptimalRuleLocalSearch}.
     * @param initializer The Initializer.
     * @param populationSize The size of the to-be-optimized population.
     */
    public KOptimalRuleExplanationOptimizer(double counterWeightNumberConditionValues,
                                            int k,
                                            OptimizationInitializer<boolean[][]> initializer,
                                            int populationSize) {
        super(
                initializer,
                new BinaryRepresentationToRuleExplanation(),
                new ScaledEnhancedRMICalculator(counterWeightNumberConditionValues),
                populationSize
        );
        usesPopulationSizeParameter = true;
        this.k = k;
    }

    @Override
    protected RuleExplanationSet optimize(boolean[][] initialization,
                                          Function<boolean[], Double> representationTranslatorAndCalculator,
                                          ObjectiveFunction<RuleExplanation, Double> objectiveFunction) {
        this.trajectoryOptimizer = new KOptimalRuleLocalSearch(k, representationTranslator, objectiveFunction);

        Set<RuleExplanation> resultSet =
                Stream.of(initialization)
                        .parallel()
                        .map(trajectoryOptimizer::optimize)
                        .collect(Collectors.toSet());

        return resultFactory.newWithCollection(labelFeature, labelValue, resultSet);
    }

    @Override
    protected int calculateRepresentationLength(RuleExplanationSet representationSpaceFoundation) {
        return representationSpaceFoundation.getNumberConditionValues();
    }

    @Override
    protected int calculatePopulationSize(RuleExplanationSet representationSpaceFoundation) {
        if (usesPopulationSizeParameter) {
            return populationSize;
        } else {
            return representationSpaceFoundation.getNumberExplanations();
        }
    }
}
