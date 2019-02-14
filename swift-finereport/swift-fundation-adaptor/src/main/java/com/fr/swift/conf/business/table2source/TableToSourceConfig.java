package com.fr.swift.conf.business.table2source;

import com.fr.config.ConfigContext;
import com.fr.config.DefaultConfiguration;
import com.fr.config.holder.factory.Holders;
import com.fr.config.holder.impl.ObjectMapConf;

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

    private ObjectMapConf<Map<String, TableToSource>> config = Holders.objMap(new HashMap<String, TableToSource>(), String.class, TableToSource.class);

    public Map<String, TableToSource> getAllConfig() {
        return config.get();
    }

    public String getConfigByTableId(String tableId) {
        TableToSource tableToSource = (TableToSource) config.get(tableId);
        if (null != tableToSource) {
            return tableToSource.getSourceKey();
        }
        return null;
    }

    public void addConfig(TableToSource table2Source) {
        config.put(table2Source.getTableId(), table2Source);
    }

    public void removeConfig(String tableId) {
        config.remove(tableId);
    }

    public void updateConfig(TableToSource table2Source) {
        addConfig(table2Source);
    }

    public void removeAllConfigs() {
        config.clear();
    }

    @Override
    public String getNameSpace() {
        return NAMESPACE;
    }
}
