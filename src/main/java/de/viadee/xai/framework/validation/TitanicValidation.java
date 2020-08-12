package de.viadee.xai.framework.validation;

import de.viadee.xai.anchor.adapter.tabular.AnchorTabular;
import de.viadee.xai.anchor.adapter.tabular.column.IntegerColumn;
import de.viadee.xai.anchor.adapter.tabular.column.StringColumn;
import de.viadee.xai.framework.adapter.data_source_adapter.NullDataAdapter;
import de.viadee.xai.framework.explanation_pipeline.AbstractStdExplanationPipeline;
import de.viadee.xai.framework.explanation_pipeline.exemplary_pipeline.ReorderedKOptMAGIX;

import static de.viadee.xai.anchor.adapter.tabular.column.DoubleColumn.fromStringInput;

/**
 * Used to evaluate different pipelines on the Titanic data set.
 */
public class TitanicValidation {

    public static void main(String[] args) {
        AnchorTabular anchorTabular = null;
        try {
            anchorTabular = new AnchorTabular.Builder()
                    .setDoBalance(false)
                    .addIgnoredColumn("PassengerId")
                    .addTargetColumn(IntegerColumn.fromStringInput("Survived"))
                    .addColumn(IntegerColumn.fromStringInput("Pclass"))
                    .addColumn(new StringColumn("Name"))
                    .addColumn(new StringColumn("Sex"))
                    .addColumn(fromStringInput("Age", -1, 0))
                    .addColumn(IntegerColumn.fromStringInput("SibSp"))
                    .addColumn(IntegerColumn.fromStringInput("Parch"))
                    .addColumn(new StringColumn("Ticket"))
                    .addColumn(fromStringInput("Fare"))
                    .addIgnoredColumn(new StringColumn("Cabin"))
                    .addColumn(new StringColumn("Embarked"))
                    .build(ClassLoader.getSystemResourceAsStream("train.csv"), true, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        NullDataAdapter dataAdapter = new NullDataAdapter(anchorTabular);
        // Can be exchanged for any other pipeline of the exemplary_pipeline-package.
        AbstractStdExplanationPipeline working = new ReorderedKOptMAGIX(dataAdapter, "Titanic");
        System.out.println(working.executePipeline().getVisualizedResult());
    }
}
