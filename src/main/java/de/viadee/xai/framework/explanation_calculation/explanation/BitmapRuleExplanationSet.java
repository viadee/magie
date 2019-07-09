package de.viadee.xai.framework.explanation_calculation.explanation;

import de.viadee.xai.framework.data.Feature;
import de.viadee.xai.framework.data.tabular_data.LabelColumn;
import de.viadee.xai.framework.data.tabular_data.TabularDataset;
import de.viadee.xai.framework.exception.RuleJoinNotLegal;
import de.viadee.xai.framework.explanation_calculation.calculator.RoaringBitmapCalculator;
import org.roaringbitmap.buffer.ImmutableRoaringBitmap;
import org.roaringbitmap.buffer.MutableRoaringBitmap;

import java.util.*;
import java.util.function.Function;

/**
 * Class containing several {@link RuleExplanation} of the same feature label.
 */
public class BitmapRuleExplanationSet implements RuleExplanationSet {

    protected final Set<RuleExplanation> ruleExplanations;

    protected final Feature.CategoricalFeature labelFeature;
    protected final int labelValue;

    protected final MutableRoaringBitmap coveredInstances;

    protected final TabularDataset<?, LabelColumn.CategoricalLabelColumn> dataset;
    protected final RoaringBitmapCalculator calculator;

    private BitmapRuleExplanationSet(TabularDataset<?, LabelColumn.CategoricalLabelColumn> dataset,
                                     RoaringBitmapCalculator calculator,
                                     Feature.CategoricalFeature labelFeature,
                                     int labelValue,
                                     Set<RuleExplanation> ruleExplanations) {
        this.dataset = dataset;
        this.calculator = calculator;
        this.labelFeature = labelFeature;
        this.labelValue = labelValue;
        this.ruleExplanations = new HashSet<>(ruleExplanations);
        this.coveredInstances = new MutableRoaringBitmap();
    }

    /**
     * Constructor for an empty BitmapRuleExplanationSet instance.
     * @param labelFeature The label feature.
     * @param labelValue The label value.
     * @param calculator The calculator with which the BitmapRuleExplanationSet's metrics and covers are calculated.
     */
    public BitmapRuleExplanationSet(final Feature.CategoricalFeature labelFeature,
                                 final int labelValue,
                                 final RoaringBitmapCalculator calculator) {
        this(
                calculator.getDataset(),
                calculator,
                labelFeature,
                labelValue,
                new HashSet<>()
        );
    }

    /**
     * Constructor a BitmapRuleExplanationSet instance from the given Collection of RuleExplanations.
     * @param labelFeature The label feature.
     * @param labelValue The label value.
     * @param ruleExplanations The RuleExplanations which are evaluated on the given setIndex and comprise the set
     *                         of stored RuleExplanations.
     * @param calculator The calculator with which the BitmapRuleExplanationSet's metrics and covers are calculated.
     */
    public BitmapRuleExplanationSet(final Feature.CategoricalFeature labelFeature,
                                 final int labelValue,
                                 final Collection<RuleExplanation> ruleExplanations,
                                 final RoaringBitmapCalculator calculator) {
        this(
                calculator.getDataset(),
                calculator,
                labelFeature,
                labelValue,
                new HashSet<>(ruleExplanations)
        );
        for (RuleExplanation ruleExplanation : ruleExplanations) {
            validate(ruleExplanation);
            coveredInstances.or(getCoverFromRuleExplanation(ruleExplanation));
        }
    }

    /**
     * Constructor for a BitmapRuleExplanationSet instance which copies the RuleExplanations and label from another
     * RuleExplanationSet. Furthermore, a new RuleExplanation is added to the copied-from set.
     * @param copyFrom The RuleExplanationSet which is to be copied.
     * @param ruleExplanation The additionally added RuleExplanation.
     * @param calculator The calculator with which the BitmapRuleExplanationSet's metrics and covers are calculated.
     */
    public BitmapRuleExplanationSet(final RuleExplanationSet copyFrom,
                                    final RuleExplanation ruleExplanation,
                                    final RoaringBitmapCalculator calculator) {
        this(
                copyFrom,
                calculator
        );
        validate(ruleExplanation);
        ruleExplanations.add(ruleExplanation);
        coveredInstances.or(getCoverFromRuleExplanation(ruleExplanation));
    }

