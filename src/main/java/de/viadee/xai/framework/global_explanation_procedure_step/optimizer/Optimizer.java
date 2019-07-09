package de.viadee.xai.framework.global_explanation_procedure_step.optimizer;

import de.viadee.xai.framework.data.Feature.CategoricalFeature;
import de.viadee.xai.framework.explanation_calculation.explanation.RuleExplanationSet;
import de.viadee.xai.framework.explanation_calculation.explanation.RuleExplanationSetFactory;
import de.viadee.xai.framework.global_explanation_procedure_step.ExplanationProcedureStep;

import java.util.function.Function;

/**
 * Interface for an optimizer. An optimizer contains the means to create an initial population,
 * conduct an optimization procedure, and translate the working-representation of the optimization
 * procedure into an Explanation or ExplanationSet. During the optimization procedure, an optimization
 * function is used. In this framework, the output always is an ExplanationSet.
 * @param <E> Type of Explanation or ExplanationSet for which to calculate the fitness.
 * @param <EF> The type of Factory used for the transformation of working-representation into domain entity.
 *            For example, a RuleExplanationFactory or a RuleExplanationSetFactory can be used to generate the domain
 *            entities RuleExplanation or RuleExplanationSet.
 * @param <ORep> Type of working-representation used by the optimization algorithm.
 * @param <FO> Type of output of the fitness function.
 */
public abstract class Optimizer<
        E,
        EF,
        ORep,
        FO extends Comparable<FO>
        > implements ExplanationProcedureStep<EF> {

    protected final OptimizationInitializer<ORep[]> initializer;
    protected final RepresentationTranslator<ORep, E, EF> representationTranslator;
    protected EF translationFactory;

    protected final ObjectiveFunction<E, FO> objectiveFunction;

    protected int populationSize;
    protected int representationLength;

    protected CategoricalFeature labelFeature;
    protected int labelValue;

    protected RuleExplanationSetFactory resultFactory;

    /**
     * Constructor for Optimizer.
     * @param initializer The initializer used to create the initial population.
     * @param representationTranslator The translator used to map optimization working-representations
     *                                 to an Explanation or an ExplanationSet.
     * @param objectiveFunction The objective function by means of which the generated Explanations are compared
     *                          during the optimization process.
     * @param populationSize The size of the population used by the contained optimization procedure.
     */
    public Optimizer(OptimizationInitializer<ORep[]> initializer,
                     RepresentationTranslator<ORep, E, EF> representationTranslator,
                     ObjectiveFunction<E, FO> objectiveFunction,
                     int populationSize) {
        this.initializer = initializer;
        this.representationTranslator = representationTranslator;
        this.objectiveFunction = objectiveFunction;
        this.populationSize = populationSize;
    }

    protected FO translateAndCalculateFitness(ORep workingRepresentation) {
        return objectiveFunction.apply(representationTranslator.apply(workingRepresentation));
    }

    /**
     * Conducts the optimization procedure.
     * @param representationSpaceFoundation The {@link RuleExplanationSet} which should be optimized in some way.
     * @return The resulting ExplanationSet.
     */
    public final RuleExplanationSet optimize(RuleExplanationSet representationSpaceFoundation) {
        this.populationSize = calculatePopulationSize(representationSpaceFoundation);
        this.representationLength = calculateRepresentationLength(representationSpaceFoundation);
        initializer.initialize(representationSpaceFoundation);
        representationTranslator.initialize(representationSpaceFoundation, translationFactory);
        objectiveFunction.initialize(representationSpaceFoundation);
        this.labelFeature = representationSpaceFoundation.getLabelFeature();
        this.labelValue = representationSpaceFoundation.getLabelValue();
        return optimize(
                initializer.apply(populationSize, representationLength),
                this::translateAndCalculateFitness,
                objectiveFunction
        );
    }

    /**
     * Helper function. Has to be overridden using the given parameters to create
     * the actual optimization process.
     * @param initialization The initializer used to create the initial population.
     * @param representationTranslatorAndCalculator The translator used to map optimization working-representations
     *                                 to an Explanation or an ExplanationSet.
     * @param objectiveFunction The objective function by means of which the generated Explanations are compared
     *                          during the optimization process.
     * @return The RuleExplanationSet containing the result of the optimization procedure.
     */
    protected abstract RuleExplanationSet optimize(ORep[] initialization,
                                                   Function<ORep, FO> representationTranslatorAndCalculator,
                                                   ObjectiveFunction<E, FO> objectiveFunction);


    protected abstract int calculateRepresentationLength(RuleExplanationSet representationSpaceFoundation);

    protected int calculatePopulationSize(RuleExplanationSet representationSpaceFoundation) {
        return populationSize;
    }

    @Override
    public void initialize(EF translationFactory, RuleExplanationSetFactory resultFactory) {
        this.translationFactory = translationFactory;
        this.resultFactory = resultFactory;
    }
}
