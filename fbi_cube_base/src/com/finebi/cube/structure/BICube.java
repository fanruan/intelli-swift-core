package com.finebi.cube.structure;

import com.finebi.cube.common.log.BILogger;
import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.data.ICubeResourceDiscovery;
import com.finebi.cube.exception.*;
import com.finebi.cube.location.ICubeResourceLocation;
import com.finebi.cube.location.ICubeResourceRetrievalService;
import com.finebi.cube.structure.column.BIColumnKey;
import com.finebi.cube.structure.column.CubeColumnReaderService;
import com.finebi.cube.structure.property.BICubeVersion;
import com.finebi.cube.structure.table.BICubeTableEntity;
import com.finebi.cube.structure.table.CompoundCubeTableReader;
import com.finebi.cube.utils.BICubeUtils;
import com.fr.bi.stable.constant.CubeConstant;
import com.fr.bi.stable.exception.BITablePathConfusionException;
import com.fr.bi.stable.exception.BITablePathEmptyException;
import com.fr.bi.stable.utils.program.BINonValueUtils;

import java.net.URISyntaxException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class created on 2016/3/14.
 *
 * @author Connery
 * @since 4.0
 */
public class BICube implements Cube {
    private static BILogger logger = BILoggerFactory.getLogger(BICube.class);
    private static final long serialVersionUID = -5241804642657280524L;
    protected ICubeResourceRetrievalService resourceRetrievalService;
    protected ICubeResourceDiscovery discovery;
    protected BICubeVersion cubeVersion;
    private static String CUBE_PROPERTY = CubeConstant.CUBE_PROPERTY;
    protected Map<String, CompoundCubeTableReader> cacheTableReader;

    public BICube(ICubeResourceRetrievalService resourceRetrievalService, ICubeResourceDiscovery discovery) {
        this.resourceRetrievalService = resourceRetrievalService;
        this.discovery = discovery;
        cubeVersion = new BICubeVersion(getCubeLocation(), discovery);
        cacheTableReader = new ConcurrentHashMap<String, CompoundCubeTableReader>();
    }

    public BICube(ICubeResourceRetrievalService resourceRetrievalService, ICubeResourceRetrievalService integrityResourceRetrievalService, ICubeResourceDiscovery discovery) {

    }

    protected ICubeResourceLocation getCubeLocation() {
        try {
            return this.resourceRetrievalService.retrieveRootResource(CUBE_PROPERTY);
        } catch (BICubeResourceAbsentException e) {
            throw BINonValueUtils.beyondControl(e);
        }
    }

    @Override
    public CubeTableEntityGetterService getCubeTable(ITableKey tableKey) {
//        synchronized (this) {
//            if (cacheTableReader.containsKey(tableKey.getSourceID())) {
//                return cacheTableReader.get(tableKey.getSourceID());
//            } else {
//                BILoggerFactory.getLogger().info("add table reader:" + tableKey.getSourceID());
//                cacheTableReader.put(tableKey.getSourceID(), new CompoundCubeTableReader(tableKey, resourceRetrievalService, discovery));
//                return cacheTableReader.get(tableKey.getSourceID());
//            }
//        }
        return new CompoundCubeTableReader(tableKey, resourceRetrievalService, discovery);
    }

    @Override
    public CubeTableEntityService getCubeTableWriter(ITableKey tableKey) {
        return new BICubeTableEntity(tableKey, resourceRetrievalService, discovery);
    }

    @Override
    public CubeColumnReaderService getCubeColumn(ITableKey tableKey, BIColumnKey field) throws BICubeColumnAbsentException {
        return getCubeTable(tableKey).getColumnDataGetter(field);
    }

    @Override
    public CubeRelationEntityGetterService getCubeRelation(ITableKey tableKey, BICubeTablePath relationPath) throws BICubeRelationAbsentException, BICubeColumnAbsentException, IllegalRelationPathException {
        return getCubeTable(tableKey).getRelationIndexGetter(relationPath);
    }

    @Override
    public CubeRelationEntityGetterService getCubeRelation(ITableKey tableKey, BICubeRelation relation) throws BICubeRelationAbsentException, BICubeColumnAbsentException, IllegalRelationPathException {
        BICubeTablePath relationPath = new BICubeTablePath();
        try {
            relationPath.addRelationAtHead(relation);
        } catch (BITablePathConfusionException e) {
            throw BINonValueUtils.illegalArgument(relation.toString() + " the relation is so terrible");
        }
        return getCubeRelation(tableKey, relationPath);
    }

