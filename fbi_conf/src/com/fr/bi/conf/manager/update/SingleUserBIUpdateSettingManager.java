package com.fr.bi.conf.manager.update;

import com.fr.bi.conf.manager.update.source.UpdateSettingSource;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Young's on 2016/4/25.
 */
public class SingleUserBIUpdateSettingManager {
    private final static String XML_TAG = "SingleUserBIUpdateSettingManager";

    private Map<String, UpdateSettingSource> updateSettings = new HashMap<String, UpdateSettingSource>();

    public Map<String, UpdateSettingSource> getUpdateSettings() {
        return updateSettings;
    }

    public void setUpdateSettings(Map<String, UpdateSettingSource> updateSettings) {
        this.updateSettings = updateSettings;
    }

    public void saveUpdateSetting(String sourceTableId, UpdateSettingSource source) {
        this.updateSettings.put(sourceTableId, source);
    }

    public void clear(){
        synchronized (updateSettings) {
            updateSettings.clear();
        }
    }
}
