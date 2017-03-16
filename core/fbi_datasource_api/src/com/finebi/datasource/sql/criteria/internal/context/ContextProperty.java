package com.finebi.datasource.sql.criteria.internal.context;

import com.fr.bi.common.container.BIMapContainer;
import com.fr.bi.exception.BIKeyAbsentException;
import com.fr.bi.exception.BIKeyDuplicateException;
import com.fr.bi.stable.utils.program.BINonValueUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class created on 2016/7/7.
 *
 * @author Connery
 * @since Advanced FineBI Analysis 1.0
 */
public class ContextProperty extends BIMapContainer<String, Object> {
    @Override
    protected Map<String, Object> initContainer() {
        return new ConcurrentHashMap<String, Object>();
    }

    @Override
    protected Object generateAbsentValue(String key) {
        return null;
    }

    public void addProperty(String name, Object property) {
        try {
            super.putKeyValue(name, property);
        } catch (BIKeyDuplicateException e) {
            throw BINonValueUtils.beyondControl(e);
        }
    }

    public Object getProperty(String name) {
        try {
            return super.getValue(name);
        } catch (BIKeyAbsentException e) {
            throw BINonValueUtils.beyondControl(e);
        }
    }
}
