package de.viadee.xai.framework.global_explanation_procedure_step.optimizer.objective_function;

import de.viadee.xai.framework.explanation_calculation.explanation.RuleExplanation;
import de.viadee.xai.framework.explanation_calculation.explanation.RuleExplanationSet;
import de.viadee.xai.framework.global_explanation_procedure_step.optimizer.ObjectiveFunction;
import org.roaringbitmap.buffer.ImmutableRoaringBitmap;

import java.util.Set;

/**
 * Modified objective function of BETA.
 * Lakkaraju, H., Kamar, E., Caruana, R., {@literal &} Leskovec, J. (2017). Interpretable {@literal &}
 * explorable approximations of black box models. arXiv preprint arXiv:1707.01154.
 */
public class BETAObjectiveCalculator implements ObjectiveFunction<RuleExplanationSet, Double> {

    protected int n;
    protected RuleExplanation[] nd;
    protected RuleExplanation[] dl;
    protected int wMax;
    protected long oMax;
    protected long oSMax;
    protected long fMax;
    protected int pMax;

    protected final double[] weights;

    /**
     * Default constructor for BETAObjectiveCalculator.
     */
    public BETAObjectiveCalculator() {
        weights = new double[] {
                1,1,1,1
        };
    }

    /**
     * Constructor for BETAObjectiveCalculator.
     * @param weights An array of size 4 specifying the weights for the different quantifications.
     */
    public BETAObjectiveCalculator(double[] weights) {
        if (weights.length != 4) {
            throw new IllegalArgumentException("Exactly four weights must be specified for the BETA objective function.");
        } else {
            this.weights = weights;
        }
    }

    private int calculateWMax(RuleExplanation[] ruleExplanations) {
        int wMax = 0;
        for (RuleExplanation ruleExplanation : ruleExplanations) {
            if (wMax < ruleExplanation.getNumberConditions()) {
                wMax = ruleExplanation.getNumberConditions();
            }
        }
        return wMax;
    }

    private long calculateOMax(int wMax, int ndlength, int dllength) {
        return wMax * ndlength * dllength;
    }

    private long calculateOSMax(int n, int ndlength, int dllength) {
        return n * ndlength * ndlength * dllength * dllength;
    }

    private long calculateFMax(int n, int ndlength, int dllength) {
        return n * ndlength * dllength;
    }

    @Override
    public Double apply(RuleExplanationSet ruleExplanationSet) {
        int f1 = calculateF1(ruleExplanationSet);
        long f3 = calculateF3(ruleExplanationSet);
        int f4 = calculateF4(ruleExplanationSet);
        long f5 = calculateF5(ruleExplanationSet);
        return weights[0] * f1 + weights[1] * f3 + weights[2] * f4 * weights[3] * f5;
    }

    private int calculateF1(RuleExplanationSet ruleExplanationSet) {
        return pMax - numPreds(ruleExplanationSet);
    }
    private int numPreds(RuleExplanationSet ruleExplanationSet) {
        int numPreds = 0;
        Set<RuleExplanation> ruleExplanations = ruleExplanationSet.getExplanations();
        for (RuleExplanation ruleExplanation : ruleExplanations) {
            // BETA did not allow disjunctions, we only count the number of conjunctions.
            numPreds += ruleExplanation.getNumberConditions();
        }
        return numPreds;
    }

    private long calculateF3(RuleExplanationSet ruleExplanationSet) {
        return oSMax - ruleOverlap(ruleExplanationSet);
    }

    private int ruleOverlap(RuleExplanationSet ruleExplanationSet) {
        Set<RuleExplanation> ruleExplanations = ruleExplanationSet.getExplanations();
        int ruleOverlap = 0;
        for (RuleExplanation ruleExplanationOuter : ruleExplanations) {
            for (RuleExplanation ruleExplanationInner : ruleExplanations) {
                if (!ruleExplanationOuter.equals(ruleExplanationInner)) {
                    ruleOverlap += ImmutableRoaringBitmap.and(
                            ruleExplanationOuter.getCoverAsBitmap(),
                            ruleExplanationInner.getCoverAsBitmap()
                    ).getCardinality();
                }
            }
        }
        return ruleOverlap;
    }

    private int calculateF4(RuleExplanationSet ruleExplanationSet) {
        return ruleExplanationSet.getNumberCoveredInstances();
    }

    private long calculateF5(RuleExplanationSet ruleExplanationSet) {
        Set<RuleExplanation> ruleExplanations = ruleExplanationSet.getExplanations();
        int disagreement = 0;
        for (RuleExplanation ruleExplanation : ruleExplanations) {
            disagreement = ruleExplanation.getNumberIncorrectlyCovered();
        }
        return fMax - disagreement;
    }

    @Override
    public void initialize(RuleExplanationSet representationSpaceFoundation) {
        n = representationSpaceFoundation.getDataset().getNumberRows();
        RuleExplanation[] ruleExplanationArray = representationSpaceFoundation.getExplanations().toArray(new RuleExplanation[0]);
        nd = ruleExplanationArray;
        dl = ruleExplanationArray;
        wMax = calculateWMax(ruleExplanationArray);
        pMax = 2 * wMax * nd.length * dl.length;
        oMax = calculateOMax(wMax, nd.length, dl.length);
        oSMax = calculateOSMax(n, nd.length, dl.length);
        fMax = calculateFMax(n, nd.length, dl.length);
    }
}
