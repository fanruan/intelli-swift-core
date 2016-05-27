package com.fr.bi.etl.analysis.manager;


import com.finebi.cube.conf.field.BusinessField;
import com.fr.bi.base.BICore;
import com.fr.bi.base.BIUser;
import com.fr.bi.etl.analysis.data.AnalysisCubeTableSource;
import com.fr.bi.exception.BIFieldAbsentException;
import com.fr.bi.stable.data.BITableID;
import com.fr.bi.stable.data.db.ICubeFieldSource;
import com.fr.json.JSONObject;

/**
 * Created by 小灰灰 on 2015/12/14.
 */
public interface BIAnalysisDataSourceManagerProvider {
    String XML_TAG = "BIAnalysisDataSourceManager";

    void addSource(AnalysisCubeTableSource source, long userId);

    BICore getCoreByTableID(BITableID id, BIUser userId);

    AnalysisCubeTableSource getTableSourceByID(BITableID id, BIUser userId);

    AnalysisCubeTableSource getTableSourceByCore(BICore core, BIUser userId);

    /**
     * 增加md5表
     *
     * @param id
     * @param userId 用户id
     */
    void addCore2SourceRelation(BITableID id, AnalysisCubeTableSource source, BIUser userId);

    void removeCore2SourceRelation(BITableID id, BIUser userId);

    /**
     * 修改md5表
     *
     * @param id
     * @param userId 用户id
     */
    void editCoreAndTable(BITableID id, AnalysisCubeTableSource source, BIUser userId);

    /**
     * 环境改变
     */
    void envChanged();

    JSONObject createJSON(BIUser user) throws Exception;

    ICubeFieldSource findDBField(BIUser user, BusinessField biField) throws BIFieldAbsentException;

    @Deprecated
    void persistData(long userId);
}