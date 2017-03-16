/**
 *
 */
package com.finebi.cube.conf.datasource;

import com.finebi.cube.conf.table.BusinessTable;
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
@BIMandatedObject(factory = IFactoryService.CONF_XML, implement = BusinessTableSourceService.class)
public class BIBusinessTableSource extends BIBasicDataSource<BITableID, BusinessTable> implements BusinessTableSourceService {


    @Override
    boolean isEqual(BusinessTable firstSource, BusinessTable secondSource) {
        return ComparatorUtils.equals(firstSource, secondSource);
    }

    @Override
    public void envChanged() {
        clear();
    }

    @Override
    protected BusinessTable generateAbsentValue(BITableID key) {
        return null;
    }

    @Override
    public BusinessTable getBusinessTable(BITableID id) throws BIKeyAbsentException {
        return getSource(id);
    }

    @Override
    public void addBusinessTable(BITableID id, BusinessTable source) throws BIKeyDuplicateException {
        addSource(id, source);
    }

    @Override
    public void removeBusinessTable(BITableID id) throws BIKeyAbsentException {
        removeSource(id);
    }

    @Override
    public void editBusinessTable(BITableID id, BusinessTable source) throws BIKeyDuplicateException, BIKeyAbsentException {
        editSource(id, source);
    }

    @Override
    public boolean containBusinessTable(BITableID id) {
        return contain(id);
    }

}