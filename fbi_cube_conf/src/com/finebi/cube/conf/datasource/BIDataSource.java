package com.finebi.cube.conf.datasource;

import com.finebi.cube.conf.field.BusinessField;
import com.fr.bi.base.BICore;
import com.fr.bi.exception.BIFieldAbsentException;
import com.fr.bi.stable.data.BITableID;
import com.fr.bi.stable.data.db.ICubeFieldSource;
import com.fr.bi.stable.data.source.ICubeTableSource;
import com.fr.json.JSONTransform;

/**
 * This class created on 2016/5/23.
 *
 * @author Connery
 * @since 4.0
 */
public interface BIDataSource<T extends ICubeTableSource> extends JSONTransform {

    void envChanged();

    BICore getCoreByID(BITableID id);

    T getTableSourceByID(BITableID id);

    T getTableSourceByMD5(BICore core);

    void addTableSource(BITableID id, T source);

    void removeTableSource(BITableID id);

    void editTableSource(BITableID id, T source);

    ICubeFieldSource findDBField(BusinessField businessField)throws BIFieldAbsentException;
}