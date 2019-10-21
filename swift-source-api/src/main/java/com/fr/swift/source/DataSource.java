package com.fr.swift.source;

/**
 * Created by pony on 2017/10/19.
 * 数据源接口
 */
public interface DataSource {

    /**
     * 获取数据源的metadata信息
     *
     * @return
     */
    SwiftMetaData getMetadata();

    SourceKey getSourceKey();

}
