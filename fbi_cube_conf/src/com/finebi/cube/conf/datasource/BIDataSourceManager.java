package com.finebi.cube.conf.datasource;

import com.finebi.cube.conf.BIDataSourceManagerProvider;
import com.finebi.cube.conf.BISystemDataManager;
import com.finebi.cube.conf.field.BusinessField;
import com.fr.bi.base.BICore;
import com.fr.bi.base.BIUser;
import com.fr.bi.common.factory.BIFactoryHelper;
import com.fr.bi.exception.BIFieldAbsentException;
import com.fr.bi.exception.BIKeyAbsentException;
import com.fr.bi.stable.data.BITableID;
import com.fr.bi.stable.data.db.ICubeFieldSource;
import com.fr.bi.stable.data.source.ICubeTableSource;
import com.fr.bi.stable.utils.program.BINonValueUtils;
import com.fr.json.JSONObject;

/**
 * This class created on 2016/5/23.
 *
 * @author Connery
 * @since 4.0
 */
public class BIDataSourceManager extends BISystemDataManager<BIDataSource> implements BIDataSourceManagerProvider {
    @Override
    public BIDataSource constructUserManagerValue(Long userId) {
        return BIFactoryHelper.getObject(BIDataSource.class, userId);
    }

    @Override
    public String managerTag() {
        return null;
    }

    @Override
    public String persistUserDataName(long key) {
        return null;
    }

    public BIDataSource getInstance(BIUser user) {
        try {
            return getValue(user.getUserId());
        } catch (BIKeyAbsentException e) {
            throw BINonValueUtils.beyondControl(e);
        }
    }


    @Override
    public BICore getCoreByTableID(BITableID id, BIUser user) {
        return getInstance(user).getCoreByID(id);
    }

    @Override
    public ICubeTableSource getTableSourceByID(BITableID id, BIUser user) {
        return getInstance(user).getTableSourceByID(id);
    }

    @Override
    public ICubeTableSource getTableSourceByCore(BICore core, BIUser user) {
        return getInstance(user).getTableSourceByMD5(core);
    }

    @Override
    public void addCore2SourceRelation(BITableID id, ICubeTableSource source, BIUser user) {
        getInstance(user).addTableSource(id, source);
    }

    @Override
    public void removeCore2SourceRelation(BITableID id, BIUser userId) {
        getInstance(userId).removeTableSource(id);
    }

    @Override
    public void editCoreAndTable(BITableID id, ICubeTableSource source, BIUser user) {
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
    public ICubeFieldSource findDBField(BIUser user, BusinessField biField) throws BIFieldAbsentException {
        return getInstance(user).findDBField(biField);
    }
}