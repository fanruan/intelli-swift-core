package com.fr.swift.config.conf.service;

import com.fr.swift.config.IConfigSegment;
import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftMetaData;

import java.util.Map;

/**
 * @author yee
 * @date 2018/3/23
 */
public interface SwiftConfigService {
    /**
     * 增加MetaData
     *
     * @param sourceKey
     * @param metaData
     * @return
     */
    boolean addMetaData(String sourceKey, SwiftMetaData metaData);

    /**
     * 批量增加MetaData
     *
     * @param metaDatas
     * @return
     */
    boolean addMetaDatas(Map<String, SwiftMetaData> metaDatas);

    /**
     * 批量删除MetaData
     *
     * @param sourceKey
     * @return
     */
    boolean removeMetaDatas(String... sourceKey);

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
    Map<String, SwiftMetaData> getAllMetaData() throws SwiftMetaDataException;

    /**
     * 根据SourceKey获取MetaData
     *
     * @param sourceKey
     * @return
     */
    SwiftMetaData getMetaDataByKey(String sourceKey) throws SwiftMetaDataException;

    boolean containsMeta(SourceKey sourceKey);

    /**
     * 批量增加Segment
     *
     * @param segments
     * @return
     */
    boolean addSegments(IConfigSegment... segments);

    /**
     * 批量删除Segment
     *
     * @param sourceKey
     * @return
     */
    boolean removeSegments(String... sourceKey);

    /**
     * 批量更新Segment
     *
     * @param segments
     * @return
     */
    boolean updateSegments(IConfigSegment... segments);

    /**
     * 获取所有Segment
     *
     * @return
     */
    Map<String, IConfigSegment> getAllSegments();

    /**
     * 根据SourceKey获取Segment
     *
     * @param sourceKey
     * @return
     */
    IConfigSegment getSegmentByKey(String sourceKey);

    /**
     * 设置cube更新路径
     *
     * @param path
     */
    boolean setSwiftPath(String path);

    /**
     * 获取cube更新路径
     *
     * @return
     */
    String getSwiftPath();
}
