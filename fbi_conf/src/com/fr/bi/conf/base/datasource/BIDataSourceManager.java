package com.fr.bi.conf.base.datasource;

import com.fr.bi.base.BICore;
import com.fr.bi.base.BIUser;
import com.fr.bi.common.container.BIStableMapContainer;
import com.fr.bi.common.factory.BIFactoryHelper;
import com.fr.bi.conf.provider.BIDataSourceManagerProvider;
import com.fr.bi.exception.BIFieldAbsentException;
import com.fr.bi.exception.BIKeyAbsentException;
import com.fr.bi.stable.data.BIField;
import com.fr.bi.stable.data.BITableID;
import com.fr.bi.stable.data.db.DBField;
import com.fr.bi.stable.data.source.ITableSource;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.json.JSONObject;

/**
 * rt
 *
 * @author Daniel
 */
public class BIDataSourceManager extends BIStableMapContainer<Long, BIDataSource> implements BIDataSourceManagerProvider {


    public BIDataSource getInstance(BIUser user) {
        try {
            return getValue(user.getUserId());
        } catch (BIKeyAbsentException e) {
            throw new NullPointerException("Please check the Userid:" + user.getUserId() + ",which getIndex empty Datasource Manager");
        }
    }

    @Override
    public BIDataSource constructValue(Long key) {
        try {
            return BIFactoryHelper.getObject(BIDataSource.class, key);
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public BICore getCoreByTableID(BITableID id, BIUser user) {
        return getInstance(user).getCoreByID(id);
    }

    @Override
    public ITableSource getTableSourceByID(BITableID id, BIUser user) {
        return getInstance(user).getTableSourceByID(id);
    }

    @Override
    public ITableSource getTableSourceByCore(BICore core, BIUser user) {
        return getInstance(user).getTableSourceByMD5(core);
    }

    @Override
    public void addCore2SourceRelation(BITableID id, ITableSource source, BIUser user) {
        getInstance(user).addTableSource(id, source);
    }

    @Override
    public void removeCore2SourceRelation(BITableID id, BIUser userId) {
        getInstance(userId).removeTableSource(id);
    }

    @Override
    public void editCoreAndTable(BITableID id, ITableSource source, BIUser user) {
        getInstance(user).editTableSource(id, source);
    }

    @Override
    public void envChanged() {
        clear();
    }

    @Override
    public JSONObject createJSON(BIUser user) throws Exception {
        return getInstance(user).createJSON();
    }

    @Override
    public DBField findDBField(BIUser user, BIField biField) throws BIFieldAbsentException {
        return getInstance(user).findDBField(biField);
    }
}