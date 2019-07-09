package de.viadee.xai.framework.explanation_calculation.calculator;

import de.viadee.xai.framework.data.Feature;
import org.ehcache.Cache;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;


/**
 * Calculator caching the absolute numbers of instances residing within different covers.
 * @param <CC> The type of calculator to delegate the actual computation in case of a cache-miss to.
 */
public class NumberCachedCalculator<CC extends CategoricalCalculator>
        extends EhCachedCategoricalCalculator<Integer, CC> {
    protected final Cache<Map<Feature.CategoricalFeature, Set<Integer>>, Integer> coveredCache;

    // Label-value --> cache
    protected final Map<Integer, Cache<Map<Feature.CategoricalFeature, Set<Integer>>, Integer>> correctlyCoveredCaches;
    protected final Map<Integer, Cache<Map<Feature.CategoricalFeature, Set<Integer>>, Integer>> incorrectlyCoveredCaches;
    protected final Map<Integer, Cache<Map<Feature.CategoricalFeature, Set<Integer>>, Integer>> correctlyNotCoveredCaches;
    protected final Map<Integer, Cache<Map<Feature.CategoricalFeature, Set<Integer>>, Integer>> incorrectlyNotCoveredCaches;

    /**
     * Constructor for NumberCachedCalculator.
     * @param delegateTo The calculator to delegate the actual computation in case of a cache-miss to.
     */
    public NumberCachedCalculator(CC delegateTo) {
        super(delegateTo, new int[] {72000, 72000, 1, 1, 72000}, Integer.class);
        Cache temp = cacheManager.getCache(cacheNames[0], Map.class, Integer.class);
        coveredCache = temp;

        correctlyCoveredCaches = new HashMap<>();
        incorrectlyCoveredCaches = new HashMap<>();
        correctlyNotCoveredCaches = new HashMap<>();
        incorrectlyNotCoveredCaches = new HashMap<>();
        for (Integer i : (delegateTo.getDataset().getProcessedLabelCol().getLabel()).getUniqueNumberRepresentations()) {
            temp = cacheManager.getCache(cacheNames[1]+i, Map.class, Integer.class);
            correctlyCoveredCaches.put(i, temp);
            temp = cacheManager.getCache(cacheNames[2]+i, Map.class, Integer.class);
            incorrectlyCoveredCaches.put(i, temp);
            temp = cacheManager.getCache(cacheNames[3]+i, Map.class, Integer.class);
            correctlyNotCoveredCaches.put(i, temp);
            temp = cacheManager.getCache(cacheNames[4]+i, Map.class, Integer.class);
            incorrectlyNotCoveredCaches.put(i, temp);
        }
    }

    protected Integer cacheHitOrCalculateAndStore(Cache<Map<Feature.CategoricalFeature, Set<Integer>>, Integer> cache,
                                                  Map<Feature.CategoricalFeature, Set<Integer>> conditions,
                                                  Integer labelValue,
                                                  BiFunction<Map<Feature.CategoricalFeature, Set<Integer>>, Integer, Integer> calculatorFunction) {
        Integer result = cache.get(conditions);
        if (result != null) {
            return result;
        } else {
            result = calculatorFunction.apply(conditions, labelValue);
            cache.put(conditions, result);
            return result;
        }
    }

    @Override
    public int getNumberCovered(Feature.CategoricalFeature categoricalFeature, int featureValue) {
        return delegateTo.getNumberCovered(categoricalFeature, featureValue);
    }


    @Override
    public int getNumberCovered(Map<Feature.CategoricalFeature, Set<Integer>> conditions) {
        Integer result = coveredCache.get(conditions);
        if (result != null) {
            return result;
        } else {
            result = delegateTo.getNumberCovered(conditions);
            coveredCache.put(conditions, result);
            return result;
        }
    }

    @Override
    public int getNumberCorrectlyCovered(Map<Feature.CategoricalFeature, Set<Integer>> conditions, int labelValue) {
        return cacheHitOrCalculateAndStore(
                correctlyCoveredCaches.get(labelValue),
                conditions,
                labelValue,
                delegateTo::getNumberCorrectlyCovered
        );
    }

    @Override
    public int getNumberIncorrectlyCovered(Map<Feature.CategoricalFeature, Set<Integer>> conditions, int labelValue) {
        return cacheHitOrCalculateAndStore(
                incorrectlyCoveredCaches.get(labelValue),
                conditions,
                labelValue,
                delegateTo::getNumberIncorrectlyCovered
        );
    }

    @Override
    public int getNumberCorrectlyNotCovered(Map<Feature.CategoricalFeature, Set<Integer>> conditions, int labelValue) {
        return cacheHitOrCalculateAndStore(
                correctlyNotCoveredCaches.get(labelValue),
                conditions,
                labelValue,
                delegateTo::getNumberCorrectlyNotCovered
        );
    }

    @Override
    public int getNumberIncorrectlyNotCovered(Map<Feature.CategoricalFeature, Set<Integer>> conditions, int labelValue) {
        return cacheHitOrCalculateAndStore(
                incorrectlyNotCoveredCaches.get(labelValue),
                conditions,
                labelValue,
                delegateTo::getNumberIncorrectlyNotCovered
        );
    }

    @Override
    public int[] getMinimalAmountNumberInCovers(Map<Feature.CategoricalFeature, Set<Integer>> conditions, int labelValue) {
        return new int[] {
                getNumberCovered(conditions),
                getNumberCorrectlyCovered(conditions, labelValue),
                getNumberIncorrectlyNotCovered(conditions, labelValue)
        };
    }
}
