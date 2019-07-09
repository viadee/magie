package de.viadee.xai.framework.adapter.data_source_adapter;

import de.viadee.xai.anchor.adapter.tabular.AnchorTabular;

/**
 * Null-Adapter implementation of a DataSourceAdapter
 */
public class NullDataAdapter implements DataSourceAdapter {

    protected AnchorTabular anchorTabular;

    /**
     * Constructor for NullDataAdapter. Directly accepts an {@link AnchorTabular}.
     * @param anchorTabular The {@link AnchorTabular}.
     */
    public NullDataAdapter(AnchorTabular anchorTabular) {
        if (anchorTabular == null) {
            throw new IllegalArgumentException("AnchorTabular must not be null.");
        }
        this.anchorTabular = anchorTabular;
    }

    @Override
    public AnchorTabular loadDataset() {
        return anchorTabular;
    }
}
