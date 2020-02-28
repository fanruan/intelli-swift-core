package com.fr.swift.config.service;

import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftMetaData;

import java.util.Map;

/**
 * @author yee
 * @date 2018/6/6
 */
public interface SwiftMetaDataService extends ConfigService<SwiftMetaData> {
    /**
     * 增加MetaData
     *
     * @param sourceKey
     * @param metaData
     * @return
     */
    boolean addMetaData(String sourceKey, SwiftMetaData metaData);


    /**
     * 批量删除MetaData
     *
     * @param sourceKey
     * @return
     */
    boolean removeMetaDatas(SourceKey... sourceKey);

    /**
     * 更新MetaData
     *
     * @param sourceKey
     * @param metaData
     * @return
     */
    boolean updateMetaData(String sourceKey, SwiftMetaData metaData);

    /**
     * 获取所有MetaData
     *
     * @return
     */
    Map<String, SwiftMetaData> getAllMetaData();

    /**
     * 模糊查metadata
     *
     * @param fuzzyName
     * @return
     */
    Map<String, SwiftMetaData> getFuzzyMetaData(String fuzzyName);

    /**
     * 根据SourceKey获取MetaData
     *
     * @param sourceKey
     * @return
     */
    SwiftMetaData getMetaDataByKey(String sourceKey);

    boolean containsMeta(SourceKey sourceKey);

    void cleanCache(String[] sourceKeys);
}