package de.viadee.xai.framework.explanation_visualizer;

import de.viadee.xai.framework.data.Feature;
import de.viadee.xai.framework.explanation_calculation.explanation.ExplanationMetricAssignation;
import de.viadee.xai.framework.explanation_calculation.explanation.RuleExplanation;
import de.viadee.xai.framework.utility.RuleMetricCalculation;
import de.viadee.xai.framework.utility.Utility;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static de.viadee.xai.framework.utility.RuleMetricCalculation.*;
import static de.viadee.xai.framework.utility.Utility.roundDouble;

/**
 * ExplanationVisualizer returning a String and several metrics for each individual {@link RuleExplanation}.
 */
public class StringRuleExplanationVisualizer implements ExplanationVisualizer<String> {

    protected final int roundPrecision;
    protected final Function<RuleExplanation, Double> sortBy;

    /**
     * Default constructor for StringRuleExplanationVisualizer.
     */
    public StringRuleExplanationVisualizer() {
        this(4);
    }

    /**
     * Constructor for StringRuleExplanationVisualizer.
     * @param roundPrecision Which precision should be used to display the metrics calculated for the {@link RuleExplanation}s?
     */
    public StringRuleExplanationVisualizer(int roundPrecision) {
        this.roundPrecision = roundPrecision;
        this.sortBy = RuleMetricCalculation::calculateRMI;
    }

    @Override
    public String visualize(List<Map<Integer, Set<RuleExplanation>>> toVisualize) {
        StringBuilder resultString = new StringBuilder();
        int count = 0;
        for (Map<Integer, Set<RuleExplanation>> currentListElement : toVisualize) {
            resultString.append("Rule structure depth: ").append(count).append("\n");
            for (Map.Entry<Integer, Set<RuleExplanation>> currentMapEntry : currentListElement.entrySet()) {
                // Sort by RMI
                List<ExplanationMetricAssignation> sortedRuleExplanations =
                        Utility.sortExplanationsViaMetric(currentMapEntry.getValue(), sortBy);
                resultString.append("\tRule set with ID: ")
                        .append(currentMapEntry.getKey())
                        .append(", size: ")
                        .append(currentMapEntry.getValue().size())
                        .append("\n");
                for (ExplanationMetricAssignation explanationMetricAssignation : sortedRuleExplanations) {
                    RuleExplanation currentExplanation = explanationMetricAssignation.getExplanation();
                    resultString.append("\t\tRuleExplanation: ").append("\n");
                    resultString.append("\t\t\tIF ");
                    boolean first = true;
                    for (Map.Entry<Feature.CategoricalFeature, Set<Integer>> conditionsForFeature :
                            currentExplanation.getConditions().entrySet()) {
                        if (!first) {
                            resultString.append("\t\t\t");
                            resultString.append("AND ");
                        }
                        resultString.append(conditionsForFeature.getKey().getName())
                                .append(" IN {")
                                .append(conditionsForFeature
                                        .getValue()
                                        .stream()
                                        .map(value -> conditionsForFeature
                                                .getKey()
                                                .getStringRepresentation(value))
                                        .collect(Collectors.joining(", ")))
                                .append("},\n");
                        first = false;
                    }
                    resultString.append("\t\t\tTHEN ")
                            .append(currentExplanation.getLabelFeature().getName())
                            .append(" IS ")
                            .append(currentExplanation
                                    .getLabelFeature()
                                    .getStringRepresentation(currentExplanation.getLabelValue())
                            )
                            .append("\n\t\t\tCoverage:  ")
                            .append(roundDouble(currentExplanation.getCoverage(), roundPrecision))
                            .append("\n\t\t\tRecall: ")
                            .append(roundDouble(calculateRecall(currentExplanation), roundPrecision))
                            .append("\n\t\t\tAccuracy: ")
                            .append(roundDouble(calculateAccuracy(currentExplanation), roundPrecision))
                            .append("\n\t\t\tPrecision: ")
                            .append(roundDouble(currentExplanation.getPrecision(), roundPrecision))
                            .append("\n\t\t\tF1-Score: ")
                            .append(roundDouble(calculateF1(currentExplanation), roundPrecision))
                            .append("\n\t\t\tMCC: ")
                            .append(roundDouble(calculateMatthewsCorrelationCoefficient(currentExplanation), roundPrecision))
                            .append("\n\t\t\tNormalized RMI: ")
                            .append(roundDouble(calculateNormalizedRMI(currentExplanation), roundPrecision));
                    resultString.append("\n\t\t_________________________________________________________\n");
                }
                resultString.append("\t################################################################\n");
            }
        }
        return resultString.toString();
    }
}
