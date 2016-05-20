package com.fr.bi.conf.base.datasource;

import com.fr.bi.base.BICore;
import com.fr.bi.exception.BIFieldAbsentException;
import com.fr.bi.stable.data.BIField;
import com.fr.bi.stable.data.BITableID;
import com.fr.bi.stable.data.db.BICubeFieldSource;
import com.fr.bi.stable.data.source.ICubeTableSource;
import com.fr.json.JSONObject;

/**
 * Created by Connery on 2015/12/7.
 */
public class BIDBDataSource implements BIDataSource {
    @Override
    public void envChanged() {

    }

    @Override
    public BICore getCoreByID(BITableID id) {
        return null;
    }

    @Override
    public ICubeTableSource getTableSourceByID(BITableID id) {
        return null;
    }

    @Override
    public ICubeTableSource getTableSourceByMD5(BICore core) {
        return null;
    }

    @Override
    public void addTableSource(BITableID id, ICubeTableSource source) {

    }

    @Override
    public void removeTableSource(BITableID id) {

    }

    @Override
    public void editTableSource(BITableID id, ICubeTableSource source) {

    }

    @Override
    public JSONObject createJSON() throws Exception {
        return null;
    }

    @Override
    public void parseJSON(JSONObject jsonObject) throws Exception {

    }

    @Override
    public BICubeFieldSource findDBField(BIField biField) throws BIFieldAbsentException {
        return null;
    }
}