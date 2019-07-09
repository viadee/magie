package de.viadee.xai.framework.utility;

import de.viadee.xai.framework.data.Feature;
import de.viadee.xai.framework.explanation_calculation.explanation.RuleExplanation;
import org.roaringbitmap.buffer.ImmutableRoaringBitmap;

/**
 * Utility class for calculating different metrics for rules.
 */
public class RuleMetricCalculation {

    private RuleMetricCalculation() {}

    /**
     * Calculates the jaccard similarity.
     * @param re1 The first {@link RuleExplanation}.
     * @param re2 The second {@link RuleExplanation}.
     * @return The jaccard similarity in [0;1].
     */
    public static double calculateJaccardSimilarity(RuleExplanation re1, RuleExplanation re2) {
        return calculateJaccardSimilarity(re1.getCoverAsBitmap(), re2.getCoverAsBitmap());
    }

    /**
     * Calculates the jaccard dissimilarity.
     * @param re1 The first {@link RuleExplanation}.
     * @param re2 The second {@link RuleExplanation}.
     * @return The jaccard dissimilarity in [0;1].
     */
    public static double calculateJaccardDissimilarity(RuleExplanation re1, RuleExplanation re2) {
        return calculateJaccardDissimilarity(re1.getCoverAsBitmap(), re2.getCoverAsBitmap());
    }

    /**
     * Calculates the Jaccard similarity of two sets represented by bitmaps.
     * @param bm1 The first bitmap.
     * @param bm2 The second bitmap.
     * @return A double in [0;1] representing the similarity of both sets.
     */
    public static double calculateJaccardSimilarity(ImmutableRoaringBitmap bm1, ImmutableRoaringBitmap bm2) {
        ImmutableRoaringBitmap intersection = ImmutableRoaringBitmap.and(bm1, bm2);
        return ((double) intersection.getCardinality()) /
                (bm1.getCardinality() + bm2.getCardinality() - intersection.getCardinality());
    }

    /**
     * Calculates the Jaccard distance of two sets represented by bitmaps.
     * @param bm1 The first set.
     * @param bm2 The second set.
     * @return A double in [0;1] representing the distance of both sets.
     */
    public static double calculateJaccardDissimilarity(ImmutableRoaringBitmap bm1, ImmutableRoaringBitmap bm2) {
        return 1 - calculateJaccardSimilarity(bm1, bm2);
    }


    /**
     * Calculates the rule mutual information as is done in MAGIX.
     * Puri, N., Gupta, P., Agarwal, P., Verma, S., {@literal &} Krishnamurthy, B. (2017).
     * MAGIX: Model Agnostic Globally Interpretable Explanations. arXiv preprint arXiv:1706.07160.
     * @param ruleExplanation The RuleExplanation for which to evaluate the cover confusion table.
     * @return A double representing the rule mutual information.
     */
    public static double calculateRMI(RuleExplanation ruleExplanation) {
        return calculateRMI(ruleExplanation.getNumberCorrectlyCovered(),
                ruleExplanation.getNumberIncorrectlyCovered(),
                ruleExplanation.getNumberIncorrectlyNotCovered(),
                ruleExplanation.getNumberCorrectlyNotCovered());
    }

    /**
     * Given four cells, calculates the rule mutual information as is done in MAGIX.
     * Puri, N., Gupta, P., Agarwal, P., Verma, S., {@literal &} Krishnamurthy, B. (2017).
     * MAGIX: Model Agnostic Globally Interpretable Explanations. arXiv preprint arXiv:1706.07160.
     * @param x11 cell_11 Number correctly classified
     * @param x12 cell_12 Number incorrectly classified
     * @param x21 cell_11 Number incorrectly not classified
     * @param x22 cell_22 Number correctly not classified
     * @return A double representing the rule mutual information.
     */
    public static double calculateRMI(int x11, int x12, int x21, int x22) {
        int row1 = x11 + x12;
        int row2 = x21 + x22;
        int col1 = x11 + x21;
        int col2 = x12 + x22;
        int total = x11 + x12 + x21 + x22;
        double mi11 = calculateMICell(x11, total, row1, col1);
        double mi12 = calculateMICell(x12, total, row1, col2);
        double mi21 = calculateMICell(x21, total, row2, col1);
        double mi22 = calculateMICell(x22, total, row2, col2);
        double mutualInformation = (mi11 + mi12 + mi21 + mi22);
        if (Double.isNaN(mutualInformation)) {
            return Double.NEGATIVE_INFINITY;
        }

        double comparisonRightSide;
        boolean comparisonEvaluation;
        if (x22 != 0) {
            comparisonRightSide = (((double) x12) / x22) * x21;
            comparisonEvaluation = x11 >= comparisonRightSide;
        } else {
            comparisonEvaluation = !((x12 > 0) && (x21 > 0));
        }

        if (comparisonEvaluation) {
            return mutualInformation;
        } else {
            return -mutualInformation;
        }
    }

