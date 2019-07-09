package de.viadee.xai.framework.global_explanation_procedure_step.explanation_mapper;

import de.viadee.xai.framework.adapter.local_explainer_adapter.LocalExplainerAdapter;
import de.viadee.xai.framework.data.Feature.CategoricalFeature;
import de.viadee.xai.framework.explanation_calculation.calculator.RoaringBitmapCalculator;
import de.viadee.xai.framework.explanation_calculation.explanation.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Contains a procedure to iteratively generate local explanations. At each iteration
 * it is checked if specifiable criteria are reached. The resulting set of explanations are
 * returned.
 */
public class ModifiedMAGIXExplanationMapper
        implements ExplanationMapper {

    protected RuleExplanationFactory ruleExplanationFactory;
    protected RuleExplanationSetFactory<?> ruleExplanationSetFactory;
    protected final BiFunction<Integer, RuleExplanationSet, Boolean> rowAcceptor;
    protected final Function<RuleExplanation, RuleExplanation[]> ruleBreaker;
    protected final Function<RuleExplanation, Boolean> ruleAcceptor;
    protected final Function<RuleExplanationSet, Boolean> terminationCriterion;
    protected LocalExplainerAdapter<?> localExplainerAdapter;

    /**
     * Constructor for ModifiedMAGIXExplanationMapper.
     * @param calculator Calculator to evaluate the created RuleExplanation and RuleExplanationSets.
     * @param localExplainerAdapter The local explainer used for the mapping procedure.
     */
    public ModifiedMAGIXExplanationMapper(final RoaringBitmapCalculator calculator,
                                          final LocalExplainerAdapter<?> localExplainerAdapter) {
        this.localExplainerAdapter = localExplainerAdapter;
        ruleExplanationFactory = new StdRuleExplanationFactory(calculator);
        ruleExplanationSetFactory = new StdRuleExplanationSetFactory(calculator);
        rowAcceptor = (index, ruleExplanationSet) -> (!(ruleExplanationSet.getCoverAsBitmap().contains(index)));
        ruleBreaker = this::leaveAsWhole;
        ruleAcceptor = (ruleAcceptor) -> true;
        terminationCriterion = (ruleExplanationSet -> ruleExplanationSet.getNumberCoveredInstances() == calculator.getDataset().getNumberRows());
    }

    /**
     * Default constructor for ModifiedMAGIXExplanationMapper.
     */
    public ModifiedMAGIXExplanationMapper() {
        this(
                (index, ruleExplanationSet) ->
                        (!(ruleExplanationSet.getCoverAsBitmap().contains(index))),
                false,
                (r) -> true,
                (ruleExplanationSet) ->
                        (ruleExplanationSet.getNumberCoveredInstances() ==
                                ruleExplanationSet.getDataset().getNumberRows())
        );
    }

    /**
     * Extended constructor for ModifiedMAGIXExplanationMapper.
     * @param rowAcceptor The function determining which rows will be locally explained.
     * @param breakUpRules Should the single conditions be used to calculate the already covered instances (=true), or
     *                     should the whole antecedent be used?
     * @param ruleAcceptor Function determining which rules are added to the result set.
     * @param terminationCriterion Function representing the termination criterion.
     */
    public ModifiedMAGIXExplanationMapper(final BiFunction<Integer, RuleExplanationSet, Boolean> rowAcceptor,
                                          final boolean breakUpRules,
                                          final Function<RuleExplanation, Boolean> ruleAcceptor,
                                          final Function<RuleExplanationSet, Boolean> terminationCriterion) {
        this.rowAcceptor = rowAcceptor;
        if (breakUpRules) {
            this.ruleBreaker = this::breakUpRuleExplanation;
        } else {
            this.ruleBreaker = this::leaveAsWhole;
        }
        this.ruleAcceptor = ruleAcceptor;
        this.terminationCriterion = terminationCriterion;
    }

    /**
     * Constructor for ModifiedMAGIXExplanationMapper.
     * @param localExplainerAdapter The local explainer used for the mapping procedure.
     * @param ruleExplanationFactory The factory for creating RuleExplanations.
     * @param ruleExplanationSetFactory The factory for creating RuleExplanationSets.
     * @param rowAcceptor A function specifying which rows containing the same label as specified
     *                    in {@link ModifiedMAGIXExplanationMapper#mapExplanations(int)}
     *                    are eligible to be locally explained. For example, instances already covered by a
     *                    RuleExplanation of a previous iteration might be discarded.
     * @param breakUpRules If true, the RuleExplanations created by the local explainer are "broken up", i.e.
     *                     for each condition contained in the Rule, a separate RuleExplanation will be created.
     *                     If false, the rules generated by the local explainer are used as-is.
     * @param ruleAcceptor For the created Rules, after potentially breaking them up, they are only accepted if this
     *                     function evaluates to true.
     * @param terminationCriterion If the RuleExplanationSet satisfies a certain restriction,
     *                      {@link ModifiedMAGIXExplanationMapper#mapExplanations(int)} will terminate.
     */
    public ModifiedMAGIXExplanationMapper(final LocalExplainerAdapter<?> localExplainerAdapter,
                                          final RuleExplanationFactory ruleExplanationFactory,
                                          final RuleExplanationSetFactory<?> ruleExplanationSetFactory,
                                          final BiFunction<Integer, RuleExplanationSet, Boolean> rowAcceptor,
                                          final boolean breakUpRules,
                                          final Function<RuleExplanation, Boolean> ruleAcceptor,
                                          final Function<RuleExplanationSet, Boolean> terminationCriterion) {
        this.ruleExplanationFactory = ruleExplanationFactory;
        this.ruleExplanationSetFactory = ruleExplanationSetFactory;
        this.rowAcceptor = rowAcceptor;
        this.localExplainerAdapter = localExplainerAdapter;
        if (breakUpRules) {
            this.ruleBreaker = this::breakUpRuleExplanation;
        } else {
            this.ruleBreaker = this::leaveAsWhole;
        }
        this.ruleAcceptor = ruleAcceptor;
        this.terminationCriterion = terminationCriterion;
    }

    @Override
    public RuleExplanationSet mapExplanations(int labelValue) {

        int[] datasetLabels = ruleExplanationSetFactory.getDataset().getProcessedLabelCol().getValues();
        RuleExplanationSet result = ruleExplanationSetFactory
                .newEmpty(ruleExplanationSetFactory.getDataset()
                        .getProcessedLabelCol()
                        .getLabel(), labelValue);
        for (int rowNumber = 0; rowNumber < result.getDataset().getNumberRows(); rowNumber++) {
            if (datasetLabels[rowNumber] == labelValue &&
                rowAcceptor.apply(rowNumber, result)) {
                RuleExplanation ruleExplanation = localExplainerAdapter.explain(result.getDataset().getInstance(rowNumber));
                RuleExplanation[] brokenUpRule = ruleBreaker.apply(ruleExplanation);
                for (RuleExplanation re : brokenUpRule) {
                    if (ruleAcceptor.apply(re)) {
                        result = ruleExplanationSetFactory.newCopyWith(result, re);
                    }
                }
                if (terminationCriterion.apply(result)) {
                    break;
                }
            }
        }
        return result;
    }

    private RuleExplanation[] breakUpRuleExplanation(RuleExplanation toBreak) {
        RuleExplanation[] ruleExplanations = new RuleExplanation[toBreak.getNumberConditionValues()];
        int count = 0;
        for (Map.Entry<CategoricalFeature, Set<Integer>> conditions : toBreak.getConditions().entrySet()) {
            for (Integer i : conditions.getValue()) {
                Map<CategoricalFeature, Set<Integer>> currentConditionLinking = new HashMap<>();
                currentConditionLinking.put(conditions.getKey(), new HashSet<>());
                currentConditionLinking.get(conditions.getKey()).add(i);
                ruleExplanations[count] = ruleExplanationFactory
                        .initialize(currentConditionLinking,
                                toBreak.getLabelFeature(),
                                toBreak.getLabelValue());
                count++;
            }
        }
        return ruleExplanations;
    }

    private RuleExplanation[] leaveAsWhole(RuleExplanation notToBreak) {
        return new RuleExplanation[] {ruleExplanationFactory.translateWithData(notToBreak)};
    }

    @Override
    public void setLocalExplainer(LocalExplainerAdapter<?> localExplainerAdapter) {
        this.localExplainerAdapter = localExplainerAdapter;
    }

    @Override
    public void initialize(RuleExplanationFactory ruleExplanationFactory, RuleExplanationSetFactory ruleExplanationSetFactory) {
        this.ruleExplanationFactory = ruleExplanationFactory;
        this.ruleExplanationSetFactory = ruleExplanationSetFactory;
    }
}
