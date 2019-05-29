package com.fr.swift.cloud.task;

import com.fr.swift.cloud.CloudProperty;
import com.fr.swift.cloud.SwiftCloudConstants;
import com.fr.swift.cloud.analysis.TemplateAnalysisUtils;
import com.fr.swift.cloud.analysis.downtime.DowntimeAnalyser;
import com.fr.swift.cloud.bean.TreasureAnalysisBean;
import com.fr.swift.cloud.bean.TreasureBean;
import com.fr.swift.cloud.kafka.MessageProducer;
import com.fr.swift.cloud.load.FileImportUtils;
import com.fr.swift.cloud.result.ArchiveDBManager;
import com.fr.swift.cloud.result.table.CustomerInfo;
import com.fr.swift.cloud.result.table.ExecutionMetric;
import com.fr.swift.cloud.result.table.LatencyTopPercentileStatistic;
import com.fr.swift.cloud.result.table.TemplateAnalysisResult;
import com.fr.swift.cloud.result.table.TemplateProperty;
import com.fr.swift.cloud.result.table.TemplatePropertyRatio;
import com.fr.swift.cloud.result.table.downtime.DowntimeExecutionResult;
import com.fr.swift.cloud.result.table.downtime.DowntimeResult;
import com.fr.swift.cloud.util.TimeUtils;
import com.fr.swift.cloud.util.ZipUtils;
import com.fr.swift.executor.task.job.BaseJob;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.util.Strings;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.persistence.Query;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;

/**
 * This class created on 2019/5/9
 *
 * @author Lucifer
 * @description
 */
public class TreasureUploadJob extends BaseJob<Boolean, TreasureBean> {

    private TreasureBean treasureBean;
    private static MessageProducer messageProducer = new MessageProducer();

    public TreasureUploadJob(TreasureBean treasureBean) {
        this.treasureBean = treasureBean;
    }

    @Override
    public Boolean call() throws Exception {
        // 通过客户的用户ID、客户的应用ID和客户的数据包日期获取数据包的下载链接
        logStartGetDownload(treasureBean.getClientId(), treasureBean.getClientAppId(), treasureBean.getYearMonth(), treasureBean.getVersion());
        String downloadLink = treasureBean.getUrl();
        if (Strings.isNotEmpty(downloadLink)) {
            SwiftLoggers.getLogger().info("get download link success. link is {}", downloadLink);
            InputStream inputStream = new URL(downloadLink).openStream();
            String downloadPath = SwiftCloudConstants.ZIP_FILE_PATH + File.separator + treasureBean.getClientId() + File.separator + treasureBean.getClientAppId() + File.separator + treasureBean.getYearMonth();
            ZipUtils.unzip(inputStream, new File(downloadPath));
            // 先导入csv文件数据到cube，然后生成分析结果，并保存到数据库
            logStartAnalyse(treasureBean.getClientId(), treasureBean.getClientAppId(), treasureBean.getYearMonth(), treasureBean.getVersion());

            FileImportUtils.load(downloadPath, treasureBean.getClientAppId(), treasureBean.getYearMonth(), treasureBean.getVersion());

            // 为了避免重复，先清除数据
            try {
                deleteIfExisting(treasureBean.getClientAppId(), treasureBean.getYearMonth());
            } catch (Exception ignored) {
            }

            TemplateAnalysisUtils.tplAnalysis(treasureBean.getClientAppId(), treasureBean.getYearMonth());
            if (!treasureBean.getVersion().equals("1.0")) {
                new DowntimeAnalyser().downtimeAnalyse(treasureBean.getClientAppId(), treasureBean.getYearMonth());
            }
            saveCustomerInfo(treasureBean.getClientId(), treasureBean.getClientAppId(), treasureBean.getYearMonth());
            TreasureAnalysisBean treasureAnalysisBean = new TreasureAnalysisBean(treasureBean.getClientId(), treasureBean.getClientAppId(), treasureBean.getYearMonth(), treasureBean.getVersion());
            messageProducer.produce(CloudProperty.getProperty().getTreasureAnalysisTopic(), treasureAnalysisBean);
        } else {
            throw new RuntimeException("Download link is empty");
        }
        return true;
    }

    private static void deleteIfExisting(String appId, String yearMonth) throws Exception {
        Date date = TimeUtils.yearMonth2Date(yearMonth);
        Session session = ArchiveDBManager.INSTANCE.getFactory().openSession();
        for (String table : tables) {
            try {
                Transaction transaction = session.beginTransaction();
                Query query = session.createQuery(deleteSql(table));
                query.setParameter("appId", appId);
                query.setParameter("yearMonth", date);
                query.executeUpdate();
                transaction.commit();
            } catch (Exception ignored) {
            }
        }
        session.close();
    }

    private void saveCustomerInfo(String clientId, String appId, String yearMonth) {
        if (isExisted(clientId, appId)) {
            return;
        }
        Session session = ArchiveDBManager.INSTANCE.getFactory().openSession();
        try {
            Transaction transaction = session.beginTransaction();
            CustomerInfo customerInfo = new CustomerInfo(clientId, appId, yearMonth);
            session.saveOrUpdate(customerInfo);
            transaction.commit();
        } catch (Exception ignored) {
        }
        session.close();
    }

    private boolean isExisted(String clientId, String appId) {
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

    private String sql(String tableName) {
        return "select 1 from " + tableName + " where clientId = :clientId and appId = :appId";
    }

    private static String[] tables = new String[]{
            ExecutionMetric.class.getSimpleName(),
            LatencyTopPercentileStatistic.class.getSimpleName(),
            TemplateAnalysisResult.class.getSimpleName(),
            TemplateProperty.class.getSimpleName(),
            TemplatePropertyRatio.class.getSimpleName(),
            DowntimeResult.class.getSimpleName(),
            DowntimeExecutionResult.class.getSimpleName(),
    };

    private static String deleteSql(String tableName) {
        return "delete from " + tableName + " where appId = :appId and yearMonth = :yearMonth";
    }

    @Override
    public TreasureBean serializedTag() {
        return treasureBean;
    }

    private void logStartGetDownload(String clientUserId, String clientAppId, String treasDate, String version) {
        SwiftLoggers.getLogger().info("======================================");
        SwiftLoggers.getLogger().info("     Start get download address");
        logClientInfo(clientUserId, clientAppId, treasDate, version);
    }

    private void logStartAnalyse(String clientUserId, String clientAppId, String treasDate, String version) {
        SwiftLoggers.getLogger().info("======================================");
        SwiftLoggers.getLogger().info("           Start Analyse");
        logClientInfo(clientUserId, clientAppId, treasDate, version);
    }

    private void logStartUpload(String clientUserId, String clientAppId, String treasDate, String version) {
        SwiftLoggers.getLogger().info("======================================");
        SwiftLoggers.getLogger().info("           Start Upload");
        logClientInfo(clientUserId, clientAppId, treasDate, version);
    }

    private void logClientInfo(String clientUserId, String clientAppId, String treasDate, String version) {
        SwiftLoggers.getLogger().info("======================================");
        SwiftLoggers.getLogger().info(" ClientUserId:\t{} ", clientUserId);
        SwiftLoggers.getLogger().info(" ClientAppId:\t{} ", clientAppId);
        SwiftLoggers.getLogger().info(" TreasDate:\t{} ", treasDate);
        SwiftLoggers.getLogger().info(" Version:\t{} ", version);
        SwiftLoggers.getLogger().info("======================================");
    }
}