    /**
     * Other approach to calculate the RMI. In the case that TP < the expected frequency,
     * the result is negated.
     * @param ruleExplanation The RuleExplanation for which to evaluate the cover confusion table.
     * @return A double representing the rule mutual information.
     */
    public static double calculateRMI2(RuleExplanation ruleExplanation) {
        return calculateRMI2(ruleExplanation.getNumberCorrectlyCovered(), ruleExplanation.getNumberIncorrectlyCovered(),
                ruleExplanation.getNumberIncorrectlyNotCovered(), ruleExplanation.getNumberCorrectlyNotCovered());
    }

    /**
     * Other approach to calculate the RMI. In the case that TP < the expected frequency,
     * the result is negated.
     * @param x11 cell_11 Number correctly classified
     * @param x12 cell_12 Number incorrectly classified
     * @param x21 cell_11 Number incorrectly not classified
     * @param x22 cell_22 Number correctly not classified
     * @return A double representing the rule mutual information.
     */
    public static double calculateRMI2(int x11, int x12, int x21, int x22) {
        // TODO Join with calculateRMI
        int row1 = x11 + x12;
        int row2 = x21 + x22;
        int col1 = x11 + x21;
        int col2 = x12 + x22;
        int total = x11 + x12 + x21 + x22;
        double mi11 = calculateMICell(x11, total, row1, col1);
        double mi12 = calculateMICell(x12, total, row1, col2);
        double mi21 = calculateMICell(x21, total, row2, col1);
        double mi22 = calculateMICell(x22, total, row2, col2);
        double mutualInformation = (mi11 + mi12 + mi21 + mi22);
        if (Double.isNaN(mutualInformation)) {
            return Double.NEGATIVE_INFINITY;
        }

        boolean comparison = x11 >= ((((double) row1) / total) * col1);

        if (comparison) {
            return mutualInformation;
        } else {
            return -mutualInformation;
        }
    }

    // Calculates the rule mutual information for one cell.
    private static double calculateMICell(int cell, int total, int row, int col) {
        if (cell == 0) {
            return 0;
        }
        return (cell*Math.log(((double) cell * total) / ((double) row * col)));
    }

    /**
     * Calculates the recall.
     * @param ruleExplanation The {@link RuleExplanation}.
     * @return The recall in [0;1].
     */
    public static double calculateRecall(RuleExplanation ruleExplanation) {
        double tp = ruleExplanation.getNumberCorrectlyCovered();
        double relevant = tp + ruleExplanation.getNumberIncorrectlyNotCovered();

        return tp / relevant;
    }

    /**
     * Calculates the F1-score.
     * @param ruleExplanation The {@link RuleExplanation}.
     * @return The F1-score in [0;1].
     */
    public static double calculateF1(RuleExplanation ruleExplanation) {
        double precision = ruleExplanation.getPrecision();
        double recall = calculateRecall(ruleExplanation);
        return 2 * ((precision*recall) / (precision+recall));
    }

    /**
     * Calculates the accuracy.
     * @param ruleExplanation The {@link RuleExplanation}.
     * @return The accuracy in [0;1].
     */
    public static double calculateAccuracy(RuleExplanation ruleExplanation) {
        double tp = ruleExplanation.getNumberCorrectlyCovered();
        double fp = ruleExplanation.getNumberIncorrectlyCovered();
        double tn = ruleExplanation.getNumberCorrectlyNotCovered();
        double fn = ruleExplanation.getNumberIncorrectlyNotCovered();

        return (tp+tn) / (tp+tn+fp+fn);
    }

    /**
     * Calculates Matthew's Correlation Coefficient (MCC).
     * @param ruleExplanation The {@link RuleExplanation}.
     * @return The MCC in [0;1].
     */
    public static double calculateMatthewsCorrelationCoefficient(RuleExplanation ruleExplanation) {
        double tp = ruleExplanation.getNumberCorrectlyCovered();
        double fp = ruleExplanation.getNumberIncorrectlyCovered();
        double tn = ruleExplanation.getNumberCorrectlyNotCovered();
        double fn = ruleExplanation.getNumberIncorrectlyNotCovered();

        return (((tp*tn) - (fp*fn)) / Math.sqrt((tp+fp) * (tp+fn) * (tn+fp) * (tn+fn)));
    }

    /**
     * Calculates rule mutual information like {@link RuleMetricCalculation#calculateRMI(RuleExplanation)},
     * furthermore normalizes the RMI utilizing a perfect partitioning of the confusion matrix.
     * @param ruleExplanation The {@link RuleExplanation}.
     * @return The normalized RMI in [-1;1].
     */
    public static double calculateNormalizedRMI(RuleExplanation ruleExplanation) {
        Feature.CategoricalFeature labelFeature = ruleExplanation.getLabelFeature();
        int labelValue = ruleExplanation.getLabelValue();
        int labelCoverSize = ruleExplanation.getCalculator().getNumberCovered(labelFeature, labelValue);
        double bestPossibleRMI = calculateRMI(
                labelCoverSize,
                0,
                0,
                ruleExplanation.getCalculator().getDataset().getNumberRows() - labelCoverSize
        );

        double achievedRMI = calculateRMI(ruleExplanation);

        return (achievedRMI / bestPossibleRMI);
    }
}
