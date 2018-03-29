package com.fr.swift.source;

import com.fr.swift.source.etl.ETLOperator;

import java.util.List;

/**
 * Created by pony on 2017/11/15.
 * ETL数据源
 */
public interface ETLDataSource extends DataSource {
    ETLOperator getOperator();

    List<DataSource> getBasedSources();
}
