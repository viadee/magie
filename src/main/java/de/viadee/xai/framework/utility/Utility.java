package de.viadee.xai.framework.utility;

import de.viadee.xai.framework.data.Feature.CategoricalFeature;
import de.viadee.xai.framework.data.FeatureValue.CategoricalFeatureValue;
import de.viadee.xai.framework.explanation_calculation.explanation.ExplanationMetricAssignation;
import de.viadee.xai.framework.explanation_calculation.explanation.RuleExplanation;
import de.viadee.xai.framework.explanation_calculation.explanation.RuleExplanationFactory;
import de.viadee.xai.framework.explanation_calculation.explanation.RuleExplanationSet;
import org.roaringbitmap.buffer.ImmutableRoaringBitmap;
import org.roaringbitmap.buffer.MutableRoaringBitmap;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Basic utility class.
 */
public class Utility {

    private Utility() {}

    /**
     * Number of threads utilized by the {@link ExecutorService} {@link Utility#FIXED_EXECUTOR}.
     */
    public static final int NUMBER_THREADS = 8;
    /**
     * Fixed {@link ExecutorService}.
     */
    public static final ExecutorService FIXED_EXECUTOR = Executors.newFixedThreadPool(NUMBER_THREADS);
    /**
     * Determines the standard allowed delta for two doubles to be considered equal.
     */
    public static final double MAX_EQUAL_DELTA = 1e-6;

    /**
     * Compares two doubles.
     * @param d1 The first double.
     * @param d2 The second double.
     * @return True, if {@link Utility#compareDoubles(double, double)} returns 0.
     */
    public static boolean doublesEqual(double d1, double d2) {
        return compareDoubles(d1, d2) == 0;
    }

    /**
     * Compares two doubles.
     * @param d1 The first double.
     * @param d2 The second double.
     * @return 1, if the first double is a least MAX_EQUAL_DELTA larger, -1, if the second double is at least
     *          MAX_EQUAL_DELTA larger, 0 else.
     */
    public static int compareDoubles(double d1, double d2) {
        if ((d1 - d2) > MAX_EQUAL_DELTA) {
            return 1;
        } else if ((d2 - d1) > MAX_EQUAL_DELTA) {
            return -1;
        } else {
            return 0;
        }
    }

    /**
     * Generates a mapping of integer to a list of char-arrays. The integer represents the number of '1's in a
     * mapped-to char-array. Proceeds iteratively and first calculates all char-arrays with one '1', then all
     * char-arrays with two '1's, and so on.
     * @param remainingPopulation Determines how many char-arrays to create.
     * @param numberConditions Determines the length of the char arrays.
     * @return The aforementioned map.
     */
    @SuppressWarnings("Duplicates")
    public static Map<Integer, List<boolean[]>> generateBinaryChars(int remainingPopulation, int numberConditions) {
        Map<Integer, List<boolean[]>> result = new HashMap<>();

        for (int i = 0; i < numberConditions; i++) {
            List<boolean[]> subResult = new ArrayList<>();

            if (i == 0) {
                // Initialize
                for (int j = 0; j < numberConditions - i; j++) {
                    boolean[] rule = new boolean[numberConditions];
                    for (int k = 0; k < numberConditions; k++)
                        rule[k] = false;
                    rule[j] = true;
                    subResult.add(rule);
                    remainingPopulation--;
                    if (remainingPopulation < 1) {
                        result.put(i, subResult);
                        return result;
                    }
                }
            } else {
                List<boolean[]> previous = result.get(i - 1);
                for (int j = 0; j < previous.size() - 1; j++) {
                    boolean[] currentPrevious = previous.get(j);
                    int foundLastIndex = -1;
                    for (int indexLastTrue = currentPrevious.length - 1; indexLastTrue >= 0; indexLastTrue--) {
                        if (currentPrevious[indexLastTrue]) {
                            foundLastIndex = indexLastTrue;
                            break;
                        }
                    }
                    foundLastIndex++;
                    for (int k = foundLastIndex; k < numberConditions; k++) {
                        boolean[] newRule = Arrays.copyOf(currentPrevious, currentPrevious.length);
                        newRule[k] = true;
                        subResult.add(newRule);
                        remainingPopulation--;
                        if (remainingPopulation < 1) {
                            result.put(i, subResult);
                            return result;
                        }
                    }
                }
            }
            result.put(i, subResult);
        }
        return result;
    }

