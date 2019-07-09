package de.viadee.xai.framework.validation;

import de.viadee.xai.anchor.adapter.tabular.AnchorTabular;
import de.viadee.xai.anchor.adapter.tabular.column.DoubleColumn;
import de.viadee.xai.anchor.adapter.tabular.column.IntegerColumn;
import de.viadee.xai.anchor.adapter.tabular.column.StringColumn;
import de.viadee.xai.framework.adapter.data_source_adapter.NullDataAdapter;
import de.viadee.xai.framework.explanation_pipeline.AbstractStdExplanationPipeline;
import de.viadee.xai.framework.explanation_pipeline.exemplary_pipeline.ReorderedKOptMAGIX;

/**
 * Used to evaluate different pipelines on the Adult data set.
 */
public class AdultValidation {

    public static void main(String[] args) {
        AnchorTabular anchorTabular = null;

        try {
            anchorTabular = new AnchorTabular.Builder()
                    .setDoBalance(false)
                    .addColumn(DoubleColumn.fromStringInput("age"))
                    .addColumn(new StringColumn("workclass"))
                    .addIgnoredColumn(DoubleColumn.fromStringInput("fnlwgt"))
                    .addColumn(new StringColumn("education"))
                    .addIgnoredColumn(IntegerColumn.fromStringInput("education-num"))
                    .addColumn(new StringColumn("marital-status"))
                    .addColumn(new StringColumn("occupation"))
                    .addColumn(new StringColumn("relationship"))
                    .addColumn(new StringColumn("race"))
                    .addColumn(new StringColumn("sex"))
                    .addIgnoredColumn(DoubleColumn.fromStringInput("capital-gain")) // Not usable for a primitive discretizer.
                    .addIgnoredColumn(DoubleColumn.fromStringInput("capital-loss")) // Not usable for a primitive discretizer.
                    .addColumn(DoubleColumn.fromStringInput("hours-per-week"))
                    .addColumn(new StringColumn("native-country"))
                    .addTargetColumn(new StringColumn("wage"))
                    .build(ClassLoader.getSystemResourceAsStream("adult.data"), true, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        NullDataAdapter dataAdapter = new NullDataAdapter(anchorTabular);
        // Can be exchanged for any other pipeline of the exemplary_pipeline-package.
        AbstractStdExplanationPipeline working = new ReorderedKOptMAGIX(dataAdapter, "Adult");
        System.out.println(working.executePipeline().getVisualizedResult());
    }
}
