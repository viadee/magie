package de.viadee.xai.framework.global_explanation_procedure_step.optimizer.rule_optimizer;

import de.viadee.xai.framework.explanation_calculation.explanation.ExplanationMetricAssignation;
import de.viadee.xai.framework.explanation_calculation.explanation.RuleExplanation;
import de.viadee.xai.framework.explanation_calculation.explanation.RuleExplanationFactory;
import de.viadee.xai.framework.explanation_calculation.explanation.RuleExplanationSet;
import de.viadee.xai.framework.global_explanation_procedure_step.optimizer.AbstractGeneticAlgorithmOptimizer;
import de.viadee.xai.framework.global_explanation_procedure_step.optimizer.OptimizationInitializer;
import de.viadee.xai.framework.global_explanation_procedure_step.optimizer.ObjectiveFunction;
import de.viadee.xai.framework.global_explanation_procedure_step.optimizer.RepresentationTranslator;
import de.viadee.xai.framework.global_explanation_procedure_step.optimizer.objective_function.ScaledEnhancedRMICalculator;
import de.viadee.xai.framework.global_explanation_procedure_step.optimizer.optimization_initializer.EvolutionStartInitializer;
import de.viadee.xai.framework.global_explanation_procedure_step.optimizer.representation_translator.BitGeneGenotypeToRuleExplanation;
import de.viadee.xai.framework.utility.Utility;
import io.jenetics.BitGene;
import io.jenetics.Genotype;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.engine.Limits;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Concrete implementation of a genetic algorithm using the jenetics-framework.
 */
public class RuleExplanationGeneticAlgorithm
        extends AbstractGeneticAlgorithmOptimizer<
                RuleExplanation,
                RuleExplanationFactory
                > {
    protected final int keepBest;

    /**
     * Default constructor for RuleExplanationGeneticAlgorithm.
     */
    public RuleExplanationGeneticAlgorithm() {
        super(
                new EvolutionStartInitializer(),
                new BitGeneGenotypeToRuleExplanation(),
                new ScaledEnhancedRMICalculator(
                        0.00001
                ),
                2400
        );
        keepBest = -1;
    }

    /**
     * Constructor for RuleExplanationGeneticAlgorithm.
     * @param counterWeightNumberConditionValues The weight for the {@link ScaledEnhancedRMICalculator}.
     */
    public RuleExplanationGeneticAlgorithm(double counterWeightNumberConditionValues) {
        this(counterWeightNumberConditionValues, -1);
    }

    /**
     * Constructor for RuleExplanationGeneticAlgorithm.
     * @param counterWeightNumberConditionValues The weight for the {@link ScaledEnhancedRMICalculator}.
     * @param keepBest Determines how many of the best-performing instances should be kept. If {@literal <} 0, all are kept.
     */
    public RuleExplanationGeneticAlgorithm(double counterWeightNumberConditionValues,
                                           int keepBest) {
        super(
                new EvolutionStartInitializer(),
                new BitGeneGenotypeToRuleExplanation(),
                new ScaledEnhancedRMICalculator(
                        counterWeightNumberConditionValues
                ),
                2400
        );
        this.keepBest = keepBest;
    }


    /**
     * Most flexible constructor for RuleExplanationGeneticAlgorithm.
     * @param initializer The Initializer used.
     * @param representationTranslator The RepresentationTranslator used.
     * @param objectiveFunction The ObjectiveFunction for RuleExplanations used.
     * @param populationSize The chosen population size.
     * @param generationsUntilConvergenceAssumed The number of generations until fitness can be deemed as converged
     *                                           according to {@link Limits#bySteadyFitness}.
     * @param maximalPhenotypeAge The maximal age of an individual within the genetic algorithm.
     * @param offspringFraction The fraction of the next generation's population which should be offspring.
     * @param crossoverProbability The probability for crossover.
     * @param maxNumberGenerations The maximal number of generations.
     * @param epsilonPopulationConvergence The epsilon for which the population can be deemed as converged according to
     *                                     {@link Limits#byPopulationConvergence(double)}.
     * @param offspringTournamentSampleSize The sample size for the {@link io.jenetics.TournamentSelector}.
     * @param survivorsTournamentSampleSize The sample size for the {@link io.jenetics.LinearRankSelector}.
     * @param keepBest Determines how many of the best-performing instances should be kept. If {@literal <} 0, all are kept.
     */
    public RuleExplanationGeneticAlgorithm(OptimizationInitializer<Genotype<BitGene>[]> initializer,
                                           RepresentationTranslator<Genotype<BitGene>, RuleExplanation, RuleExplanationFactory> representationTranslator,
                                           ObjectiveFunction<RuleExplanation, Double> objectiveFunction,
                                           int populationSize,
                                           int generationsUntilConvergenceAssumed,
                                           int maximalPhenotypeAge,
                                           double offspringFraction,
                                           double crossoverProbability,
                                           int maxNumberGenerations,
                                           double epsilonPopulationConvergence,
                                           int offspringTournamentSampleSize,
                                           int survivorsTournamentSampleSize,
                                           int keepBest) {
        super(
                initializer,
                representationTranslator,
                objectiveFunction,
                populationSize,
                generationsUntilConvergenceAssumed,
                maximalPhenotypeAge,
                offspringFraction,
                crossoverProbability,
                maxNumberGenerations,
                epsilonPopulationConvergence,
                offspringTournamentSampleSize,
                survivorsTournamentSampleSize
        );
        this.keepBest = keepBest;
    }

    protected RuleExplanationSet transformGenotypes(EvolutionResult<BitGene, Double> evolutionResult) {
        Set<RuleExplanation> resultSet = new HashSet<>();
        for (Genotype<BitGene> genotype : evolutionResult.getGenotypes()) {
            resultSet.add(representationTranslator.apply(genotype));
        }
        if (keepBest > 0 && keepBest < resultSet.size()) {
            List<ExplanationMetricAssignation> sortedRules = Utility.sortExplanationsViaMetric(resultSet, objectiveFunction);
            resultSet = new HashSet<>();
            for (int i = 0; i < keepBest; i++) {
                resultSet.add(sortedRules.get(i).getExplanation());
            }
        }
        return resultFactory.newWithCollection(labelFeature, labelValue, resultSet);
    }

    @Override
    protected int calculateRepresentationLength(RuleExplanationSet representationSpaceFoundation) {
        return representationSpaceFoundation.getNumberConditionValues();
    }
}
