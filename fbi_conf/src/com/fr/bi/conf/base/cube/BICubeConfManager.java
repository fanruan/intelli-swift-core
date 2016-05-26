package com.fr.bi.conf.base.cube;

import com.finebi.cube.api.BICubeManager;
import com.finebi.cube.conf.BICubeConfigureCenter;
import com.fr.bi.stable.data.BITableID;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.utils.BIIDUtils;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.json.JSONObject;

import java.util.Set;

/**
 * Created by Young's on 2016/5/19.
 */
public class BICubeConfManager {
    private String cubePath;
    private String loginField;

    public String getCubePath() {
        return cubePath;
    }

    public void setCubePath(String cubePath) {
        this.cubePath = cubePath;
    }

    public String getLoginField() {
        return loginField;
    }

    public void setLoginField(String loginField) {
        this.loginField = loginField;
    }

    public JSONObject createJSON() throws Exception {
        JSONObject jo = new JSONObject();
        if (cubePath != null) {
            jo.put("cube_path", cubePath);
        }
        if (loginField != null) {
            jo.put("login_field", loginField);
        }
        return jo;
    }

    public Object getFieldValue(long userId) {
        try {
            String tableId = BIIDUtils.getTableIDFromFieldID(loginField);
            BITableID tId = new BITableID(tableId);
            CubeTableSource source = BICubeConfigureCenter.getDataSourceManager().getTableSource(tId);
            Set set = source.getFieldDistinctNewestValues(BIIDUtils.getFieldNameFromFieldID(loginField), BICubeManager.getInstance().fetchCubeLoader(userId), userId);
            return set;
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }
        return null;
    }

}
