package com.fr.bi.conf.provider;

import com.fr.bi.conf.manager.update.SingleUserBIUpdateSettingManager;
import com.fr.bi.conf.manager.update.source.UpdateSettingSource;
import com.fr.json.JSONObject;

import java.util.Map;

/**
 * Created by Young's on 2016/4/23.
 */
public interface BIUpdateFrequencyManagerProvider {
    String XML_TAG = "BIUpdateFrequencyManagerProvider";

    SingleUserBIUpdateSettingManager getUpdateSettingManager(long userId);

    void saveUpdateSetting(String tableId, UpdateSettingSource source, long userId);

    void removeUpdateSetting(String sourceTableId, long userId);

    Map<String, UpdateSettingSource> getUpdateSettings(long userId);

    JSONObject createJSON(long userId) throws Exception;

    void clear(long userId);

    @Deprecated
    void persistData(long userId);
}
