package de.viadee.xai.framework.explanation_calculation.explanation;

import de.viadee.xai.framework.data.Feature.CategoricalFeature;
import de.viadee.xai.framework.data.tabular_data.LabelColumn;
import de.viadee.xai.framework.data.tabular_data.TabularDataset;
import de.viadee.xai.framework.explanation_calculation.calculator.RoaringBitmapCalculator;

import java.util.Map;
import java.util.Set;

/**
 * Interface of all factories generating RuleExplanations. A RuleExplanationFactory instantiates a certain
 * type of RuleExplanation. For example, one might want to swap a RuleExplanation storing its calculated covers
 * with a RuleExplanation which operates in-place. Possibly, one wants to restrict / guide a rule-creation process
 * which also can be done here.
 */
public interface RuleExplanationFactory {

    /**
     * Creates a RuleExplanation given conditions, the label feature and label value.
     * @param conditions A mapping from the CategoricalFeature to the int working-representations of the values contained
     *                   in the RuleExplanation.
     * @param labelFeature The label which is predicted by the RuleExplanation.
     * @param labelValue The value of the label which is predicted by the RuleExplanation.
     * @return The generated RuleExplanation.
     * {@link RuleExplanation}
     * {@link CategoricalFeature}
     */
    RuleExplanation initialize(Map<CategoricalFeature, Set<Integer>> conditions,
                               CategoricalFeature labelFeature,
                               int labelValue);

    /**
     * Creates a RuleExplanation which is a near-exact copy of the given RuleExplanation.
     * The only difference is, that the new RuleExplanation has another context, which is defined
     * in this factory(e.g. the dataset on which the Rule is evaluated).
     * @param toTranslate The RuleExplanation which is possibly recalculated.
     * @return The RuleExplanation which's covers are calculated on a certain dataset.
     */
    RuleExplanation translateWithData(RuleExplanation toTranslate);


    /**
     * Gets the dataset which was used to calculate the covers of the RuleExplanation.
     * @return The used dataset.
     */
    TabularDataset<?, LabelColumn.CategoricalLabelColumn> getDataset();

    /**
     * Returns the calculator which is injected into the {@link RuleExplanation}s created by this factory.
     * @return The calculator.
     */
    RoaringBitmapCalculator getCalculator();
}
