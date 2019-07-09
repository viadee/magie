package de.viadee.xai.framework.exception;

import de.viadee.xai.framework.data.Feature;

/**
 * Subclass of RuntimeException. To be thrown if during a methods execution no Feature is found
 * which suits the criteria.
 */
public class FeatureNotFound extends RuntimeException {

    /**
     * Constructor for FeatureNotFound.
     * @param feature The feature which could not be found.
     */
    public FeatureNotFound(final Feature feature) {
        this(feature.getName());
    }

    /**
     * Constructor for FeatureNotFound.
     * @param featureName The name of the feature which could not be found.
     */
    public FeatureNotFound(final String featureName) {
        super("Feature with name " + featureName + " not found.");
    }
}
