package de.viadee.xai.framework.exception;

import de.viadee.xai.framework.data.Feature.CategoricalFeature;

import java.io.Serializable;


/**
 * RuntimeException which is thrown if for a CategoricalFeature working representation, no
 * String representation of the Integer working-representation can be found.
 */
public class RepresentationNotFound extends RuntimeException {
    /**
     * Constructor for RepresentationNotFound.
     * @param f The CategoricalFeature for which a certain representation could not be found.
     * @param serializable The value for which no String representation could be found.
     */
    public RepresentationNotFound(CategoricalFeature f, Serializable serializable) {
        super("No representation available for " + serializable.toString() + " in Feature " + f.toString());
    }
}