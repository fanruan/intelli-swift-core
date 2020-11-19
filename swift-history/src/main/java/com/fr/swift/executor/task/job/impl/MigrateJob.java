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
import java.util.concurrent.CountDownLatch;

/**
 * @author Hoky
 * @date 2020/7/21
 * @description
 * @since swift-1.2.0
 */
public class MigrateJob extends BaseJob<Boolean, MigrateBean> {

    private static final String SEPARATOR = "/";

    private static final String PATH_CUBES = "cubes" + SEPARATOR;

    private static final String UNDERSCORE = "_";

    private static final String ZIP_NAME = "zip";

    private static final String DOT = ".";

    private static CountDownLatch countDownLatch = new CountDownLatch(1);
    ;

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
        String ownNodeId = nodeInfoService.getOwnNodeInfo().getNodeId();
        final String migratePath = cubePath + PATH_CUBES + migrateIndex;
        final String zipPath = migratePath + UNDERSCORE + ZIP_NAME;
        final String zipName = migratePath + DOT + ZIP_NAME;

        SwiftNodeInfo targetNodeInfo = nodeInfoService.getNodeInfo(migrateTarget);
        String targetCubePath = targetNodeInfo.getCubePath();
        final String targetPath = targetCubePath + PATH_CUBES + migrateIndex + UNDERSCORE + ownNodeId + DOT + ZIP_NAME;

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
                filePacket.setTargetPath(targetPath);
                fileUploadClient = new FileUploadClient();

                String[] addressArray = targetNodeInfo.getMigServerAddress().split(":");
                String ip = addressArray[0];
                int port = Integer.parseInt(addressArray[1]);
                if (uploadFile(fileUploadClient, ip, port, filePacket)) {
                    if (!serviceContext.deleteFiles(targetPath, migrateTarget)) {
                        SwiftLoggers.getLogger().error("migrate files error!");
                        return false;
                    } else {
                        SwiftLoggers.getLogger().info("migration finished!");
                    }
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

    public static void countDown() {
        countDownLatch.countDown();
    }

    private static boolean uploadFile(FileUploadClient fileUploadClient, String ip, int port, FilePacket filePacket) throws InterruptedException {
        if (fileUploadClient.connect(ip, port)) {
            if (fileUploadClient.writeAndFlush(filePacket)) {
                countDownLatch.await();
            } else {
                return false;
            }
        } else {
            return false;
        }
        return true;
    }
}