    /**
     * Sorts a list of Explanations in descending order. The order is determined by a metric-calculation
     * function.
     * @param explanations The Collection of explanations.
     * @param sortByMetricCalculation The function calculating the metric by which the explanations are sorted.
     * @return A list of explanations sorted by the calculated metric.
     */
    public static List<ExplanationMetricAssignation> sortExplanationsViaMetric(
            Collection<RuleExplanation> explanations,
            Function<RuleExplanation, Double> sortByMetricCalculation) {
        List<ExplanationMetricAssignation> rulesWithMetrics =
                explanations
                        .stream()
                        .map(e -> new ExplanationMetricAssignation(e, sortByMetricCalculation.apply(e)))
                        .collect(Collectors.toList());
        rulesWithMetrics.sort(Collections.reverseOrder()); // Sort in descending order
        return rulesWithMetrics;
    }

    /**
     * Given a collection of RuleExplanations and a RuleExplanationFactory, "adapts" each RuleExplanation
     * according to {@link RuleExplanationFactory#translateWithData(RuleExplanation)}
     * @param ruleExplanations The RuleExplanations to be translated/adapted.
     * @param ruleExplanationFactory The RuleExplanationFactory used for the adaptation-process.
     * @return The Set of adapted RuleExplanations.
     */
    public static Set<RuleExplanation> translateRuleExplanationsWithFactory(
            Collection<RuleExplanation> ruleExplanations,
            RuleExplanationFactory ruleExplanationFactory) {
        Set<RuleExplanation> adaptedRuleExplanations = new HashSet<>();

        for (RuleExplanation ruleExplanation : ruleExplanations) {
            adaptedRuleExplanations.add(ruleExplanationFactory.translateWithData(ruleExplanation));
        }

        return adaptedRuleExplanations;
    }

    /**
     * Given a {@link RuleExplanationSet}, deterministically extracts an array containing all conditions, represented
     * as {@link CategoricalFeatureValue}s. This is, e.g., used in {@link de.viadee.xai.framework.global_explanation_procedure_step.optimizer.Optimizer}-
     * classes to get the working representation for {@link de.viadee.xai.framework.global_explanation_procedure_step.optimizer.OptimizationInitializer}-
     * and {@link de.viadee.xai.framework.global_explanation_procedure_step.optimizer.RepresentationTranslator}-instances.
     * @param conditionFoundation The {@link RuleExplanationSet} from which the conditions are extracted.
     * @return An array containing a {@link CategoricalFeatureValue} for each condition.
     */
    public static CategoricalFeatureValue[] transformConditionsMapToArray(RuleExplanationSet conditionFoundation) {
        int numberConditions = conditionFoundation.getNumberConditionValues();
        CategoricalFeatureValue[] orderedConditionFeatureValues = new CategoricalFeatureValue[numberConditions];
        int count = 0;
        Map<CategoricalFeature, Set<Integer>> conditionsToValues = conditionFoundation.getConditions();
        for (Map.Entry<CategoricalFeature, Set<Integer>> entry : conditionsToValues.entrySet()) {
            for (Integer i: entry.getValue()) {
                orderedConditionFeatureValues[count] = new CategoricalFeatureValue(entry.getKey(), i);
                count++;
            }
        }
        return orderedConditionFeatureValues;
    }

    /**
     * Transforms a set of Integers into an {@link ImmutableRoaringBitmap}.
     * @param toTransform The to-be-transformed set.
     * @return The {@link ImmutableRoaringBitmap}.
     */
    public static ImmutableRoaringBitmap transformSetToBitmap(Set<Integer> toTransform) {
        MutableRoaringBitmap result = new MutableRoaringBitmap();
        for (Integer i : toTransform) {
            result.add(i);
        }
        return result;
    }

    /**
     * Transforms an array of {@link ImmutableRoaringBitmap}s into an array of Integer-sets.
     * @param toTransform The {@link ImmutableRoaringBitmap}s.
     * @return The Integer-sets.
     */
    public static Set<Integer>[] transformBitmapsToSets(ImmutableRoaringBitmap[] toTransform) {
        Set<Integer>[] result = new HashSet[toTransform.length];
        for (int i = 0; i < toTransform.length; i++) {
            result[i] = transformBitmapToSet(toTransform[i]);
        }
        return result;
    }

    /**
     * Transforms an {@link ImmutableRoaringBitmap} into a set of integers.
     * @param toTransform The {@link ImmutableRoaringBitmap}.
     * @return The Integer-set.
     */
    public static Set<Integer> transformBitmapToSet(ImmutableRoaringBitmap toTransform) {
        Set<Integer> result = new HashSet<>(toTransform.getCardinality());
        for (Integer i : toTransform) {
            result.add(i);
        }
        return result;
    }

    /**
     * Rounds a double to the given precision.
     * @param d The double.
     * @param precision The given precision.
     * @return The rounded double.
     */
    public static double roundDouble(double d, int precision) {
        double precisionScaling = Math.pow(10, precision);
        return Math.round(d * precisionScaling) / precisionScaling;
    }
}
