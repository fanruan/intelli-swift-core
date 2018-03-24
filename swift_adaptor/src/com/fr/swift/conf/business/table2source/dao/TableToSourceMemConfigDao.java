package com.fr.swift.conf.business.table2source.dao;

import com.fr.swift.conf.business.table2source.TableToSource;
import com.fr.swift.conf.business.table2source.TableToSourceContainer;
import com.fr.swift.conf.business.table2source.pojo.TableToSourcePojo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * @author yee
 * @date 2018/3/23
 */
public class TableToSourceMemConfigDao implements TableToSourceConfigDao {

    private TableToSourceContainer container = TableToSourceContainer.getContainer();

    @Override
    public List<TableToSource> getAllConfig() {
        return container.getResources();
    }

    @Override
    public String getConfigByTableId(String tableId) {
        List<TableToSource> allConfig = getAllConfig();
        for (TableToSource tableSource : allConfig) {
            if (tableSource.getTableId().equals(tableId)) {
                return tableSource.getSourceKey();
            }
        }
        return null;
    }

    @Override
    public boolean addConfig(TableToSource... table2Source) {
        List<TableToSource> target = new ArrayList<TableToSource>();
        target.addAll(Arrays.asList(table2Source));
        container.saveResources(target);
        return true;
    }

    @Override
    public boolean removeConfig(String... tableId) {
        for (String table : tableId) {
            List<TableToSource> allConfig = getAllConfig();
            Iterator<TableToSource> iterator = allConfig.iterator();
            while (iterator.hasNext()) {
                if (iterator.next().getTableId().equals(table)) {
                    iterator.remove();
                    break;
                }
            }
        }
        return true;
    }

    @Override
    public boolean updateConfig(TableToSource... table2Source) {
        List<TableToSource> allConfig = getAllConfig();
        Iterator<TableToSource> iterator = allConfig.iterator();
        while (iterator.hasNext()) {
            TableToSource config = iterator.next();
            for (TableToSource table : table2Source) {
                if (config.getTableId().equals(table.getTableId())) {
                    iterator.remove();
                    break;
                }
            }
        }
        allConfig.addAll(Arrays.asList(table2Source));
        container.saveResources(allConfig);
        return true;
    }

    @Override
    public boolean removeAllConfigs() {
        container.saveResources(new ArrayList<TableToSource>());
        return true;
    }

    @Override
    public boolean addConfig(String tableId, String sourceKey) {
        return addConfig(new TableToSourcePojo(tableId, sourceKey));
    }
}
