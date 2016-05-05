package com.finebi.cube.structure;

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
    protected ITableKey tableKey;

    public BICubeTableRelationEntityManager(ICubeResourceRetrievalService resourceRetrievalService, ITableKey tableKey) {
        this.resourceRetrievalService = resourceRetrievalService;
        this.tableKey = tableKey;
    }

    @Override
    protected Map<ICubeResourceLocation, ICubeRelationEntityService> initContainer() {
        return new HashMap<ICubeResourceLocation, ICubeRelationEntityService>();
    }

    @Override
    protected ICubeRelationEntityService generateAbsentValue(ICubeResourceLocation key) {
        try {
            return new BICubeRelationEntity(key);
        } catch (Exception e) {
            BINonValueUtils.beyondControl("Please check current thread context,which may be accessed by different thread.This" +
                    "situation may be complex,because the super map have taken measure to avoid such problem situation");
        }
        return null;

    }

    protected void checkPath(BICubeTablePath path) throws BITablePathEmptyException, IllegalRelationPathException {
        ITableKey pathFirstTable = path.getFirstRelation().getPrimaryTable();
        if (!ComparatorUtils.equals(pathFirstTable, tableKey)) {
            throw new IllegalRelationPathException("the Path start table is:" + pathFirstTable.toString()
                    + "which should be" + tableKey.toString());
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
            location = resourceRetrievalService.retrieveResource(tableKey, relationPath);
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
    public void releaseResource() {
        Iterator<ICubeRelationEntityService> it = container.values().iterator();
        while (it.hasNext()) {
            it.next().releaseResource();
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
