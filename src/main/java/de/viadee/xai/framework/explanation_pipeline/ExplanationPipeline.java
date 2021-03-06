package de.viadee.xai.framework.explanation_pipeline;

import de.viadee.xai.anchor.adapter.tabular.AnchorTabular;
import de.viadee.xai.framework.adapter.black_box_classifier_adapter.BlackBoxClassifierAdapter;
import de.viadee.xai.framework.adapter.data_source_adapter.DataSourceAdapter;
import de.viadee.xai.framework.adapter.local_explainer_adapter.LocalExplainerAdapter;
import de.viadee.xai.framework.data.index.RoaringBitmapIndex;
import de.viadee.xai.framework.data.index.SimpleRoaringBitmapIndex;
import de.viadee.xai.framework.data.tabular_data.LabelColumn.CategoricalLabelColumn;
import de.viadee.xai.framework.data.tabular_data.TabularDataset;
import de.viadee.xai.framework.exception.PipelineExecutionFailed;
import de.viadee.xai.framework.explanation_calculation.calculator.RoaringBitmapCalculator;
import de.viadee.xai.framework.explanation_calculation.calculator.SimpleRoaringBitmapCalculator;
import de.viadee.xai.framework.explanation_calculation.explanation.*;
import de.viadee.xai.framework.explanation_visualizer.ExplanationVisualizer;
import de.viadee.xai.framework.global_explanation_procedure_step.ExplanationProcedureStep;
import de.viadee.xai.framework.global_explanation_procedure_step.explanation_mapper.AllConditionsMapper;
import de.viadee.xai.framework.global_explanation_procedure_step.explanation_mapper.ExplanationMapper;
import de.viadee.xai.framework.global_explanation_procedure_step.explanation_structurer.ExplanationStructurer;
import de.viadee.xai.framework.global_explanation_procedure_step.explanation_structurer.NullRuleExplanationStructurer;
import de.viadee.xai.framework.global_explanation_procedure_step.optimizer.Optimizer;
import de.viadee.xai.framework.global_explanation_procedure_step.postprocessor.Postprocessor;
import de.viadee.xai.framework.persistence.PersistenceService;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * The implementation of a concrete workflow, i.e., a global explanation procedure approach.
 * @param <I> The chosen data type utilized by the {@link LocalExplainerAdapter}.
 * @param <F> The chosen visualization for the generated global explanation.
 */
