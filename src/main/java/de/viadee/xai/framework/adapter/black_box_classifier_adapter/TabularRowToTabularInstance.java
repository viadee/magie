package de.viadee.xai.framework.adapter.black_box_classifier_adapter;

import de.viadee.xai.anchor.adapter.tabular.TabularInstance;
import de.viadee.xai.anchor.adapter.tabular.column.DoubleColumn;
import de.viadee.xai.anchor.adapter.tabular.column.GenericColumn;
import de.viadee.xai.anchor.adapter.tabular.column.StringColumn;
import de.viadee.xai.framework.data.Feature;
import de.viadee.xai.framework.data.FeatureValue;
import de.viadee.xai.framework.data.tabular_data.TabularRow;
import de.viadee.xai.framework.exception.FeatureNotLegal;

import java.io.Serializable;

/**
 * Converts a TabularRow into a TabularInstance. Can be used, e.g., for the AnchorLocalExplainerAdapter.
 * Uses the names of features to initialize the corresponding columnsBeforeTarget of a TabularInstance.
 */
public class TabularRowToTabularInstance implements TabularRowTransformer<TabularInstance> {


    @Override
    public TabularInstance apply(TabularRow tabularRow) {
        FeatureValue[] featuresValues = tabularRow.getOriginalValues();
        if (featuresValues.length != tabularRow.getNumberProcessedCatFeatureValues()) {
            throw new FeatureNotLegal(Feature.NumericFeature.class);
        }

        // Initialize attributes of DataInstance
        GenericColumn[] originalCols = new GenericColumn[tabularRow.getFeatureCount()];
        Serializable[] originalInstance = new Serializable[tabularRow.getFeatureCount()];
        Integer[] processedInstance = new Integer[tabularRow.getFeatureCount()];
        GenericColumn labelCol = new GenericColumn(tabularRow.getOriginalLabelValue().getFeature().getName());
        Serializable originalLabelValue = getTargetValue(tabularRow.getOriginalLabelValue());
        int processedLabelValue = (int) getTargetValue(tabularRow.getProcessedLabelValue());

        for (int j = 0; j < originalCols.length; j++) {
            // Fill up the DataInstance iteratively using the names of the features of the TabularRow.
            Feature f = featuresValues[j].getFeature();
            Feature.CategoricalFeature pF = tabularRow.getProcessedCatFeatureForName(f.getName());
            if (f instanceof Feature.NumericFeature) {
                originalCols[j] = new DoubleColumn(f.getName());
            } else {
                originalCols[j] = new StringColumn(f.getName());
            }
            originalInstance[j] = tabularRow.getOriginalValue(f);
            processedInstance[j] = tabularRow.getProcessedValue(pF);
        }

        return new TabularInstance(
                originalCols,
                labelCol,
                originalInstance,
                processedInstance,
                originalLabelValue,
                processedLabelValue
        );
    }

    private Serializable getTargetValue(FeatureValue targetValue) {
        if (targetValue instanceof FeatureValue.NumericFeatureValue) {
            return ((FeatureValue.NumericFeatureValue) targetValue).getValue();
        } else {
            return ((FeatureValue.CategoricalFeatureValue) targetValue).getValue();
        }
    }
}
