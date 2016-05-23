/**
 *
 */
package com.finebi.cube.conf.datasource;

import com.finebi.cube.conf.BIConfigureManagerCenter;
import com.finebi.cube.conf.field.IBusinessField;
import com.finebi.cube.conf.table.IBusinessTable;
import com.fr.bi.base.BICore;
import com.fr.bi.common.factory.IFactoryService;
import com.fr.bi.common.factory.annotation.BIMandatedObject;
import com.fr.bi.exception.BIFieldAbsentException;
import com.fr.bi.stable.data.BITableID;
import com.fr.bi.stable.data.db.BICubeFieldSource;
import com.fr.bi.stable.data.source.ICubeTableSource;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.general.ComparatorUtils;
import com.fr.general.GeneralContext;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;
import com.fr.stable.EnvChangedListener;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

@BIMandatedObject(factory = IFactoryService.CONF_XML, implement = BIDataSource.class)
public class BIXMLDataSource implements BIDataSource {


    private Map<BICore, ICubeTableSource> md5Tables = new ConcurrentHashMap<BICore, ICubeTableSource>();

    private Map<BITableID, BICore> idMd5Tables = new ConcurrentHashMap<BITableID, BICore>();

    private long userId;

    public BIXMLDataSource(long userId) {
        this.userId = userId;
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
    public BICubeFieldSource findDBField(IBusinessField businessField) throws BIFieldAbsentException {
        IBusinessTable table = businessField.getTableBelongTo();
        ICubeTableSource tableSource = getTableSourceByID(table.getID());
        BICubeFieldSource[] BICubeFieldSources = tableSource.getFieldsArray(null);
        for (int i = 0; i < BICubeFieldSources.length; i++) {
            if (ComparatorUtils.equals(businessField.getFieldName(), BICubeFieldSources[i].getFieldName())) {
                return BICubeFieldSources[i];
            }
        }
        throw new BIFieldAbsentException("the field is:" + businessField != null ? businessField.getFieldName() : "null");
    }
}