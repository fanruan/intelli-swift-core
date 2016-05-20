/**
 *
 */
package com.fr.bi.conf.base.datasource;

import com.fr.base.FRContext;
import com.fr.bi.base.BIBasicCore;
import com.fr.bi.base.BICore;
import com.fr.bi.common.factory.IFactoryService;
import com.fr.bi.common.factory.annotation.BIMandatedObject;
import com.fr.bi.conf.data.source.*;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.exception.BIFieldAbsentException;
import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.bi.stable.data.BIField;
import com.fr.bi.stable.data.BITableID;
import com.fr.bi.stable.data.Table;
import com.fr.bi.stable.data.db.BICubeFieldSource;
import com.fr.bi.stable.data.source.ICubeTableSource;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.file.XMLFileManager;
import com.fr.fs.control.UserControl;
import com.fr.general.ComparatorUtils;
import com.fr.general.GeneralContext;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;
import com.fr.stable.EnvChangedListener;
import com.fr.stable.StringUtils;
import com.fr.stable.xml.XMLPrintWriter;
import com.fr.stable.xml.XMLableReader;

import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

@BIMandatedObject(factory = IFactoryService.CONF_XML, implement = BIDataSource.class)
public class BIXMLDataSource extends XMLFileManager implements BIDataSource {

    private static final String XML_TAG = "DataSourceManager";

    private Map<BICore, ICubeTableSource> md5Tables = new ConcurrentHashMap<BICore, ICubeTableSource>();

    private Map<BITableID, BICore> idMd5Tables = new ConcurrentHashMap<BITableID, BICore>();

    private long userId;


    public BIXMLDataSource(long userId) {
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
/**
 * Connery：尽量少用生成新对象的方式当clear用。
 * 或者通过generateEmpty类似函数统一处理也不是不可以。但是原来
 * 这里原来是new HashMap，但是最初对象是ConcurrentMap的。
 * 肯定是上面修改成了Concurrent但是这里忘记修改了。
 * 说不定某次调用了一下envChanged，然后就出现了线程问题
 * 这种导致的Bug肯定又很难还原很难解决了。
 */
        md5Tables.clear();
        idMd5Tables.clear();
        readXMLFile();
    }

    @Override
    public BICore getCoreByID(BITableID id) {
        return idMd5Tables.get(id);
    }

    @Override
    public ICubeTableSource getTableSourceByID(BITableID id) {
        if (id == null) {
            return null;
        }
        BICore biCore = getCoreByID(id);
        return biCore == null ? null : getTableSourceByMD5(biCore);
    }

    @Override
    public ICubeTableSource getTableSourceByMD5(BICore core) {
        return md5Tables.get(core);
    }

    @Override
    public void removeTableSource(BITableID id) {
        synchronized (this) {
            if (idMd5Tables.containsKey(id)) {
                idMd5Tables.remove(id);
            }
        }
    }

    /**
     * 增加md5表
     */
    @Override
    public void addTableSource(BITableID id, ICubeTableSource source) {
        synchronized (this) {
            BICore core = source.fetchObjectCore();
            md5Tables.putAll(source.createSourceMap());
            idMd5Tables.put(id, core);
        }
    }

    /**
     * 修改md5表
     */
    @Override
    public void editTableSource(BITableID id, ICubeTableSource source) {
        synchronized (this) {
            BICore md5Name = source.fetchObjectCore();
            BICore oldMD5 = idMd5Tables.get(id);
            idMd5Tables.put(id, md5Name);
            md5Tables.put(md5Name, source);
            if (oldMD5 != null && !idMd5Tables.values().contains(oldMD5)) {
                md5Tables.remove(oldMD5);
            }
            try {
                FRContext.getCurrentEnv().writeResource(this);
            } catch (Exception e) {
                FRContext.getLogger().error(e.getMessage(), e);
            }
        }
    }


    @Override
    public void writeXML(XMLPrintWriter writer) {
        writer.startTAG(XML_TAG);
        writer.attr(BIBaseConstant.VERSIONTEXT, BIBaseConstant.VERSION);
        Iterator<Entry<BITableID, BICore>> idIt = idMd5Tables.entrySet().iterator();
        while (idIt.hasNext()) {
            Entry<BITableID, BICore> entry = idIt.next();
            writer.startTAG("idmd5");
            writer.attr("id", entry.getKey().getIdentityValue()).attr("md5", entry.getValue().getID().getIdentityValue());
            writer.end();
        }
        Iterator<ICubeTableSource> md5tableIt = md5Tables.values().iterator();
        while (md5tableIt.hasNext()) {
            md5tableIt.next().writeXML(writer);
        }
        writer.end();
    }


    @Override
    public void readXML(XMLableReader reader) {
        if (reader.isChildNode()) {
            if (ComparatorUtils.equals(reader.getTagName(), "idmd5")) {
                /**
                 * TODO 这里已经认定MD5为Core了
                 */
                idMd5Tables.put(new BITableID(reader.getAttrAsString("id", StringUtils.EMPTY)), BIBasicCore.generateValueCore(reader.getAttrAsString("md5", StringUtils.EMPTY)));
            } else if (ComparatorUtils.equals(reader.getTagName(), ETLTableSource.XML_TAG)) {
                ETLTableSource table = new ETLTableSource();
                reader.readXMLObject(table);
                md5Tables.put(table.fetchObjectCore(), table);
            } else if (ComparatorUtils.equals(reader.getTagName(), SQLTableSource.XML_TAG)) {
                SQLTableSource table = new SQLTableSource();
                reader.readXMLObject(table);
                md5Tables.put(table.fetchObjectCore(), table);
            } else if (ComparatorUtils.equals(reader.getTagName(), ExcelTableSource.XML_TAG)) {
                ExcelTableSource table = new ExcelTableSource();
                reader.readXMLObject(table);
                md5Tables.put(table.fetchObjectCore(), table);
            } else if (ComparatorUtils.equals(reader.getTagName(), DBTableSource.XML_TAG)) {
                DBTableSource table = new DBTableSource();
                reader.readXMLObject(table);
                md5Tables.put(table.fetchObjectCore(), table);
            } else if (ComparatorUtils.equals(reader.getTagName(), ServerTableSource.XML_TAG)) {
                ServerTableSource table = new ServerTableSource();
                reader.readXMLObject(table);
                md5Tables.put(table.fetchObjectCore(), table);
            }
        }
    }

    @Override
    public JSONObject createJSON() throws JSONException {
        JSONObject jo = new JSONObject();
        for (Entry<BITableID, BICore> id : idMd5Tables.entrySet()) {
            ICubeTableSource source = md5Tables.get(id.getValue());
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
        return userId == UserControl.getInstance().getSuperManagerID() ?
                "bidatasource.xml" : ("singleuserpackets" + File.separator + "bidatasource_" + userId + ".xml");
    }

    @Override
    public BICubeFieldSource findDBField(BIField biField) throws BIFieldAbsentException {
        Table table = biField.getTableBelongTo();
        ICubeTableSource tableSource = getTableSourceByID(table.getID());
        BICubeFieldSource[] BICubeFieldSources = tableSource.getFieldsArray(null);
        for (int i = 0; i < BICubeFieldSources.length; i++) {
            if (ComparatorUtils.equals(biField.getFieldName(), BICubeFieldSources[i].getFieldName())) {
                return BICubeFieldSources[i];
            }
        }
        throw new BIFieldAbsentException("the field is:" + biField != null ? biField.getFieldName() : "null");
    }
}