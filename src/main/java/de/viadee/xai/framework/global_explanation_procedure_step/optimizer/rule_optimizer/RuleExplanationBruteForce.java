package de.viadee.xai.framework.global_explanation_procedure_step.optimizer.rule_optimizer;

import de.viadee.xai.framework.explanation_calculation.explanation.ExplanationMetricAssignation;
import de.viadee.xai.framework.explanation_calculation.explanation.RuleExplanation;
import de.viadee.xai.framework.explanation_calculation.explanation.RuleExplanationFactory;
import de.viadee.xai.framework.explanation_calculation.explanation.RuleExplanationSet;
import de.viadee.xai.framework.global_explanation_procedure_step.optimizer.AbstractBruteForceOptimizer;
import de.viadee.xai.framework.global_explanation_procedure_step.optimizer.ObjectiveFunction;
import de.viadee.xai.framework.global_explanation_procedure_step.optimizer.objective_function.ScaledEnhancedRMICalculator;
import de.viadee.xai.framework.global_explanation_procedure_step.optimizer.optimization_initializer.BinaryAscendingInitializer;
import de.viadee.xai.framework.global_explanation_procedure_step.optimizer.representation_translator.BinaryRepresentationToRuleExplanation;
import de.viadee.xai.framework.utility.Utility;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Rule optimization-algorithm primitively evaluating every possible {@link RuleExplanation} containing up to a certain
 * number of conditions.
 */
public class RuleExplanationBruteForce
        extends AbstractBruteForceOptimizer<
                        RuleExplanation,
                        RuleExplanationFactory
                        > {

    /**
     * Constructor for RuleExplanationBruteForce.
     * @param numberConditions The maximum number of allowed conditions.
     */
    public RuleExplanationBruteForce(int numberConditions) {
        super(
                new BinaryAscendingInitializer(),
                new BinaryRepresentationToRuleExplanation(),
                new ScaledEnhancedRMICalculator(0.0005),
                numberConditions,
                200
        );
    }

    @Override
    protected RuleExplanationSet optimize(boolean[][] initialization,
                                          Function<boolean[], Double> representationTranslatorAndCalculator,
                                          ObjectiveFunction<RuleExplanation, Double> objectiveFunction) {
        Set<RuleExplanation> ruleExplanations = new HashSet<>();
        for (boolean[] init : initialization) {
            // We will not use representationTranslatorAndCalculator for now, as the RuleExplanations are to be
            // stored directly.
            ruleExplanations.add(representationTranslator.apply(init));
        }
        List<ExplanationMetricAssignation> explanationMetricAssignations =
                Utility.sortExplanationsViaMetric(
                        ruleExplanations,
                        objectiveFunction);

        return resultFactory
                .newWithCollection(
                        labelFeature,
                        labelValue,
                        explanationMetricAssignations.stream()
                                .limit(keepBest)
                                .collect(Collectors.toSet())
                                .stream()
                                .map(ExplanationMetricAssignation::getExplanation)
                                .collect(Collectors.toSet())
                );
    }

    @Override
    protected int calculateRepresentationLength(RuleExplanationSet representationSpaceFoundation) {
        return representationSpaceFoundation.getNumberConditionValues();
    }
}
