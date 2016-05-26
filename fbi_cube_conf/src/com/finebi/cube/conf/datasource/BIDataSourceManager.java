package com.finebi.cube.conf.datasource;

import com.finebi.cube.conf.BIDataSourceManagerProvider;
import com.finebi.cube.conf.BISystemDataManager;
import com.finebi.cube.conf.field.BusinessField;
import com.finebi.cube.conf.table.BusinessTable;
import com.fr.bi.common.factory.BIFactoryHelper;
import com.fr.bi.exception.BIFieldAbsentException;
import com.fr.bi.exception.BIKeyAbsentException;
import com.fr.bi.exception.BIKeyDuplicateException;
import com.fr.bi.stable.data.BIFieldID;
import com.fr.bi.stable.data.BITableID;
import com.fr.bi.stable.data.db.ICubeFieldSource;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.utils.program.BINonValueUtils;
import com.fr.general.ComparatorUtils;

/**
 * This class created on 2016/5/23.
 *
 * @author Connery
 * @since 4.0
 */
public class BIDataSourceManager extends BISystemDataManager<DataSourceCompound> implements BIDataSourceManagerProvider {
    @Override
    public DataSourceCompound constructUserManagerValue(Long userId) {
        return BIFactoryHelper.getObject(DataSourceCompound.class);
    }

    @Override
    public String managerTag() {
        return BIDataSourceManagerProvider.XML_TAG;
    }

    @Override
    public String persistUserDataName(long key) {
        return managerTag();
    }

    public DataSourceCompound getInstance() {
        try {
            return getValue(-999L);
        } catch (BIKeyAbsentException e) {
            throw BINonValueUtils.beyondControl(e);
        }
    }


    @Override
    public ICubeFieldSource findDBField(BusinessField businessField) throws BIFieldAbsentException {
        BusinessTable table = businessField.getTableBelongTo();
        CubeTableSource tableSource = table.getTableSource();
        ICubeFieldSource[] BICubeFieldSources = tableSource.getFieldsArray(null);
        for (int i = 0; i < BICubeFieldSources.length; i++) {
            if (ComparatorUtils.equals(businessField.getFieldName(), BICubeFieldSources[i].getFieldName())) {
                return BICubeFieldSources[i];
            }
        }
        throw new BIFieldAbsentException("the field is:" + businessField != null ? businessField.getFieldName() : "null");
    }

    @Override
    public void envChanged() {
        getInstance().envChanged();
    }

    @Override
    public BusinessTable getBusinessTable(BIFieldID id) throws BIKeyAbsentException {
        return getInstance().getBusinessTable(id);
    }

    @Override
    public void addBusinessTable(BIFieldID id, BusinessTable source) throws BIKeyDuplicateException {
        getInstance().addBusinessTable(id, source);
    }

    @Override
    public void removeBusinessTable(BIFieldID id) throws BIKeyAbsentException {
        getInstance().removeBusinessTable(id);
    }

    @Override
    public void editBusinessTable(BIFieldID id, BusinessTable source) throws BIKeyDuplicateException, BIKeyAbsentException {
        getInstance().editBusinessTable(id, source);
    }

    @Override
    public CubeTableSource getTableSource(BITableID id) throws BIKeyAbsentException {
        return getInstance().getTableSource(id);
    }

    @Override
    public void addTableSource(BITableID id, CubeTableSource source) throws BIKeyDuplicateException {
        getInstance().addTableSource(id, source);
    }

    @Override
    public void removeTableSource(BITableID id) throws BIKeyAbsentException {
        getInstance().removeTableSource(id);
    }

    @Override
    public void editTableSource(BITableID id, CubeTableSource source) throws BIKeyDuplicateException, BIKeyAbsentException {
        getInstance().editTableSource(id, source);

    }

    @Override
    public void persistData(long userId) {
        persistUserData(userId);
    }
}