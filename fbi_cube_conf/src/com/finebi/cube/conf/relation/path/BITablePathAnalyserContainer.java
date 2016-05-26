package com.finebi.cube.conf.relation.path;

import com.fr.bi.common.container.BIHashMapContainer;
import com.fr.bi.common.container.BIMapContainer;
import com.fr.bi.exception.BIKeyAbsentException;
import com.fr.bi.exception.BIKeyDuplicateException;
import com.finebi.cube.conf.table.BusinessTable;

import java.util.Map;

/**
 * Created by Connery on 2016/1/13.
 */
public class BITablePathAnalyserContainer extends BIHashMapContainer<BusinessTable, BITablePathAnalyser> {
    @Override
    protected BITablePathAnalyser getValue(BusinessTable key) throws BIKeyAbsentException {
        return super.getValue(key);
    }

    @Override
    protected void setContainer(Map<BusinessTable, BITablePathAnalyser> container) {
        super.setContainer(container);
    }

    @Override
    protected Map<BusinessTable, BITablePathAnalyser> getContainer() {
        return super.getContainer();
    }

    @Override
    protected void putKeyValue(BusinessTable key, BITablePathAnalyser value) throws BIKeyDuplicateException {
        super.putKeyValue(key, value);
    }

    @Override
    protected Boolean containsKey(BusinessTable key) {
        return super.containsKey(key);
    }

    @Override
    protected void clear() {
        super.clear();
    }

    @Override
    protected void remove(BusinessTable key) throws BIKeyAbsentException {
        super.remove(key);
    }

    @Override
    protected void copyTo(BIMapContainer<BusinessTable, BITablePathAnalyser> container) {
        super.copyTo(container);
    }
}