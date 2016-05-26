package com.finebi.cube.conf.datasource;

import com.finebi.cube.conf.BICubeConfigureCenter;
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

import java.util.Set;

/**
 * This class created on 2016/5/23.
 *
 * @author Connery
 * @since 4.0
 */
public class BIDataSourceManager extends BISystemDataManager<DataSourceCompoundService> implements BIDataSourceManagerProvider {
    @Override
    public DataSourceCompoundService constructUserManagerValue(Long userId) {
        return BIFactoryHelper.getObject(DataSourceCompoundService.class);
    }

    @Override
    public String managerTag() {
        return BIDataSourceManagerProvider.XML_TAG;
    }

    @Override
    public String persistUserDataName(long key) {
        return managerTag();
    }
    @Override
    public DataSourceCompoundService constructValue(Long key) {
        DataSourceCompoundService manager = constructUserManagerValue(key);
        initialUserManager(key, manager);
        return manager;
    }
    /**
     * 获得key的value
     * <p/>
     * 如果key值不存在，先调用一次相应key值缺失的函数。
     *
     * @param key 键
     * @return 值
     * @throws BIKeyAbsentException 经过key不存在函数处理后
     *                              如果依然不存在那么抛错
     */
    protected DataSourceCompoundService getValue(Long key) throws BIKeyAbsentException {
        BINonValueUtils.checkNull(key);
        synchronized (container) {
            if (!container.containsKey(key)) {
                DataSourceCompoundService value = generateAbsentValue(key);
                try {
                    if(value!=null) {
                        putKeyValue(key, value);
                    }
                    Set<BusinessTable> allTable = BICubeConfigureCenter.getPackageManager().getAllTables(-999);
                    initialDataSource(allTable);
                } catch (Exception e) {
                    throw BINonValueUtils.beyondControl();
                }
            }
            if (containsKey(key)) {
                return container.get(key);
            } else {
                throw new BIKeyAbsentException();
            }

        }
    }

    public DataSourceCompoundService getInstance() {
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
    public BusinessTable getBusinessTable(BITableID id) throws BIKeyAbsentException {
        return getInstance().getBusinessTable(id);
    }

    @Override
    public void addBusinessTable(BITableID id, BusinessTable source) throws BIKeyDuplicateException {
        getInstance().addBusinessTable(id, source);
    }

    @Override
    public void removeBusinessTable(BITableID id) throws BIKeyAbsentException {
        getInstance().removeBusinessTable(id);
    }

    @Override
    public void editBusinessTable(BITableID id, BusinessTable source) throws BIKeyDuplicateException, BIKeyAbsentException {
        getInstance().editBusinessTable(id, source);
    }

    @Override
    public CubeTableSource getTableSource(BusinessTable businessTable) throws BIKeyAbsentException {
        return getInstance().getTableSource(businessTable);
    }

    @Override
    public void addTableSource(BusinessTable businessTable, CubeTableSource source) throws BIKeyDuplicateException {
        getInstance().addTableSource(businessTable, source);
    }

    @Override
    public void removeTableSource(BusinessTable businessTable) throws BIKeyAbsentException {
        getInstance().removeTableSource(businessTable);
    }

    @Override
    public void editTableSource(BusinessTable businessTable, CubeTableSource source) throws BIKeyDuplicateException, BIKeyAbsentException {
        getInstance().editTableSource(businessTable, source);

    }

    @Override
    public boolean containTableSource(BusinessTable businessTable) {
        return getInstance().containTableSource(businessTable);
    }


    @Override
    public void persistData(long userId) {
        persistUserData(userId);
    }

    @Override
    public boolean containBusinessTable(BITableID id) {
        return getInstance().containBusinessTable(id);
    }


    public void initialDataSource(Set<BusinessTable> tables) {
        getInstance().initialDataSource(tables);
    }

    @Override
    public BusinessField getBusinessField(BIFieldID id) throws BIKeyAbsentException {
        return getInstance().getBusinessField(id);
    }

    @Override
    public void addBusinessField(BIFieldID id, BusinessField source) throws BIKeyDuplicateException {
        getInstance().addBusinessField(id, source);
    }

    @Override
    public void removeBusinessField(BIFieldID id) throws BIKeyAbsentException {
        getInstance().removeBusinessField(id);
    }

    @Override
    public void editBusinessField(BIFieldID id, BusinessField source) throws BIKeyDuplicateException, BIKeyAbsentException {
        getInstance().editBusinessField(id, source);
    }

    @Override
    public boolean containFieldSource(BIFieldID id) {
        return getInstance().containFieldSource(id);
    }
}