package de.viadee.xai.framework.validation;

import de.viadee.xai.anchor.adapter.tabular.AnchorTabular;
import de.viadee.xai.anchor.adapter.tabular.column.StringColumn;
import de.viadee.xai.framework.adapter.data_source_adapter.NullDataAdapter;
import de.viadee.xai.framework.explanation_pipeline.AbstractStdExplanationPipeline;
import de.viadee.xai.framework.explanation_pipeline.exemplary_pipeline.DirectMemeticMining;

/**
 * Used to evaluate different pipelines on the Car data set.
 */
public class CarValidation {

    public static void main(String[] args) {
        AnchorTabular anchorTabular = null;
        try {
            AnchorTabular.Builder builder = new AnchorTabular.Builder().setDoBalance(false);
            anchorTabular = builder
                    .addColumn(new StringColumn("BuyingPrice"))
                    .addColumn(new StringColumn("MaintPrice"))
                    .addColumn(new StringColumn("NumberDoors"))
                    .addColumn(new StringColumn("CapacityPersons"))
                    .addColumn(new StringColumn("LuggageSize"))
                    .addColumn(new StringColumn("Safety"))
                    .addTargetColumn(new StringColumn("CarAcceptability"))
                    .build(ClassLoader.getSystemResourceAsStream("car.data"), false, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        NullDataAdapter dataAdapter = new NullDataAdapter(anchorTabular);
        // Can be exchanged for any other pipeline of the exemplary_pipeline-package.
        AbstractStdExplanationPipeline working = new DirectMemeticMining(dataAdapter, "Car");
        System.out.println(working.executePipeline().getVisualizedResult());
    }
}
