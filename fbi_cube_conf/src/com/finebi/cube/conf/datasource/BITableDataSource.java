/**
 *
 */
package com.finebi.cube.conf.datasource;

import com.finebi.cube.conf.table.BusinessTable;
import com.fr.bi.common.factory.IFactoryService;
import com.fr.bi.common.factory.annotation.BIMandatedObject;
import com.fr.bi.exception.BIKeyAbsentException;
import com.fr.bi.exception.BIKeyDuplicateException;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.general.ComparatorUtils;

import java.util.Set;

/**
 * This class created on 2016/5/23.
 *
 * @author Connery
 * @since 4.0
 */
@BIMandatedObject(factory = IFactoryService.CONF_XML, implement = TableDataSourceService.class)
public class BITableDataSource extends BIBasicDataSource<BusinessTable, CubeTableSource> implements TableDataSourceService {

    @Override
    boolean isEqual(CubeTableSource firstSource, CubeTableSource secondSource) {
        return ComparatorUtils.equals(firstSource.getSourceID(), secondSource.getSourceID());
    }

    @Override
    protected CubeTableSource generateAbsentValue(BusinessTable key) {
        return null;
    }

    @Override
    public CubeTableSource getTableSource(BusinessTable tableID) throws BIKeyAbsentException {
        return getSource(tableID);
    }

    @Override
    public void addTableSource(BusinessTable tableID, CubeTableSource cubeTableSource) throws BIKeyDuplicateException {
        addSource(tableID, cubeTableSource);
    }

    @Override
    public void editTableSource(BusinessTable tableID, CubeTableSource cubeTableSource) throws BIKeyDuplicateException, BIKeyAbsentException {
        editSource(tableID, cubeTableSource);
    }

    @Override
    public void removeTableSource(BusinessTable businessTable) throws BIKeyAbsentException {
        removeSource(businessTable);
    }

    @Override
    public boolean containTableSource(BusinessTable businessTable) {
        return contain(businessTable);
    }

    @Override
    public boolean isRecord(CubeTableSource tableSource) {
        return cacheContainSource(tableSource);
    }

    @Override
    public Set<BusinessTable> getAllBusinessTable() {
        return container.keySet();
    }

    @Override
    public void envChanged() {
        clear();
    }
}