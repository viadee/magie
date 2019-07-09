package de.viadee.xai.framework.explanation_calculation.calculator;

import de.viadee.xai.framework.data.Feature.CategoricalFeature;
import org.ehcache.Cache;
import org.roaringbitmap.buffer.ImmutableRoaringBitmap;
import org.roaringbitmap.buffer.MutableRoaringBitmap;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

/**
 * Calculator implementing a primitive strategy to reuse covers for the calculation of further covers.
 */
public class CoverCacheReusingCalculator extends CoverCachedCalculator {

    /**
     * Constructor for CoverCacheReusingCalculator.
     * @param calculator The calculator to which the computation is delegated in case of a cache-miss.
     */
    public CoverCacheReusingCalculator(RoaringBitmapCalculator calculator) {
        super(calculator);
    }

    @Override
    public ImmutableRoaringBitmap getCoveredAsBitmap(Map<CategoricalFeature, Set<Integer>> conditions) {
        ImmutableRoaringBitmap result = coveredCache.get(conditions);
        if (result != null) {
            // If the conditions are cached, return it.
            return result;
        } else if (conditions.size() > 1) {
            // If conditions is not cached, check if a subset of size = conditions.size() - 1 is cached:
            Map<CategoricalFeature, Set<Integer>> conditionSubset;
            Set<Map.Entry<CategoricalFeature, Set<Integer>>> entries = conditions.entrySet();
            ImmutableRoaringBitmap preResult;
            for (Map.Entry<CategoricalFeature, Set<Integer>> entry : entries) {
                conditionSubset = new HashMap<>(conditions);
                conditionSubset.remove(entry.getKey());
                preResult = coveredCache.get(conditionSubset);
                if (preResult != null) {
                    // If the subset is cached, it can be used to compute the new one.
                    result =
                            super.getCoveredAsBitmap(entry.getKey(), entry.getValue()).toMutableRoaringBitmap();
                    ((MutableRoaringBitmap) result).and(preResult);
                    coveredCache.put(conditions, result);
                    return result;
                }
            }
        }
        result = delegateTo.getCoveredAsBitmap(conditions);
        coveredCache.put(conditions, result);
        return result;
    }

    @Override
    protected ImmutableRoaringBitmap cacheHitOrCalculateAndStore(Cache<Map<CategoricalFeature, Set<Integer>>, ImmutableRoaringBitmap> cache,
                                                       Map<CategoricalFeature, Set<Integer>> conditions,
                                                       Integer labelValue,
                                                       BiFunction<Map<CategoricalFeature, Set<Integer>>, Integer, ImmutableRoaringBitmap> calculatorFunction) {
        BiConsumer<MutableRoaringBitmap, ImmutableRoaringBitmap> cacheAccumulationFunction = (set1, set2) -> set1.and(set2);
        return reuseAndCalculate(cache, conditions, labelValue, calculatorFunction, cacheAccumulationFunction);
    }

    @Override
    protected ImmutableRoaringBitmap cacheHitOrCalculateAndStoreForNotCovered(Cache<Map<CategoricalFeature, Set<Integer>>, ImmutableRoaringBitmap> cache,
                                                                    Map<CategoricalFeature, Set<Integer>> conditions,
                                                                    Integer labelValue,
                                                                    BiFunction<Map<CategoricalFeature, Set<Integer>>, Integer, ImmutableRoaringBitmap> calculatorFunction) {
        BiConsumer<MutableRoaringBitmap, ImmutableRoaringBitmap> cacheAccumulationFunction = (set1, set2) -> set1.or(set2);
        return reuseAndCalculate(cache, conditions, labelValue, calculatorFunction, cacheAccumulationFunction);
    }

    // Main business logic. Utilizes "cacheHitOrCalculateAndStore(...)" or "cacheHitOrCalculateAndStoreForNotCovered(...)"
    // to primitively check for covers upon which the further calculation can be based.
    protected ImmutableRoaringBitmap reuseAndCalculate(Cache<Map<CategoricalFeature, Set<Integer>>, ImmutableRoaringBitmap> cache,
                                             Map<CategoricalFeature, Set<Integer>> conditions,
                                             Integer labelValue,
                                             BiFunction<Map<CategoricalFeature, Set<Integer>>, Integer, ImmutableRoaringBitmap> calculatorFunction,
                                             BiConsumer<MutableRoaringBitmap, ImmutableRoaringBitmap> cacheAccumulationFunction) {
        ImmutableRoaringBitmap result = cache.get(conditions);
        if (result != null) {
            // If the conditions are cached, return it.
            return result;
        } else if (conditions.size() > 1) {
            // If conditions is not cached, check if a subset of size = conditions.size() - 1 is cached:
            Map<CategoricalFeature, Set<Integer>> conditionSubset;
            Set<Map.Entry<CategoricalFeature, Set<Integer>>> entries = conditions.entrySet();
            ImmutableRoaringBitmap preResult;
            for (Map.Entry<CategoricalFeature, Set<Integer>> entry : entries) {
                conditionSubset = new HashMap<>(conditions);
                conditionSubset.remove(entry.getKey());
                preResult = cache.get(conditionSubset);
                if (preResult != null) {
                    // If the subset is cached, it can be used to compute the new one.
                    Map<CategoricalFeature, Set<Integer>> additionalCondition = new HashMap<>();
                    additionalCondition.put(entry.getKey(), entry.getValue());
                    result = super.cacheHitOrCalculateAndStore(
                            cache,
                            additionalCondition,
                            labelValue,
                            calculatorFunction
                    ).toMutableRoaringBitmap();
                    cacheAccumulationFunction.accept((MutableRoaringBitmap) result, preResult);
                    cache.put(conditions, result);
                    return result;
                }
            }
        }
        result = calculatorFunction.apply(conditions, labelValue);
        cache.put(conditions, result);
        return result;
    }

}
