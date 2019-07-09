package de.viadee.xai.framework.global_explanation_procedure_step.optimizer.representation_translator;

import de.viadee.xai.framework.data.Feature.CategoricalFeature;
import de.viadee.xai.framework.explanation_calculation.explanation.RuleExplanation;
import de.viadee.xai.framework.explanation_calculation.explanation.RuleExplanationSet;
import de.viadee.xai.framework.explanation_calculation.explanation.RuleExplanationSetFactory;
import de.viadee.xai.framework.global_explanation_procedure_step.optimizer.RepresentationTranslator;
import io.jenetics.BitGene;
import io.jenetics.Chromosome;
import io.jenetics.Genotype;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Transforms a {@link Genotype} containing {@link BitGene}s into a {@link RuleExplanationSet}.
 */
public class BitGeneGenotypeToRuleExplanationSet
        implements RepresentationTranslator<
        Genotype<BitGene>,
        RuleExplanationSet,
        RuleExplanationSetFactory> {

    protected RuleExplanationSetFactory ruleExplanationSetFactory;
    protected RuleExplanationSet representationSpaceFoundation;
    protected RuleExplanation[] ruleExplanations;
    protected CategoricalFeature labelFeature;
    protected int labelValue;

    @Override
    public void initialize(RuleExplanationSet representationSpaceFoundation, RuleExplanationSetFactory factory) {
        this.ruleExplanationSetFactory = factory;
        this.representationSpaceFoundation = representationSpaceFoundation;
        this.labelFeature = this.representationSpaceFoundation.getLabelFeature();
        this.labelValue = this.representationSpaceFoundation.getLabelValue();
        Set<RuleExplanation> ruleExplanations = this.representationSpaceFoundation.getExplanations();

        this.ruleExplanations = ruleExplanations.toArray(new RuleExplanation[0]);
    }

    @Override
    public RuleExplanationSet apply(Genotype<BitGene> genotype) {
        List<RuleExplanation> ruleExplanations = new LinkedList<>();
        Chromosome<BitGene> bitGenes = genotype.getChromosome();
        for (int i = 0; i < bitGenes.length(); i++) {
            if (bitGenes.getGene(i).booleanValue()) {
                ruleExplanations.add(this.ruleExplanations[i]);
            }
        }
        return ruleExplanationSetFactory.newWithCollection(labelFeature, labelValue, ruleExplanations);
    }
}
