package com.fr.bi.conf.base.datasource;

import com.fr.bi.base.BICore;
import com.fr.bi.exception.BIFieldAbsentException;
import com.fr.bi.stable.data.BIField;
import com.fr.bi.stable.data.BITableID;
import com.fr.bi.stable.data.db.DBField;
import com.fr.bi.stable.data.source.ITableSource;
import com.fr.json.JSONTransform;

/**
 * Created by Connery on 2015/12/7.
 */
public interface BIDataSource<T extends ITableSource> extends JSONTransform {

    void envChanged();

    BICore getCoreByID(BITableID id);

    T getTableSourceByID(BITableID id);

    T getTableSourceByMD5(BICore core);

    void addTableSource(BITableID id, T source);

    void removeTableSource(BITableID id);

    void editTableSource(BITableID id, T source);

    DBField findDBField(BIField biField)throws BIFieldAbsentException;
}