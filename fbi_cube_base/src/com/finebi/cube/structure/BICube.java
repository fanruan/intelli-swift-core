package com.finebi.cube.structure;

import com.finebi.cube.data.ICubeResourceDiscovery;
import com.finebi.cube.exception.BICubeColumnAbsentException;
import com.finebi.cube.exception.BICubeRelationAbsentException;
import com.finebi.cube.exception.BICubeResourceAbsentException;
import com.finebi.cube.exception.IllegalRelationPathException;
import com.finebi.cube.location.ICubeResourceLocation;
import com.finebi.cube.location.ICubeResourceRetrievalService;
import com.finebi.cube.structure.column.BIColumnKey;
import com.finebi.cube.structure.column.ICubeColumnReaderService;
import com.finebi.cube.structure.property.BICubeVersion;
import com.finebi.cube.structure.table.BICubeTableEntity;
import com.finebi.cube.structure.table.CompoundCubeTableReader;
import com.fr.bi.stable.exception.BITablePathConfusionException;
import com.fr.bi.stable.utils.program.BINonValueUtils;

/**
 * This class created on 2016/3/14.
 *
 * @author Connery
 * @since 4.0
 */
public class BICube implements ICube {
    private ICubeResourceRetrievalService resourceRetrievalService;
    private ICubeResourceDiscovery discovery;
    private BICubeVersion cubeVersion;
    private static String CUBE_PROPERTY = "property";

    public BICube(ICubeResourceRetrievalService resourceRetrievalService, ICubeResourceDiscovery discovery) {
        this.resourceRetrievalService = resourceRetrievalService;
        this.discovery = discovery;
        cubeVersion = new BICubeVersion(getCubeLocation(), discovery);
    }

    private ICubeResourceLocation getCubeLocation() {
        try {
            return this.resourceRetrievalService.retrieveRootResource(CUBE_PROPERTY);
        } catch (BICubeResourceAbsentException e) {
            throw BINonValueUtils.beyondControl(e);
        }
    }

    @Override
    public ICubeTableEntityGetterService getCubeTable(ITableKey tableKey) {
        return new CompoundCubeTableReader(tableKey, resourceRetrievalService, discovery);
    }

    @Override
    public ICubeTableEntityService getCubeTableWriter(ITableKey tableKey) {
        return new BICubeTableEntity(tableKey, resourceRetrievalService, discovery);

    }

    @Override
    public ICubeColumnReaderService getCubeColumn(ITableKey tableKey, BIColumnKey field) throws BICubeColumnAbsentException {
        return getCubeTable(tableKey).getColumnDataGetter(field);
    }

    @Override
    public ICubeRelationEntityGetterService getCubeRelation(ITableKey tableKey, BICubeTablePath relationPath) throws BICubeRelationAbsentException, BICubeColumnAbsentException, IllegalRelationPathException {
        return getCubeTable(tableKey).getRelationIndexGetter(relationPath);
    }

    @Override
    public ICubeRelationEntityGetterService getCubeRelation(ITableKey tableKey, BICubeRelation relation) throws BICubeRelationAbsentException, BICubeColumnAbsentException, IllegalRelationPathException {
        BICubeTablePath relationPath = new BICubeTablePath();
        try {
            relationPath.addRelationAtHead(relation);
        } catch (BITablePathConfusionException e) {
            throw BINonValueUtils.illegalArgument(relation.toString() + " the relation is so terrible");
        }
        return getCubeRelation(tableKey, relationPath);
    }

    @Override
    public boolean canRead(ITableKey tableKey) {
        try {
            ICubeResourceLocation location = resourceRetrievalService.retrieveResource(tableKey);
            if (isResourceExist(location)) {
                ICubeTableEntityGetterService tableEntityGetterService = getCubeTable(tableKey);
                return tableEntityGetterService.tableDataAvailable();
            }
            return false;

        } catch (BICubeResourceAbsentException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean isResourceExist(ICubeResourceLocation location) {
        return discovery.isResourceExist(location);
    }

    public long getCubeVersion() {
        return cubeVersion.getCubeVersion();
    }

    @Override
    public void addVersion(long version) {
        cubeVersion.addVersion(version);
    }

    @Override
    public void clear() {
        cubeVersion.clear();
    }
}
