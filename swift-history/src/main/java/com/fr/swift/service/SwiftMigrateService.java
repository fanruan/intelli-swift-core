package com.fr.swift.service;

import com.fr.swift.annotation.SwiftService;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.cube.CubePathBuilder;
import com.fr.swift.exception.SwiftServiceException;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.SegmentKey;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
    public boolean appointMigrate(Map<SegmentKey, byte[]> segments, String location) {
        //todo：2020/7/29 还需要检查location是否是本地cube或本地迁移配置文件中的地址，符合则继续
        long t = System.currentTimeMillis();
        segments.forEach((segment, data) -> {
            //暂时先这样写，之后改到IoUtil里
            String destPath = location + new CubePathBuilder(segment).build();
            File file = new File(destPath);
            try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file))) {
                bos.write(data);
            } catch (IOException e) {
                SwiftLoggers.getLogger().error(e.getMessage());
            }
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
