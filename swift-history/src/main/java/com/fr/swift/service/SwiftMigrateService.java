package com.fr.swift.service;

import com.fr.swift.annotation.SwiftService;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.disk.DiskUtil;
import com.fr.swift.exception.SwiftServiceException;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.SegmentKey;

import java.io.File;
import java.util.Map;

/**
 * @author Moira
 * @date 2020/7/21
 * @description
 * @since swift-1.2.0
 */
@SwiftService(name = "migrate")
@SwiftBean(name = "migrate")
public class SwiftMigrateService extends AbstractSwiftService implements MigrateService {
    @Override
    public ServiceType getServiceType() {
        return ServiceType.MIGRATE;
    }

    @Override
    public boolean appointLocalMigrate(Map<File, SegmentKey> segments, String path, String prePath) throws Exception {
        long t = System.currentTimeMillis();
        segments.forEach((segment, segmentKey) -> {
            String destPath = path + segment.getAbsolutePath().substring(prePath.length());
            DiskUtil.copyFile(segment, new File(destPath));
        });
        SwiftLoggers.getLogger().info("Migration spends ", System.currentTimeMillis() - t);
        return true;
    }

    @Override
    public boolean appointRemoteMigrate(Map<File, SegmentKey> segments, String path, String prePath) throws Exception {
        //todo：2020/7/29 还需要检查path是否是本地cube或本地迁移配置文件中的地址，都符合则继续
        long t = System.currentTimeMillis();
        segments.forEach((segment, segmentKey) -> {
            String destPath = path + segment.getAbsolutePath().substring(prePath.length());
            DiskUtil.copyFile(segment, new File(destPath));
        });
        SwiftLoggers.getLogger().info("Migration spends ", System.currentTimeMillis() - t);
        return true;
    }

    @Override
    public boolean start() throws SwiftServiceException {
        return super.start();
    }

    @Override
    public boolean shutdown() throws SwiftServiceException {
        return super.shutdown();
    }
}
