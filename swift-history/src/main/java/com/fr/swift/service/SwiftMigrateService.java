package com.fr.swift.service;

import com.fr.swift.SwiftContext;
import com.fr.swift.annotation.SwiftService;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.exception.SwiftServiceException;
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

    private static final String SEPARATOR = "/";

    private static final String UNDERSCORE = "_";

    @Override
    public ServiceType getServiceType() {
        return ServiceType.MIGRATE;
    }

    @Override
    public Boolean deleteMigratedFile(String targetPath) {
        try {
            targetZipPath = getTargetZipPath(targetPath);
            String clusterId = getClusterId(targetPath);
            String readyUncompressPath = zipFilesPath(targetPath);
            //第一次解压到一个带"_zip"的文件目录，该目录下的还是未解压各个表的压缩文件
            MigrationZipUtils.unCompress(targetPath, readyUncompressPath);
            String zipClusterIdPath = targetZipPath + UNDERSCORE + clusterId;
            String firstUncompressPath = targetPath.substring(0, targetPath.lastIndexOf(".")) + UNDERSCORE + "zip";
            File zipFile = new File(firstUncompressPath + SEPARATOR + new File(firstUncompressPath).list()[0]);
            //第二次解压到一个带 _clusterId 的文件目录，防止两个节点同时传文件造成冲突
            for (File zip : zipFile.listFiles()) {
                MigrationZipUtils.unCompress(zip.getPath(), zipClusterIdPath);
            }
            File clusterIdFile = new File(zipClusterIdPath);
            //判断带年月的（例如202006）的文件夹是否存在
            if (!new File(targetPath).exists()) {
                //如果不存在，直接重命名
                clusterIdFile.renameTo(new File(targetZipPath));
            } else {
                //存在就将各个块移动过去
                for (File tableFile : clusterIdFile.listFiles()) {
                    for (File segFile : tableFile.listFiles()) {
                        FileUtil.move(segFile, new File(targetZipPath + SEPARATOR + tableFile.getName() + SEPARATOR + segFile.getName()), true);
                    }
                }
            }
            //删除所有的多余文件
            MigrationZipUtils.delDir(targetPath);
            MigrationZipUtils.delDir(firstUncompressPath);
            MigrationZipUtils.delDir(zipClusterIdPath);
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
            return false;
        }
        return true;
    }

    @Override
    public Boolean updateMigratedSegsConfig(List<SegmentKey> segmentKeys) {
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
        return targetPath.substring(0, targetPath.lastIndexOf(".")) + "_zip";
    }

    private String getTargetZipPath(String targetPath) {
        return targetPath.substring(0, targetPath.lastIndexOf("CLOUD") - 1);
    }

    private String getClusterId(String targetPath) {
        String zipName = targetPath.substring(targetPath.lastIndexOf("/") + 1);
        return zipName.substring(zipName.indexOf("_") + 1, zipName.lastIndexOf("."));
    }

}
