package com.fr.bi.conf.base.cube;

import com.finebi.cube.api.BICubeManager;
import com.fr.bi.base.BIUser;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.stable.data.BITableID;
import com.fr.bi.stable.data.source.ITableSource;
import com.fr.bi.stable.utils.BIIDUtils;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.json.JSONObject;

import java.util.Set;

/**
 * Created by Young's on 2016/5/19.
 */
public class BICubeConfManager {
    private String cubePath;
    private String fieldId;

    public String getCubePath() {
        return cubePath;
    }

    public void setCubePath(String cubePath) {
        this.cubePath = cubePath;
    }

    public String getFieldId() {
        return fieldId;
    }

    public void setFieldId(String fieldId) {
        this.fieldId = fieldId;
    }

    public JSONObject createJSON() throws Exception{
        JSONObject jo = new JSONObject();
        if(cubePath != null) {
            jo.put("cube_path", cubePath);
        }
        if(fieldId != null) {
            jo.put("field_id", fieldId);
        }
        return  jo;
    }

    public Object getFieldValue(long userId) {
        try {
            String tableId = BIIDUtils.getTableIDFromFieldID(fieldId);
            BITableID tId = new BITableID(tableId);
            ITableSource source = BIConfigureManagerCenter.getDataSourceManager().getTableSourceByID(tId, new BIUser(userId));
            Set set = source.getFieldDistinctNewestValues(BIIDUtils.getFieldNameFromFieldID(fieldId), BICubeManager.getInstance().fetchCubeLoader(userId), userId);
            return set;
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }
        return null;
    }

}
