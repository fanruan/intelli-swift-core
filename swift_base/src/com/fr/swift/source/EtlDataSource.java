package com.fr.swift.source;

import com.fr.swift.source.etl.ETLOperator;

import java.util.List;
import java.util.Map;

/**
 * @author pony
 * @date 2017/11/15
 * ETL数据源
 */
public interface EtlDataSource extends DataSource {
    ETLOperator getOperator();

    List<SourceKey> getBasedSourceKeys();

    List<DataSource> getBasedSources();

    Map<Integer, String> getFieldsInfo();
}