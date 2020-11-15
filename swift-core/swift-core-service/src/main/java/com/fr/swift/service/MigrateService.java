package com.fr.swift.service;

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
     * @param targetPath 目标路径
     * @return
     */
    Boolean deleteMigraFile(String targetPath);
}
