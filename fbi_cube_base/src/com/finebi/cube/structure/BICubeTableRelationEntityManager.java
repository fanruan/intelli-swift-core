package com.finebi.cube.structure;

import com.finebi.cube.data.ICubeResourceDiscovery;
import com.finebi.cube.exception.BICubeRelationAbsentException;
import com.finebi.cube.exception.BICubeResourceAbsentException;
import com.finebi.cube.exception.IllegalRelationPathException;
import com.finebi.cube.location.ICubeResourceLocation;
import com.finebi.cube.location.ICubeResourceRetrievalService;
import com.fr.bi.common.container.BIMapContainer;
import com.fr.bi.exception.BIKeyAbsentException;
import com.fr.bi.stable.exception.BITablePathEmptyException;
import com.fr.bi.stable.utils.program.BINonValueUtils;
import com.fr.general.ComparatorUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * This class created on 2016/3/7.
 *
 * @author Connery
 * @since 4.0
 */
public class BICubeTableRelationEntityManager extends BIMapContainer<ICubeResourceLocation, ICubeRelationEntityService> implements ICubeRelationManagerService {

    protected ICubeResourceRetrievalService resourceRetrievalService;
    protected ITableKey hostTableKey;
    /**
     * 当前所有的Relation归属是hostTable，但是这不代表该relation只能由HostTable使用
     * 例如A作为ETL_A表的子表，那么ETL_A应该拥有A表拥有的关系。
     * 倘若有ETL_A-C的Relation，但是在A表中的字段A.a的索引的话，会到A表中来根据ETL_A-C来计算
     * A.a-C的索引。
     */
    protected ITableKey owner;
    protected ICubeResourceDiscovery discovery;

    public BICubeTableRelationEntityManager(ICubeResourceRetrievalService resourceRetrievalService, ITableKey hostTableKey, ICubeResourceDiscovery discovery) {
        this.resourceRetrievalService = resourceRetrievalService;
        this.hostTableKey = hostTableKey;
        this.discovery = discovery;
        this.owner = hostTableKey;
    }

    public void setOwner(ITableKey owner) {
        this.owner = owner;
    }

    @Override
    protected Map<ICubeResourceLocation, ICubeRelationEntityService> initContainer() {
        return new HashMap<ICubeResourceLocation, ICubeRelationEntityService>();
    }

    @Override
    protected ICubeRelationEntityService generateAbsentValue(ICubeResourceLocation key) {
        try {
            return new BICubeRelationEntity(discovery, key);
        } catch (Exception e) {
            BINonValueUtils.beyondControl("Please check current thread context,which may be accessed by different thread.This" +
                    "situation may be complex,because the super map have taken measure to avoid such problem situation");
        }
        return null;

    }

    protected void checkPath(BICubeTablePath path) throws BITablePathEmptyException, IllegalRelationPathException {
        ITableKey pathFirstTable = path.getFirstRelation().getPrimaryTable();
        if (!ComparatorUtils.equals(pathFirstTable, owner)) {
            throw new IllegalRelationPathException("the Path start table is:" + pathFirstTable.toString()
                    + "which should be" + owner.toString());
        }
    }

    public ICubeRelationEntityService getRelationService(BICubeTablePath relationPath) throws BICubeRelationAbsentException, IllegalRelationPathException {
        ICubeResourceLocation location;
        try {
            checkPath(relationPath);
        } catch (BITablePathEmptyException e) {
            throw new BICubeRelationAbsentException(e.getMessage(), e);
        }
        try {
            location = resourceRetrievalService.retrieveResource(hostTableKey, relationPath);
        } catch (BICubeResourceAbsentException e) {
            throw new BICubeRelationAbsentException(e.getMessage(), e);
        } catch (BITablePathEmptyException e) {
            throw new BICubeRelationAbsentException(e.getMessage(), e);
        }

        try {
            return getValue(location);
        } catch (BIKeyAbsentException e) {
            throw new BICubeRelationAbsentException(e.getMessage(), e);
        }
    }


    @Override
    public void clear() {
        Iterator<ICubeRelationEntityService> it = container.values().iterator();
        while (it.hasNext()) {
            it.next().clear();
        }
        super.clear();
    }

    public boolean existPath(BICubeTablePath path) {
        try {
            ICubeRelationEntityService service = getRelationService(path);
            return service.isEmpty();
        } catch (Exception e) {
            throw BINonValueUtils.beyondControl(e);
        }
    }
}
