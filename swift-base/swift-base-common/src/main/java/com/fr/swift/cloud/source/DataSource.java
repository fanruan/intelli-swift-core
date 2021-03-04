package com.fr.swift.cloud.source;

import com.fr.swift.cloud.source.core.CoreService;

/**
 * Created by pony on 2017/10/19.
 * 数据源接口
 */
public interface DataSource extends Source, CoreService {

    /**
     * 获取数据源的metadata信息
     *
     * @return
     */
    SwiftMetaData getMetadata();

}
