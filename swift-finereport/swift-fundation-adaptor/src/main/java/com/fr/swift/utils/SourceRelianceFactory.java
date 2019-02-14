package com.fr.swift.utils;

import com.fr.swift.increment.Increment;
import com.fr.swift.reliance.SourceReliance;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.EtlDataSource;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.etl.OperatorType;
import com.fr.swift.util.DataSourceUtils;
import com.fr.swift.util.SourceNodeUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This class created on 2018/4/12
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class SourceRelianceFactory {

    public static SourceReliance generateSourceReliance(List<DataSource> baseDataSources, List<DataSource> allDataSourceList, Map<String, List<Increment>> incrementMap) {
        List<SourceKey> baseSourceKeys = new ArrayList<SourceKey>();
        List<DataSource> baseSources = new ArrayList<DataSource>();
        for (DataSource baseDataSource : baseDataSources) {
            baseSourceKeys.add(baseDataSource.getSourceKey());
            if (baseDataSource instanceof EtlDataSource) {
                List<DataSource> need2AddList = getAllSourceNotGenerate(baseDataSource);
                baseSources.addAll(need2AddList);
                for (DataSource need2Add : need2AddList) {
                    baseSourceKeys.add(need2Add.getSourceKey());
                }
//                if (etlIsBaseTable((EtlDataSource) baseDataSource)) {
//                    DataSource dataSource = ((EtlDataSource) baseDataSource).getBasedSources().get(0);
//                    allDataSourceList.add(dataSource);
//                    baseSourceKeys.add(dataSource.getSourceKey());
//                    baseSources.add(dataSource);
//                } else {
//
//                }
            }
        }
        baseDataSources.addAll(baseSources);
        List<DataSource> relianceSources = DataSourceUtils.calculateReliances(baseSourceKeys, allDataSourceList);
        SourceReliance sourceReliance = new SourceReliance(baseDataSources, relianceSources);
        SourceNodeUtils.calculateSourceNode(sourceReliance, incrementMap);
        return sourceReliance;
    }

    //行列转换、自循环等基于基础表的表，要把基础表加入。
    private static boolean etlIsBaseTable(EtlDataSource etlDataSource) {
        if (etlDataSource.getOperator().getOperatorType() == OperatorType.ONE_UNION_RELATION ||
                etlDataSource.getOperator().getOperatorType() == OperatorType.TWO_UNION_RELATION ||
                etlDataSource.getOperator().getOperatorType() == OperatorType.COLUMN_ROW_TRANS) {
            return true;
        }
        return false;
    }

    //别的分析表，递归算父表，并判断父表存不存在，存在的话，就不管，不存在加入baseSource
    public static List<DataSource> getAllSourceNotGenerate(DataSource dataSource) {
        List<DataSource> dataSourceList = new ArrayList<DataSource>();
        dataSourceList.add(dataSource);
        if (dataSource instanceof EtlDataSource) {
            List<DataSource> baseDataSources = ((EtlDataSource) dataSource).getBasedSources();
            for (DataSource baseDataSource : baseDataSources) {
                dataSourceList.addAll(getAllSourceNotGenerate(baseDataSource));
            }
        }
        return dataSourceList;
    }
}
