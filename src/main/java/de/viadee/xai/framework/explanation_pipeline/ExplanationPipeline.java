package de.viadee.xai.framework.explanation_pipeline;

import de.viadee.xai.framework.explanation_calculation.explanation.RuleExplanation;
import de.viadee.xai.framework.explanation_calculation.explanation.RuleExplanationFactory;
import de.viadee.xai.framework.explanation_calculation.explanation.RuleExplanationSet;
import de.viadee.xai.framework.explanation_calculation.explanation.RuleExplanationSetFactory;
import de.viadee.xai.framework.global_explanation_procedure_step.explanation_mapper.ExplanationMapper;
import de.viadee.xai.framework.global_explanation_procedure_step.explanation_structurer.ExplanationStructurer;
import de.viadee.xai.framework.global_explanation_procedure_step.optimizer.Optimizer;
import de.viadee.xai.framework.global_explanation_procedure_step.postprocessor.Postprocessor;

public interface ExplanationPipeline<
        I,
        F> {

    /**
     * Executes the overall pipeline.
     * @return The {@link GlobalExplanationContainer} carrying the visualization as well as the {@link RuleExplanationSet}.
     */
    GlobalExplanationContainer<F> executePipeline();

    /**
     * Adds a rule optimizer to the pipeline.
     * @param toAdd The rule optimizer.
     * @return The ExplanationPipeline for chaining.
     */
    ExplanationPipeline<I, F> addRuleOptimizer(Optimizer<RuleExplanation, RuleExplanationFactory, ?, ?> toAdd);

    /**
     * Adds a postprocessor to the pipeline.
     * @param toAdd The postprocesor.
     * @return The ExplanationPipeline for chaining.
     */
    ExplanationPipeline<I, F> add(Postprocessor toAdd);

    /**
     * Adds a rule set optimizer to the pipeline.
     * @param toAdd The rule set optimizer.
     * @return The ExplanationPipeline for chaining.
     */
    ExplanationPipeline<I, F>
    addRuleSetOptimizer(Optimizer<RuleExplanationSet, RuleExplanationSetFactory, ?, ?> toAdd);

    /**
     * Adds the explanation mapper to the pipeline.
     * @param toAdd The explanation mapper.
     * @return The ExplanationPipeline for chaining.
     */
    ExplanationPipeline<I, F> add(ExplanationMapper toAdd);

    /**
     * Adds the explanation structurer to the pipeline.
     * @param toAdd The explanation structurer
     * @return The ExplanationPipeline for chaining.
     */
    ExplanationPipeline<I, F> add(ExplanationStructurer toAdd);

    /**
     * Adds a postprocessor to the pipeline. Possibly, declares the output of this step to be persisted.
     * @param toAdd The postprocessor.
     * @param store If true, the step's result is persisted.
     * @return The ExplanationPipeline for chaining.
     */
    ExplanationPipeline<I, F> add(Postprocessor toAdd, boolean store);

    /**
     * Adds a rule set optimizer to the pipeline. Possibly, declares the output of this step to be persisted.
     * @param toAdd The rule set optimizer.
     * @param store If true, the step's result is persisted.
     * @return The ExplanationPipeline for chaining.
     */
    ExplanationPipeline<I, F>
    addRuleSetOptimizer(Optimizer<RuleExplanationSet, RuleExplanationSetFactory, ?, ?> toAdd, boolean store);


    /**
     * Adds a rule optimizer to the pipeline. Possibly, declares the output of this step to be persisted.
     * @param toAdd The rule optimizer.
     * @param store If true, the step's result is persisted.
     * @return The ExplanationPipeline for chaining.
     */
    ExplanationPipeline<I, F>
    addRuleOptimizer(Optimizer<RuleExplanation, RuleExplanationFactory, ?, ?> toAdd, boolean store);

    /**
     * Adds the explanation mapper to the pipeline. Possibly, declares the output of this step to be persisted.
     * @param toAdd The explanation mapper.
     * @param store If true, the step's result is persisted.
     * @return The ExplanationPipeline for chaining.
     */
    ExplanationPipeline<I, F> add(ExplanationMapper toAdd, boolean store);

    /**
     * Adds a postprocessor to the pipeline.
     * The postprocessor will utilize the test data set.
     * @param toAdd The postprocessor.
     * @return The ExplanationPipeline for chaining.
     */
    ExplanationPipeline<I, F> addWithTest(Postprocessor toAdd);

    /**
     * Adds a rule optimizer to the pipeline.
     * The rule optimizer will utilize the test data set.
     * @param toAdd The rule optimizer.
     * @return The ExplanationPipeline for chaining.
     */
    ExplanationPipeline<I, F>
    addRuleOptimizerWithTest(Optimizer<RuleExplanation, RuleExplanationFactory, ?, ?> toAdd);

    /**
     * Adds a rule set optimizer to the pipeline.
     * The rule set optimizer will utilize the test data set.
     * @param toAdd The rule set optimizer.
     * @return The ExplanationPipeline for chaining.
     */
    ExplanationPipeline<I, F>
    addRuleSetOptimizerWithTest(Optimizer<RuleExplanationSet, RuleExplanationSetFactory, ?, ?> toAdd);

    /**
     * Adds the explanation mapper to the pipeline.
     * The explanation mapper will utilize the test data set.
     * @param toAdd The explanation mapper.
     * @return The ExplanationPipeline for chaining.
     */
    ExplanationPipeline<I, F> addWithTest(ExplanationMapper toAdd);

    /**
     * Adds a postprocessor to the pipeline. Possibly, declares the output of this step to be persisted.
     * The postprocessor will utilize the test data set.
     * @param toAdd The postprocessor.
     * @param store If true, the step's result is persisted.
     * @return The ExplanationPipeline for chaining.
     */
    ExplanationPipeline<I, F> addWithTest(Postprocessor toAdd, boolean store);

    /**
     * Adds a rule set optimizer to the pipeline. Possibly, declares the output of this step to be persisted.
     * The rule set optimizer will utilize the test data set.
     * @param toAdd The rule set optimizer.
     * @param store If true, the step's result is persisted.
     * @return The ExplanationPipeline for chaining.
     */
    ExplanationPipeline<I, F>
    addRuleSetOptimizerWithTest(Optimizer<RuleExplanationSet, RuleExplanationSetFactory, ?, ?> toAdd, boolean store);

    /**
     * Adds a rule optimizer to the pipeline. Possibly, declares the output of this step to be persisted.
     * The rule optimizer will utilize the test data set.
     * @param toAdd The rule optimizer.
     * @param store If true, the step's result is persisted.
     * @return The ExplanationPipeline for chaining.
     */
    ExplanationPipeline<I, F>
    addRuleOptimizerWithTest(Optimizer<RuleExplanation, RuleExplanationFactory, ?, ?> toAdd, boolean store);

    /**
     * Adds the explanation mapper to the pipeline. Possibly, declares the output of this step to be persisted.
     * The explanation mapper will utilize the test data set.
     * @param toAdd The explanation mapper.
     * @param store If true, the step's result is persisted.
     * @return The ExplanationPipeline for chaining.
     */
    ExplanationPipeline<I, F> addWithTest(ExplanationMapper toAdd, boolean store);

}
