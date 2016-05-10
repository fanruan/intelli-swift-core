package com.finebi.cube.structure;

import com.finebi.cube.data.ICubeResourceDiscovery;
import com.finebi.cube.location.ICubeResourceRetrievalService;

/**
 * This class created on 2016/5/9.
 *
 * @author Connery
 * @since 4.0
 */
public class BICubeCompoundTable extends BICubeTableEntity {
    public BICubeCompoundTable(ITableKey tableKey, ICubeResourceRetrievalService resourceRetrievalService, ICubeResourceDiscovery discovery) {
        super(tableKey, resourceRetrievalService, discovery);
    }
}
