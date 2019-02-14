package com.fr.swift.conf.updateInfo;

import com.fr.config.ConfigContext;
import com.fr.config.DefaultConfiguration;
import com.fr.config.holder.factory.Holders;
import com.fr.config.holder.impl.MapConf;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yee
 * @date 2018/5/11
 */
public class TableUpdateInfoConfig extends DefaultConfiguration {
    private static final String NAMESPACE = "SwiftTableUpdateInfoConfig";
    private static TableUpdateInfoConfig configuration;

    public static TableUpdateInfoConfig getInstance() {
        if (null == configuration) {
            configuration = ConfigContext.getConfigInstance(TableUpdateInfoConfig.class);
        }
        return configuration;
    }

    private MapConf<Map<String, String>> updateInfoHolder = Holders.map(
            new HashMap<String, String>(), String.class, String.class
    );

    public void addOrUpdateInfo(String tableName, String updateInfo) {
        updateInfoHolder.put(tableName, updateInfo);
    }

    public void removeUpdateInfo(String tableName) {
        updateInfoHolder.remove(tableName);
    }

    public Map<String, String> getAllUpdateInfo() {
        return updateInfoHolder.get();
    }

    public String getUpdateInfo(String tableName) {
        return (String) updateInfoHolder.get(tableName);
    }

    @Override
    public String getNameSpace() {
        return NAMESPACE;
    }
}
