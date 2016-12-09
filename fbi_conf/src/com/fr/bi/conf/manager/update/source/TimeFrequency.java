package com.fr.bi.conf.manager.update.source;

import com.fr.bi.stable.constant.DBConstant;
import com.fr.json.JSONObject;
import com.fr.json.JSONTransform;

import java.io.Serializable;

/**
 * Created by Young's on 2016/4/25.
 */
public class TimeFrequency implements JSONTransform,Serializable {
    private static final long serialVersionUID = -457266689095798658L;
    private int updateFrequency = DBConstant.UPDATE_FREQUENCY.EVER_DAY;
    private int updateTime;
    private int updateType = DBConstant.SINGLE_TABLE_UPDATE_TYPE.ALL;

    public int getUpdateFrequency() {
        return updateFrequency;
    }

    public void setUpdateFrequency(int updateFrequency) {
        this.updateFrequency = updateFrequency;
    }

    public int getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(int updateTime) {
        this.updateTime = updateTime;
    }

    public int getUpdateType() {
        return updateType;
    }

    public void setUpdateType(int updateType) {
        this.updateType = updateType;
    }

    @Override
    public JSONObject createJSON() throws Exception {
        JSONObject jo = new JSONObject();
        jo.put("frequency", this.updateFrequency);
        jo.put("time", this.updateTime);
        jo.put("updateType", this.updateType);
        return jo;
    }

    @Override
    public void parseJSON(JSONObject jo) throws Exception {
        if (jo.has("frequency")) {
            this.updateFrequency = jo.getInt("frequency");
        }
        if (jo.has("time")) {
            this.updateTime = jo.getInt("time");
        }
        if (jo.has("updateType")) {
            this.updateType = jo.getInt("updateType");
        }
    }
}
