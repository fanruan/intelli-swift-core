package com.fr.swift.util;

import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.EtlDataSource;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.etl.ETLOperator;
import com.fr.swift.source.etl.OperatorType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * datasource部分工具类
 */
public class DataSourceUtils {

    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(DataSourceUtils.class);

    /**
     * 计算datasource在cube中存储的真实路径节点
     *
     * @param dataSource
     * @return
     */
    public static SourceKey getSwiftSourceKey(DataSource dataSource) {
        try {
            if (dataSource instanceof EtlDataSource) {
                EtlDataSource etlSource = (EtlDataSource) dataSource;
                if (etlSource.getOperator().getOperatorType().isAddColumn()) {
                    assert etlSource.getBasedSources().size() == 1;
                    DataSource baseDataSource = etlSource.getBasedSources().get(0);
                    return getSwiftSourceKey(baseDataSource);
                }
            }
            return dataSource.getSourceKey();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return dataSource.getSourceKey();
        }
    }

    public static boolean isAddColumn(DataSource dataSource) {
        try {
            if (dataSource instanceof EtlDataSource) {
                EtlDataSource etlSource = (EtlDataSource) dataSource;
                return etlSource.getOperator().getOperatorType().isAddColumn();
            }
            return false;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return false;
        }
    }

    /**
     * datasource新增字段list
     *
     * @param dataSource
     * @return
     */
    public static List<String> getAddFields(DataSource dataSource) {
        List<String> fields = new ArrayList<String>();
        try {
            if (dataSource instanceof EtlDataSource) {
                EtlDataSource etlSource = (EtlDataSource) dataSource;
                ETLOperator etlOperator = etlSource.getOperator();
                OperatorType type = etlOperator.getOperatorType();
                if (type.isAddColumn()) {
                    List<String> names = etlOperator.getNewAddedName();
                    if (etlSource.getFieldsInfo() != null) {
                        for (int i = 0; i < names.size(); i++) {
                            int originColumnSize = etlSource.getFieldsInfo().size() - names.size();
                            if (Util.equals(etlSource.getFieldsInfo().get(i + originColumnSize), names.get(i))) {
                                fields.add(names.get(i));
                            } else {
                                fields.add(etlSource.getFieldsInfo().get(i + originColumnSize));
                            }
                        }
                    } else {
                        fields.addAll(names);
                    }
                }
            }
            return fields;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return fields;
        }
    }


    /**
     * @param baseSourceKeys    需要计算依赖的sourcekeys
     * @param allDataSourceList 计算样本
     * @return
     */
    public static List<DataSource> calculateReliances(List<SourceKey> baseSourceKeys, List<DataSource> allDataSourceList) {
        Map<SourceKey, DataSource> relianceSourceMap = new HashMap<SourceKey, DataSource>();
        for (SourceKey baseSourceKey : baseSourceKeys) {
            for (DataSource dataSource : allDataSourceList) {
                List<Set<DataSource>> sourceList = datasourceReliance(dataSource);
                for (Set<DataSource> sourceSet : sourceList) {
                    Iterator<DataSource> iterator = sourceSet.iterator();
                    while (iterator.hasNext()) {
                        DataSource relianceSource = iterator.next();
                        if (isRelyOn(baseSourceKey, relianceSource)) {
                            if (!relianceSourceMap.containsKey(relianceSource.getSourceKey())) {
                                relianceSourceMap.put(relianceSource.getSourceKey(), relianceSource);
                            }
                        }
                    }
                }
            }
        }
        return new ArrayList<DataSource>(relianceSourceMap.values());
    }

    /**
     * @param baseSourceKey     需要计算依赖的sourcekey
     * @param allDataSourceList 计算样本
     */
    public static List<DataSource> calculateReliance(SourceKey baseSourceKey, List<DataSource> allDataSourceList) {
        Map<SourceKey, DataSource> relianceSourceMap = new HashMap<SourceKey, DataSource>();

        for (DataSource dataSource : allDataSourceList) {
            List<Set<DataSource>> sourceList = datasourceReliance(dataSource);
            for (Set<DataSource> sourceSet : sourceList) {
                Iterator<DataSource> iterator = sourceSet.iterator();
                while (iterator.hasNext()) {
                    DataSource relianceSource = iterator.next();
                    if (isRelyOn(baseSourceKey, relianceSource)) {
                        if (!relianceSourceMap.containsKey(relianceSource.getSourceKey())) {
                            relianceSourceMap.put(relianceSource.getSourceKey(), relianceSource);
                        }
                    }
                }
            }
        }
        return new ArrayList<DataSource>(relianceSourceMap.values());
    }

    /**
     * 递归查找datasource，找到datasource的所有层级
     *
     * @param dataSource
     * @return
     */
    static List<Set<DataSource>> datasourceReliance(DataSource dataSource) {
        List<Set<DataSource>> generateTable = new ArrayList<Set<DataSource>>();
        if (dataSource instanceof EtlDataSource) {
            Iterator<DataSource> it = ((EtlDataSource) dataSource).getBasedSources().iterator();
            while (it.hasNext()) {
                DataSource baseSource = it.next();
                List<Set<DataSource>> base = datasourceReliance(baseSource);
                if (!base.isEmpty()) {
                    for (int i = 0; i < base.size(); i++) {
                        generateTable.add(i, base.get(i));
                    }
                }
            }
        }
        Set<DataSource> set = new HashSet<DataSource>();
        set.add(dataSource);
        generateTable.add(set);
        return generateTable;
    }

    /**
     * 递归查看当前datasource是否依赖baseSourcekey
     *
     * @param baseSourceKey
     * @param dataSource
     * @return
     */
    static boolean isRelyOn(SourceKey baseSourceKey, DataSource dataSource) {
        if (dataSource instanceof EtlDataSource) {
            if (((EtlDataSource) dataSource).getBasedSourceKeys().contains(baseSourceKey)) {
                return true;
            } else {
                boolean result = false;
                for (DataSource baseDataSource : ((EtlDataSource) dataSource).getBasedSources()) {
                    result = result || isRelyOn(baseSourceKey, baseDataSource);
                }
                return result;
            }
        } else {
            return Util.equals(dataSource.getSourceKey(), baseSourceKey);
        }
    }
}
