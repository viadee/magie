package de.viadee.xai.framework.global_explanation_procedure_step.postprocessor;

import de.viadee.xai.framework.data.tabular_data.LabelColumn;
import de.viadee.xai.framework.data.tabular_data.TabularDataset;
import de.viadee.xai.framework.explanation_calculation.calculator.RoaringBitmapCalculator;
import de.viadee.xai.framework.explanation_calculation.explanation.*;
import de.viadee.xai.framework.utility.Utility;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Derives the baseline accuracy of a dataset given the label's frequency in the dataset.
 * Thereafter, compares the roundPrecision of single rule explanations with this baseline accuracy.
 * If certain rules were calculated on a different dataset than the one used in this class,
 * they are reevaluated.
 */
public class BaselineRuleExplanationFilter implements Postprocessor {

    protected TabularDataset<?, LabelColumn.CategoricalLabelColumn> dataset;
    protected RoaringBitmapCalculator calculator;
    protected RuleExplanationFactory ruleExplanationFactory;
    protected RuleExplanationSetFactory<?> ruleExplanationSetFactory;

    /**
     * Constructor for BaselineRuleExplanationFilter.
     * @param calculator The calculator with which is used to transform RuleExplanations and on which
     *                the resulting RuleExplanationSet's cover is computed.
     */
    public BaselineRuleExplanationFilter(RoaringBitmapCalculator calculator) {
        this.ruleExplanationSetFactory = new StdRuleExplanationSetFactory(calculator);
        this.ruleExplanationFactory = new MinimalCoversRuleExplanationFactory(calculator);
        this.dataset = calculator.getDataset();
        this.calculator = calculator;
    }

    /**
     * Constructor for BaselineRuleExplanationFilter. The data of the passed RuleExplanationSetFactory
     * and the RuleExplanationFactory can differ. The roundPrecision is only calculated using the
     * dataset of the RuleExplanationFactory.
     */
    public BaselineRuleExplanationFilter() {}

    @Override
    public RuleExplanationSet postprocess(RuleExplanationSet toProcess) {
        Set<RuleExplanation> rules = toProcess.getExplanations();

        rules = Utility.translateRuleExplanationsWithFactory(rules, ruleExplanationFactory);

        List<RuleExplanation> resultingExplanations = new ArrayList<>();
        int labelFrequency = calculator.getNumberCovered(toProcess.getLabelFeature(), toProcess.getLabelValue());
        double baselinePrecision = ((double) labelFrequency) / this.dataset.getNumberRows();

        for (RuleExplanation ruleExplanation : rules) {
            if (ruleExplanation.getPrecision() > baselinePrecision) {
                resultingExplanations.add(ruleExplanation);
            }
        }

        return ruleExplanationSetFactory.newWithCollection(toProcess.getLabelFeature(), toProcess.getLabelValue(), resultingExplanations);
    }

    @Override
    public void initialize(RuleExplanationFactory ruleExplanationFactory, RuleExplanationSetFactory ruleExplanationSetFactory) {
        this.calculator = ruleExplanationFactory.getCalculator();
        this.ruleExplanationFactory = ruleExplanationFactory;
        this.ruleExplanationSetFactory = ruleExplanationSetFactory;
        this.dataset = ruleExplanationFactory.getDataset();
    }
}
