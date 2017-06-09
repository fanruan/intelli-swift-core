package com.finebi.cube.structure.table;/**
 * Created by roy on 2017/5/24.
 */

import com.finebi.cube.common.log.BILogger;
import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.data.ICubeResourceDiscovery;
import com.finebi.cube.location.ICubeResourceRetrievalService;
import com.finebi.cube.structure.CubeTableEntityService;
import com.finebi.cube.structure.ITableKey;

import java.util.List;

public class CompoundCubeTableReaderNodeFromMultiSource extends CompoundCubeTableReaderNode{
    private final static BILogger LOGGER = BILoggerFactory.getLogger(CompoundCubeTableReaderNodeFromMultiSource.class);
    private ICubeResourceRetrievalService integrityResourceRetrievalService;


    public CompoundCubeTableReaderNodeFromMultiSource(List<ITableKey> tableKeys, ICubeResourceRetrievalService resourceRetrievalService, ICubeResourceRetrievalService integrityResourceRetrievalService, ICubeResourceDiscovery discovery) {
        super(tableKeys, resourceRetrievalService, integrityResourceRetrievalService, discovery);
        this.resourceRetrievalService = resourceRetrievalService;
        this.integrityResourceRetrievalService = integrityResourceRetrievalService;
        this.discovery = discovery;
        for (ITableKey tableKey : tableKeys) {
            CubeTableEntityService tableEntityService = new CompoundCubeTableReaderFromMultiSource(tableKey, resourceRetrievalService, integrityResourceRetrievalService, discovery);
            if (masterTable == null) {
                masterTable = tableEntityService;
            }
            initialFieldSource(tableEntityService);
            currentLevelTables.add(tableEntityService);
        }
    }
}