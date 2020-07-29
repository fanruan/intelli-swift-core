package com.fr.swift.service;

import com.fr.swift.segment.SegmentKey;

import java.io.File;
import java.util.Map;

/**
 * @author Moira
 * @date 2020/7/21
 * @description
 * @since swift-1.2.0
 */
public interface MigrateService extends SwiftService {
    /**
     * 区分本地和远程
     *
     * @param segments 待迁移块
     * @param path     待迁移路径
     * @param prePath  块原来的路径
     * @return
     * @throws Exception
     */
    boolean appointLocalMigrate(Map<File, SegmentKey> segments, String path, String prePath) throws Exception;

    boolean appointRemoteMigrate(Map<File, SegmentKey> segments, String path, String prePath) throws Exception;
}
