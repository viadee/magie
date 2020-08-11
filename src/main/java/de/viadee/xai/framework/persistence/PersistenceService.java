package de.viadee.xai.framework.persistence;

import de.viadee.xai.framework.data.index.RoaringBitmapIndex;
import de.viadee.xai.framework.data.tabular_data.LabelColumn.CategoricalLabelColumn;
import de.viadee.xai.framework.data.tabular_data.TabularDataset;
import de.viadee.xai.framework.explanation_calculation.calculator.RoaringBitmapCalculator;
import de.viadee.xai.framework.explanation_calculation.explanation.RuleExplanationSet;
import de.viadee.xai.framework.explanation_pipeline.ExplanationPipeline;
import de.viadee.xai.framework.persistence.calculator_persistence_service.CalculatorPersistenceService;
import de.viadee.xai.framework.persistence.calculator_persistence_service.NullCalculatorPersister;
import de.viadee.xai.framework.persistence.data_persistence_service.DataPersistenceService;
import de.viadee.xai.framework.persistence.data_persistence_service.NullDataPersister;
import de.viadee.xai.framework.persistence.explanation_persistence_service.ExplanationPersistenceService;
import de.viadee.xai.framework.persistence.explanation_persistence_service.NullExplanationPersister;
import de.viadee.xai.framework.utility.Tuple;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Standard implementation of a facade-PersistenceService.
 * Utilizes specific persistence service-classes to persist explanations, calculators, data sets, and indexes.
 */
public class PersistenceService { // TODO Setup persistence if at least one set is to be stored.

    protected final ExplanationPersistenceService explanationPersistenceService;
    protected final DataPersistenceService dataPersistenceService;
    protected final CalculatorPersistenceService calculatorPersistenceService;

    /**
     * Constructor for PersistenceService.
     * @param explanationPersistenceService The persister for explanations.
     * @param dataPersistenceService The persister for indexes and data sets.
     * @param calculatorPersistenceService The persister for calculators.
     */
    public PersistenceService(ExplanationPersistenceService explanationPersistenceService,
                              DataPersistenceService dataPersistenceService,
                              CalculatorPersistenceService calculatorPersistenceService) {
        if (explanationPersistenceService == null) {
            this.explanationPersistenceService = new NullExplanationPersister();
        } else {
            this.explanationPersistenceService = explanationPersistenceService;
        }
        if (dataPersistenceService == null) {
            this.dataPersistenceService = new NullDataPersister();
        } else {
            this.dataPersistenceService = dataPersistenceService;
        }
        if (calculatorPersistenceService == null) {
            this.calculatorPersistenceService = new NullCalculatorPersister();
        } else {
            this.calculatorPersistenceService = calculatorPersistenceService;
        }
    }

    /**
     * Persists the given {@link RuleExplanationSet}s for a given step.
     * @param ruleExplanationSets The to-be-persisted sets.
     * @param stepNumber The number of the conducted step.
     * @return A mapping from the label value to the ID of the persisted {@link RuleExplanationSet}.
     */
    public Map<Integer, Integer> persistExplanations(List<RuleExplanationSet> ruleExplanationSets, int stepNumber) {
        return explanationPersistenceService.persistExplanations(ruleExplanationSets, stepNumber);
    }

    /**
     * Persists the given calculator.
     * @param calculator The calculator.
     * @return The ID of the persisted calculator.
     */
    public int persistCalculator(RoaringBitmapCalculator calculator) {
        return calculatorPersistenceService.persistCalculator(calculator);
    }

    /**
     * Persists the given data set and index.
     * @param data The data set.
     * @param index The index.
     * @return The ID of the persisted data set and index.
     */
    public int persistData(TabularDataset<CategoricalLabelColumn, CategoricalLabelColumn> data,
                               RoaringBitmapIndex index) {
        return dataPersistenceService.persistData(data, index);
    }

    /**
     * Loads previously persisted explanations.
     * @param id The ID of the corresponding set.
     * @return The loaded {@link RuleExplanationSet}.
     */
    public RuleExplanationSet loadExplanations(int id) {
        return explanationPersistenceService.loadExplanations(id);
    }

    /**
     * Loads multiple {@link RuleExplanationSet}s with different labels. They must exhibit different labels as
     * otherwise, they cannot be further processed unambiguously. This is because {@link ExplanationPipeline}s
     * are executed in a per-label fashion.
     * @param ids The IDs of {@link RuleExplanationSet} which necessarily must have different labels.
     * @return The set of {@link RuleExplanationSet}s.
     */
    public Set<RuleExplanationSet> loadExplanationsForAllLabels(int[] ids) {
        return explanationPersistenceService.loadExplanationsForAllLabels(ids);
    }

    /**
     * Loads the specified calculator.
     * @param id The ID of the calculator.
     * @return The loaded calculator.
     */
    public RoaringBitmapCalculator loadCalculator(int id) {
        return calculatorPersistenceService.loadCalculator(id);
    }

    /**
     * Loads the specified {@link TabularDataset} and {@link RoaringBitmapIndex}.
     * @param id The ID determining the tuple to be loaded.
     * @return The tuple of {@link TabularDataset} and {@link RoaringBitmapIndex}.
     */
    public Tuple<TabularDataset<CategoricalLabelColumn, CategoricalLabelColumn>, RoaringBitmapIndex>
    loadData(int id) {
        return dataPersistenceService.loadData(id);
    }

    /**
     * Terminates all used sub-persistence services.
     */
    public void terminate() {
        calculatorPersistenceService.terminate();
        dataPersistenceService.terminate();
        explanationPersistenceService.terminate();
    }

}
