package de.viadee.xai.framework.explanation_calculation.calculator;

import de.viadee.xai.framework.data.Feature.CategoricalFeature;
import org.ehcache.Cache;
import org.roaringbitmap.buffer.ImmutableRoaringBitmap;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;

/**
 * Calculator caching the covers directly.
 */
public class CoverCachedCalculator
        extends EhCachedCategoricalCalculator<ImmutableRoaringBitmap, RoaringBitmapCalculator> implements RoaringBitmapCalculator {
    protected final Cache<Map<CategoricalFeature, Set<Integer>>, ImmutableRoaringBitmap> coveredCache;

    // Label-value --> cache
    protected final Map<Integer, Cache<Map<CategoricalFeature, Set<Integer>>, ImmutableRoaringBitmap>> correctlyCoveredCaches;
    protected final Map<Integer, Cache<Map<CategoricalFeature, Set<Integer>>, ImmutableRoaringBitmap>> incorrectlyCoveredCaches;
    protected final Map<Integer, Cache<Map<CategoricalFeature, Set<Integer>>, ImmutableRoaringBitmap>> correctlyNotCoveredCaches;
    protected final Map<Integer, Cache<Map<CategoricalFeature, Set<Integer>>, ImmutableRoaringBitmap>> incorrectlyNotCoveredCaches;

    /**
     * Constructor for the CoverCachedCalculator.
     * @param calculator The calculator to which the computation is delegated in case of a cache-miss.
     */
    public CoverCachedCalculator(RoaringBitmapCalculator calculator) {
        super(
                calculator,
                ImmutableRoaringBitmap.class
        );
        Cache temp = cacheManager.getCache(cacheNames[0], Map.class, ImmutableRoaringBitmap.class);
        coveredCache = temp;

        correctlyCoveredCaches = new HashMap<>();
        incorrectlyCoveredCaches = new HashMap<>();
        correctlyNotCoveredCaches = new HashMap<>();
        incorrectlyNotCoveredCaches = new HashMap<>();
        for (Integer i : (calculator.getDataset().getProcessedLabelCol().getLabel()).getUniqueNumberRepresentations()) {
            temp = cacheManager.getCache(cacheNames[1]+i, Map.class, ImmutableRoaringBitmap.class);
            correctlyCoveredCaches.put(i, temp);
            temp = cacheManager.getCache(cacheNames[2]+i, Map.class, ImmutableRoaringBitmap.class);
            incorrectlyCoveredCaches.put(i, temp);
            temp = cacheManager.getCache(cacheNames[3]+i, Map.class, ImmutableRoaringBitmap.class);
            correctlyNotCoveredCaches.put(i, temp);
            temp = cacheManager.getCache(cacheNames[4]+i, Map.class, ImmutableRoaringBitmap.class);
            incorrectlyNotCoveredCaches.put(i, temp);
        }
    }

    @Override
    public ImmutableRoaringBitmap getCoveredAsBitmap(CategoricalFeature categoricalFeature, Set<Integer> featureValues) {
        return delegateTo.getCoveredAsBitmap(categoricalFeature, featureValues);
    }

    @Override
    public ImmutableRoaringBitmap getCoveredAsBitmap(CategoricalFeature categoricalFeature, Integer featureValue) {
        return delegateTo.getCoveredAsBitmap(categoricalFeature, featureValue);
    }

    // Delegates to the calculator if the queried cover is not within the cache.
    protected ImmutableRoaringBitmap cacheHitOrCalculateAndStore(Cache<Map<CategoricalFeature, Set<Integer>>, ImmutableRoaringBitmap> cache,
                                                       Map<CategoricalFeature, Set<Integer>> conditions,
                                                       Integer labelValue,
                                                       BiFunction<Map<CategoricalFeature, Set<Integer>>, Integer, ImmutableRoaringBitmap> calculatorFunction) {
        ImmutableRoaringBitmap result = cache.get(conditions);
        if (result != null) {
            return result;
        } else {
            result = calculatorFunction.apply(conditions, labelValue);
            cache.put(conditions, result);
            return result;
        }
    }


    protected ImmutableRoaringBitmap cacheHitOrCalculateAndStoreForNotCovered(Cache<Map<CategoricalFeature, Set<Integer>>, ImmutableRoaringBitmap> cache,
                                                                    Map<CategoricalFeature, Set<Integer>> conditions,
                                                                    Integer labelValue,
                                                                    BiFunction<Map<CategoricalFeature, Set<Integer>>, Integer, ImmutableRoaringBitmap> calculatorFunction) {
        // In this case, the two functions do not differ (see "cacheHitOrCalculateAndStore(...)").
        // If cached results are to be reused, they must be distinguished.
        return cacheHitOrCalculateAndStore(cache, conditions, labelValue, calculatorFunction);
    }

    @Override
    public ImmutableRoaringBitmap getCoveredAsBitmap(Map<CategoricalFeature, Set<Integer>> conditions) {
        ImmutableRoaringBitmap result = coveredCache.get(conditions);
        if (result != null) {
            return result;
        } else {
            result = delegateTo.getCoveredAsBitmap(conditions);
            coveredCache.put(conditions, result);
            return result;
        }
    }

    @Override
    public ImmutableRoaringBitmap getCorrectlyCoveredAsBitmap(Map<CategoricalFeature, Set<Integer>> conditions, int labelValue) {
        return cacheHitOrCalculateAndStore(
                correctlyCoveredCaches.get(labelValue),
                conditions,
                labelValue,
                delegateTo::getCorrectlyCoveredAsBitmap
        );
    }

    @Override
    public ImmutableRoaringBitmap getIncorrectlyCoveredAsBitmap(Map<CategoricalFeature, Set<Integer>> conditions, int labelValue) {
        return cacheHitOrCalculateAndStore(
                incorrectlyCoveredCaches.get(labelValue),
                conditions,
                labelValue,
                delegateTo::getIncorrectlyCoveredAsBitmap
        );
    }

    @Override
    public ImmutableRoaringBitmap getCorrectlyNotCoveredAsBitmap(Map<CategoricalFeature, Set<Integer>> conditions, int labelValue) {
        return cacheHitOrCalculateAndStoreForNotCovered(
                correctlyNotCoveredCaches.get(labelValue),
                conditions,
                labelValue,
                delegateTo::getCorrectlyNotCoveredAsBitmap
        );
    }

    @Override
    public ImmutableRoaringBitmap getIncorrectlyNotCoveredAsBitmap(Map<CategoricalFeature, Set<Integer>> conditions, int labelValue) {
        return cacheHitOrCalculateAndStoreForNotCovered(
                incorrectlyNotCoveredCaches.get(labelValue),
                conditions,
                labelValue,
                delegateTo::getIncorrectlyNotCoveredAsBitmap
        );
    }

    @Override
    public ImmutableRoaringBitmap[] getCoversAsBitmap(Map<CategoricalFeature, Set<Integer>> conditions, int labelValue) {
        ImmutableRoaringBitmap[] result = new ImmutableRoaringBitmap[4];
        result[0] = getCorrectlyCoveredAsBitmap(conditions, labelValue);
        result[1] = getIncorrectlyCoveredAsBitmap(conditions, labelValue);
        result[2] = getCorrectlyNotCoveredAsBitmap(conditions, labelValue);
        result[3] = getIncorrectlyNotCoveredAsBitmap(conditions, labelValue);
        return result;
    }

    @Override
    public ImmutableRoaringBitmap[] getMinimalNumberCoversAsBitmap(Map<CategoricalFeature, Set<Integer>> conditions, int labelValue) {
        ImmutableRoaringBitmap[] result = new ImmutableRoaringBitmap[3];
        result[0] = getCoveredAsBitmap(conditions);
        result[1] = getCorrectlyCoveredAsBitmap(conditions, labelValue);
        result[2] = getIncorrectlyNotCoveredAsBitmap(conditions, labelValue);
        return result;
    }

    public int getNumberCovered(CategoricalFeature categoricalFeature, int featureValue) {
        ImmutableRoaringBitmap result = delegateTo.getCoveredAsBitmap(categoricalFeature, featureValue);
        return result.getCardinality();
    }

    @Override
    public int getNumberCovered(Map<CategoricalFeature, Set<Integer>> conditions) {
        ImmutableRoaringBitmap result = coveredCache.get(conditions);
        if (result != null) {
            return result.getCardinality();
        } else {
            result = delegateTo.getCoveredAsBitmap(conditions);
            coveredCache.put(conditions, result);
            return result.getCardinality();
        }
    }

    // Delegates to the calculator if the queried cover is not within the cache.
    protected int cacheHitOrCalculateAndStoreNumber(Cache<Map<CategoricalFeature, Set<Integer>>, ImmutableRoaringBitmap> cache,
                                                  Map<CategoricalFeature, Set<Integer>> conditions,
                                                  Integer labelValue,
                                                  BiFunction<Map<CategoricalFeature, Set<Integer>>, Integer, ImmutableRoaringBitmap> calculatorFunction) {
        ImmutableRoaringBitmap result = cache.get(conditions);
        if (result != null) {
            return result.getCardinality();
        } else {
            result = calculatorFunction.apply(conditions, labelValue);
            cache.put(conditions, result);
            return result.getCardinality();
        }
    }

    @Override
    public int getNumberCorrectlyCovered(Map<CategoricalFeature, Set<Integer>> conditions, int labelValue) {
        return cacheHitOrCalculateAndStoreNumber(
                correctlyCoveredCaches.get(labelValue),
                conditions,
                labelValue,
                delegateTo::getCorrectlyCoveredAsBitmap
        );
    }

    @Override
    public int getNumberIncorrectlyCovered(Map<CategoricalFeature, Set<Integer>> conditions, int labelValue) {
        return cacheHitOrCalculateAndStoreNumber(
                incorrectlyCoveredCaches.get(labelValue),
                conditions,
                labelValue,
                delegateTo::getIncorrectlyCoveredAsBitmap
        );
    }

    @Override
    public int getNumberCorrectlyNotCovered(Map<CategoricalFeature, Set<Integer>> conditions, int labelValue) {
        return cacheHitOrCalculateAndStoreNumber(
                correctlyNotCoveredCaches.get(labelValue),
                conditions,
                labelValue,
                delegateTo::getCorrectlyNotCoveredAsBitmap
        );
    }

    @Override
    public int getNumberIncorrectlyNotCovered(Map<CategoricalFeature, Set<Integer>> conditions, int labelValue) {
        return cacheHitOrCalculateAndStoreNumber(
                incorrectlyNotCoveredCaches.get(labelValue),
                conditions,
                labelValue,
                delegateTo::getIncorrectlyNotCoveredAsBitmap
        );
    }

    @Override
    public int[] getNumberInCovers(Map<CategoricalFeature, Set<Integer>> conditions, int labelValue) {
        int[] result = new int[4];
        result[0] = getNumberCorrectlyCovered(conditions, labelValue);
        result[1] = getNumberIncorrectlyCovered(conditions, labelValue);
        result[2] = getNumberCorrectlyNotCovered(conditions, labelValue);
        result[3] = getNumberIncorrectlyNotCovered(conditions, labelValue);
        return result;
    }

    @Override
    public int[] getMinimalAmountNumberInCovers(Map<CategoricalFeature, Set<Integer>> conditions, int labelValue) {
        int[] result = new int[3];
        result[0] = getNumberCovered(conditions);
        result[1] = getNumberCorrectlyCovered(conditions, labelValue);
        result[2] = getNumberIncorrectlyNotCovered(conditions, labelValue);
        return result;
    }
}
