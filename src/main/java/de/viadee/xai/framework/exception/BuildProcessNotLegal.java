package de.viadee.xai.framework.exception;

/**
 * RuntimeException to be thrown if an error occurs during the build process of a TabularDataset.
 */
public class BuildProcessNotLegal extends RuntimeException {
    /**
     * Constructor for BuildProcessNotLegal.
     * @param reason The String containing the reason.
     */
    public BuildProcessNotLegal(String reason) {
        super("The build process has been found to be illegal: " + reason);
    }
}
