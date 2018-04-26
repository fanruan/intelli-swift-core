package com.fr.swift.conf.business.table2source.dao;

import com.fr.config.Configuration;
import com.fr.swift.conf.business.table2source.TableToSource;
import com.fr.swift.conf.business.table2source.TableToSourceConfig;
import com.fr.swift.conf.business.table2source.unique.TableToSourceUnique;
import com.fr.transaction.Configurations;
import com.fr.transaction.Worker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author yee
 * @date 2018/3/23
 */
public class TableToSourceConfigDaoImpl implements TableToSourceConfigDao {
    private TableToSourceConfig config;

    public TableToSourceConfigDaoImpl() {
        config = TableToSourceConfig.getInstance();
    }

    @Override
    public List<TableToSource> getAllConfig() {
        Map<String, TableToSource> map = config.getAllConfig();
        List<TableToSource> target = new ArrayList<TableToSource>();
        Iterator<Map.Entry<String, TableToSource>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            target.add(iterator.next().getValue());
        }
        return Collections.unmodifiableList(target);
    }

    @Override
    public String getConfigByTableId(String tableId) {
        return config.getConfigByTableId(tableId);
    }

    @Override
    public boolean addConfig(final TableToSource... table2Source) {
        return Configurations.update(new TableToSourceConfigWorker() {
            @Override
            public void run() {
                for (TableToSource source : table2Source) {
                    config.addConfig(source);
                }
            }
        });
    }

    @Override
    public boolean removeConfig(final String... tableId) {
        return Configurations.update(new TableToSourceConfigWorker() {
            @Override
            public void run() {
                for (String source : tableId) {
                    config.removeConfig(source);
                }
            }
        });
    }

    @Override
    public boolean updateConfig(final TableToSource... table2Source) {
        return Configurations.update(new TableToSourceConfigWorker() {
            @Override
            public void run() {
                for (TableToSource source : table2Source) {
                    config.updateConfig(source);
                }
            }
        });
    }

    @Override
    public boolean removeAllConfigs() {
        return Configurations.update(new TableToSourceConfigWorker() {
            @Override
            public void run() {
                config.removeAllConfigs();
            }
        });
    }

    @Override
    public boolean addConfig(String tableId, String sourceKey) {
        return addConfig(new TableToSourceUnique(tableId, sourceKey));
    }

    private abstract class TableToSourceConfigWorker implements Worker {

        @Override
        public Class<? extends Configuration>[] targets() {
            return new Class[] {TableToSourceConfig.class};
        }
    }
}
