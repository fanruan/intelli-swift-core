package com.fr.bi.conf.manager.update;

import com.finebi.cube.conf.BISystemDataManager;
import com.fr.bi.conf.manager.update.source.UpdateSettingSource;
import com.fr.bi.conf.provider.BIUpdateFrequencyManagerProvider;
import com.fr.bi.exception.BIKeyAbsentException;
import com.fr.json.JSONObject;

import java.util.Iterator;
import java.util.Map;

/**
 * Created by Young's on 2016/4/25.
 */
public class BIUpdateSettingManager extends BISystemDataManager<SingleUserBIUpdateSettingManager> implements BIUpdateFrequencyManagerProvider {
    @Override
    public SingleUserBIUpdateSettingManager constructUserManagerValue(Long userId) {
        return new SingleUserBIUpdateSettingManager();
    }

    @Override
    public String managerTag() {
        return "BIUpdateSettingManager";
    }

    @Override
    public String persistUserDataName(long key) {
        return managerTag();
    }

    @Override
    public SingleUserBIUpdateSettingManager getUpdateSettingManager(long userId) {
        try {
            return getValue(userId);
        } catch (BIKeyAbsentException e) {
            throw new NullPointerException("Please check the userID:" + userId + ",which getIndex a empty manager");
        }
    }

    @Override
    public void saveUpdateSetting(String sourceTableId, UpdateSettingSource source, long userId) {
        getUpdateSettingManager(userId).saveUpdateSetting(sourceTableId, source);
    }

    @Override
    public void removeUpdateSetting(String sourceTableId, long userId) {
        getUpdateSettingManager(userId).removeUpdateSetting(sourceTableId);
    }
    @Override
    public Map<String, UpdateSettingSource> getUpdateSettings(long userId) {
        return getUpdateSettingManager(userId).getUpdateSettings();
    }

    @Override
    public JSONObject createJSON(long userId) throws Exception {
        JSONObject jo = new JSONObject();
        Map<String, UpdateSettingSource> settings = getUpdateSettings(userId);
        Iterator<Map.Entry<String, UpdateSettingSource>> iterator = settings.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, UpdateSettingSource> entry = iterator.next();
            jo.put(entry.getKey(), entry.getValue().createJSON());
        }
        return jo;
    }

    @Override
    public void clear(long userId) {
        getUpdateSettingManager(userId).clear();
    }

    @Override
    public void persistData(long userId) {
        persistUserData(userId);
    }
}
