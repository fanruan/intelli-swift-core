package com.finebi.cube.structure;/**
 * Created by roy on 2017/5/24.
 */

import com.finebi.cube.common.log.BILogger;
import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.data.ICubeResourceDiscovery;
import com.finebi.cube.location.ICubeResourceRetrievalService;
import com.finebi.cube.structure.property.BICubeVersion;
import com.finebi.cube.structure.table.CompoundCubeTableReader;
import com.finebi.cube.structure.table.CompoundCubeTableReaderFromMultiSource;

import java.util.concurrent.ConcurrentHashMap;

public class BICubeFromMultiSource extends BICube {
    private final static BILogger LOGGER = BILoggerFactory.getLogger(BICubeFromMultiSource.class);
    private ICubeResourceRetrievalService integrityResourceRetrievalService;

    public BICubeFromMultiSource(ICubeResourceRetrievalService resourceRetrievalService, ICubeResourceRetrievalService integrityResourceRetrievalService, ICubeResourceDiscovery discovery) {
        super(resourceRetrievalService, integrityResourceRetrievalService, discovery);
        this.resourceRetrievalService = resourceRetrievalService;
        this.integrityResourceRetrievalService = integrityResourceRetrievalService;
        this.discovery = discovery;
        cubeVersion = new BICubeVersion(getCubeLocation(), discovery);
        cacheTableReader = new ConcurrentHashMap<String, CompoundCubeTableReader>();
    }


    @Override
    public CubeTableEntityGetterService getCubeTable(ITableKey tableKey) {
        if (integrityResourceRetrievalService != null) {
            return new CompoundCubeTableReaderFromMultiSource(tableKey, resourceRetrievalService, integrityResourceRetrievalService, discovery);
        } else {
            LOGGER.warn("the integrityResourceRetrievalService is null can not get date from multi source, try to get date from single source");
            return new CompoundCubeTableReader(tableKey, resourceRetrievalService, discovery);
        }
    }
}