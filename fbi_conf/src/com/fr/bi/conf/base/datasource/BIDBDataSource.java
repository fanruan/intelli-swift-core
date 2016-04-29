package com.fr.bi.conf.base.datasource;

import com.fr.bi.base.BICore;
import com.fr.bi.exception.BIFieldAbsentException;
import com.fr.bi.stable.data.BIField;
import com.fr.bi.stable.data.BITableID;
import com.fr.bi.stable.data.db.DBField;
import com.fr.bi.stable.data.source.ITableSource;
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
    public ITableSource getTableSourceByID(BITableID id) {
        return null;
    }

    @Override
    public ITableSource getTableSourceByMD5(BICore core) {
        return null;
    }

    @Override
    public void addTableSource(BITableID id, ITableSource source) {

    }

    @Override
    public void removeTableSource(BITableID id) {

    }

    @Override
    public void editTableSource(BITableID id, ITableSource source) {

    }

    @Override
    public JSONObject createJSON() throws Exception {
        return null;
    }

    @Override
    public void parseJSON(JSONObject jsonObject) throws Exception {

    }

    @Override
    public DBField findDBField(BIField biField) throws BIFieldAbsentException {
        return null;
    }
}