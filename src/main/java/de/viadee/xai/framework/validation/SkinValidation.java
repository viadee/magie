package de.viadee.xai.framework.validation;

import de.viadee.xai.anchor.adapter.tabular.AnchorTabular;
import de.viadee.xai.anchor.adapter.tabular.column.DoubleColumn;
import de.viadee.xai.anchor.adapter.tabular.column.IntegerColumn;
import de.viadee.xai.framework.adapter.data_source_adapter.NullDataAdapter;
import de.viadee.xai.framework.explanation_pipeline.AbstractStdExplanationPipeline;
import de.viadee.xai.framework.explanation_pipeline.exemplary_pipeline.ReorderedMAGIX;

/**
 * Used to evaluate different pipelines on the Skin data set.
 */
public class SkinValidation {

    public static void main(String[] args) {
        AnchorTabular anchorTabular = null;
        try {
            anchorTabular = new AnchorTabular.Builder()
                    .setDoBalance(false)
                    .addColumn(DoubleColumn.fromStringInput("B"))
                    .addColumn(DoubleColumn.fromStringInput("G"))
                    .addColumn(DoubleColumn.fromStringInput("R"))
                    .addTargetColumn(IntegerColumn.fromStringInput("y"))
                    .build(ClassLoader.getSystemResourceAsStream("Skin_NonSkin.txt"), false, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        NullDataAdapter dataAdapter = new NullDataAdapter(anchorTabular);
        // Can be exchanged for any other pipeline of the exemplary_pipeline-package.
        AbstractStdExplanationPipeline working = new ReorderedMAGIX(dataAdapter, "Skin");
        System.out.println(working.executePipeline().getVisualizedResult());
    }
}
