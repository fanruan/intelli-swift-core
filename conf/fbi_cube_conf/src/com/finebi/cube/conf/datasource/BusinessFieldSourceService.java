package com.finebi.cube.conf.datasource;

import com.finebi.cube.conf.field.BusinessField;
import com.fr.bi.exception.BIKeyAbsentException;
import com.fr.bi.exception.BIKeyDuplicateException;
import com.fr.bi.stable.data.BIFieldID;

/**
 * This class created on 2016/5/26.
 *
 * @author Connery
 * @since 4.0
 */
public interface BusinessFieldSourceService {
    void envChanged();

    BusinessField getBusinessField(BIFieldID id) throws BIKeyAbsentException;

    void addBusinessField(BIFieldID id, BusinessField source) throws BIKeyDuplicateException;

    void removeBusinessField(BIFieldID id) throws BIKeyAbsentException;

    void editBusinessField(BIFieldID id, BusinessField source) throws BIKeyDuplicateException, BIKeyAbsentException;

    boolean containFieldSource(BIFieldID id);
}
