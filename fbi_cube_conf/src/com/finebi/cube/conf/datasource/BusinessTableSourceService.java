package com.finebi.cube.conf.datasource;

import com.finebi.cube.conf.table.BusinessTable;
import com.fr.bi.exception.BIKeyAbsentException;
import com.fr.bi.exception.BIKeyDuplicateException;
import com.fr.bi.stable.data.BITableID;

/**
 * This class created on 2016/5/25.
 *
 * @author Connery
 * @since 4.0
 */
public interface BusinessTableSourceService {
    void envChanged();

    BusinessTable getBusinessTable(BITableID id) throws BIKeyAbsentException;

    void addBusinessTable(BITableID id, BusinessTable source) throws BIKeyDuplicateException;

    void removeBusinessTable(BITableID id) throws BIKeyAbsentException;

    void editBusinessTable(BITableID id, BusinessTable source) throws BIKeyDuplicateException, BIKeyAbsentException;

    boolean containBusinessTable(BITableID id);
}
