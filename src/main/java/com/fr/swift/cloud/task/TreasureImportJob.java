package com.fr.swift.cloud.task;

import com.fr.swift.cloud.SwiftCloudConstants;
import com.fr.swift.cloud.bean.TreasureBean;
import com.fr.swift.cloud.load.FileImportUtils;
import com.fr.swift.cloud.util.CloudLogUtils;
import com.fr.swift.cloud.util.ZipUtils;
import com.fr.swift.executor.TaskProducer;
import com.fr.swift.executor.task.job.BaseJob;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.util.Strings;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;

/**
 * This class created on 2019/5/9
 *
 * @author Lucifer
 * @description
 */
public class TreasureImportJob extends BaseJob<Boolean, TreasureBean> {

    private TreasureBean treasureBean;
    private final static int RETRY_TIMES = 10;
    private final static long RETRY_INTERVAL = 10 * 1000L;

    public TreasureImportJob(TreasureBean treasureBean) {
        this.treasureBean = treasureBean;
    }

    @Override
    public Boolean call() throws Exception {
        // 通过客户的用户ID、客户的应用ID和客户的数据包日期获取数据包的下载链接
        CloudLogUtils.logStartJob(treasureBean.getClientId(), treasureBean.getClientAppId(), treasureBean.getYearMonth(), treasureBean.getVersion(), "download");
        String downloadLink = treasureBean.getUrl();
        if (Strings.isNotEmpty(downloadLink)) {
            SwiftLoggers.getLogger().info("get download link success. link is {}", downloadLink);
            InputStream inputStream = new URL(downloadLink).openStream();
            String downloadPath = SwiftCloudConstants.ZIP_FILE_PATH + File.separator + treasureBean.getClientId() + File.separator + treasureBean.getClientAppId() + File.separator + treasureBean.getYearMonth();
            ZipUtils.unzip(inputStream, new File(downloadPath));
            // 先导入csv文件数据到cube，然后生成分析结果，并保存到数据库
            CloudLogUtils.logStartJob(treasureBean.getClientId(), treasureBean.getClientAppId(), treasureBean.getYearMonth(), treasureBean.getVersion(), "import");
            FileImportUtils.load(downloadPath, treasureBean.getClientAppId(), treasureBean.getYearMonth(), treasureBean.getVersion());
            TaskProducer.produceTask(new TreasureAnalysisTask(treasureBean));
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
}
