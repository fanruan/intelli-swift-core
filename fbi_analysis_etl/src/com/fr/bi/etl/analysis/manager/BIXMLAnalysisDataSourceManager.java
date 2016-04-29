package com.fr.bi.etl.analysis.manager;

import com.fr.base.FRContext;
import com.fr.bi.base.BICore;
import com.fr.bi.common.factory.IFactoryService;
import com.fr.bi.common.factory.annotation.BIMandatedObject;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.etl.analysis.data.AnalysisDataSource;
import com.fr.bi.etl.analysis.data.AnalysisTableSource;
import com.fr.bi.exception.BIFieldAbsentException;
import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.bi.stable.data.BIField;
import com.fr.bi.stable.data.BITableID;
import com.fr.bi.stable.data.db.DBField;
import com.fr.bi.stable.data.source.ITableSource;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.file.XMLFileManager;
import com.fr.general.GeneralContext;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;
import com.fr.stable.EnvChangedListener;
import com.fr.stable.xml.XMLPrintWriter;
import com.fr.stable.xml.XMLableReader;

import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by 小灰灰 on 2015/12/14.
 */
@BIMandatedObject(factory = IFactoryService.CONF_XML, implement = AnalysisDataSource.class)
public class BIXMLAnalysisDataSourceManager extends XMLFileManager implements AnalysisDataSource {

    private static final String XML_TAG = "DataSourceManager";

    private Map<BICore, AnalysisTableSource> md5Tables = new ConcurrentHashMap<BICore, AnalysisTableSource>();

    private Map<BITableID, BICore> idMd5Tables = new ConcurrentHashMap<BITableID, BICore>();

    private long userId;


    public BIXMLAnalysisDataSourceManager(long userId) {
        this.userId = userId;
        readXMLFile();
    }

    static {
        GeneralContext.addEnvChangedListener(new EnvChangedListener() {
            @Override
            public void envChanged() {
                BIConfigureManagerCenter.getDataSourceManager().envChanged();
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
        readXMLFile();
    }

    @Override
    public BICore getCoreByID(BITableID id) {
        return idMd5Tables.get(id);
    }

    @Override
    public AnalysisTableSource getTableSourceByID(BITableID id) {
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
    public AnalysisTableSource getTableSourceByMD5(BICore core) {
        return md5Tables.get(core);
    }

    /**
     * 增加md5表
     */
    @Override
    public void addTableSource(BITableID id, AnalysisTableSource source) {
        synchronized (this) {
            BICore bimd5Core =  source.fetchObjectCore();
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
    public void editTableSource(BITableID id, AnalysisTableSource source) {
        synchronized (this) {
            BICore md5Name =  source.fetchObjectCore();
            BICore oldMD5 = idMd5Tables.get(id);
            idMd5Tables.put(id, md5Name);
            md5Tables.put(md5Name, source);
            if (oldMD5 != null && !idMd5Tables.values().contains(oldMD5)) {
                md5Tables.remove(oldMD5);
            }
            try {
                FRContext.getCurrentEnv().writeResource(this);
            } catch (Exception e) {
                BILogger.getLogger().error(e.getMessage(), e);
            }
        }
    }


    @Override
    public void writeXML(XMLPrintWriter writer) {
        writer.startTAG(XML_TAG);
        writer.attr(BIBaseConstant.VERSIONTEXT, BIBaseConstant.VERSION);
        Iterator<Map.Entry<BITableID, BICore>> idIt = idMd5Tables.entrySet().iterator();
        while (idIt.hasNext()) {
            Map.Entry<BITableID, BICore> entry = idIt.next();
            writer.startTAG("idmd5");
            writer.attr("id", entry.getKey().getIdentityValue()).attr("md5", (String) entry.getValue().getAttribute(0));
            writer.end();
        }
        Iterator<AnalysisTableSource> md5tableIt = md5Tables.values().iterator();
        while (md5tableIt.hasNext()) {
            md5tableIt.next().writeXML(writer);
        }
        writer.end();
    }


    @Override
    public void readXML(XMLableReader reader) {

    }

    @Override
    public JSONObject createJSON() throws JSONException {
        JSONObject jo = new JSONObject();
        for (Map.Entry<BITableID, BICore> id : idMd5Tables.entrySet()) {
            ITableSource source = md5Tables.get(id.getValue());
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
    public String fileName() {
        //single user etl datasource
        return "sue" + File.separator + "datasource" + File.separator + userId + ".xml";
    }

    @Override
    public DBField findDBField(BIField biField) throws BIFieldAbsentException {
        return null;
    }
}