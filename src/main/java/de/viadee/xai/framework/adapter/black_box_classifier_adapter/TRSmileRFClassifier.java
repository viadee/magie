package de.viadee.xai.framework.adapter.black_box_classifier_adapter;

import de.viadee.xai.anchor.adapter.tabular.TabularInstance;
import de.viadee.xai.anchor.adapter.tabular.column.GenericColumn;
import de.viadee.xai.anchor.adapter.tabular.column.StringColumn;
import de.viadee.xai.framework.adapter.local_explainer_adapter.AnchorLocalExplainerAdapter;
import de.viadee.xai.framework.data.tabular_data.TabularRow;

import java.text.ParseException;

/**
 * Implementation of the Smile random forest classifier for the AnchorLocalExplainerAdapter.
 * {@link AnchorLocalExplainerAdapter}
 */
public class TRSmileRFClassifier extends TRSmileClassifier<TabularInstance> {
    private GenericColumn[] orderedGenericColumns;
    protected TabularRowToTabularInstance transformer = new TabularRowToTabularInstance();

    /**
     * Constructor for a TRSmileRFClassifier instance.
     */
    public TRSmileRFClassifier() {
        super(500);
    }

    @Override
    public Integer apply(TabularInstance tabularInstance) {
        return classifier.predict(transform(tabularInstance));
    }

    // Transform a TabularInstance into a double-vector which can then be used by the random forest.
    protected double[] transform(TabularInstance tabularInstance) {
        if (orderedGenericColumns == null) {
            orderedGenericColumns = sortGenericColumns(tabularInstance.getFeatures());
        }
        double[] result = new double[orderedGenericColumns.length];
        for (int i = 0; i < orderedGenericColumns.length; i++) {
            if (orderedGenericColumns[i] instanceof StringColumn) {
                try {
                    result[i] = orderedAttributes[i]
                            .valueOf(
                                    Integer.toString(
                                            (int) (
                                                    (double) tabularInstance.getTransformedValue(
                                                            orderedGenericColumns[i].getName())
                                            )
                                    )
                            );
                } catch (ParseException pe) {
                    pe.printStackTrace();
                }
            } else {
                result[i] = (double) tabularInstance.getTransformedValue(orderedGenericColumns[i].getName());
            }
        }

        return result;
    }


    @Override
    public int predict(TabularRow tabularRow) {
        return classifier.predict(transform(transformer.apply(tabularRow)));
    }

    // Establish a mapping between the GenericColumns used by the TabularInstance and the Attribute-array.
    // This mapping is needed by the transform(...)-method so that the transformation can function correctly.
    private GenericColumn[] sortGenericColumns(GenericColumn[] genericColumns) {
        if (genericColumns.length != orderedAttributes.length) {
            throw new RuntimeException("Something in the transformation of GenericColumns to TabularInstance has gone wrong:" +
                    " The number of attributes needed are not the same as given by the TabularInstance.");
        }
        int[] sortedIndices = new int[genericColumns.length];
        for (int i = 0; i < genericColumns.length; i++) {
            for (int j = 0; j < orderedAttributes.length; j++) {
                if (genericColumns[i].getName().equals(orderedAttributes[j].getName())) {
                    sortedIndices[i] = j;
                    break;
                }
            }
        }
        GenericColumn[] result = new GenericColumn[genericColumns.length];
        for (int i = 0; i < sortedIndices.length; i++) {
            result[sortedIndices[i]] = genericColumns[i];
        }
        return result;
    }
}
