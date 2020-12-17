package com.fr.swift.service;

import com.fr.swift.SwiftContext;
import com.fr.swift.annotation.SwiftService;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.exception.SwiftServiceException;
import com.fr.swift.executor.task.constants.PathConstants;
import com.fr.swift.executor.task.utils.MigrationZipUtils;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SegmentService;
import com.fr.swift.util.FileUtil;

import java.io.File;
import java.util.List;

/**
 * @author Hoky
 * @date 2020/7/21
 * @description
 * @since swift-1.2.0
 */
@SwiftService(name = "migrate")
@SwiftBean(name = "migrate")
public class SwiftMigrateService extends AbstractSwiftService implements MigrateService {

    private String targetZipPath;

    private static String CLOUD_NAME = "CLOUD";

    @Override
    public ServiceType getServiceType() {
        return ServiceType.MIGRATE;
    }

    @Override
    public Boolean deleteMigratedFile(String targetPath) {
        SwiftLoggers.getLogger().info("start to delete migrated file at : {}", targetPath);
        String firstUncompressPath = "";
        String zipClusterIdPath = "";
        try {
            long startTime = System.currentTimeMillis();
            targetZipPath = getTargetZipPath(targetPath);
            String clusterId = getClusterId(targetPath);
            String readyUncompressPath = zipFilesPath(targetPath);
            //第一次解压到一个带"_zip"的文件目录，该目录下的还是未解压各个表的压缩文件
            MigrationZipUtils.unCompress(targetPath, readyUncompressPath);
            zipClusterIdPath = targetZipPath + PathConstants.UNDERSCORE + clusterId;
            firstUncompressPath = targetPath.substring(0, targetPath.lastIndexOf(PathConstants.DOT)) + PathConstants.UNDERSCORE + PathConstants.ZIP_NAME;
            File zipFile = new File(firstUncompressPath + PathConstants.SEPARATOR + new File(firstUncompressPath).list()[0]);
            //第二次解压到一个带 _clusterId 的文件目录，防止两个节点同时传文件造成冲突
            for (File zip : zipFile.listFiles()) {
                MigrationZipUtils.unCompress(zip.getPath(), zipClusterIdPath);
            }
            File clusterIdFile = new File(zipClusterIdPath);
            //判断带年月的（例如202006）的文件夹是否存在
            if (!new File(targetZipPath).exists()) {
                //如果不存在，直接重命名
                clusterIdFile.renameTo(new File(targetZipPath));
            } else {
                //存在就将各个块移动过去
                for (File tableFile : clusterIdFile.listFiles()) {
                    File[] files = tableFile.listFiles();
                    if (files != null) {
                        for (File segFile : files) {
                            FileUtil.move(segFile, new File(targetZipPath + PathConstants.SEPARATOR + tableFile.getName() + PathConstants.SEPARATOR + segFile.getName()), true);
                        }
                    }
                }
            }
            SwiftLoggers.getLogger().info("uncompressisng cost : " + (System.currentTimeMillis() - startTime) + " ms");
            //删除所有的多余文件
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
            return false;
        } finally {
            try {
                MigrationZipUtils.delDir(targetPath);
                MigrationZipUtils.delDir(firstUncompressPath);
                MigrationZipUtils.delDir(zipClusterIdPath);
            } catch (Exception e) {
                SwiftLoggers.getLogger().error(e);
            }
        }
        return true;

    }

    @Override
    public Boolean updateMigratedSegsConfig(List<SegmentKey> segmentKeys) {
        SwiftLoggers.getLogger().info("start to add {} segments to container", segmentKeys.size());
        final SegmentService segmentService = SwiftContext.get().getBean(SegmentService.class);
        segmentService.addSegments(segmentKeys);
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

    private String zipFilesPath(String targetPath) {
        return targetPath.substring(0, targetPath.lastIndexOf(".")) + PathConstants.UNDERSCORE + PathConstants.ZIP_NAME;
    }

    private String getTargetZipPath(String targetPath) {
        return targetPath.substring(0, targetPath.lastIndexOf(CLOUD_NAME) - 1);
    }

    private String getClusterId(String targetPath) {
        String zipName = targetPath.substring(targetPath.lastIndexOf("/") + 1);
        return zipName.substring(zipName.indexOf(PathConstants.UNDERSCORE) + 1, zipName.lastIndexOf(PathConstants.DOT));
    }

}
