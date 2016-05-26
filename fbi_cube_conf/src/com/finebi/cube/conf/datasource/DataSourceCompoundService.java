package com.finebi.cube.conf.datasource;

import com.finebi.cube.conf.table.BusinessTable;

import java.util.Set;

/**
 * This class created on 2016/5/23.
 *
 * @author Connery
 * @since 4.0
 */
public interface DataSourceCompoundService extends BusinessTableSourceService, TableDataSourceService, BusinessFieldSourceService {

    void initialDataSource(Set<BusinessTable> tables);
}