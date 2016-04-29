package com.fr.bi.conf.manager.update.source;

import com.fr.bi.stable.constant.DBConstant;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.json.JSONTransform;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Young's on 2016/4/25.
 */
public class UpdateSettingSource implements JSONTransform {
    private int updateType = DBConstant.SINGLE_TABLE_UPDATE_TYPE.ALL;

    private String partAddSQL;
    private String partDeleteSQL;
    private String partModifySQL;

    private int togetherOrNever = DBConstant.SINGLE_TABLE_UPDATE.TOGETHER;

    private List<TimeFrequency> timeList = new ArrayList<TimeFrequency>();

    public int getUpdateType() {
        return updateType;
    }

    public void setUpdateType(int updateType) {
        this.updateType = updateType;
    }

    public String getPartAddSQL() {
        return partAddSQL;
    }

    public void setPartAddSQL(String partAddSQL) {
        this.partAddSQL = partAddSQL;
    }

    public String getPartDeleteSQL() {
        return partDeleteSQL;
    }

    public void setPartDeleteSQL(String partDeleteSQL) {
        this.partDeleteSQL = partDeleteSQL;
    }

    public String getPartModifySQL() {
        return partModifySQL;
    }

    public void setPartModifySQL(String partModifySQL) {
        this.partModifySQL = partModifySQL;
    }

    public int getTogetherOrNever() {
        return togetherOrNever;
    }

    public void setTogetherOrNever(int togetherOrNever) {
        this.togetherOrNever = togetherOrNever;
    }

    public List<TimeFrequency> getTimeList() {
        return timeList;
    }

    public void setTimeList(List<TimeFrequency> timeList) {
        this.timeList = timeList;
    }

    @Override
    public JSONObject createJSON() throws Exception {
        JSONObject jo = new JSONObject();
        jo.put("update_type", this.updateType);
        if(this.partAddSQL != null) {
            jo.put("add_sql", this.partAddSQL);
        }
        if(this.partDeleteSQL != null) {
            jo.put("delete_sql", this.partDeleteSQL);
        }
        if(this.partModifySQL != null) {
            jo.put("modify_sql", this.partModifySQL);
        }
        jo.put("together_never", this.togetherOrNever);
        JSONArray ja = new JSONArray();
        for(int i = 0; i < this.timeList.size(); i++) {
            TimeFrequency tf = this.timeList.get(i);
            ja.put(tf.createJSON());
        }
        jo.put("time_list", ja);
        return jo;
    }

    @Override
    public void parseJSON(JSONObject jo) throws Exception {
        if(jo.has("update_type")) {
            this.updateType = jo.getInt("update_type");
        }
        if(jo.has("add_sql")) {
            this.partAddSQL = jo.getString("add_sql");
        }
        if(jo.has("delete_sql")) {
            this.partDeleteSQL = jo.getString("delete_sql");
        }
        if(jo.has("modify_sql")) {
            this.partModifySQL = jo.getString("modify_sql");
        }
        if(jo.has("together_never")) {
            this.togetherOrNever = jo.getInt("together_never");
        }
        if(jo.has("time_list")) {
            JSONArray ja = jo.getJSONArray("time_list");
            for(int i = 0; i < ja.length(); i++) {
                TimeFrequency timeFrequency = new TimeFrequency();
                timeFrequency.parseJSON(ja.getJSONObject(i));
                this.timeList.add(timeFrequency);
            }
        }
    }

}
