package com.finebi.cube.structure;

import com.finebi.cube.data.ICubeResourceDiscovery;
import com.finebi.cube.exception.BICubeRelationAbsentException;
import com.finebi.cube.exception.BICubeResourceAbsentException;
import com.finebi.cube.exception.IllegalRelationPathException;
import com.finebi.cube.location.ICubeResourceLocation;
import com.finebi.cube.location.ICubeResourceRetrievalService;
import com.finebi.cube.structure.column.BIColumnKey;
import com.fr.bi.exception.BIKeyAbsentException;
import com.fr.bi.stable.exception.BITablePathEmptyException;

import java.net.URISyntaxException;

/**
 * 主表中字段到子表的索引管理
 * This class created on 2016/3/31.
 *
 * @author Connery
 * @since 4.0
 */
public class BICubeFieldRelationManager extends BICubeTableRelationEntityManager {

    private BIColumnKey currentFieldKey;


    public BICubeFieldRelationManager(ICubeResourceRetrievalService resourceRetrievalService, ITableKey tableKey, BIColumnKey currentFieldKey, ICubeResourceDiscovery discovery) {
        super(resourceRetrievalService, tableKey, discovery);
        this.currentFieldKey = currentFieldKey;
    }

    public ICubeRelationEntityService getRelationService(BICubeTablePath relationPath) throws BICubeRelationAbsentException, IllegalRelationPathException {
        ICubeResourceLocation location;
        try {
            checkPath(relationPath);
        } catch (BITablePathEmptyException e) {
            throw new BICubeRelationAbsentException(e.getMessage(), e);
        }
        try {
            location = resourceRetrievalService.retrieveResource(tableKey, currentFieldKey, relationPath);
        } catch (BICubeResourceAbsentException e) {
            throw new BICubeRelationAbsentException(e.getMessage(), e);
        } catch (URISyntaxException e) {
            throw new BICubeRelationAbsentException(e.getMessage(), e);
        }

        try {
            return getValue(location);
        } catch (BIKeyAbsentException e) {
            throw new BICubeRelationAbsentException(e.getMessage(), e);
        }
    }
}
