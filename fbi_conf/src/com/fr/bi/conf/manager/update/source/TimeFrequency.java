package com.fr.bi.conf.manager.update.source;

import com.fr.bi.stable.constant.DBConstant;
import com.fr.json.JSONObject;
import com.fr.json.JSONTransform;

/**
 * Created by Young's on 2016/4/25.
 */
public class TimeFrequency implements JSONTransform {
    private int updateFrequency = DBConstant.UPDATE_FREQUENCY.EVER_DAY;
    private int updateTime;

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

    @Override
    public JSONObject createJSON() throws Exception {
        JSONObject jo = new JSONObject();
        jo.put("frequency", this.updateFrequency);
        jo.put("time", this.updateTime);
        return jo;
    }

    @Override
    public void parseJSON(JSONObject jo) throws Exception {
        if(jo.has("frequency")) {
            this.updateFrequency = jo.getInt("frequency");
        }
        if(jo.has("time")) {
            this.updateTime = jo.getInt("time");
        }
    }
}
