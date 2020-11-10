package com.fr.swift.executor.task.job.impl;

import com.fr.swift.SwiftContext;
import com.fr.swift.basics.base.selector.ProxySelector;
import com.fr.swift.config.service.SwiftCubePathService;
import com.fr.swift.executor.task.job.BaseJob;
import com.fr.swift.executor.task.netty.client.FileUploadClient;
import com.fr.swift.executor.task.netty.protocol.FilePacket;
import com.fr.swift.executor.task.utils.MigrationZipUtils;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.service.ServiceContext;

import java.io.File;
import java.util.List;

/**
 * @author Hoky
 * @date 2020/7/21
 * @description
 * @since swift-1.2.0
 */
public class MigrateJob extends BaseJob<Boolean, List<String>> {

    private static final SwiftCubePathService PATH_SVC = SwiftContext.get().getBean(SwiftCubePathService.class);
    private static final String SEPARATOR = "/";
    private static final String SEMICOLON = ";";
    private int yearMonth;
    private String clusterID;
    private String remotePath;
    private String yearMonthPath;
    private String zipTargetPath;
    private String zipTargetName;
    private String ip;
    private int port;
    private FilePacket filePacket;


    public MigrateJob(int yearMonth, String clusterID, String remotePath) {
        this.yearMonth = yearMonth;
        this.clusterID = clusterID;
        this.remotePath = remotePath + "cubes" + this.yearMonth + ".zip";
        yearMonthPath = PATH_SVC.getSwiftPath() + "cubes" + SEPARATOR + this.yearMonth;
        zipTargetPath = PATH_SVC.getSwiftPath() + "cubes" + SEPARATOR + this.yearMonth + "_zip";
        zipTargetName = PATH_SVC.getSwiftPath() + "cubes" + SEPARATOR + this.yearMonth + ".zip";
    }

    @Override
    public Boolean call() throws Exception {
        SwiftLoggers.getLogger().info("Start migrate job!");
        ip = "127.0.0.1";
        port = 8123;
        final ServiceContext serviceContext = ProxySelector.getProxy(ServiceContext.class);
        File yearMonthFile = new File(yearMonthPath);
        FileUploadClient fileUploadClient = null;
        if (!yearMonthFile.exists()) {
            throw new Exception("yearMonth path not exist!");
        }
        if (yearMonthFile.isDirectory()) {
            try {
                File[] filesBeforeZip = yearMonthFile.listFiles();
                new File(zipTargetPath).mkdirs();
                for (File file : filesBeforeZip) {
                    MigrationZipUtils.toZip(file.getPath(), zipTargetPath + SEPARATOR + file.getName() + ".zip", false);
                }
                MigrationZipUtils.toZip(zipTargetPath, zipTargetName, false);

                //netty传输
                filePacket = new FilePacket();
                File file = new File(zipTargetName);
                filePacket.setFile(file);
                fileUploadClient = new FileUploadClient();
                if (fileUploadClient.connect(ip, port, filePacket)) {
                    fileUploadClient.closeFuture();
                    SwiftLoggers.getLogger().info("migration finished!");
                    serviceContext.remoteDelete(remotePath, clusterID);
                } else {
                    SwiftLoggers.getLogger().error("deliver file error!");
                    return false;
                }
            } catch (Exception e) {
                SwiftLoggers.getLogger().error(e);
                return false;
            } finally {
                fileUploadClient.shutdownGroup();
                MigrationZipUtils.delDir(zipTargetPath);
                MigrationZipUtils.delDir(zipTargetName);
            }
        } else {
            throw new Exception("yearMonth path error!");
        }
        return true;
    }


}
