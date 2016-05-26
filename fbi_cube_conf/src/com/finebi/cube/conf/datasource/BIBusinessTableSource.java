/**
 *
 */
package com.finebi.cube.conf.datasource;

import com.finebi.cube.conf.table.BusinessTable;
import com.fr.bi.common.factory.IFactoryService;
import com.fr.bi.common.factory.annotation.BIMandatedObject;
import com.fr.bi.exception.BIKeyAbsentException;
import com.fr.bi.exception.BIKeyDuplicateException;
import com.fr.bi.stable.data.BIFieldID;
import com.fr.general.ComparatorUtils;

/**
 * This class created on 2016/5/23.
 *
 * @author Connery
 * @since 4.0
 */
@BIMandatedObject(factory = IFactoryService.CONF_XML, implement = BusinessTableSourceService.class)
public class BIBusinessTableSource extends BIBasicDataSource<BIFieldID, BusinessTable> implements BusinessTableSourceService {


    @Override
    boolean isEqual(BusinessTable firstSource, BusinessTable secondSource) {
        return ComparatorUtils.equals(firstSource, secondSource);
    }

    @Override
    public void envChanged() {
        clear();
    }

    @Override
    protected BusinessTable generateAbsentValue(BIFieldID key) {
        return null;
    }

    @Override
    public BusinessTable getBusinessTable(BIFieldID id) throws BIKeyAbsentException {
        return getSource(id);
    }

    @Override
    public void addBusinessTable(BIFieldID id, BusinessTable source) throws BIKeyDuplicateException {
        addSource(id, source);
    }

    @Override
    public void removeBusinessTable(BIFieldID id) throws BIKeyAbsentException {
        removeSource(id);
    }

    @Override
    public void editBusinessTable(BIFieldID id, BusinessTable source) throws BIKeyDuplicateException, BIKeyAbsentException {
        editSource(id, source);
    }

    @Override
    public boolean containFieldID(BIFieldID id) {
        return contain(id);
    }

}