    @Override
    public ICubeRelationEntityService getCubeRelationWriter(ITableKey tableKey, BICubeRelation relation) throws BICubeRelationAbsentException, BICubeColumnAbsentException, IllegalRelationPathException {
        BICubeTablePath relationPath = new BICubeTablePath();
        try {
            relationPath.addRelationAtHead(relation);
        } catch (BITablePathConfusionException e) {
            throw BINonValueUtils.illegalArgument(relation.toString() + " the relation is so terrible");
        }
        return (ICubeRelationEntityService) getCubeTableWriter(tableKey).getRelationIndexGetter(relationPath);
    }

    @Override
    public ICubeRelationEntityService getCubeRelationWriter(ITableKey tableKey, BICubeTablePath relationPath) throws BICubeRelationAbsentException, BICubeColumnAbsentException, IllegalRelationPathException {
        return (ICubeRelationEntityService) getCubeTableWriter(tableKey).getRelationIndexGetter(relationPath);
    }

    @Override
    public boolean exist(ITableKey tableKey) {
        if (isResourceExist(tableKey)) {
            CubeTableEntityGetterService tableEntityGetterService = getCubeTable(tableKey);
            boolean result = BICubeUtils.tableExist(tableKey, tableEntityGetterService, this);
            tableEntityGetterService.clear();
            return result;
        } else {
            return false;
        }
    }

    @Override
    public boolean exist(ITableKey tableKey, BICubeRelation relation) {
        BICubeTablePath relationPath = new BICubeTablePath();
        try {
            relationPath.addRelationAtHead(relation);
        } catch (BITablePathConfusionException e) {
            throw BINonValueUtils.illegalArgument(relation.toString() + " the relation is so terrible");
        }
        return exist(tableKey, relationPath);
    }

    @Override
    public boolean exist(ITableKey tableKey, BICubeTablePath relationPath) {
        if (isResourceExist(tableKey)) {
            CubeTableEntityGetterService tableEntityGetterService = getCubeTable(tableKey);
            boolean result = BICubeUtils.tableRelationExist(tableKey, relationPath, tableEntityGetterService, this);
            tableEntityGetterService.clear();
            return result;
        } else {
            return false;
        }
    }

    @Override
    public boolean exist(ITableKey tableKey, BIColumnKey field, BICubeTablePath relationPath) {
        if (isResourceExist(tableKey)) {
            CubeTableEntityGetterService tableEntityGetterService = getCubeTable(tableKey);
            boolean result = BICubeUtils.tableFieldRelationExist(tableKey, field, relationPath, tableEntityGetterService, this);
            tableEntityGetterService.clear();
            return result;
        } else {
            return false;
        }
    }

    @Override
    public boolean isResourceExist(ITableKey tableKey) {
        try {
            ICubeResourceLocation location = resourceRetrievalService.retrieveResource(tableKey);
            ICubeResourceLocation versionLocation = location.buildChildLocation("version");
            return isResourceExist(versionLocation.getRealLocation());
        } catch (BICubeResourceAbsentException e) {
            logger.warn(e.getMessage(), e);
            return false;
        } catch (URISyntaxException e) {
            logger.warn(e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean isResourceExist(ITableKey tableKey, BICubeTablePath relationPath) {
        try {
            ICubeResourceLocation location = resourceRetrievalService.retrieveResource(tableKey, relationPath);
            ICubeResourceLocation versionLocation = location.buildChildLocation("version");
            return isResourceExist(versionLocation.getRealLocation());
        } catch (BICubeResourceAbsentException e) {
            logger.warn(e.getMessage(), e);
            return false;
        } catch (BITablePathEmptyException e) {
            logger.warn(e.getMessage(), e);
            return false;
        }catch (URISyntaxException e) {
            logger.warn(e.getMessage(), e);
            return false;
        }
    }

    @Override
    public ICubeResourceRetrievalService getCubeResourceRetrievalService() {
        return resourceRetrievalService;
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
        cubeVersion.forceRelease();
    }

    @Override
    public void clear() {
        cubeVersion.clear();
        cacheTableReader.clear();
    }

    @Override
    public Boolean isVersionAvailable() {
        return cubeVersion.isVersionAvailable();
    }


}
