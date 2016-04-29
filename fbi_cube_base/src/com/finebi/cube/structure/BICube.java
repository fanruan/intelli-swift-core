package com.finebi.cube.structure;

import com.finebi.cube.exception.BICubeColumnAbsentException;
import com.finebi.cube.exception.BICubeRelationAbsentException;
import com.finebi.cube.exception.IllegalRelationPathException;
import com.finebi.cube.location.ICubeResourceRetrievalService;
import com.finebi.cube.structure.column.BIColumnKey;
import com.finebi.cube.structure.column.ICubeColumnReaderService;
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

    public BICube(ICubeResourceRetrievalService resourceRetrievalService) {
        this.resourceRetrievalService = resourceRetrievalService;
    }

    @Override
    public ICubeTableEntityGetterService getCubeTable(ITableKey tableKey) {
        return new BICubeTableEntity(tableKey, resourceRetrievalService);
    }

    @Override
    public ICubeColumnReaderService getCubeColumn(ITableKey tableKey, BIColumnKey field) throws BICubeColumnAbsentException {
        return new BICubeTableEntity(tableKey, resourceRetrievalService).getColumnDataGetter(field);
    }

    @Override
    public ICubeRelationEntityGetterService getCubeRelation(ITableKey tableKey, BICubeTablePath relationPath) throws BICubeRelationAbsentException, BICubeColumnAbsentException, IllegalRelationPathException {
        return new BICubeTableEntity(tableKey, resourceRetrievalService).getRelationIndexGetter(relationPath);
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
}
