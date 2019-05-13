package com.fr.swift.task;

import com.fr.swift.boot.controller.SwiftCloudController;
import com.fr.swift.cloud.SwiftCloudConstants;
import com.fr.swift.cloud.SwiftCloudUtils;
import com.fr.swift.cloud.analysis.TemplateAnalysisUtils;
import com.fr.swift.cloud.load.CSVImportUtils;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.repository.utils.ZipUtils;
import com.fr.swift.util.Strings;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

/**
 * @author yee
 * @date 2019-03-01
 */
@Deprecated
public class AnalyseTask implements Runnable {

    private String appKey;
    private String appSecret;
    private String clientUserId;
    private String clientAppId;
    private String treasDate;

    public AnalyseTask(String appKey, String appSecret, String clientUserId, String clientAppId, String treasDate) {
        this.appKey = appKey;
        this.appSecret = appSecret;
        this.clientUserId = clientUserId;
        this.clientAppId = clientAppId;
        this.treasDate = treasDate;
    }

    @Override
    public void run() {
        try {
            // 通过客户的用户ID、客户的应用ID和客户的数据包日期获取数据包的下载链接
            logStartGetDownload(clientUserId, clientAppId, treasDate);
            String downloadLink = SwiftCloudUtils.getDownloadLink(appKey, appSecret, clientUserId, clientAppId, treasDate);

            if (Strings.isNotEmpty(downloadLink)) {
                SwiftLoggers.getLogger().info("get download link success. link is {}", downloadLink);
                InputStream inputStream = new URL(downloadLink).openStream();
                String downloadPath = SwiftCloudConstants.ZIP_FILE_PATH + File.separator + clientUserId + File.separator + clientAppId + File.separator + treasDate;
                ZipUtils.unZip(downloadPath, inputStream);
                logStartAnalyse(clientUserId, clientAppId, treasDate);
                // 先导入csv文件数据到cube，然后生成分析结果，并保存到数据库
                CSVImportUtils.load(downloadPath, clientAppId, treasDate);
                TemplateAnalysisUtils.tplAnalysis(clientAppId, treasDate);

                // 云端的path
//                    String reportPath = "";
//                    String filePath = "";
//                    File report = new File(filePath);
//                    logStartUpload(clientUserId, clientAppId, treasDate);
//                    boolean upload = SwiftCloudUtils.upload(report, appKey, appSecret, clientUserId, clientAppId, treasDate, reportPath);
//                    if (upload) {
//                        SwiftLoggers.getLogger().info("Upload report {} success", reportPath);
//                    }
            } else {
                throw new RuntimeException("Download link is empty");
            }
        } catch (Exception e) {
            SwiftLoggers.getLogger().warn(e);
        }
    }

    private void logStartGetDownload(String clientUserId, String clientAppId, String treasDate) {
        SwiftLoggers.getLogger().info("======================================");
        SwiftLoggers.getLogger().info("     Start get download address");
        SwiftCloudController.logClientInfo(clientUserId, clientAppId, treasDate);
    }

    private void logStartAnalyse(String clientUserId, String clientAppId, String treasDate) {
        SwiftLoggers.getLogger().info("======================================");
        SwiftLoggers.getLogger().info("           Start Analyse");
        SwiftCloudController.logClientInfo(clientUserId, clientAppId, treasDate);
    }

    private void logStartUpload(String clientUserId, String clientAppId, String treasDate) {
        SwiftLoggers.getLogger().info("======================================");
        SwiftLoggers.getLogger().info("           Start Upload");
        SwiftCloudController.logClientInfo(clientUserId, clientAppId, treasDate);
    }
}
