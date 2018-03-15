package com.fr.swift.conf;

import com.fr.config.ConfigContext;
import com.fr.config.DefaultConfiguration;
import com.fr.config.holder.factory.Holders;
import com.fr.config.holder.impl.MapConf;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yee
 * @date 2018/3/14
 */
public class TableToSourceConfig extends DefaultConfiguration {
    private static final String NAMESPACE = "swift_table_source";
    private static TableToSourceConfig configuration;

    public static TableToSourceConfig getInstance() {
        if (null == configuration) {
            configuration = ConfigContext.getConfigInstance(TableToSourceConfig.class);
        }
        return configuration;
    }

    private MapConf<Map<String, String>> config = Holders.map(new HashMap<String, String>(), String.class, String.class);

    public Map<String, String> getAllConfig() {
        return config.get();
    }

    public String getConfigByTableId(String tableId) {
        return (String) config.get(tableId);
    }

    public void addConfig(String tableId, String sourceKey) {
        config.put(tableId, sourceKey);
    }

    public void removeConfig(String tableId) {
        config.remove(tableId);
    }

    public void updateConfig(String tableId, String sourceKey) {
        addConfig(tableId, sourceKey);
    }

    public void removeAllConfigs() {
        config.clear();
    }

    @Override
    public String getNameSpace() {
        return NAMESPACE;
    }
}
