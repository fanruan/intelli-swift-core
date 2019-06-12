package com.fr.swift.cloud.task;

import com.fr.swift.cloud.CloudProperty;
import com.fr.swift.cloud.analysis.ConfEntityQuery;
import com.fr.swift.cloud.analysis.CustomBaseInfoQuery;
import com.fr.swift.cloud.analysis.FunctionUsageRateQuery;
import com.fr.swift.cloud.analysis.PluginUsageQuery;
import com.fr.swift.cloud.analysis.SystemUsageInfoQuery;
import com.fr.swift.cloud.analysis.TemplateUsageInfoQuery;
import com.fr.swift.cloud.analysis.downtime.DowntimeAnalyser;
import com.fr.swift.cloud.analysis.template.TemplateAnalysisUtils;
import com.fr.swift.cloud.bean.TreasureAnalysisBean;
import com.fr.swift.cloud.bean.TreasureBean;
import com.fr.swift.cloud.kafka.MessageProducer;
import com.fr.swift.cloud.result.ArchiveDBManager;
import com.fr.swift.cloud.result.table.ConfEntity;
import com.fr.swift.cloud.result.table.CustomerBaseInfo;
import com.fr.swift.cloud.result.table.CustomerInfo;
import com.fr.swift.cloud.result.table.FunctionUsageRate;
import com.fr.swift.cloud.result.table.PluginUsage;
import com.fr.swift.cloud.result.table.SystemUsageInfo;
import com.fr.swift.cloud.result.table.TemplateUsageInfo;
import com.fr.swift.cloud.result.table.downtime.DowntimeExecutionResult;
import com.fr.swift.cloud.result.table.downtime.DowntimeResult;
import com.fr.swift.cloud.result.table.template.ExecutionMetric;
import com.fr.swift.cloud.result.table.template.LatencyTopPercentileStatistic;
import com.fr.swift.cloud.result.table.template.TemplateAnalysisResult;
import com.fr.swift.cloud.result.table.template.TemplateProperty;
import com.fr.swift.cloud.result.table.template.TemplatePropertyRatio;
import com.fr.swift.cloud.util.CloudLogUtils;
import com.fr.swift.cloud.util.TimeUtils;
import com.fr.swift.executor.task.job.BaseJob;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This class created on 2019/6/12
 *
 * @author Lucifer
 * @description
 */
public class TreasureAnalysisJob extends BaseJob<Boolean, TreasureBean> {

    private TreasureBean treasureBean;
    private static MessageProducer messageProducer = new MessageProducer();

    public TreasureAnalysisJob(TreasureBean treasureBean) {
        this.treasureBean = treasureBean;
    }

    @Override
    public Boolean call() throws Exception {
        String appId = treasureBean.getClientAppId();
        String yearMonth = treasureBean.getYearMonth();

        CloudLogUtils.logStartJob(treasureBean.getClientId(), appId, yearMonth, treasureBean.getVersion(), "analysis");
        // 为了避免重复，先清除数据
        try {
            deleteIfExisting(appId, yearMonth);
        } catch (Exception ignored) {
        }
        TemplateAnalysisUtils.tplAnalysis(appId, yearMonth);
        List<DowntimeResult> downtimeResultList = new ArrayList<>();
        if (!treasureBean.getVersion().equals("1.0")) {
            downtimeResultList.addAll(new DowntimeAnalyser().downtimeAnalyse(appId, yearMonth));
        }
        saveCustomerInfo(treasureBean.getClientId(), appId, yearMonth);

        Session session = ArchiveDBManager.INSTANCE.getFactory().openSession();
        Transaction transaction = session.beginTransaction();
        try {
            List<Object> saveList = new ArrayList<>();
            saveList.addAll(new ConfEntityQuery().query(appId, yearMonth));
            saveList.addAll(new PluginUsageQuery().query(appId, yearMonth));
            saveList.addAll((new FunctionUsageRateQuery().query(appId, yearMonth)));
            saveList.add(new CustomBaseInfoQuery().query(appId, yearMonth));
            saveList.add(new TemplateUsageInfoQuery().query(appId, yearMonth));
            saveList.add(new SystemUsageInfoQuery().query(appId, yearMonth, downtimeResultList));
            for (int i = 0; i < saveList.size(); i++) {
                try {
                    session.save(saveList.get(i));
                    if (i % 20 == 0) {
                        session.flush();
                        session.clear();
                    }
                } catch (Exception ignore) {

                }
            }
        } finally {
            transaction.commit();
            try {
                session.close();
            } catch (Exception ignored) {
            }
        }

        CloudLogUtils.logStartJob(treasureBean.getClientId(), treasureBean.getClientAppId(), treasureBean.getYearMonth(), treasureBean.getVersion(), "send message");
        TreasureAnalysisBean treasureAnalysisBean = new TreasureAnalysisBean(treasureBean.getClientId(), treasureBean.getClientAppId(), treasureBean.getYearMonth(), treasureBean.getVersion());
        messageProducer.produce(CloudProperty.getProperty().getTreasureAnalysisTopic(), treasureAnalysisBean);
        return true;
    }

    @Override
    public TreasureBean serializedTag() {
        return treasureBean;
    }

    public static void deleteIfExisting(String appId, String yearMonth) throws Exception {
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

    public static void saveCustomerInfo(String clientId, String appId, String yearMonth) {
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

    private static String[] tables = new String[]{
            ExecutionMetric.class.getSimpleName(),
            LatencyTopPercentileStatistic.class.getSimpleName(),
            TemplateAnalysisResult.class.getSimpleName(),
            TemplateProperty.class.getSimpleName(),
            TemplatePropertyRatio.class.getSimpleName(),
            DowntimeResult.class.getSimpleName(),
            DowntimeExecutionResult.class.getSimpleName(),
            ConfEntity.class.getSimpleName(),
            CustomerBaseInfo.class.getSimpleName(),
            FunctionUsageRate.class.getSimpleName(),
            PluginUsage.class.getSimpleName(),
            SystemUsageInfo.class.getSimpleName(),
            TemplateUsageInfo.class.getSimpleName()
    };

    private static String deleteSql(String tableName) {
        return "delete from " + tableName + " where appId = :appId and yearMonth = :yearMonth";
    }
}
