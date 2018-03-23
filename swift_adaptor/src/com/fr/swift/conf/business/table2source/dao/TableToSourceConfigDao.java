package com.fr.swift.conf.business.table2source.dao;

import com.fr.swift.conf.business.table2source.TableToSource;

import java.util.List;

/**
 * @author yee
 * @date 2018/3/23
 */
public interface TableToSourceConfigDao {
    List<TableToSource> getAllConfig();

    String getConfigByTableId(String tableId);

    boolean addConfig(TableToSource... table2Source);
    boolean removeConfig(String... tableId);

    boolean updateConfig(TableToSource... table2Source);

    boolean removeAllConfigs();

    boolean addConfig(String tableId, String sourceKey);
}
