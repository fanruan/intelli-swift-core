package com.finebi.cube.conf.datasource;

import com.finebi.cube.conf.table.BusinessTable;
import com.fr.bi.common.factory.IFactoryService;
import com.fr.bi.common.factory.annotation.BIMandatedObject;
import com.fr.bi.exception.BIKeyAbsentException;
import com.fr.bi.exception.BIKeyDuplicateException;
import com.fr.bi.stable.data.BIFieldID;
import com.fr.bi.stable.data.BITableID;
import com.fr.bi.stable.data.source.CubeTableSource;

/**
 * This class created on 2016/5/25.
 *
 * @author Connery
 * @since 4.0
 */
@BIMandatedObject(factory = IFactoryService.CONF_XML, implement = DataSourceCompound.class)

public class DataSourceCompound implements FieldDataSourceService, TableDataSourceService {
    private FieldDataSourceService fieldDataSource;
    private TableDataSourceService tableDataSource;

    public DataSourceCompound(FieldDataSourceService fieldDataSource, TableDataSourceService tableDataSource) {
        this.fieldDataSource = fieldDataSource;
        this.tableDataSource = tableDataSource;
    }

    public DataSourceCompound() {
        this.fieldDataSource = new BIFieldDataSource();
        this.tableDataSource = new BITableDataSource();
    }

    @Override
    public BusinessTable getBusinessTable(BIFieldID id) throws BIKeyAbsentException {
        return fieldDataSource.getBusinessTable(id);
    }

    @Override
    public void addBusinessTable(BIFieldID id, BusinessTable source) throws BIKeyDuplicateException {
        fieldDataSource.addBusinessTable(id, source);
    }

    @Override
    public void removeBusinessTable(BIFieldID id) throws BIKeyAbsentException {
        fieldDataSource.removeBusinessTable(id);
    }

    @Override
    public void editBusinessTable(BIFieldID id, BusinessTable source) throws BIKeyDuplicateException, BIKeyAbsentException {
        fieldDataSource.editBusinessTable(id, source);
    }

    @Override
    public void envChanged() {
        tableDataSource.envChanged();
        fieldDataSource.envChanged();
    }

    @Override
    public CubeTableSource getTableSource(BITableID id) throws BIKeyAbsentException {
        return tableDataSource.getTableSource(id);
    }

    @Override
    public void addTableSource(BITableID id, CubeTableSource source) throws BIKeyDuplicateException {
        tableDataSource.addTableSource(id, source);
    }

    @Override
    public void removeTableSource(BITableID id) throws BIKeyAbsentException {
        tableDataSource.removeTableSource(id);
    }

    @Override
    public void editTableSource(BITableID id, CubeTableSource source) throws BIKeyDuplicateException, BIKeyAbsentException {
        tableDataSource.editTableSource(id, source);
    }
}
