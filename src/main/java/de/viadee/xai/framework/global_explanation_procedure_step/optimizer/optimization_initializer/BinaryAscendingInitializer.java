package de.viadee.xai.framework.global_explanation_procedure_step.optimizer.optimization_initializer;

import de.viadee.xai.framework.explanation_calculation.explanation.RuleExplanationSet;
import de.viadee.xai.framework.global_explanation_procedure_step.optimizer.OptimizationInitializer;

import java.util.List;
import java.util.Map;

import static de.viadee.xai.framework.utility.Utility.generateBinaryChars;

/**
 * Initializer which, for a given population size and binary representation length first creates every binary string
 * with one active bit, then with two active bits, etc. This is done until the number of binary bits equals the size
 * of the population.
 */
public class BinaryAscendingInitializer implements OptimizationInitializer<boolean[][]> {

    @Override
    public boolean[][] apply(Integer populationSize, Integer representationLength) {
        Map<Integer, List<boolean[]>> mappedBinaries = generateBinaryChars(populationSize, representationLength);
        boolean[][] result = new boolean[populationSize][representationLength];
        int count = 0;
        for (List<boolean[]> binaryStrings : mappedBinaries.values()) {
            for (boolean[] binaryString : binaryStrings) {
                result[count] = binaryString;
                count++;
            }
        }
        return result;
    }

    @Override
    public void initialize(RuleExplanationSet representationSpaceFoundation) {}
}
