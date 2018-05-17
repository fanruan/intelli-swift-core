package com.fr.swift.conf.updateInfo;

import com.finebi.conf.internalimp.update.GlobalUpdateSetting;
import com.finebi.conf.internalimp.update.TableUpdateInfo;
import com.finebi.conf.structure.bean.table.FineBusinessTable;
import com.fr.config.Configuration;
import com.fr.general.ComparatorUtils;
import com.fr.stable.StringUtils;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.util.Crasher;
import com.fr.swift.constants.UpdateConstants;
import com.fr.third.fasterxml.jackson.core.JsonProcessingException;
import com.fr.third.fasterxml.jackson.databind.ObjectMapper;
import com.fr.transaction.Configurations;
import com.fr.transaction.Worker;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author yee
 * @date 2018/5/11
 */
public class TableUpdateInfoConfigService {
    private TableUpdateInfoConfig config;
    private ObjectMapper objectMapper;
    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(TableUpdateInfoConfigService.class);


    private TableUpdateInfoConfigService() {
        this.config = TableUpdateInfoConfig.getInstance();
        this.objectMapper = new ObjectMapper();
    }

    private static class SingletonHolder {
        private static final TableUpdateInfoConfigService INSTANCE = new TableUpdateInfoConfigService();
    }

    public static TableUpdateInfoConfigService getService() {
        return SingletonHolder.INSTANCE;
    }

    //table
    public boolean addOrUpdateInfo(final Map<FineBusinessTable, TableUpdateInfo> updateInfoMap) {
        return Configurations.update(new TableUpdateWorker() {
            @Override
            public void run() {
                Iterator<Map.Entry<FineBusinessTable, TableUpdateInfo>> iterator = updateInfoMap.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<FineBusinessTable, TableUpdateInfo> entry = iterator.next();
                    FineBusinessTable table = entry.getKey();
                    TableUpdateInfo info = entry.getValue();
                    try {
                        config.addOrUpdateInfo(table.getName(), objectMapper.writeValueAsString(info));
                    } catch (JsonProcessingException e) {
                        Crasher.crash(e);
                    }
                }
            }
        });
    }

    public Map<String, TableUpdateInfo> getAllTableUpdateInfo() {
        Map<String, String> info = config.getAllUpdateInfo();
        Map<String, TableUpdateInfo> result = new HashMap<String, TableUpdateInfo>();
        Iterator<Map.Entry<String, String>> iterator = info.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> target = iterator.next();
            String key = target.getKey();
            String value = target.getValue();
            if (!ComparatorUtils.equals(key, UpdateConstants.GLOBAL_KEY)) {
                try {
                    result.put(key, objectMapper.readValue(value, TableUpdateInfo.class));
                } catch (IOException e) {
                    LOGGER.error(e);
                }
            }
        }
        return Collections.unmodifiableMap(result);
    }

    public TableUpdateInfo getTableUpdateInfo(String tableName) {
        String value = config.getUpdateInfo(tableName);
        if (StringUtils.isEmpty(value)) {
            return null;
        }
        try {
            return objectMapper.readValue(value, TableUpdateInfo.class);
        } catch (IOException e) {
            return null;
        }
    }

    public boolean removeUpdateInfo(final List<String> tableNames) {
        return Configurations.update(new TableUpdateWorker() {
            @Override
            public void run() {
                for (String table : tableNames) {
                    config.removeUpdateInfo(table);
                }
            }
        });
    }

    //package
    public boolean addOrUpdatPackageInfo(final String packId, final TableUpdateInfo info) {

        return Configurations.update(new TableUpdateWorker() {
            @Override
            public void run() {
                try {
                    config.addOrUpdateInfo(packId, objectMapper.writeValueAsString(info));
                } catch (JsonProcessingException e) {
                    Crasher.crash(e);
                }
            }
        });
    }

    public Map<String, TableUpdateInfo> getAllPackageUpdateInfo() {
        return this.getAllTableUpdateInfo();
    }

    public TableUpdateInfo getPackageUpdateInfo(String packageId) {
        String value = config.getUpdateInfo(packageId);
        if (StringUtils.isEmpty(value)) {
            return null;
        }
        try {
            return objectMapper.readValue(value, TableUpdateInfo.class);
        } catch (IOException e) {
            return null;
        }
    }

    public boolean removePackageInfo(final List<String> packageIds) {
        return Configurations.update(new TableUpdateWorker() {
            @Override
            public void run() {
                for (String table : packageIds) {
                    config.removeUpdateInfo(table);
                }
            }
        });
    }


    //gloable
    public boolean addOrUpdateGlobalUpdateSettings(final GlobalUpdateSetting setting) {
        return Configurations.update(new TableUpdateWorker() {
            @Override
            public void run() {
                try {
                    config.addOrUpdateInfo(UpdateConstants.GLOBAL_KEY, objectMapper.writeValueAsString(setting));
                } catch (JsonProcessingException e) {
                    Crasher.crash(e);
                }
            }
        });
    }

    public GlobalUpdateSetting getGlobalUpdateSettings() {
        String value = config.getUpdateInfo(UpdateConstants.GLOBAL_KEY);
        if (StringUtils.isEmpty(value)) {
            return null;
        }
        try {
            return objectMapper.readValue(value, GlobalUpdateSetting.class);
        } catch (IOException e) {
            return null;
        }
    }

    private abstract class TableUpdateWorker implements Worker {

        @Override
        public Class<? extends Configuration>[] targets() {
            return new Class[]{TableUpdateInfoConfig.class};
        }
    }
}
