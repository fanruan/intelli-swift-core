package com.fr.swift.service;

import com.fr.swift.segment.SegmentKey;

import java.util.Map;

/**
 * @author Moira
 * @date 2020/7/21
 * @description
 * @since swift-1.2.0
 */
public interface MigrateService extends SwiftService {
    /**
     * 不区分本地和远程
     *
     * @param segments 待迁移块
     * @param location 待迁移路径
     * @return
     */
    Boolean appointMigrate(Map<SegmentKey, Map<String, byte[]>> segments, String location);
}
