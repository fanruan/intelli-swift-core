package com.fr.swift.service;

import com.fr.swift.annotation.SwiftService;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.cube.CubePathBuilder;
import com.fr.swift.cube.CubeUtil;
import com.fr.swift.exception.SwiftServiceException;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.property.MigrateProperty;
import com.fr.swift.property.SwiftProperty;
import com.fr.swift.segment.SegmentKey;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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

    private static final String SEPARATOR = "/";

    @Override
    public ServiceType getServiceType() {
        return ServiceType.MIGRATE;
    }

    @Override
    public List<SegmentKey> appointMigrate(Map<SegmentKey, Map<String, byte[]>> segments, String location) {
        if (!isCubeOrMigrationPath(location)) {
            SwiftLoggers.getLogger().error("Incorrect migration path!");
            return Collections.emptyList();
        }
        long t = System.currentTimeMillis();
        List<SegmentKey> success = new ArrayList<>();
        for (Map.Entry<SegmentKey, Map<String, byte[]>> segment : segments.entrySet()) {
            String destPath = location + SEPARATOR + new CubePathBuilder(segment.getKey())
                    .setTempDir(CubeUtil.getCurrentDir(segment.getKey().getTable())).build();
            success.add(segment.getKey());
            for (Map.Entry<String, byte[]> data : segment.getValue().entrySet()) {
                String path = data.getKey();
                File fileDir = new File(destPath + path.substring(0, path.lastIndexOf(SEPARATOR)));
                fileDir.mkdirs();
                File file = new File(destPath + path);
                try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file))) {
                    bos.write(data.getValue());
                } catch (IOException e) {
                    SwiftLoggers.getLogger().error(e.getMessage());
                    success.remove(segment.getKey());
                    break;
                }
            }
        }
        SwiftLoggers.getLogger().info("Migration spends {} ms", System.currentTimeMillis() - t);
        return success;
    }

    @Override
    public boolean start() throws SwiftServiceException {
        return super.start();
    }

    @Override
    public boolean shutdown() throws SwiftServiceException {
        return super.shutdown();
    }

    private static boolean isCubeOrMigrationPath(String path) {
        return SwiftProperty.get().getCubesPath().equals(path) || MigrateProperty.get().getBackupPath().endsWith(path);
    }
}
