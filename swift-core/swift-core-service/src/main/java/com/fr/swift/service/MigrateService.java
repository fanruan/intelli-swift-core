package com.fr.swift.service;

import com.fr.swift.segment.SegmentKey;

import java.util.List;

/**
 * @author Moira
 * @date 2020/7/21
 * @description
 * @since swift-1.2.0
 */
public interface MigrateService extends SwiftService {
    /**
     * 删除迁移后临时文件
     *
     * @param targetPath 目标路径
     * @return
     */
    Boolean deleteMigratedFile(String targetPath);

    /**
     * 更新获得的迁移新块信息至缓存
     *
     * @param segmentKeys
     * @return
     */
    Boolean updateMigratedSegsConfig(List<SegmentKey> segmentKeys);
}
