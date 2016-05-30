package com.finebi.cube.conf.datasource;

import com.finebi.cube.conf.field.BusinessField;
import com.finebi.cube.conf.table.BusinessTable;
import com.finebi.cube.conf.table.BusinessTableHelper;
import com.fr.bi.common.factory.IFactoryService;
import com.fr.bi.common.factory.annotation.BIMandatedObject;
import com.fr.bi.common.persistent.xml.BIIgnoreField;
import com.fr.bi.exception.BIKeyAbsentException;
import com.fr.bi.exception.BIKeyDuplicateException;
import com.fr.bi.stable.data.BIFieldID;
import com.fr.bi.stable.data.BITableID;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.utils.program.BINonValueUtils;

import java.util.List;
import java.util.Set;

/**
 * This class created on 2016/5/25.
 *
 * @author Connery
 * @since 4.0
 */
@BIMandatedObject(factory = IFactoryService.CONF_XML, implement = DataSourceCompoundService.class)
public class DataSourceCompound implements DataSourceCompoundService {
    @BIIgnoreField
    private BusinessTableSourceService businessTabledDataSource;
    private TableDataSourceService tableDataSource;
    @BIIgnoreField
    private BusinessFieldSourceService businessFieldDataService;

    public DataSourceCompound(BusinessTableSourceService businessTabledDataSource, TableDataSourceService tableDataSource) {
        this.businessTabledDataSource = businessTabledDataSource;
        this.tableDataSource = tableDataSource;
    }

    public DataSourceCompound() {
        this.businessTabledDataSource = new BIBusinessTableSource();
        this.tableDataSource = new BITableDataSource();
        businessFieldDataService = new BusinessFieldSource();
    }

    @Override
    public BusinessTable getBusinessTable(BITableID id) throws BIKeyAbsentException {
        return businessTabledDataSource.getBusinessTable(id);
    }

    @Override
    public void addBusinessTable(BITableID id, BusinessTable source) throws BIKeyDuplicateException {
        businessTabledDataSource.addBusinessTable(id, source);
    }

    @Override
    public void removeBusinessTable(BITableID id) throws BIKeyAbsentException {
        businessTabledDataSource.removeBusinessTable(id);
    }

    @Override
    public void editBusinessTable(BITableID id, BusinessTable source) throws BIKeyDuplicateException, BIKeyAbsentException {
        businessTabledDataSource.editBusinessTable(id, source);
    }

    @Override
    public void envChanged() {
        tableDataSource.envChanged();
        businessTabledDataSource.envChanged();
    }

    @Override
    public CubeTableSource getTableSource(BusinessTable businessTable) throws BIKeyAbsentException {
        return tableDataSource.getTableSource(businessTable);
    }

    @Override
    public void addTableSource(BusinessTable businessTable, CubeTableSource source) throws BIKeyDuplicateException {
        if (tableDataSource.containTableSource(businessTable)) {
            try {
                removeTableSource(businessTable);
            } catch (BIKeyAbsentException e) {
                e.printStackTrace();
            }
        }
        tableDataSource.addTableSource(businessTable, source);
        addBusinessTable(businessTable);
    }

    private void addBusinessTable(BusinessTable businessTable) {
        try {
            if (containTableSource(businessTable)) {
                businessTable.setSource(getTableSource(businessTable));
                addTable2FieldSource(businessTable);
                addTable2TableSource(businessTable);
            }
        } catch (BIKeyAbsentException e) {
            throw BINonValueUtils.beyondControl(e);
        }
    }

    private void addTable2TableSource(BusinessTable businessTable) {
        if (!businessTabledDataSource.containBusinessTable(businessTable.getID())) {
            try {
                businessTabledDataSource.addBusinessTable(businessTable.getID(), businessTable);
            } catch (BIKeyDuplicateException e) {
                e.printStackTrace();
            }
        }
    }

    private void addTable2FieldSource(BusinessTable businessTable) {
        if (businessTable.getTableSource() != null) {
            List<BusinessField> fields = BusinessTableHelper.getTableFields(businessTable);
            for (BusinessField field : fields) {
                if (!containFieldSource(field.getFieldID())) {
                    try {
                        addBusinessField(field.getFieldID(), field);
                    } catch (BIKeyDuplicateException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public void removeTableSource(BusinessTable businessTable) throws BIKeyAbsentException {
        tableDataSource.removeTableSource(businessTable);
        /**
         * BusinessTable和TableSource对应关系被删除
         * 那么tableID和BusinessTable也要被删除。
         */
        businessTabledDataSource.removeBusinessTable(businessTable.getID());
    }

    @Override
    public void editTableSource(BusinessTable businessTable, CubeTableSource source) throws BIKeyDuplicateException, BIKeyAbsentException {
        tableDataSource.editTableSource(businessTable, source);
    }

    @Override
    public boolean containTableSource(BusinessTable businessTable) {
        return tableDataSource.containTableSource(businessTable);
    }

    @Override
    public Set<BusinessTable> getAllBusinessTable() {
        return tableDataSource.getAllBusinessTable();
    }

    @Override
    public boolean containBusinessTable(BITableID id) {
        return businessTabledDataSource.containBusinessTable(id);
    }

    @Override
    public BusinessField getBusinessField(BIFieldID id) throws BIKeyAbsentException {
        return businessFieldDataService.getBusinessField(id);
    }

    @Override
    public void addBusinessField(BIFieldID id, BusinessField source) throws BIKeyDuplicateException {
        businessFieldDataService.addBusinessField(id, source);
    }

    @Override
    public void removeBusinessField(BIFieldID id) throws BIKeyAbsentException {
        businessFieldDataService.removeBusinessField(id);
    }

    @Override
    public void editBusinessField(BIFieldID id, BusinessField source) throws BIKeyDuplicateException, BIKeyAbsentException {
        businessFieldDataService.editBusinessField(id, source);
    }

    @Override
    public boolean containFieldSource(BIFieldID id) {
        return businessFieldDataService.containFieldSource(id);
    }

    @Override
    public void initialDataSource(Set<BusinessTable> tables) {
        for (BusinessTable table : tables) {
            addBusinessTable(table);
        }
    }

    @Override
    public void initialAll() {
        initialDataSource(tableDataSource.getAllBusinessTable());
    }
}
