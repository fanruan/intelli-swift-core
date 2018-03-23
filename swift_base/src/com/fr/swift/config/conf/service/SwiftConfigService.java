package com.fr.swift.config.conf.service;

import com.fr.swift.config.IConfigSegment;
import com.fr.swift.config.IMetaData;

import java.util.Map;

/**
 * @author yee
 * @date 2018/3/23
 */
public interface SwiftConfigService {
    boolean addMetaData(String sourceKey, IMetaData metaData);
    boolean addMetaDatas(Map<String, IMetaData> metaDatas);
    boolean removeMetaDatas(String... sourceKey);
    boolean updateMetaData(String sourceKey, IMetaData metaData);
    Map<String, IMetaData> getAllMetaData();
    IMetaData getMetaDataByKey(String sourceKey);

    boolean addSegments(IConfigSegment... segments);
    boolean removeSegments(String... sourceKey);
    boolean updateSegments(IConfigSegment... segments);
    Map<String, IConfigSegment> getAllSegments();
    IConfigSegment getSegmentByKey(String sourceKey);
}
