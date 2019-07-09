package de.viadee.xai.framework.global_explanation_procedure_step.optimizer;

import de.viadee.xai.framework.explanation_calculation.explanation.RuleExplanationSet;
import io.jenetics.*;
import io.jenetics.engine.*;
import io.jenetics.stat.DoubleMomentStatistics;
import io.jenetics.util.Factory;
import io.jenetics.util.ISeq;

import java.util.Arrays;
import java.util.function.Function;

/**
 * An abstract superclass used for genetic algorithms in the jenetics-framework.
 * This abstract superclass can be used to optimize RuleExplanationSets and RuleExplanations.
 * @param <E> The type of Explanation or ExplanationSet to calculate the fitness for.
 *           Should be 'RuleExplanation', or 'RuleExplanationSet'.
 * @param <EF> The type of factory used to create the entities of type &lt;E&gt;.
 */
public abstract class AbstractGeneticAlgorithmOptimizer<
        E,
        EF
        >
        extends Optimizer<
        E,
        EF,
        Genotype<BitGene>,
        Double
        > {
    protected final double epsilonPopulationConvergence;
    protected final int maxNumberGenerations;
    protected final int maximalPhenotypeAge;
    protected final int maximalCreationRetries;
    protected final double offspringFraction;

    protected double mutationProbability;
    protected final double crossoverProbability;
    protected double swapProbability;
    protected Alterer<BitGene, Double> firstAlterer;
    protected Alterer<BitGene, Double>[] otherAlterers;

    protected final int offspringTournamentSampleSize;
    protected final int survivorsTournamentSampleSize;
    protected final Selector<BitGene, Double> offspringSelector;
    protected final Selector<BitGene, Double> survivorSelector;

    protected Factory<Genotype<BitGene>> bitGeneFactory;

    protected final int generationsUntilConvergenceAssumed;

    /**
     * Constructor for AbstractGeneticAlgorithmOptimizer.
     * @param initializer The initializer used to create the initial population with.
     * @param representationTranslator The translator used to map from Genotype&lt;BitGene&gt;
     *                                 to the to-be-optimized type (&lt;E&gt;).
     * @param objectiveFunction The objective function which is used for the optimization procedure.
     * @param populationSize The size of the population.
     * */
    public AbstractGeneticAlgorithmOptimizer(
            OptimizationInitializer<Genotype<BitGene>[]> initializer,
            RepresentationTranslator<Genotype<BitGene>, E, EF> representationTranslator,
            ObjectiveFunction<E, Double> objectiveFunction,
            int populationSize
    ) {
        super(initializer,
                representationTranslator,
                objectiveFunction,
                populationSize
        );
        generationsUntilConvergenceAssumed = 80;
        maximalPhenotypeAge = 70; // Default in jenetics
        maximalCreationRetries = 10;
        offspringFraction = 0.6;
        crossoverProbability = 0.5;
        maxNumberGenerations = 600;

        epsilonPopulationConvergence = 0.001;

        offspringTournamentSampleSize = 120;
        survivorsTournamentSampleSize = 120;
        offspringSelector = new TournamentSelector<>(offspringTournamentSampleSize);
        survivorSelector = new LinearRankSelector<>(survivorsTournamentSampleSize);
    }

    /**
     * Most flexible constructor for AbstractGeneticAlgorithmOptimizer.
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
    public AbstractGeneticAlgorithmOptimizer(OptimizationInitializer<Genotype<BitGene>[]> initializer,
                                             RepresentationTranslator<Genotype<BitGene>, E, EF> representationTranslator,
                                             ObjectiveFunction<E, Double> objectiveFunction,
                                             int populationSize,
                                             int generationsUntilConvergenceAssumed,
                                             int maximalPhenotypeAge,
                                             double offspringFraction,
                                             double crossoverProbability,
                                             int maxNumberGenerations,
                                             double epsilonPopulationConvergence,
                                             int offspringTournamentSampleSize,
                                             int survivorsTournamentSampleSize) {
        super(initializer,
                representationTranslator,
                objectiveFunction,
                populationSize
        );
        this.generationsUntilConvergenceAssumed = generationsUntilConvergenceAssumed;
        this.maximalPhenotypeAge = maximalPhenotypeAge; // Default in jenetics
        maximalCreationRetries = 10;
        this.offspringFraction = offspringFraction;
        this.crossoverProbability = crossoverProbability;
        this.maxNumberGenerations = maxNumberGenerations;

        this.epsilonPopulationConvergence = epsilonPopulationConvergence;

        this.offspringTournamentSampleSize = offspringTournamentSampleSize;
        this.survivorsTournamentSampleSize = survivorsTournamentSampleSize;
        offspringSelector = new TournamentSelector<>(offspringTournamentSampleSize);
        survivorSelector = new LinearRankSelector<>(survivorsTournamentSampleSize);
    }

    @Override
    protected RuleExplanationSet optimize(Genotype<BitGene>[] initialization,
                                          Function<Genotype<BitGene>, Double> representationTranslatorAndCalculator,
                                          ObjectiveFunction<E, Double> objectiveFunction) {
        mutationProbability = ((double) 2) / representationLength;
        swapProbability = (((double) 2) / representationLength);
        bitGeneFactory = Genotype.of(BitChromosome.of(representationLength, (((double) 2) / representationLength)));
        firstAlterer = new UniformCrossover<>(crossoverProbability, swapProbability);
        otherAlterers = new Alterer[1];
        otherAlterers[0] = new Mutator<>(mutationProbability);

        Engine<BitGene, Double> bitGeneEngine =
                Engine.builder(representationTranslatorAndCalculator, bitGeneFactory)
                        .populationSize(populationSize)
                        .individualCreationRetries(maximalCreationRetries)
                        .alterers(firstAlterer, otherAlterers)
                        .maximalPhenotypeAge(maximalPhenotypeAge)
                        .offspringSelector(offspringSelector)
                        .survivorsSelector(survivorSelector)
                        .offspringFraction(offspringFraction)
                        .build();
        EvolutionStatistics<Double, DoubleMomentStatistics> statistics = EvolutionStatistics.ofNumber();

        ISeq<Phenotype<BitGene, Double>> population =
                Arrays.stream(initialization)
                        .map(gt -> Phenotype.of(gt, 0, representationTranslatorAndCalculator))
                        .limit(populationSize)
                        .collect(ISeq.toISeq());

        EvolutionResult<BitGene, Double> results =
                bitGeneEngine
                        .stream(EvolutionStart.of(population, 1))
                        .limit(Limits.bySteadyFitness(generationsUntilConvergenceAssumed))
                        .limit(Limits.byPopulationConvergence(epsilonPopulationConvergence))
                        .limit(maxNumberGenerations)
                        .peek(statistics)
                        .collect(EvolutionResult.toBestEvolutionResult());
        return transformGenotypes(results);
    }

    // Transform the final population to a RuleExplanationSet representing the result of the optimization procedure.
    protected abstract RuleExplanationSet transformGenotypes(EvolutionResult<BitGene, Double> evolutionResult);
}
