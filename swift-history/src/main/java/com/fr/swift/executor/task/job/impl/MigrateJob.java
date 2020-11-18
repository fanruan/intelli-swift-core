package com.fr.swift.executor.task.job.impl;

import com.fr.swift.SwiftContext;
import com.fr.swift.basics.base.selector.ProxySelector;
import com.fr.swift.config.entity.SwiftNodeInfo;
import com.fr.swift.config.service.SwiftNodeInfoService;
import com.fr.swift.executor.task.bean.MigrateBean;
import com.fr.swift.executor.task.job.BaseJob;
import com.fr.swift.executor.task.netty.client.FileUploadClient;
import com.fr.swift.executor.task.netty.protocol.FilePacket;
import com.fr.swift.executor.task.utils.MigrationZipUtils;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.service.ServiceContext;

import java.io.File;
import java.util.Objects;

/**
 * @author Hoky
 * @date 2020/7/21
 * @description
 * @since swift-1.2.0
 */
public class MigrateJob extends BaseJob<Boolean, MigrateBean> {

    private static final String PATH_CUBES = "cubes/";
    private static final String SEPARATOR = "/";

    private SwiftNodeInfoService nodeInfoService = SwiftContext.get().getBean(SwiftNodeInfoService.class);

    private MigrateBean migrateBean;

    public MigrateJob(MigrateBean migrateBean) {
        this.migrateBean = migrateBean;
    }

    @Override
    public Boolean call() throws Exception {
        SwiftLoggers.getLogger().info("Start migrate job!");
        final String migrateIndex = migrateBean.getMigrateIndex();
        final String migrateTarget = migrateBean.getMigrateTarget();

        String cubePath = nodeInfoService.getOwnNodeInfo().getCubePath();
        final String migratePath = cubePath + PATH_CUBES + migrateIndex;
        final String zipPath = migratePath + "_zip";
        final String zipName = migratePath + ".zip";

        SwiftNodeInfo targetNodeInfo = nodeInfoService.getNodeInfo(migrateTarget);
        String targetCubePath = targetNodeInfo.getCubePath();
        final String targetPath = targetCubePath + PATH_CUBES + migrateIndex + ".zip";

        final ServiceContext serviceContext = ProxySelector.getProxy(ServiceContext.class);
        File migrateFile = new File(migratePath);
        FileUploadClient fileUploadClient = null;
        if (!migrateFile.exists()) {
            throw new Exception(String.format("migrate path %s not exist!", migratePath));
        }
        if (migrateFile.isDirectory()) {
            try {
                File[] filesBeforeZip = migrateFile.listFiles();
                new File(zipPath).mkdirs();
                for (File file : Objects.requireNonNull(filesBeforeZip)) {
                    MigrationZipUtils.toZip(file.getPath(), zipPath + SEPARATOR + file.getName() + ".zip", false);
                }
                MigrationZipUtils.toZip(zipPath, zipName, false);

                //netty传输
                FilePacket filePacket = new FilePacket();
                File file = new File(zipName);
                filePacket.setFile(file);
                fileUploadClient = new FileUploadClient();

                String[] addressArray = targetNodeInfo.getMigServerAddress().split(":");
                String ip = addressArray[0];
                int port = Integer.parseInt(addressArray[1]);
                if (fileUploadClient.connect(ip, port, filePacket)) {
                    fileUploadClient.closeFuture();
                    SwiftLoggers.getLogger().info("migration finished!");
                    serviceContext.deleteFiles(targetPath, migrateTarget);
                } else {
                    SwiftLoggers.getLogger().error("deliver file error!");
                    return false;
                }
            } catch (Exception e) {
                SwiftLoggers.getLogger().error(e);
                return false;
            } finally {
                Objects.requireNonNull(fileUploadClient).shutdownGroup();
                MigrationZipUtils.delDir(zipPath);
                MigrationZipUtils.delDir(zipName);
            }
        } else {
            throw new Exception(String.format("migrate path %s error!", migratePath));
        }
        return true;
    }

    @Override
    public MigrateBean serializedTag() {
        return migrateBean;
    }
}