public abstract class ExplanationPipeline< // TODO Create interface, make this the standard template.
        I,
        F> {

    protected final PersistenceService persistenceService;
    protected final PipelineContext context;
    protected final BlackBoxClassifierAdapter<I> blackBoxClassifierAdapter;
    protected final DataSourceAdapter dataSourceAdapter;
    protected final LocalExplainerAdapter<I> localExplainerAdapter;
    protected ExplanationMapper explanationMapper;
    protected Map<Integer, Optimizer<RuleExplanation, ?, ?, ?>> ruleOptimizers = new HashMap<>();
    protected Map<Integer, Postprocessor> postprocessors = new HashMap<>();
    protected Map<Integer, Optimizer<RuleExplanationSet, ?, ?, ?>> ruleSetOptimizers = new HashMap<>();
    protected int stepCount = 0;
    protected ExplanationStructurer explanationStructurer;
    protected ExplanationVisualizer<F> explanationVisualizer;
    protected TabularDataset<CategoricalLabelColumn, CategoricalLabelColumn> dataset;
    protected TabularDataset<CategoricalLabelColumn, CategoricalLabelColumn> testData;

    protected RoaringBitmapIndex trainingIndex;
    protected RoaringBitmapIndex testIndex;
    protected RoaringBitmapCalculator calculatorTraining;
    protected RoaringBitmapCalculator calculatorTest;
    protected RuleExplanationFactory countingRuleExplanationFactoryTraining;
    protected RuleExplanationFactory coverRuleExplanationFactoryTraining;
    protected RuleExplanationSetFactory ruleExplanationSetFactoryTraining;

    protected RuleExplanationFactory countingRuleExplanationFactoryTest;
    protected RuleExplanationFactory coverRuleExplanationFactoryTest;
    protected RuleExplanationSetFactory ruleExplanationSetFactoryTest;

    protected final int STORE_FOR_MAPPER = -1;

    protected Map<Integer, List<RuleExplanationSet>> storedResultsForSteps = new HashMap<>();

    protected Logger logger;

    /**
     * Constructor for ExplanationPipeline.
     * @param context A context can be passed carrying persistence data or other parameters.
     * @param dataSourceAdapter The DataSourceAdapter.
     * @param blackBoxClassifierAdapter The BlackBoxClassifierAdapter.
     * @param localExplainerAdapter The LocalExplainerAdapter.
     * @param explanationVisualizer The ExplanationVisualizer.
     * @param persistenceService The PersistenceService.
     */
    public ExplanationPipeline(PipelineContext context,
                               DataSourceAdapter dataSourceAdapter,
                               BlackBoxClassifierAdapter<I> blackBoxClassifierAdapter,
                               LocalExplainerAdapter<I> localExplainerAdapter,
                               ExplanationVisualizer<F> explanationVisualizer,
                               PersistenceService persistenceService) {
        this.persistenceService = persistenceService;
        this.context = context;
        this.dataSourceAdapter = dataSourceAdapter;
        this.blackBoxClassifierAdapter = blackBoxClassifierAdapter;
        this.localExplainerAdapter = localExplainerAdapter;
        this.explanationVisualizer = explanationVisualizer;
        if (!validatePipelineContext(context)) {
            throw new PipelineExecutionFailed("The validation of the PipelineContext failed.");
        }
        this.logger = Logger.getLogger(ExplanationPipeline.class.getName());
    }

    /**
     * Executes the overall pipeline.
     * @return The {@link GlobalExplanationContainer} carrying the visualization as well as the {@link RuleExplanationSet}.
     */
    public GlobalExplanationContainer<F> executePipeline() {
        AnchorTabular anchorTabular =
                dataSourceAdapter.loadDataset();
        logger.info("Data adapter loaded AnchorTabular.");

        prepareDataset(anchorTabular);
        logger.info("Data set was prepared.");

        splitData();
        logger.info("splitData() has finished.");

        prepareIndex();
        logger.info("prepareIndex() has finished.");

        prepareCalculator();
        logger.info("prepareCalculator() has finished.");

        prepareExplanationFactories();
        logger.info("prepareExplanationFactories() has finished.");

        prepareExplanationSetFactories();
        logger.info("prepareExplanationSetFactories() has finished.");

        boolean enforceRuleMining = false;

        if (localExplainerAdapter != null) {
            if (blackBoxClassifierAdapter != null) {
                localExplainerAdapter.initialize(blackBoxClassifierAdapter, coverRuleExplanationFactoryTraining);
                logger.info("LocalExplainerAdapter.initialize(...) has finished.");
            } else {
                logger.warn("No black box classifier has been found; the data will be mined directly!");
                enforceRuleMining = true;
            }
        }

        addSteps();
        logger.info("addSteps() has finished.");

        if (explanationMapper == null || enforceRuleMining) {
            explanationMapper =
                    new AllConditionsMapper();
            explanationMapper.initialize(coverRuleExplanationFactoryTraining, ruleExplanationSetFactoryTraining);
            logger.info("AllConditionsMapper was initialized.");
        }

        if (explanationStructurer == null) {
            explanationStructurer = new NullRuleExplanationStructurer();
            logger.info("NullRuleExplanationStructurer was initialized.");
        }

        Set<RuleExplanationSet> resultingSets = executeSteps(dataset.getProcessedLabelCol().getLabel().getUniqueNumberRepresentations());
        logger.info("executeSteps(...) has finished.");

        List<Map<Integer, Set<RuleExplanation>>> structuredExplanations = explanationStructurer.structure(resultingSets);
        logger.info("ExplanationStructurer was executed.");

        Map<Integer, Map<Integer, Integer>> idsOfPersistedExplanations = persistExplanations();
        logger.info("persistExplanations() has finished.");

        persistCalculator();
        logger.info("persistCalculator() has finished.");

        persistData();
        logger.info("persistData() has finished.");

        persistenceService.terminate();
        logger.info("PersistenceService was terminated.");

        F visualizedResult = explanationVisualizer.visualize(structuredExplanations);
        logger.info("ExplanationVisualizer was executed.");

        return new GlobalExplanationContainer<>(visualizedResult, idsOfPersistedExplanations);
    }

    protected void persistCalculator() {
        // TODO
    }

    protected void persistData() {
        // TODO
    }

    protected Map<Integer, Map<Integer, Integer>> persistExplanations() {
        Map<Integer, Map<Integer, Integer>> labelToStepIds = new HashMap<>();
        for (Map.Entry<Integer, List<RuleExplanationSet>> entry : storedResultsForSteps.entrySet()) {
            labelToStepIds.put(entry.getKey(), persistenceService.persistExplanations(entry.getValue(), entry.getKey()));
        }
        return labelToStepIds;
    }

    /**
     * Adds a postprocessor to the pipeline.
     * @param toAdd The postprocesor.
     * @return The ExplanationPipeline for chaining.
     */
    public ExplanationPipeline<I, F> add(Postprocessor toAdd) {
        return add(toAdd, false);
    }

    /**
     * Adds a rule optimizer to the pipeline.
     * @param toAdd The rule optimizer.
     * @return The ExplanationPipeline for chaining.
     */
    public ExplanationPipeline<I, F>
    addRuleOptimizer(Optimizer<RuleExplanation, RuleExplanationFactory, ?, ?> toAdd) {
        return addRuleOptimizer(toAdd, false);
    }

    /**
     * Adds a rule set optimizer to the pipeline.
     * @param toAdd The rule set optimizer.
     * @return The ExplanationPipeline for chaining.
     */
    public ExplanationPipeline<I, F>
    addRuleSetOptimizer(Optimizer<RuleExplanationSet, RuleExplanationSetFactory, ?, ?> toAdd) {
        return addRuleSetOptimizer(toAdd, false);
    }

    /**
     * Adds the explanation mapper to the pipeline.
     * @param toAdd The explanation mapper.
     * @return The ExplanationPipeline for chaining.
     */
    public ExplanationPipeline<I, F> add(ExplanationMapper toAdd) {
        return add(toAdd, false);
    }

    /**
     * Adds the explanation structurer to the pipeline.
     * @param toAdd The explanation structurer
     * @return The ExplanationPipeline for chaining.
     */
    public ExplanationPipeline<I, F> add(ExplanationStructurer toAdd) {
        this.explanationStructurer = toAdd;
        return this;
    }

    /**
     * Adds a postprocessor to the pipeline. Possibly, declares the output of this step to be persisted.
     * @param toAdd The postprocessor.
     * @param store If true, the step's result is persisted.
     * @return The ExplanationPipeline for chaining.
     */
    public ExplanationPipeline<I, F> add(Postprocessor toAdd, boolean store) {
        initializeStep(toAdd, false, false, store, stepCount);
        this.postprocessors.put(stepCount, toAdd);
        stepCount++;
        return this;
    }

    /**
     * Adds a rule set optimizer to the pipeline. Possibly, declares the output of this step to be persisted.
     * @param toAdd The rule set optimizer.
     * @param store If true, the step's result is persisted.
     * @return The ExplanationPipeline for chaining.
     */
    public ExplanationPipeline<I, F>
    addRuleSetOptimizer(Optimizer<RuleExplanationSet, RuleExplanationSetFactory, ?, ?> toAdd, boolean store) {
        initializeOptimizationStep(toAdd, store, stepCount, ruleExplanationSetFactoryTraining, ruleExplanationSetFactoryTraining);
        this.ruleSetOptimizers.put(stepCount, toAdd);
        stepCount++;
        return this;
    }

    /**
     * Adds a rule optimizer to the pipeline. Possibly, declares the output of this step to be persisted.
     * @param toAdd The rule optimizer.
     * @param store If true, the step's result is persisted.
     * @return The ExplanationPipeline for chaining.
     */
    public ExplanationPipeline<I, F>
    addRuleOptimizer(Optimizer<RuleExplanation, RuleExplanationFactory, ?, ?> toAdd, boolean store) {
        initializeOptimizationStep(toAdd, store, stepCount, countingRuleExplanationFactoryTraining, ruleExplanationSetFactoryTraining);
        this.ruleOptimizers.put(stepCount, toAdd);
        stepCount++;
        return this;
    }

    /**
     * Adds the explanation mapper to the pipeline. Possibly, declares the output of this step to be persisted.
     * @param toAdd The explanation mapper.
     * @param store If true, the step's result is persisted.
     * @return The ExplanationPipeline for chaining.
     */
    public ExplanationPipeline<I, F> add(ExplanationMapper toAdd, boolean store) {
        initializeStep(toAdd, false, true, store, STORE_FOR_MAPPER);
        this.explanationMapper = toAdd;
        this.explanationMapper.setLocalExplainer(localExplainerAdapter);
        return this;
    }

    /**
     * Adds a postprocessor to the pipeline.
     * The postprocessor will utilize the test data set.
     * @param toAdd The postprocessor.
     * @return The ExplanationPipeline for chaining.
     */
    public ExplanationPipeline<I, F> addWithTest(Postprocessor toAdd) {
        return addWithTest(toAdd, false);
    }

    /**
     * Adds a rule optimizer to the pipeline.
     * The rule optimizer will utilize the test data set.
     * @param toAdd The rule optimizer.
     * @return The ExplanationPipeline for chaining.
     */
    public ExplanationPipeline<I, F>
    addRuleOptimizerWithTest(Optimizer<RuleExplanation, RuleExplanationFactory, ?, ?> toAdd) {
        return addRuleOptimizerWithTest(toAdd, false);
    }

    /**
     * Adds a rule set optimizer to the pipeline.
     * The rule set optimizer will utilize the test data set.
     * @param toAdd The rule set optimizer.
     * @return The ExplanationPipeline for chaining.
     */
    public ExplanationPipeline<I, F>
    addRuleSetOptimizerWithTest(Optimizer<RuleExplanationSet, RuleExplanationSetFactory, ?, ?> toAdd) {
        return addRuleSetOptimizerWithTest(toAdd, false);
    }

    /**
     * Adds the explanation mapper to the pipeline.
     * The explanation mapper will utilize the test data set.
     * @param toAdd The explanation mapper.
     * @return The ExplanationPipeline for chaining.
     */
    public ExplanationPipeline<I, F> addWithTest(ExplanationMapper toAdd) {
        return addWithTest(toAdd, false);
    }


    /**
     * Adds a postprocessor to the pipeline. Possibly, declares the output of this step to be persisted.
     * The postprocessor will utilize the test data set.
     * @param toAdd The postprocessor.
     * @param store If true, the step's result is persisted.
     * @return The ExplanationPipeline for chaining.
     */
    public ExplanationPipeline<I, F> addWithTest(Postprocessor toAdd, boolean store) {
        checkTestExists();
        initializeStep(toAdd, true, false, store, stepCount);
        this.postprocessors.put(stepCount, toAdd);
        stepCount++;
        return this;
    }

    /**
     * Adds a rule set optimizer to the pipeline. Possibly, declares the output of this step to be persisted.
     * The rule set optimizer will utilize the test data set.
     * @param toAdd The rule set optimizer.
     * @param store If true, the step's result is persisted.
     * @return The ExplanationPipeline for chaining.
     */
    public ExplanationPipeline<I, F>
    addRuleSetOptimizerWithTest(Optimizer<RuleExplanationSet, RuleExplanationSetFactory, ?, ?> toAdd, boolean store) {
        checkTestExists();
        initializeOptimizationStep(toAdd, store, stepCount, ruleExplanationSetFactoryTest, ruleExplanationSetFactoryTest);
        this.ruleSetOptimizers.put(stepCount, toAdd);
        stepCount++;
        return this;
    }

    /**
     * Adds a rule optimizer to the pipeline. Possibly, declares the output of this step to be persisted.
     * The rule optimizer will utilize the test data set.
     * @param toAdd The rule optimizer.
     * @param store If true, the step's result is persisted.
     * @return The ExplanationPipeline for chaining.
     */
    public ExplanationPipeline<I, F>
    addRuleOptimizerWithTest(Optimizer<RuleExplanation, RuleExplanationFactory, ?, ?> toAdd, boolean store) {
        checkTestExists();
        initializeOptimizationStep(toAdd, store, stepCount, countingRuleExplanationFactoryTest, ruleExplanationSetFactoryTest);
        this.ruleOptimizers.put(stepCount, toAdd);
        stepCount++;
        return this;
    }

    /**
     * Adds the explanation mapper to the pipeline. Possibly, declares the output of this step to be persisted.
     * The explanation mapper will utilize the test data set.
     * @param toAdd The explanation mapper.
     * @param store If true, the step's result is persisted.
     * @return The ExplanationPipeline for chaining.
     */
    public ExplanationPipeline<I, F> addWithTest(ExplanationMapper toAdd, boolean store) {
        checkTestExists();
        initializeStep(toAdd, true, true, store, stepCount);
        this.explanationMapper = toAdd;
        this.explanationMapper.setLocalExplainer(localExplainerAdapter);
        return this;
    }

    protected void initializeStep(
            ExplanationProcedureStep<RuleExplanationFactory> toAdd,
            boolean useTestData,
            boolean useCountingCalculator,
            boolean store,
            int stepNumber
    ) {
        initializeProcedureStep(toAdd, useTestData, useCountingCalculator);
        if (store) {
            store(stepNumber);
        }
    }

    protected <Fa> void initializeOptimizationStep(
            Optimizer<?, Fa, ?, ?> optimizer,
            boolean store,
            int stepNumber,
            Fa factory1,
            RuleExplanationSetFactory factory2
    ) {
        optimizer.initialize(factory1, factory2);
        if (store) {
            store(stepNumber);
        }
    }

    protected void initializeProcedureStep(ExplanationProcedureStep<RuleExplanationFactory> exs, boolean useTestData, boolean useCountingCalculator) {
        if (useTestData) {
            if (useCountingCalculator) {
                exs.initialize(countingRuleExplanationFactoryTest, ruleExplanationSetFactoryTest);
            } else {
                exs.initialize(coverRuleExplanationFactoryTest, ruleExplanationSetFactoryTest);
            }
        } else {
            if (useCountingCalculator) {
                exs.initialize(countingRuleExplanationFactoryTraining, ruleExplanationSetFactoryTraining);
            } else {
                exs.initialize(coverRuleExplanationFactoryTraining, ruleExplanationSetFactoryTraining);
            }
        }
    }

    protected void store(int stepNumber) {
        this.storedResultsForSteps.put(stepNumber, new ArrayList<>());
    }

    protected void checkTestExists() {
        if (testData == null) {
            throw new PipelineExecutionFailed("Test data was required, yet, no split was conducted");
        }
    }

    protected Set<RuleExplanationSet> executeSteps(Set<Integer> labelValues) {
        Set<RuleExplanationSet> result = new HashSet<>();
        RuleExplanationSet currentSet = null;
        for (int labelValue : labelValues) {
            logger.info("For label value: " + dataset.getProcessedLabelCol().getLabel().getStringRepresentation(labelValue));
            long startTime = System.nanoTime();
            currentSet = explanationMapper.mapExplanations(labelValue);
            long endTime = System.nanoTime();
            logger.info("For mapper: " + explanationMapper.getClass().getSimpleName() +
                    ", time: " + ((endTime - startTime)/1000000));
            checkAndStore(currentSet, STORE_FOR_MAPPER);
            for (int i = 0; i < stepCount; i++) {
                currentSet = executeStep(currentSet, i);
            }
            result.add(currentSet);
        }
        if (currentSet == null) {
            throw new PipelineExecutionFailed(
                    "The explanation result is null. " +
                            "Maybe the label column has not been correctly loaded?"
            );
        } else {
            return result;
        }
    }

    protected RuleExplanationSet executeStep(RuleExplanationSet input, int stepNumber) {
        RuleExplanationSet result;
        logger.info("|##############################################|");
        logger.info("Step number: " + stepNumber);
        logger.info("Number explanations: " + input.getNumberExplanations());
        logger.info("Number condition values: " + input.getNumberConditionValues());
        long startTime = System.nanoTime();
        if (ruleOptimizers.containsKey(stepNumber)) {
            if (input.getNumberConditionValues() < 2) {
                checkAndStore(input, stepNumber);
                return input;
            }
            result = ruleOptimizers.get(stepNumber).optimize(input);
            long endTime = System.nanoTime();
            logger.info("For rule optimizer: " + ruleOptimizers.get(stepNumber).getClass().getSimpleName() +
                    ", time: " + ((endTime - startTime)/1000000));
        } else if (ruleSetOptimizers.containsKey(stepNumber)) {
            if (input.getNumberExplanations() < 2) {
                checkAndStore(input, stepNumber);
                return input;
            }
            result = ruleSetOptimizers.get(stepNumber).optimize(input);
            long endTime = System.nanoTime();
            logger.info("For rule set optimizer: " + ruleSetOptimizers.get(stepNumber).getClass().getSimpleName() +
                    ", time: " + ((endTime - startTime)/1000000));
        } else if (postprocessors.containsKey(stepNumber)) {
            result = postprocessors.get(stepNumber).postprocess(input);
            long endTime = System.nanoTime();
            logger.info("For postprocessor: " + postprocessors.get(stepNumber).getClass().getSimpleName() +
                    ", time: " + ((endTime - startTime)/1000000));
        } else {
            throw new PipelineExecutionFailed(
                    "No type of global explanation procedure " +
                    "step could be mapped to step number " + stepNumber + "."
            );
        }

        checkAndStore(result, stepNumber);
        return result;
    }

    protected void checkAndStore(RuleExplanationSet result, int stepNumber) {
        if (storedResultsForSteps.containsKey(stepNumber)) {
            storedResultsForSteps.get(stepNumber).add(result);
        }
    }

    protected boolean validatePipelineContext(PipelineContext pipelineContext) {
        return true;
    }

    protected abstract void prepareDataset(AnchorTabular anchorTabular);

    protected void splitData() {}

    protected void prepareIndex() {
        trainingIndex = new SimpleRoaringBitmapIndex(dataset);
        if (testData != null) {
            testIndex = new SimpleRoaringBitmapIndex(testData);
        }
    }

    protected void prepareCalculator() {
        calculatorTraining = new SimpleRoaringBitmapCalculator(trainingIndex);//new RoaringBitmapNumberCachedCalculator(new SimpleRoaringBitmapCalculator(trainingIndex)));
        if (testIndex != null) {
            calculatorTest = new SimpleRoaringBitmapCalculator(testIndex);//new RoaringBitmapNumberCachedCalculator(new SimpleRoaringBitmapCalculator(testIndex)));
        }
    }

    protected void prepareExplanationFactories() {
        countingRuleExplanationFactoryTraining = new StdRuleExplanationFactory(calculatorTraining);
        coverRuleExplanationFactoryTraining = new MinimalCoversRuleExplanationFactory(calculatorTraining);
        if (calculatorTest != null) {
            countingRuleExplanationFactoryTest = new StdRuleExplanationFactory(calculatorTest);
            coverRuleExplanationFactoryTest = new MinimalCoversRuleExplanationFactory(calculatorTest);
        }
    }

    protected void prepareExplanationSetFactories() {
        ruleExplanationSetFactoryTraining = new StdRuleExplanationSetFactory(calculatorTraining);
        if (calculatorTest != null) {
            ruleExplanationSetFactoryTest = new StdRuleExplanationSetFactory(calculatorTest);
        }
    }

    protected abstract void addSteps();
}
