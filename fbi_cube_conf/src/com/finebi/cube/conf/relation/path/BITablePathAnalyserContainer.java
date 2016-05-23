package com.finebi.cube.conf.relation.path;

import com.fr.bi.common.container.BIHashMapContainer;
import com.fr.bi.common.container.BIMapContainer;
import com.fr.bi.exception.BIKeyAbsentException;
import com.fr.bi.exception.BIKeyDuplicateException;
import com.finebi.cube.conf.table.IBusinessTable;

import java.util.Map;

/**
 * Created by Connery on 2016/1/13.
 */
public class BITablePathAnalyserContainer extends BIHashMapContainer<IBusinessTable, BITablePathAnalyser> {
    @Override
    protected BITablePathAnalyser getValue(IBusinessTable key) throws BIKeyAbsentException {
        return super.getValue(key);
    }

    @Override
    protected void setContainer(Map<IBusinessTable, BITablePathAnalyser> container) {
        super.setContainer(container);
    }

    @Override
    protected Map<IBusinessTable, BITablePathAnalyser> getContainer() {
        return super.getContainer();
    }

    @Override
    protected void putKeyValue(IBusinessTable key, BITablePathAnalyser value) throws BIKeyDuplicateException {
        super.putKeyValue(key, value);
    }

    @Override
    protected Boolean containsKey(IBusinessTable key) {
        return super.containsKey(key);
    }

    @Override
    protected void clear() {
        super.clear();
    }

    @Override
    protected void remove(IBusinessTable key) throws BIKeyAbsentException {
        super.remove(key);
    }

    @Override
    protected void copyTo(BIMapContainer<IBusinessTable, BITablePathAnalyser> container) {
        super.copyTo(container);
    }
}