package com.finebi.cube.conf.datasource;

import com.fr.bi.exception.BIKeyAbsentException;
import com.fr.bi.exception.BIKeyDuplicateException;
import com.fr.bi.stable.data.BITableID;
import com.fr.bi.stable.data.source.CubeTableSource;

/**
 * This class created on 2016/5/25.
 *
 * @author Connery
 * @since 4.0
 */
public interface TableDataSourceService {
    void envChanged();

    CubeTableSource getTableSource(BITableID id) throws BIKeyAbsentException;

    void addTableSource(BITableID id, CubeTableSource source) throws BIKeyDuplicateException;

    void removeTableSource(BITableID id) throws BIKeyAbsentException;

    void editTableSource(BITableID id, CubeTableSource source) throws BIKeyDuplicateException, BIKeyAbsentException;
}
