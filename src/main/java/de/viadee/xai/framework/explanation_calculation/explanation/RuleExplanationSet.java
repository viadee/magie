package de.viadee.xai.framework.explanation_calculation.explanation;

import de.viadee.xai.framework.data.Feature.CategoricalFeature;
import de.viadee.xai.framework.data.tabular_data.LabelColumn;
import de.viadee.xai.framework.data.tabular_data.TabularDataset;
import de.viadee.xai.framework.explanation_calculation.calculator.RoaringBitmapCalculator;
import org.roaringbitmap.buffer.ImmutableRoaringBitmap;

import java.util.Map;
import java.util.Set;

/**
 * Interface for all sets of RuleExplanations. A RuleExplanationSet contains {@link RuleExplanation}s
 * all explaining the same label.
 */
public interface RuleExplanationSet {

    /**
     * Returns the bitmap representing all instances covered by the rule on the initial dataset.
     * @return The bitmap.
     */
    ImmutableRoaringBitmap getCoverAsBitmap();

    /**
     * Returns the coverage calculated on the initial dataset.
     * @return The coverage.
     */
    double getCoverage();

    /**
     * Returns the number of instances covered by the RuleExplanationSet on the initial dataset.
     * @return The number of covered instances.
     */
    int getNumberCoveredInstances();

    /**
     * Returns the feature of the label predicted by the rules within the RuleExplanationSet.
     * @return The CategoricalFeature.
     */
    CategoricalFeature getLabelFeature();

    /**
     * Returns the value of the label predicted by the rules within the RuleExplanationSet.
     * @return The CategoricalFeature.
     */
    int getLabelValue();

    /**
     * Returns all conditions contained in the RuleExplanationSet.
     * @return The condition consisting of CategoricalFeatures mapped to a Set of integer working-representations defining allowed/needed values.
     * {@link CategoricalFeature}
     */
    Map<CategoricalFeature, Set<Integer>> getConditions();

    /**
     * Returns the sum of the amount of values for all CategoricalFeatures.
     * @return The amount of all values for all CategoricalFeatures contained in the RuleExplanationSet.
     */
    int getNumberConditionValues();


    /**
     * Returns the initial dataset, i.e., the dataset on which the metrics and covers of the RuleExplanationSet were
     * created on.
     * @return The dataset.
     */
    TabularDataset<?, LabelColumn.CategoricalLabelColumn> getDataset();

    /**
     * Returns the calculator injected into this object to calculate the covers of the contained {@link RuleExplanation}.
     * @return The calculator.
     */
    RoaringBitmapCalculator getCalculator();

    /**
     * Returns the Set of {@link RuleExplanation}s contained in this {@link RuleExplanationSet}.
     * @return The set of {@link RuleExplanation}s.
     */
    Set<RuleExplanation> getExplanations();

    /**
     * Returns the number of contained {@link RuleExplanation}s.
     * @return The number of contained {@link RuleExplanation}s.
     */
    int getNumberExplanations();
}
