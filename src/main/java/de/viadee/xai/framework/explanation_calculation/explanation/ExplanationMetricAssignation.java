package de.viadee.xai.framework.explanation_calculation.explanation;

import de.viadee.xai.framework.utility.Utility;

/**
 * Wrapper for {@link RuleExplanation}. A metric can be calculated for the {@link RuleExplanation} which then can be
 * used to compare and sort multiple {@link RuleExplanation}s.
 */
public class ExplanationMetricAssignation implements Comparable<ExplanationMetricAssignation> {
    protected RuleExplanation explanation;
    protected double metricValue;

    /**
     * Constructor for ExplanationMetricAssignation.
     * @param explanation The {@link RuleExplanation} contained within this wrapper-object.
     * @param metricValue The metric value used for comparing the {@link RuleExplanation}s.
     */
    public ExplanationMetricAssignation(final RuleExplanation explanation,
                                        final double metricValue) {
        this.explanation = explanation;
        this.metricValue = metricValue;
    }

    /**
     * Returns the calculated metric.
     * @return The metric value.
     */
    public double getMetricValue() {
        return metricValue;
    }

    /**
     * Returns the {@link RuleExplanation} for which the object was created.
     * @return The corresponding rule.
     */
    public RuleExplanation getExplanation() {
        return explanation;
    }

    @Override
    public int compareTo(final ExplanationMetricAssignation otherEMA) {
        return Utility.compareDoubles(metricValue, otherEMA.metricValue);
    }

    @Override
    public int hashCode() {
        return explanation.hashCode();
    }

    @Override
    public boolean equals(final Object object) {
        if (!(object instanceof ExplanationMetricAssignation)) {
            return false;
        } else {
            return explanation.equals(object) &&
                    (Utility.compareDoubles(metricValue, ((ExplanationMetricAssignation) object).metricValue) == 0);
        }
    }

    @Override
    public String toString() {
        return "ExplanationMetricAssignation{" +
                "explanation=" + explanation +
                "metricValue=" + metricValue;
    }
}
