package de.viadee.xai.framework.exception;

import de.viadee.xai.framework.data.Feature.CategoricalFeature;

/**
 * RuntimeException which is thrown if a RuleExplanation is added to a RuleExplanationSet
 * but, e.g., has a different label than the RuleExplanationSet.
 */
public class RuleJoinNotLegal extends RuntimeException {

    /**
     * Constructor for RuleJoinNotLegal
     * @param expectedFeature The feature which should be predicted by the RuleExplanation.
     * @param expectedLabel The feature value which should be predicted by the RuleExplanation.
     * @param actualFeature The actual feature which is predicted by the RuleExplanation.
     * @param actualLabel The actual feature value which is predicted by the RuleExplanation.
     */
    public RuleJoinNotLegal(final CategoricalFeature expectedFeature,
                            final int expectedLabel,
                            final CategoricalFeature actualFeature,
                            final int actualLabel) {
        super("While joing rules into a set, " + expectedFeature.getName() + " with the label " + expectedLabel + " was expected, " +
                "however, the feature " + actualFeature.getName() + " with the label " + actualLabel + " was found.");
    }
}
