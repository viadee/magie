package de.viadee.xai.framework.adapter.local_explainer_adapter;

import de.viadee.xai.anchor.adapter.tabular.TabularInstance;
import de.viadee.xai.anchor.adapter.tabular.TabularPerturbationFunction;
import de.viadee.xai.anchor.algorithm.AnchorConstructionBuilder;
import de.viadee.xai.anchor.algorithm.AnchorResult;
import de.viadee.xai.anchor.algorithm.NoCandidateFoundException;
import de.viadee.xai.framework.adapter.black_box_classifier_adapter.BlackBoxClassifierAdapter;
import de.viadee.xai.framework.adapter.black_box_classifier_adapter.TabularRowToTabularInstance;
import de.viadee.xai.framework.data.Feature.CategoricalFeature;
import de.viadee.xai.framework.data.FeatureValue;
import de.viadee.xai.framework.data.tabular_data.LabelColumn;
import de.viadee.xai.framework.data.tabular_data.TabularDataset;
import de.viadee.xai.framework.data.tabular_data.TabularRow;
import de.viadee.xai.framework.explanation_calculation.explanation.RuleExplanation;
import de.viadee.xai.framework.explanation_calculation.explanation.RuleExplanationFactory;

import java.util.*;

import static de.viadee.xai.framework.utility.Utility.FIXED_EXECUTOR;
import static de.viadee.xai.framework.utility.Utility.NUMBER_THREADS;

/**
 * Wrapper for the Anchors algorithm as a local explainer. Contains the functionality to transform a TabularRow
 * into a data format understandable by Anchors and explain TabularRows accordingly.
 */
public class AnchorLocalExplainerAdapter
        implements LocalExplainerAdapter<TabularInstance> {
    protected TabularDataset<?, LabelColumn.CategoricalLabelColumn> dataset;
    protected TabularInstance[] tabularInstances;
    protected BlackBoxClassifierAdapter<TabularInstance> blackBoxClassifier;
    protected RuleExplanationFactory factory;
    protected TabularRowToTabularInstance transformer = new TabularRowToTabularInstance();
    protected final boolean sampleInput;
    protected final int numberSamples;
    protected final int seed;
    protected final int beamSize;
    protected final double tau;
    protected final double tauDiscrepancy;


    /**
     * Constructor for the AnchorLocalExplainerAdapter. Contains default parametrizations.
     * @param sampleInput Decides on whether the whole data set is used for the perturbation function?
     */
    public AnchorLocalExplainerAdapter(boolean sampleInput) {
        this.sampleInput = sampleInput;
        this.seed = 42;
        this.numberSamples = 4000;
        this.beamSize = 2;
        this.tau = 0.7;
        this.tauDiscrepancy = 0.05;

    }

    /**
     * Constructor for the AnchorLocalExplainerAdapter offering more configuration options.
     * @param sampleInput Decides on whether the whole data set is used for the perturbation function?
     * @param numberSamples How many instances should be used to construct the perturbation function?
     * @param seed Determine the seed based on which the sampling is conducted.
     * @param beamSize Determines the beam size used in the beam search of the Anchors algorithm.
     * @param tau Demanded minimum precision of a local explanation constructed by Anchors.
     * @param tauDiscrepancy Demanded certainty concerning the parameter tau.
     */
    public AnchorLocalExplainerAdapter(boolean sampleInput,
                                       int numberSamples,
                                       int seed,
                                       int beamSize,
                                       int tau,
                                       int tauDiscrepancy) {
        this.sampleInput = sampleInput;
        this.numberSamples = numberSamples;
        this.seed = seed;
        this.beamSize = beamSize;
        this.tau = tau;
        this.tauDiscrepancy = tauDiscrepancy;

    }

    private TabularInstance getTabularInstance(int i) {
        TabularRow tabularRow = dataset.getInstance(i);
        return transformer.apply(tabularRow);
    }

    @Override
    public RuleExplanation explain(TabularRow toExplain) {
        TabularInstance toExplainInstance = transformer.apply(toExplain);
        TabularPerturbationFunction tbf = new TabularPerturbationFunction(toExplainInstance, tabularInstances);
        AnchorConstructionBuilder<TabularInstance> anchorConstructionBuilder
                = new AnchorConstructionBuilder<>(
                        blackBoxClassifier::apply,
                        tbf,
                        toExplainInstance,
                ((FeatureValue.CategoricalFeatureValue) toExplain.getProcessedLabelValue()).getValue()
                ).enableThreading(NUMBER_THREADS, FIXED_EXECUTOR, null)
                .setTau(tau)
                .setTauDiscrepancy(tauDiscrepancy)
                .setBeamSize(beamSize);

        try {
            AnchorResult<TabularInstance> anchorResult =
                    anchorConstructionBuilder.build().constructAnchor();
            return generateRuleExplanation(anchorResult);
        } catch (NoCandidateFoundException e) {
            e.printStackTrace();
            FeatureValue.CategoricalFeatureValue label = (FeatureValue.CategoricalFeatureValue) toExplain.getProcessedLabelValue();
            return factory.initialize(new HashMap<>(), label.getFeature(), label.getValue());
        }
    }


    protected RuleExplanation generateRuleExplanation(AnchorResult<TabularInstance> anchorResult) {
        TabularInstance tabularInstance = anchorResult.getInstance();
        List<Integer> orderedAnchorFeatures = anchorResult.getOrderedFeatures();

        Map<CategoricalFeature, Set<Integer>> conditions = new HashMap<>();
        for (int i : orderedAnchorFeatures) {
            CategoricalFeature currentFeature = dataset
                    .getProcessedCatFeatureForName(
                            tabularInstance
                                    .getFeatures()[i]
                                    .getName()
                    );

            conditions.putIfAbsent(
                    currentFeature,
                    new HashSet<>()
            );
            conditions.get(currentFeature).add(tabularInstance.getValue(i));
        }
        int labelValue = tabularInstance.getDiscretizedLabel();
        CategoricalFeature labelFeature = dataset.getProcessedLabelCol().getLabel();
        return factory.initialize(conditions, labelFeature, labelValue);
    }

    @Override
    public void initialize(
            BlackBoxClassifierAdapter<TabularInstance> blackBoxClassifierAdapter,
            RuleExplanationFactory ruleExplanationFactory
    ) {
        this.dataset = ruleExplanationFactory.getDataset();
        this.factory = ruleExplanationFactory;
        if (sampleInput) {
            List<TabularInstance> tabularInstances = new ArrayList<>();
            for (int i = 0; i < dataset.getNumberRows(); i++) {
                tabularInstances.add(getTabularInstance(i));
            }
            int maximumSize = Math.min(numberSamples, tabularInstances.size());
            this.tabularInstances = new TabularInstance[maximumSize];
            Collections.shuffle(tabularInstances, new Random(seed));
            for (int i = 0; i < tabularInstances.size() && i < numberSamples; i++) {
                this.tabularInstances[i] = tabularInstances.get(i);
            }
        } else {
            this.tabularInstances = new TabularInstance[dataset.getNumberRows()];
            for (int i = 0; i < tabularInstances.length; i++) {
                this.tabularInstances[i] = getTabularInstance(i);
            }
        }
        this.blackBoxClassifier = blackBoxClassifierAdapter;
    }
}
