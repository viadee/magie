package de.viadee.xai.framework.global_explanation_procedure_step.optimizer.representation_translator;

import de.viadee.xai.framework.data.Feature.CategoricalFeature;
import de.viadee.xai.framework.data.FeatureValue.CategoricalFeatureValue;
import de.viadee.xai.framework.explanation_calculation.explanation.RuleExplanation;
import de.viadee.xai.framework.explanation_calculation.explanation.RuleExplanationFactory;
import de.viadee.xai.framework.explanation_calculation.explanation.RuleExplanationSet;
import de.viadee.xai.framework.global_explanation_procedure_step.optimizer.RepresentationTranslator;
import de.viadee.xai.framework.utility.Utility;
import io.jenetics.BitGene;
import io.jenetics.Chromosome;
import io.jenetics.Genotype;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Transforms a Genotype&lt;BitGene&gt; into a RuleExplanation.
 * The {@link BitGeneGenotypeToRuleExplanation#initialize(RuleExplanationSet, RuleExplanationFactory)}
 * method must be called before applying this functional class.
 */
public class BitGeneGenotypeToRuleExplanation
        implements RepresentationTranslator<
        Genotype<BitGene>,
        RuleExplanation,
        RuleExplanationFactory> {

    protected RuleExplanationFactory factory;
    protected RuleExplanationSet ruleExplanationSet;
    protected CategoricalFeatureValue[] orderedConditionFeatureValues;
    protected CategoricalFeature labelFeature;
    protected int labelValue;

    @Override
    public RuleExplanation apply(final Genotype<BitGene> genotype) {
        Map<CategoricalFeature, Set<Integer>> conditionFeaturesToValues = new HashMap<>();
        Chromosome<BitGene> bitGenes = genotype.getChromosome();
        for (int i = 0; i < bitGenes.length(); i++) {
            if (bitGenes.getGene(i).booleanValue()) {
                conditionFeaturesToValues.putIfAbsent(orderedConditionFeatureValues[i].getFeature(), new HashSet<>());
                conditionFeaturesToValues.get(orderedConditionFeatureValues[i].getFeature()).add(orderedConditionFeatureValues[i].getValue());
            }
        }
        return factory.initialize(conditionFeaturesToValues, labelFeature, labelValue);
    }

    @Override
    public void initialize(RuleExplanationSet representationSpaceFoundation, RuleExplanationFactory factory) {
        this.factory = factory;
        this.ruleExplanationSet = representationSpaceFoundation;
        labelFeature = ruleExplanationSet.getLabelFeature();
        labelValue = ruleExplanationSet.getLabelValue();

        orderedConditionFeatureValues =
                Utility.transformConditionsMapToArray(ruleExplanationSet);
    }
}
