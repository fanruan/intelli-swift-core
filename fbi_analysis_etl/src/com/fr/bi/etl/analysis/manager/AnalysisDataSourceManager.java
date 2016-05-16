package com.fr.bi.etl.analysis.manager;

import com.fr.bi.base.BICore;
import com.fr.bi.base.BIUser;
import com.fr.bi.common.factory.BIFactoryHelper;
import com.fr.bi.conf.base.BISystemDataManager;
import com.fr.bi.etl.analysis.data.AnalysisDataSource;
import com.fr.bi.etl.analysis.data.AnalysisTableSource;
import com.fr.bi.exception.BIFieldAbsentException;
import com.fr.bi.exception.BIKeyAbsentException;
import com.fr.bi.stable.data.BIField;
import com.fr.bi.stable.data.BITableID;
import com.fr.bi.stable.data.db.DBField;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.fs.control.UserControl;
import com.fr.json.JSONObject;

import java.io.File;

/**
 * Created by 小灰灰 on 2015/12/14.
 */
public class AnalysisDataSourceManager extends BISystemDataManager<AnalysisDataSource> implements BIAnalysisDataSourceManagerProvider {

    private transient AnalysisDataSource superManager = getInstance(new BIUser(UserControl.getInstance().getSuperManagerID()));

    public AnalysisDataSource getInstance(BIUser user) {
        try {
            return getValue(user.getUserId());
        } catch (BIKeyAbsentException e) {

            throw new NullPointerException("Please check the userID:" + user.getUserId() + ",which getIndex a empty manager");
        }

    }

    @Override
    public AnalysisDataSource constructUserManagerValue(Long userId) {
        try {
            return BIFactoryHelper.getObject(AnalysisDataSource.class, userId);
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public String managerTag() {
        return "AnalysisDataSourceManager" ;
    }

    @Override
    public String persistUserDataName(long key) {
        return "sue" + File.separator + "datasource"  + key;
    }

    @Override
    public BICore getCoreByTableID(BITableID id, BIUser user) {
        BICore md5 = getInstance(user).getCoreByID(id);
        if (md5 == null) {
            md5 = superManager.getCoreByID(id);
        }
        return md5;
    }

    @Override
    public AnalysisTableSource getTableSourceByID(BITableID id, BIUser user) {
        AnalysisTableSource source = getInstance(user).getTableSourceByID(id);
        if (source == null) {
            source = superManager.getTableSourceByID(id);
        }
        return source;
    }

    @Override
    public AnalysisTableSource getTableSourceByCore(BICore core, BIUser user) {
        AnalysisTableSource source = getInstance(user).getTableSourceByMD5(core);
        if (source == null) {
            source = superManager.getTableSourceByMD5(core);
        }
        return source;
    }

    @Override
    public void addCore2SourceRelation(BITableID id, AnalysisTableSource source, BIUser user) {
        getInstance(user).addTableSource(id, source);
    }

    @Override
    public void removeCore2SourceRelation(BITableID id, BIUser user) {
        getInstance(user).removeTableSource(id);
    }

    @Override
    public void editCoreAndTable(BITableID id, AnalysisTableSource source, BIUser user) {
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
        return null;
    }

    @Override
    public void addSource(AnalysisTableSource source, long userId) {
        getInstance(new BIUser(userId)).addCoreSource(source);
    }
}