    /**
     * Constructor for a BitmapRuleExplanationSet instance which copies the RuleExplanations and label from another
     * RuleExplanationSet. If the other RuleExplanationSet does not use the same TabularDataset, the cover is recalculated
     * with the contained RuleExplanations.
     * @param copyFrom The RuleExplanationSet which is to be copied.
     * @param calculator The calculator with which the BitmapRuleExplanationSet's metrics and covers are calculated.
     */
    public BitmapRuleExplanationSet(final RuleExplanationSet copyFrom,
                                    final RoaringBitmapCalculator calculator) {
        this(
                calculator.getDataset(),
                calculator,
                copyFrom.getLabelFeature(),
                copyFrom.getLabelValue(),
                copyFrom.getExplanations()
        );
        coveredInstances.or(getCoverFromRuleExplanationSet(copyFrom));
    }

    protected ImmutableRoaringBitmap getCoverFromRuleExplanationSet(RuleExplanationSet ruleExplanationSet) {
        if (dataset.equals(ruleExplanationSet.getDataset())) {
            return ruleExplanationSet.getCoverAsBitmap();
        } else {
            return getIndicesFromRuleExplanations(ruleExplanationSet.getExplanations(),
                    this::getCoverFromRuleExplanation);
        }
    }

    protected ImmutableRoaringBitmap getIndicesFromRuleExplanations(Set<RuleExplanation> ruleExplanations,
                                                          Function<
                                                                  RuleExplanation,
                                                                  ImmutableRoaringBitmap
                                                                  > coverageCalculation) {
        MutableRoaringBitmap result = new MutableRoaringBitmap();
        for (RuleExplanation ruleExplanation : ruleExplanations) {
            result.or(coverageCalculation.apply(ruleExplanation));
        }
        return result;
    }


    protected ImmutableRoaringBitmap getCoverFromRuleExplanation(RuleExplanation ruleExplanation) {
        if (dataset.equals(ruleExplanation.getDataset())) {
            return ruleExplanation.getCoverAsBitmap();
        } else {
            return ruleExplanation.calculateCoverAsBitmap(calculator);
        }
    }

    protected void validate(final RuleExplanation ruleExplanation) {
        if (!(labelFeature.equals(ruleExplanation.getLabelFeature())) ||
                (labelValue != ruleExplanation.getLabelValue())) {
            throw new RuleJoinNotLegal(labelFeature, labelValue,
                    ruleExplanation.getLabelFeature(), ruleExplanation.getLabelValue());
        }
    }

    @Override
    public ImmutableRoaringBitmap getCoverAsBitmap() {
        return coveredInstances;
    }

    @Override
    public int getNumberCoveredInstances() {
        return coveredInstances.getCardinality();
    }

    @Override
    public double getCoverage() {
        return ((double) coveredInstances.getCardinality()) / dataset.getNumberRows();
    }

    @Override
    public Map<Feature.CategoricalFeature, Set<Integer>> getConditions() {
        Map<Feature.CategoricalFeature, Set<Integer>> features = new HashMap<>();
        for (RuleExplanation re : ruleExplanations) {
            for (Feature.CategoricalFeature cf : re.getConditionFeatures()) {
                features.putIfAbsent(cf, new HashSet<>());
                features.get(cf).addAll(re.getConditionValues(cf));
            }
        }
        return Collections.unmodifiableMap(features);
    }

    @Override
    public int getNumberExplanations() {
        return ruleExplanations.size();
    }

    @Override
    public int getNumberConditionValues() {
        int result = 0;
        for (Set<Integer> re : getConditions().values()) {
            result += re.size();
        }
        return result;
    }

    @Override
    public Feature.CategoricalFeature getLabelFeature() {
        return labelFeature;
    }

    @Override
    public int getLabelValue() {
        return labelValue;
    }

    @Override
    public RoaringBitmapCalculator getCalculator() {
        return calculator;
    }

    @Override
    public String toString() {
        return "BitmapRuleExplanationSet{" +
                "\n\truleExplanations=" + ruleExplanations +
                ",\n\tlabelFeature=" + labelFeature +
                ",\n\tlabelValue=" + labelFeature.getStringRepresentation(labelValue) +
                "}";
    }

    @Override
    public Set<RuleExplanation> getExplanations() {
        return ruleExplanations;
    }

    @Override
    public TabularDataset<?, LabelColumn.CategoricalLabelColumn> getDataset() {
        return dataset;
    }
}
