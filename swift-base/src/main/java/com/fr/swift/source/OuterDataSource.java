package com.fr.swift.source;

import java.io.Serializable;

/**
 * Created by pony on 2017/11/15.
 * 外部数据源
 */
public interface OuterDataSource extends DataSource, Serializable {

    /**
     * 获取原始表的metadata信息
     *
     * @return
     */
    SwiftMetaData getOuterMetadata();

}
