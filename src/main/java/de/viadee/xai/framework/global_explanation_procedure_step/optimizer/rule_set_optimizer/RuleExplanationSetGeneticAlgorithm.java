package de.viadee.xai.framework.global_explanation_procedure_step.optimizer.rule_set_optimizer;

import de.viadee.xai.framework.explanation_calculation.explanation.RuleExplanationSet;
import de.viadee.xai.framework.explanation_calculation.explanation.RuleExplanationSetFactory;
import de.viadee.xai.framework.global_explanation_procedure_step.optimizer.AbstractGeneticAlgorithmOptimizer;
import de.viadee.xai.framework.global_explanation_procedure_step.optimizer.OptimizationInitializer;
import de.viadee.xai.framework.global_explanation_procedure_step.optimizer.ObjectiveFunction;
import de.viadee.xai.framework.global_explanation_procedure_step.optimizer.RepresentationTranslator;
import de.viadee.xai.framework.global_explanation_procedure_step.optimizer.objective_function.BETAObjectiveCalculator;
import de.viadee.xai.framework.global_explanation_procedure_step.optimizer.optimization_initializer.EvolutionStartInitializer;
import de.viadee.xai.framework.global_explanation_procedure_step.optimizer.representation_translator.BitGeneGenotypeToRuleExplanationSet;
import io.jenetics.BitGene;
import io.jenetics.Genotype;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.engine.Limits;

public class RuleExplanationSetGeneticAlgorithm
        extends AbstractGeneticAlgorithmOptimizer<
        RuleExplanationSet,
        RuleExplanationSetFactory
        > {

    public RuleExplanationSetGeneticAlgorithm() {
        this(new double[] {1, 1, 1, 1});
    }

    public RuleExplanationSetGeneticAlgorithm(double[] weights) {
        super(
                new EvolutionStartInitializer(),
                new BitGeneGenotypeToRuleExplanationSet(),
                new BETAObjectiveCalculator(weights),
                400
        );
    }

    /**
     * Most flexible constructor for RuleExplanationSetGeneticAlgorithm.
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
     */
    public RuleExplanationSetGeneticAlgorithm(OptimizationInitializer<Genotype<BitGene>[]> initializer,
                                              RepresentationTranslator<Genotype<BitGene>, RuleExplanationSet, RuleExplanationSetFactory> representationTranslator,
                                              ObjectiveFunction<RuleExplanationSet, Double> objectiveFunction,
                                              int populationSize,
                                              int generationsUntilConvergenceAssumed,
                                              int maximalPhenotypeAge,
                                              double offspringFraction,
                                              double crossoverProbability,
                                              int maxNumberGenerations,
                                              double epsilonPopulationConvergence,
                                              int offspringTournamentSampleSize,
                                              int survivorsTournamentSampleSize) {
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
    }

    @Override
    protected RuleExplanationSet transformGenotypes(EvolutionResult<BitGene, Double> evolutionResult) {
        return representationTranslator.apply(evolutionResult.getBestPhenotype().getGenotype());
    }

    @Override
    protected int calculateRepresentationLength(RuleExplanationSet representationSpaceFoundation) {
        return representationSpaceFoundation.getNumberExplanations();
    }
}
