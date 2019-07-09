package de.viadee.xai.framework.global_explanation_procedure_step.optimizer.representation_translator;

import de.viadee.xai.framework.data.Feature.CategoricalFeature;
import de.viadee.xai.framework.data.FeatureValue.CategoricalFeatureValue;
import de.viadee.xai.framework.explanation_calculation.explanation.RuleExplanation;
import de.viadee.xai.framework.explanation_calculation.explanation.RuleExplanationFactory;
import de.viadee.xai.framework.explanation_calculation.explanation.RuleExplanationSet;
import de.viadee.xai.framework.global_explanation_procedure_step.optimizer.RepresentationTranslator;
import de.viadee.xai.framework.utility.Utility;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Transforms a binary string, represented by boolean[], to a {@link RuleExplanation}.
 */
public class BinaryRepresentationToRuleExplanation
        implements RepresentationTranslator<
        boolean[],
        RuleExplanation,
        RuleExplanationFactory
        > {

    protected RuleExplanationFactory ruleExplanationFactory;
    protected RuleExplanationSet representationSpaceFoundation;
    protected CategoricalFeatureValue[] orderedConditionFeatureValues;

    protected CategoricalFeature labelFeature;
    protected int labelValue;

    @Override
    public void initialize(RuleExplanationSet representationSpaceFoundation, RuleExplanationFactory factory) {
        this.ruleExplanationFactory = factory;
        this.representationSpaceFoundation = representationSpaceFoundation;
        this.labelFeature = this.representationSpaceFoundation.getLabelFeature();
        this.labelValue = this.representationSpaceFoundation.getLabelValue();

        orderedConditionFeatureValues =
                Utility.transformConditionsMapToArray(this.representationSpaceFoundation);

    }

    @Override
    public RuleExplanation apply(boolean[] binaryRepresentation) {
        Map<CategoricalFeature, Set<Integer>> conditions = new HashMap<>();
        for (int i = 0; i < binaryRepresentation.length; i++) {
            if (binaryRepresentation[i]) {
                conditions.putIfAbsent(orderedConditionFeatureValues[i].getFeature(), new HashSet<>());
                conditions.get(orderedConditionFeatureValues[i].getFeature()).add(orderedConditionFeatureValues[i].getValue());
            }
        }
        return ruleExplanationFactory.initialize(conditions, labelFeature, labelValue);
    }
}
