package de.viadee.xai.framework.explanation_calculation.explanation;

import de.viadee.xai.framework.data.Feature.CategoricalFeature;
import de.viadee.xai.framework.data.tabular_data.LabelColumn;
import de.viadee.xai.framework.data.tabular_data.TabularDataset;

import java.util.Collection;

/**
 * A RuleExplanationSetFactory instantiates RuleExplanationSets. The reasoning behind this type
 * of factory is similar to {@link RuleExplanationFactory}.
 * The dataset of the contained RuleExplanations can differ from the dataset which is used
 * by the RuleExplanationSet for the calculation of its covers.
 * @param <RES> The contained type of RuleExplanationSet.
 */
public interface RuleExplanationSetFactory<RES extends RuleExplanationSet> {

    /**
     * Creates a new RuleExplanationSet without any rules.
     * @param labelFeature The label feature to be predicted by contained rules.
     * @param labelValue The label value to be predicted by contained rules.
     * @return The new RuleExplanationSet.
     */
    RES newEmpty(final CategoricalFeature labelFeature,
                 final int labelValue);

    /**
     * Creates a new RuleExplanationSet with a Collection of RuleExplanations.
     * @param labelFeature The label feature to be predicted by contained rules.
     * @param labelValue The label value to be predicted by contained rules.
     * @param ruleExplanations The RuleExplanations which will be contained in the set.
     * @return The new RuleExplanationSet.
     */
    RES newWithCollection(final CategoricalFeature labelFeature,
                          final int labelValue,
                          final Collection<RuleExplanation> ruleExplanations);

    /**
     * Creates a new RuleExplanationSet from an existing one. The RuleExplanationSet
     * will be copied entirely. Furthermore, a new instance is added.
     * @param copyFrom The RuleExplanationSet which will be copied.
     * @param ruleExplanation The RuleExplanation which will be added.
     * @return The new RuleExplanationSet.
     */
    RES newCopyWith(final RuleExplanationSet copyFrom,
                    final RuleExplanation ruleExplanation);

    /**
     * Creates a new RuleExplanationSet from an existing one. Can be used to adapt it at
     * certain positions. For example, the new RuleExplanationSet could contain a different dataset
     * so that its covers are now calculated on the new dataset.
     * @param copyFrom The RuleExplanationSet to be copied.
     * @return A, possibly adapted, RuleExplanationSet.
     */
    RES translateWithData(RuleExplanationSet copyFrom);

    /**
     * Gets the dataset which was used to calculate the covers of the RuleExplanationSet.
     * @return The used dataset.
     */
    TabularDataset<?, LabelColumn.CategoricalLabelColumn> getDataset();

}
