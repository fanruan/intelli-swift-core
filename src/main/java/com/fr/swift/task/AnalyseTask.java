package com.fr.swift.task;

import com.fr.swift.boot.controller.SwiftCloudController;
import com.fr.swift.cloud.SwiftCloudConstants;
import com.fr.swift.cloud.SwiftCloudUtils;
import com.fr.swift.cloud.analysis.TemplateAnalysisUtils;
import com.fr.swift.cloud.load.CSVImportUtils;
import com.fr.swift.cloud.result.ArchiveDBManager;
import com.fr.swift.cloud.result.table.CustomerInfo;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.repository.utils.ZipUtils;
import com.fr.swift.util.Strings;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.persistence.Query;
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
                CSVImportUtils.load(downloadPath, clientAppId, treasDate,"1.0");
                TemplateAnalysisUtils.tplAnalysis(clientAppId, treasDate);
                saveCustomerInfo(clientUserId, clientAppId);

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

    private static void saveCustomerInfo(String clientId, String appId) {
        if (isExisted(clientId, appId)) {
            return;
        }
        Session session = ArchiveDBManager.INSTANCE.getFactory().openSession();
        try {
            Transaction transaction = session.beginTransaction();
            CustomerInfo customerInfo = new CustomerInfo(clientId, appId);
            session.saveOrUpdate(customerInfo);
            transaction.commit();
        } catch (Exception ignored) {
        }
        session.close();
    }

    private static boolean isExisted(String clientId, String appId) {
        Session session = ArchiveDBManager.INSTANCE.getFactory().openSession();
        try {
            Query query = session.createQuery(sql(CustomerInfo.class.getSimpleName()));
            query.setParameter("clientId", clientId);
            query.setParameter("appId", appId);
            return ((org.hibernate.query.Query) query).uniqueResult() != null;
        } catch (Exception ignored) {
        }
        session.close();
        return false;
    }

    private static String sql(String tableName) {
        return "select 1 from " + tableName + " where clientId = :clientId and appId = :appId";
    }
}
