package com.fr.swift.source;

import com.fr.swift.source.etl.ETLOperator;

import java.util.List;
import java.util.Map;

/**
 * Created by pony on 2017/11/15.
 * ETL数据源
 */
public interface ETLDataSource extends DataSource {
    ETLOperator getOperator();

    List<DataSource> getBasedSources();

    /**
     * 获取使用的字段信息
     * @return
     */
    Map<Integer, String> getFieldsInfo();
}
