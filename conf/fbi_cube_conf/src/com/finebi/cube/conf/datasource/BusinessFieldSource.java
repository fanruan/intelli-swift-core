package com.finebi.cube.conf.datasource;

import com.finebi.cube.conf.field.BusinessField;
import com.fr.bi.exception.BIKeyAbsentException;
import com.fr.bi.exception.BIKeyDuplicateException;
import com.fr.bi.stable.data.BIFieldID;
import com.fr.general.ComparatorUtils;

/**
 * This class created on 2016/5/26.
 *
 * @author Connery
 * @since 4.0
 */
public class BusinessFieldSource extends BIBasicDataSource<BIFieldID, BusinessField> implements BusinessFieldSourceService {
    @Override
    boolean isEqual(BusinessField firstSource, BusinessField secondSource) {
        return ComparatorUtils.equals(firstSource, secondSource);
    }

    @Override
    protected BusinessField generateAbsentValue(BIFieldID key) {
        return null;
    }

    @Override
    public void envChanged() {
        clear();
    }

    @Override
    public BusinessField getBusinessField(BIFieldID id) throws BIKeyAbsentException {
        return getSource(id);
    }

    @Override
    public void addBusinessField(BIFieldID id, BusinessField source) throws BIKeyDuplicateException {
        addSource(id, source);
    }

    @Override
    public void removeBusinessField(BIFieldID id) throws BIKeyAbsentException {
        removeSource(id);
    }

    @Override
    public void editBusinessField(BIFieldID id, BusinessField source) throws BIKeyDuplicateException, BIKeyAbsentException {
        removeSource(id);
        addBusinessField(id, source);
    }

    @Override
    public boolean containFieldSource(BIFieldID id) {
        return contain(id);
    }
}
