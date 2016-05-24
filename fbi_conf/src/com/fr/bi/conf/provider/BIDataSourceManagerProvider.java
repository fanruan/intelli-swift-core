package com.fr.bi.conf.provider;

import com.fr.bi.base.BICore;
import com.fr.bi.base.BIUser;
import com.fr.bi.exception.BIFieldAbsentException;
import com.fr.bi.stable.data.BIField;
import com.fr.bi.stable.data.BITableID;
import com.fr.bi.stable.data.db.ICubeFieldSource;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.json.JSONObject;

public interface BIDataSourceManagerProvider<T extends CubeTableSource> {

    String XML_TAG = "BIDataSourceManager";

    BICore getCoreByTableID(BITableID id, BIUser userId);

    T getTableSourceByID(BITableID id, BIUser userId);

    T getTableSourceByCore(BICore core, BIUser userId);

    /**
     * 增加md5表
     *
     * @param id
     * @param userId 用户id
     */
    void addCore2SourceRelation(BITableID id, T source, BIUser userId);

    void removeCore2SourceRelation(BITableID id, BIUser userId);

    /**
     * 修改md5表
     *
     * @param id
     * @param userId 用户id
     */
    void editCoreAndTable(BITableID id, T source, BIUser userId);

    /**
     * 环境改变
     */
    void envChanged();

    JSONObject createJSON(BIUser user) throws Exception;

    ICubeFieldSource findDBField(BIUser user, BIField biField) throws BIFieldAbsentException;

    @Deprecated
    void persistData(long userId);
}