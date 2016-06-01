package com.finebi.cube.conf.datasource;

import com.finebi.cube.conf.table.BusinessTable;
import com.fr.bi.exception.BIKeyAbsentException;
import com.fr.bi.exception.BIKeyDuplicateException;
import com.fr.bi.stable.data.source.CubeTableSource;

import java.util.Set;

/**
 * This class created on 2016/5/25.
 *
 * @author Connery
 * @since 4.0
 */
public interface TableDataSourceService {
    void envChanged();

    CubeTableSource getTableSource(BusinessTable businessTable) throws BIKeyAbsentException;

    void addTableSource(BusinessTable businessTable, CubeTableSource source) throws BIKeyDuplicateException;

    void removeTableSource(BusinessTable businessTable) throws BIKeyAbsentException;

    void editTableSource(BusinessTable businessTable, CubeTableSource source) throws BIKeyDuplicateException, BIKeyAbsentException;

    boolean containTableSource(BusinessTable businessTable);

    Set<BusinessTable> getAllBusinessTable();
}
