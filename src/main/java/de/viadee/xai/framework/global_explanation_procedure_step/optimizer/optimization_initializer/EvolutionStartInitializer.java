package de.viadee.xai.framework.global_explanation_procedure_step.optimizer.optimization_initializer;

import de.viadee.xai.framework.explanation_calculation.explanation.RuleExplanationSet;
import de.viadee.xai.framework.global_explanation_procedure_step.optimizer.OptimizationInitializer;
import io.jenetics.BitChromosome;
import io.jenetics.BitGene;
import io.jenetics.Genotype;

import java.util.List;
import java.util.Map;

import static de.viadee.xai.framework.utility.Utility.generateBinaryChars;

/**
 * An initializer for a genetic algorithm run on the jenetics-framework.
 * For a given population size and representation length, first generates all binary
 * strings containing one '1', therafter all binary strings containing two '1's and so on.
 * This procedure corresponds the procedure used by the MAGIX authors.
 * Puri, N., Gupta, P., Agarwal, P., Verma, S., {@literal &} Krishnamurthy, B. (2017).
 * MAGIX: Model Agnostic Globally Interpretable Explanations. arXiv preprint arXiv:1706.07160.
 */
public class EvolutionStartInitializer implements OptimizationInitializer<Genotype<BitGene>[]> {

    @Override
    public Genotype<BitGene>[] apply(Integer populationSize, Integer representationLength) {
        boolean[] zeroChars = new boolean[representationLength];
        Genotype<BitGene>[] genotypes = new Genotype[populationSize];

        Map<Integer, List<boolean[]>> resultingInitializers = generateBinaryChars(populationSize - 1, representationLength);

        genotypes[0] = Genotype.of(BitChromosome.of(constructBinaryStringFromBooleanArray(zeroChars), representationLength, ((double) 1)/ representationLength));
        int count = 1;
        for (int i = 0; i < resultingInitializers.size(); i++) {
            List<boolean[]> currentInitializer = resultingInitializers.get(i);
            for (int j = 0; j < currentInitializer.size(); j++) {
                genotypes[count] =
                        Genotype.of(BitChromosome.of(
                                constructBinaryStringFromBooleanArray(currentInitializer.get(j)),
                                representationLength,
                                ((double) 2)/ representationLength
                        ));
                count++;
            }
        }
        for (int i = count; i < populationSize; i++) {
            genotypes[i] = Genotype.of(BitChromosome.of(representationLength));
        }
        return genotypes;
    }

    @Override
    public void initialize(RuleExplanationSet representationSpaceFoundation) {}


    private String constructBinaryStringFromBooleanArray(boolean[] booleans) {
        char[] chars = new char[booleans.length];

        for(int i = 0; i < chars.length; i++) {
            chars[i] = booleans[i] ? '1' : '0';
        }

        return new String(chars);
    }
}
