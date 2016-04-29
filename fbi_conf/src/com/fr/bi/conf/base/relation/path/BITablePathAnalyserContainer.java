package com.fr.bi.conf.base.relation.path;

import com.fr.bi.common.container.BIHashMapContainer;
import com.fr.bi.common.container.BIMapContainer;
import com.fr.bi.exception.BIKeyAbsentException;
import com.fr.bi.exception.BIKeyDuplicateException;
import com.fr.bi.stable.data.Table;

import java.util.Map;

/**
 * Created by Connery on 2016/1/13.
 */
public class BITablePathAnalyserContainer extends BIHashMapContainer<Table, BITablePathAnalyser> {
    @Override
    protected BITablePathAnalyser getValue(Table key) throws BIKeyAbsentException {
        return super.getValue(key);
    }

    @Override
    protected void setContainer(Map<Table, BITablePathAnalyser> container) {
        super.setContainer(container);
    }

    @Override
    protected Map<Table, BITablePathAnalyser> getContainer() {
        return super.getContainer();
    }

    @Override
    protected void putKeyValue(Table key, BITablePathAnalyser value) throws BIKeyDuplicateException {
        super.putKeyValue(key, value);
    }

    @Override
    protected Boolean containsKey(Table key) {
        return super.containsKey(key);
    }

    @Override
    protected void clear() {
        super.clear();
    }

    @Override
    protected void remove(Table key) throws BIKeyAbsentException {
        super.remove(key);
    }

    @Override
    protected void copyTo(BIMapContainer<Table, BITablePathAnalyser> container) {
        super.copyTo(container);
    }
}