package de.viadee.xai.framework.explanation_calculation.calculator;

import de.viadee.xai.framework.data.Feature;
import de.viadee.xai.framework.data.tabular_data.LabelColumn;
import de.viadee.xai.framework.data.tabular_data.TabularDataset;
import org.ehcache.CacheManager;
import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.EntryUnit;

import java.util.Map;
import java.util.Set;

/**
 * Abstract superclass of all calculators utilizing EhCache.
 * @param <C> The type of cover-representation, e.g. {@link org.roaringbitmap.buffer.ImmutableRoaringBitmap}.
 * @param <CC> The calculator to delegate the actual computation to, in case of a cache-miss.
 */
public abstract class EhCachedCategoricalCalculator<C, CC extends CategoricalCalculator>
        implements CategoricalCalculator {

    protected final CC delegateTo;

    protected final static String[] cacheNames = new String[] {
            "covered",
            "correctlyCovered",
            "incorrectlyCovered",
            "correctlyNotCovered",
            "incorrectlyNotCovered"
    };
    protected final int[] cacheSizes;

    protected final CacheManager cacheManager;
    protected final CacheConfiguration cacheConfigurationCovered;
    protected final CacheConfiguration cacheConfigurationCorrectlyCovered;
    protected final CacheConfiguration cacheConfigurationIncorrectlyCovered;
    protected final CacheConfiguration cacheConfigurationCorrectlyNotCovered;
    protected final CacheConfiguration cacheConfigurationIncorrectlyNotCovered;
    protected final int expiryIdle;

    protected final Class<C> cachedType;

    /**
     * Constructor for EhCachedCategoricalCalculator.
     * @param delegateTo The calculator to delegate to if a cache-miss is yielded.
     * @param cachedType The type of cover-representation.
     */
    public EhCachedCategoricalCalculator(CC delegateTo,
                                         Class<C> cachedType) {
        this.cachedType = cachedType;
        cacheSizes = new int[] {
                3600,
                3600,
                1,
                1,
                3600
        };
        expiryIdle = 4;
        this.delegateTo = delegateTo;
        cacheConfigurationCovered = generateCacheConfiguration(cacheSizes[0]);
        cacheManager = CacheManagerBuilder
                .newCacheManagerBuilder()
                .withCache(
                        cacheNames[0],
                        cacheConfigurationCovered
                )
                .build();
        cacheManager.init();
        cacheConfigurationCorrectlyCovered = generateCacheConfiguration(cacheSizes[1]);
        cacheConfigurationIncorrectlyCovered = generateCacheConfiguration(cacheSizes[2]);
        cacheConfigurationCorrectlyNotCovered = generateCacheConfiguration(cacheSizes[3]);
        cacheConfigurationIncorrectlyNotCovered = generateCacheConfiguration(cacheSizes[4]);
        for (Integer i : (delegateTo.getDataset().getProcessedLabelCol().getLabel()).getUniqueNumberRepresentations()) {
            cacheManager.createCache(cacheNames[1] + i, cacheConfigurationCorrectlyCovered);
            cacheManager.createCache(cacheNames[2] + i, cacheConfigurationIncorrectlyCovered);
            cacheManager.createCache(cacheNames[3] + i, cacheConfigurationCorrectlyNotCovered);
            cacheManager.createCache(cacheNames[4] + i, cacheConfigurationIncorrectlyNotCovered);
        }
    }

    /**
     * Constructor for EhCachedCategoricalCalculator.
     * @param delegateTo The calculator to delegate to if a cache-miss is yielded.
     * @param cacheSizes The sizes of the 5 caches (cover, true positives, false positives,
     *                   true negatives, false negatives)
     * @param cachedType The type of cover-representation.
     */
    public EhCachedCategoricalCalculator(CC delegateTo,
                                         int[] cacheSizes,
                                         Class<C> cachedType) {
        this.cachedType = cachedType;
        if (cacheSizes.length != 5) {
            throw new IllegalArgumentException("The size must be specified for five caches.");
        }
        this.cacheSizes = cacheSizes;
        expiryIdle = 4;
        this.delegateTo = delegateTo;
        cacheConfigurationCovered = generateCacheConfiguration(cacheSizes[0]);
        cacheManager = CacheManagerBuilder
                .newCacheManagerBuilder()
                .withCache(
                        cacheNames[0],
                        cacheConfigurationCovered
                )
                .build();
        cacheManager.init();
        cacheConfigurationCorrectlyCovered = generateCacheConfiguration(cacheSizes[1]);
        cacheConfigurationIncorrectlyCovered = generateCacheConfiguration(cacheSizes[2]);
        cacheConfigurationCorrectlyNotCovered = generateCacheConfiguration(cacheSizes[3]);
        cacheConfigurationIncorrectlyNotCovered = generateCacheConfiguration(cacheSizes[4]);
        for (Integer i : (delegateTo.getDataset().getProcessedLabelCol().getLabel()).getUniqueNumberRepresentations()) {
            cacheManager.createCache(cacheNames[1] + i, cacheConfigurationCorrectlyCovered);
            cacheManager.createCache(cacheNames[2] + i, cacheConfigurationIncorrectlyCovered);
            cacheManager.createCache(cacheNames[3] + i, cacheConfigurationCorrectlyNotCovered);
            cacheManager.createCache(cacheNames[4] + i, cacheConfigurationIncorrectlyNotCovered);
        }
    }

    protected CacheConfiguration generateCacheConfiguration(int numberEntries) {
        return CacheConfigurationBuilder
                .newCacheConfigurationBuilder(
                        Map.class,
                        cachedType,
                        ResourcePoolsBuilder.newResourcePoolsBuilder().heap(numberEntries, EntryUnit.ENTRIES)
                ).build();
    }

    @Override
    public int getNumberCovered(Map<Feature.CategoricalFeature, Set<Integer>> conditions) {
        return delegateTo.getNumberCovered(conditions);
    }

    @Override
    public int getNumberCorrectlyCovered(Map<Feature.CategoricalFeature, Set<Integer>> conditions, int labelValue) {
        return delegateTo.getNumberCorrectlyCovered(conditions, labelValue);
    }

    @Override
    public int getNumberIncorrectlyCovered(Map<Feature.CategoricalFeature, Set<Integer>> conditions, int labelValue) {
        return delegateTo.getNumberIncorrectlyCovered(conditions, labelValue);
    }

    @Override
    public int getNumberCorrectlyNotCovered(Map<Feature.CategoricalFeature, Set<Integer>> conditions, int labelValue) {
        return delegateTo.getNumberCorrectlyNotCovered(conditions, labelValue);
    }

    @Override
    public int getNumberIncorrectlyNotCovered(Map<Feature.CategoricalFeature, Set<Integer>> conditions, int labelValue) {
        return delegateTo.getNumberIncorrectlyNotCovered(conditions, labelValue);
    }

    @Override
    public int[] getNumberInCovers(Map<Feature.CategoricalFeature, Set<Integer>> conditions, int labelValue) {
        return delegateTo.getNumberInCovers(conditions, labelValue);
    }

    @Override
    public int[] getMinimalAmountNumberInCovers(Map<Feature.CategoricalFeature, Set<Integer>> conditions, int labelValue) {
        return delegateTo.getMinimalAmountNumberInCovers(conditions, labelValue);
    }

    @Override
    public TabularDataset<?, LabelColumn.CategoricalLabelColumn> getDataset() {
        return delegateTo.getDataset();
    }
}
