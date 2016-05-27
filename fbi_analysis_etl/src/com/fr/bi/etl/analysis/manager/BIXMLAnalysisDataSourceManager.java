package com.fr.bi.etl.analysis.manager;

import com.finebi.cube.conf.BICubeConfigureCenter;
import com.finebi.cube.conf.field.BusinessField;
import com.fr.bi.base.BICore;
import com.fr.bi.common.factory.IFactoryService;
import com.fr.bi.common.factory.annotation.BIMandatedObject;
import com.fr.bi.etl.analysis.data.AnalysisCubeTableSource;
import com.fr.bi.etl.analysis.data.AnalysisDataSource;
import com.fr.bi.exception.BIFieldAbsentException;
import com.fr.bi.stable.data.BITableID;
import com.fr.bi.stable.data.db.CubeFieldSource;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.general.GeneralContext;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;
import com.fr.stable.EnvChangedListener;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by 小灰灰 on 2015/12/14.
 */
@BIMandatedObject(factory = IFactoryService.CONF_XML, implement = AnalysisDataSource.class)
public class BIXMLAnalysisDataSourceManager implements AnalysisDataSource {

    private static final String XML_TAG = "DataSourceManager";

    private Map<BICore, AnalysisCubeTableSource> md5Tables = new ConcurrentHashMap<BICore, AnalysisCubeTableSource>();

    private Map<BITableID, BICore> idMd5Tables = new ConcurrentHashMap<BITableID, BICore>();

    private long userId;


    public BIXMLAnalysisDataSourceManager(long userId) {
        this.userId = userId;
    }

    static {
        GeneralContext.addEnvChangedListener(new EnvChangedListener() {
            @Override
            public void envChanged() {
                BICubeConfigureCenter.getDataSourceManager().envChanged();
            }
        });
    }

    /**
     * 环境改变
     */
    @Override
    public void envChanged() {

        md5Tables.clear();

        idMd5Tables.clear();
    }

    @Override
    public BICore getCoreByID(BITableID id) {
        return idMd5Tables.get(id);
    }

    @Override
    public AnalysisCubeTableSource getTableSourceByID(BITableID id) {
        if (id == null) {
            return null;
        }
        BICore md5 = getCoreByID(id);
        if (md5 == null) {
            return null;
        }
        return getTableSourceByMD5(md5);
    }

    @Override
    public AnalysisCubeTableSource getTableSourceByMD5(BICore core) {
        return md5Tables.get(core);
    }

    /**
     * 增加md5表
     */
    @Override
    public void addTableSource(BITableID id, AnalysisCubeTableSource source) {
        synchronized (this) {
            BICore bimd5Core = source.fetchObjectCore();
            md5Tables.put(bimd5Core, source);
            idMd5Tables.put(id, bimd5Core);
        }
    }

    @Override
    public void removeTableSource(BITableID id) {

    }

    /**
     * 修改md5表
     */
    @Override
    public void editTableSource(BITableID id, AnalysisCubeTableSource source) {
        synchronized (this) {
            BICore md5Name = source.fetchObjectCore();
            BICore oldMD5 = idMd5Tables.get(id);
            idMd5Tables.put(id, md5Name);
            md5Tables.put(md5Name, source);
            if (oldMD5 != null && !idMd5Tables.values().contains(oldMD5)) {
                md5Tables.remove(oldMD5);
            }
        }
    }

    @Override
    public JSONObject createJSON() throws JSONException {
        JSONObject jo = new JSONObject();
        for (Map.Entry<BITableID, BICore> id : idMd5Tables.entrySet()) {
            CubeTableSource source = md5Tables.get(id.getValue());
            if (source != null) {
                try {
                    jo.put(id.getKey().getIdentityValue(), source.createJSON());
                } catch (Exception e) {
                    BILogger.getLogger().error(e.getMessage(), e);
                }
            }
        }
        return jo;
    }

    @Override
    public void parseJSON(JSONObject jsonObject) throws Exception {

    }

    @Override
    public CubeFieldSource findDBField(BusinessField businessField) throws BIFieldAbsentException {
        return null;
    }

    @Override
    public void addCoreSource(AnalysisCubeTableSource source) {
        synchronized (this) {
            BICore bimd5Core = source.fetchObjectCore();
            md5Tables.put(bimd5Core, source);
        }
    }
}