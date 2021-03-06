package com.fr.swift.cloud.executor.task.job.impl;

import com.fr.swift.cloud.SwiftContext;
import com.fr.swift.cloud.basics.base.selector.ProxySelector;
import com.fr.swift.cloud.config.entity.SwiftNodeInfo;
import com.fr.swift.cloud.config.service.SwiftNodeInfoService;
import com.fr.swift.cloud.executor.task.bean.MigrateBean;
import com.fr.swift.cloud.executor.task.constants.PathConstants;
import com.fr.swift.cloud.executor.task.job.BaseJob;
import com.fr.swift.cloud.executor.task.netty.client.FileUploadClient;
import com.fr.swift.cloud.executor.task.netty.client.FileUploadClientHandler;
import com.fr.swift.cloud.executor.task.netty.protocol.FilePacket;
import com.fr.swift.cloud.executor.task.utils.MigrationZipUtils;
import com.fr.swift.cloud.log.SwiftLoggers;
import com.fr.swift.cloud.service.ServiceContext;

import java.io.File;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

/**
 * @author Hoky
 * @date 2020/7/21
 * @description
 * @since swift-1.2.0
 */
public class MigrateJob extends BaseJob<Boolean, MigrateBean> {

    private static CountDownLatch countDownLatch;

    private SwiftNodeInfoService nodeInfoService = SwiftContext.get().getBean(SwiftNodeInfoService.class);

    private MigrateBean migrateBean;

    private String uuid;

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
        final String migratePath = cubePath + PathConstants.PATH_CUBES + migrateIndex;
        final String zipPath = migratePath + PathConstants.UNDERSCORE + PathConstants.ZIP_NAME;
        final String zipName = migratePath + PathConstants.DOT + PathConstants.ZIP_NAME;

        SwiftNodeInfo targetNodeInfo = nodeInfoService.getNodeInfo(migrateTarget);
        String targetCubePath = targetNodeInfo.getCubePath();
        final String targetPath = targetCubePath + PathConstants.PATH_CUBES + migrateIndex + PathConstants.UNDERSCORE + ownNodeId + PathConstants.DOT + PathConstants.ZIP_NAME;

        final ServiceContext serviceContext = ProxySelector.getProxy(ServiceContext.class);
        File migrateFile = new File(migratePath);
        FileUploadClient fileUploadClient = null;
        if (!migrateFile.exists()) {
            throw new Exception(String.format("migrate path %s not exist!", migratePath));
        }
        if (migrateFile.isDirectory()) {
            try {
                fileUploadClient = new FileUploadClient();
                File[] filesBeforeZip = migrateFile.listFiles();
                new File(zipPath).mkdirs();
                for (File file : Objects.requireNonNull(filesBeforeZip)) {
                    MigrationZipUtils.toZip(file.getPath(), zipPath + PathConstants.SEPARATOR + file.getName() + ".zip", false);
                }
                MigrationZipUtils.toZip(zipPath, zipName, false);

                //netty传输
                FilePacket filePacket = new FilePacket();
                uuid = filePacket.getUuid();
                File file = new File(zipName);
                filePacket.setFile(file);
                filePacket.setStartPos(0);     //要传输的文件的初始信息
                filePacket.setTargetPath(targetPath);

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
                MigrationZipUtils.delDir(zipPath);
                MigrationZipUtils.delDir(zipName);
                fileUploadClient.shutdownGroup();
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

    private boolean uploadFile(FileUploadClient fileUploadClient, String ip, int port, FilePacket filePacket) throws InterruptedException {
        int limitTransferHour = SwiftContext.get().getBean(SwiftNodeInfoService.class).getOwnNodeInfo().getLimitTransferHour();
        if (new Date(System.currentTimeMillis()).getHours() >= limitTransferHour) {
            SwiftLoggers.getLogger().error("Transfer start overtime!");
            return false;
        }
        countDownLatch = new CountDownLatch(1);
        if (fileUploadClient.connect(ip, port, filePacket)) {
            if (fileUploadClient.writeAndFlush(filePacket)) {
                countDownLatch.await();
                fileUploadClient.closeFuture();
                return FileUploadClientHandler.isTransfer(uuid);
            }
        }
        return false;
    }
}
