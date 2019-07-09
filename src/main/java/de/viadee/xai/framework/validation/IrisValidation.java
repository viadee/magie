package de.viadee.xai.framework.validation;

import de.viadee.xai.anchor.adapter.tabular.AnchorTabular;
import de.viadee.xai.anchor.adapter.tabular.column.DoubleColumn;
import de.viadee.xai.anchor.adapter.tabular.column.StringColumn;
import de.viadee.xai.framework.adapter.data_source_adapter.NullDataAdapter;
import de.viadee.xai.framework.explanation_pipeline.AbstractStdExplanationPipeline;
import de.viadee.xai.framework.explanation_pipeline.exemplary_pipeline.ReorderedKOptMAGIX;

/**
 * Used to evaluate different pipelines on the Iris data set.
 */
public class IrisValidation {

    public static void main(String[] args) {
        AnchorTabular anchorTabular = null;
        try {
            anchorTabular = new AnchorTabular.Builder()
                    .setDoBalance(false)
                    .addColumn(DoubleColumn.fromStringInput("SepalLength[cm]"))
                    .addColumn(DoubleColumn.fromStringInput("SepalWidth[cm]"))
                    .addColumn(DoubleColumn.fromStringInput("PetalLength[cm]"))
                    .addColumn(DoubleColumn.fromStringInput("PetalWidth[cm]"))
                    .addTargetColumn(new StringColumn("Species"))
                    .build(ClassLoader.getSystemResourceAsStream("iris.txt"), false, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        NullDataAdapter dataAdapter = new NullDataAdapter(anchorTabular);
        // Can be exchanged for any other pipeline of the exemplary_pipeline-package.
        AbstractStdExplanationPipeline working = new ReorderedKOptMAGIX(dataAdapter, "Iris");
        System.out.println(working.executePipeline().getVisualizedResult());
    }
}
