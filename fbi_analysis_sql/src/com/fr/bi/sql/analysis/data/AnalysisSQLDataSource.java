package com.fr.bi.sql.analysis.data;


import com.finebi.cube.conf.field.BusinessField;
import com.fr.bi.base.BICore;
import com.fr.bi.exception.BIFieldAbsentException;
import com.fr.bi.stable.data.BITableID;
import com.fr.bi.stable.data.db.ICubeFieldSource;
import com.fr.json.JSONTransform;

/**
 * Created by 小灰灰 on 2015/12/14.
 */
public interface AnalysisSQLDataSource extends JSONTransform {
    void addCoreSource(AnalysisSQLTableSource source);

    void envChanged();

    BICore getCoreByID(BITableID id);

    AnalysisSQLTableSource getTableSourceByID(BITableID id);

    AnalysisSQLTableSource getTableSourceByMD5(BICore core);

    void addTableSource(BITableID id, AnalysisSQLTableSource source);

    void removeTableSource(BITableID id);

    void editTableSource(BITableID id, AnalysisSQLTableSource source);

    ICubeFieldSource findDBField(BusinessField businessField) throws BIFieldAbsentException;
}