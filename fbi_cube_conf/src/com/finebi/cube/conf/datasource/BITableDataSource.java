/**
 *
 */
package com.finebi.cube.conf.datasource;

import com.fr.bi.common.factory.IFactoryService;
import com.fr.bi.common.factory.annotation.BIMandatedObject;
import com.fr.bi.exception.BIKeyAbsentException;
import com.fr.bi.exception.BIKeyDuplicateException;
import com.fr.bi.stable.data.BITableID;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.general.ComparatorUtils;

/**
 * This class created on 2016/5/23.
 *
 * @author Connery
 * @since 4.0
 */
@BIMandatedObject(factory = IFactoryService.CONF_XML, implement = TableDataSourceService.class)
public class BITableDataSource extends BIBasicDataSource<BITableID, CubeTableSource> implements TableDataSourceService {

    @Override
    boolean isEqual(CubeTableSource firstSource, CubeTableSource secondSource) {
        return ComparatorUtils.equals(firstSource.getSourceID(), secondSource.getSourceID());
    }

    @Override
    protected CubeTableSource generateAbsentValue(BITableID key) {
        return null;
    }

    @Override
    public CubeTableSource getTableSource(BITableID tableID) throws BIKeyAbsentException {
        return getSource(tableID);
    }

    @Override
    public void addTableSource(BITableID tableID, CubeTableSource cubeTableSource) throws BIKeyDuplicateException {
        addSource(tableID, cubeTableSource);
    }

    @Override
    public void editTableSource(BITableID tableID, CubeTableSource cubeTableSource) throws BIKeyDuplicateException, BIKeyAbsentException {
        editSource(tableID, cubeTableSource);
    }

    @Override
    public void removeTableSource(BITableID id) throws BIKeyAbsentException {
        removeSource(id);
    }

    @Override
    public void envChanged() {
        clear();
    }
}