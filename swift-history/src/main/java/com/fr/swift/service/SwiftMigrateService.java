package com.fr.swift.service;

import com.fr.swift.annotation.SwiftService;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.exception.SwiftServiceException;
import com.fr.swift.executor.task.utils.MigrationZipUtils;
import com.fr.swift.log.SwiftLoggers;

import java.io.File;

/**
 * @author Hoky
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
    public Boolean deleteMigraFile(String targetPath) {
        try {
            String readyUncompressPath = zipFilesPath(targetPath);
            MigrationZipUtils.unCompress(targetPath, readyUncompressPath);
            String zipsPath = targetPath.substring(0, targetPath.lastIndexOf("."));
            String firstUncompressPath = targetPath.substring(0, targetPath.lastIndexOf(".")) + "_zip";
            File zipFile = new File(firstUncompressPath);
            for (File zip : zipFile.listFiles()) {
                MigrationZipUtils.unCompress(zip.getPath(), zipsPath);
            }
            MigrationZipUtils.delDir(targetPath);
            MigrationZipUtils.delDir(firstUncompressPath);
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
            return false;
        }
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

    private static String zipFilesPath(String targetPath) {
        return targetPath.substring(0, targetPath.lastIndexOf("/"));
    }

}
