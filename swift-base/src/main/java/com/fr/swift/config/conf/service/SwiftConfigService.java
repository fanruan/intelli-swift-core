package com.fr.swift.config.conf.service;

import com.fr.swift.config.IConfigSegment;
import com.fr.swift.config.IMetaData;
import com.fr.swift.config.conf.bean.SegmentBean;
import com.fr.swift.source.SourceKey;

import java.util.List;
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
    boolean addMetaData(String sourceKey, IMetaData metaData);

    /**
     * 批量增加MetaData
     *
     * @param metaDatas
     * @return
     */
    boolean addMetaDatas(Map<String, IMetaData> metaDatas);

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
    boolean updateMetaData(String sourceKey, IMetaData metaData);

    /**
     * 获取所有MetaData
     *
     * @return
     */
    Map<String, IMetaData> getAllMetaData();

    /**
     * 根据SourceKey获取MetaData
     *
     * @param sourceKey
     * @return
     */
    IMetaData getMetaDataByKey(String sourceKey);

    boolean containsMeta(SourceKey sourceKey);

    /**
     * Segment
     *
     * @param segments
     * @return
     */
    boolean addSegments(List<SegmentBean> segments);

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
    boolean updateSegments(List<SegmentBean> segments);

    /**
     * 获取所有Segment
     *
     * @return
     */
    Map<String, List<SegmentBean>> getAllSegments();

    /**
     * 根据SourceKey获取Segment
     *
     * @param sourceKey
     * @return
     */
    List<SegmentBean> getSegmentByKey(String sourceKey);

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
