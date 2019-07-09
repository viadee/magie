package de.viadee.xai.framework.exception;

import de.viadee.xai.framework.data.Feature;

/**
 * Subclass of RuntimeException. To be thrown, if a type of feature is used in a context where it must
 * not be used. For example, a method might only use CategoricalFeatures, yet, also NumericFeatures are passed.
 */
public class FeatureNotLegal extends RuntimeException {

    /**
     * Constructor for FeatureNotLegal.
     * @param featureType The class of the feature which is not allowed.
     */
    public FeatureNotLegal(final Class<? extends Feature> featureType) {
        super("Features of the type " + featureType.getName() + " are not allowed here.");
    }

    /**
     * Constructor for FeatureNotLegal.
     * @param feature The specific feature which must not be used.
     */
    public FeatureNotLegal(final Feature feature) {
        super("Feature " + feature.getName() + " of type " + feature.getClass().getName() + " is not legal here.");
    }
}
