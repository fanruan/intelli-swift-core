package com.finebi.cube.conf.datasource;

import com.finebi.cube.conf.table.BusinessTable;
import com.fr.bi.exception.BIKeyAbsentException;
import com.fr.bi.exception.BIKeyDuplicateException;
import com.fr.bi.stable.data.BIFieldID;

/**
 * This class created on 2016/5/25.
 *
 * @author Connery
 * @since 4.0
 */
public interface FieldDataSourceService {
    void envChanged();

    BusinessTable getBusinessTable(BIFieldID id) throws BIKeyAbsentException;

    void addBusinessTable(BIFieldID id, BusinessTable source) throws BIKeyDuplicateException;

    void removeBusinessTable(BIFieldID id) throws BIKeyAbsentException;

    void editBusinessTable(BIFieldID id, BusinessTable source) throws BIKeyDuplicateException, BIKeyAbsentException;
}
