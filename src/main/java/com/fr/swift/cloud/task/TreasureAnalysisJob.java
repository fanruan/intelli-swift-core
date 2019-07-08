package com.fr.swift.cloud.task;

import com.fr.swift.SwiftContext;
import com.fr.swift.cloud.CloudProperty;
import com.fr.swift.cloud.analysis.CloudQuery;
import com.fr.swift.cloud.analysis.ICloudQuery;
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
import java.util.Map;

/**
 * This class created on 2019/6/12
 *
 * @author Lucifer
 * @description
 */

public class TreasureAnalysisJob extends BaseJob<Boolean, TreasureBean> {

    private TreasureBean treasureBean;
    private static MessageProducer messageProducer = new MessageProducer();
    private static Map<String, Object> objectMap = SwiftContext.get().getBeansByAnnotations(CloudQuery.class);
    private static List<String> queryList = new ArrayList<>();

    static {
        for (Map.Entry<String, Object> entry : objectMap.entrySet()) {
            CloudQuery cloudQuery = entry.getValue().getClass().getAnnotation(CloudQuery.class);
            if (cloudQuery != null) {
                queryList.add(cloudQuery.name());
            }
        }
    }

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
            for (String table : tables) {
                deleteIfExisting(appId, yearMonth, table);
            }
        } catch (Exception ignored) {
        }

        TemplateAnalysisUtils.tplAnalysis(appId, yearMonth);
        saveCustomerInfo(treasureBean.getClientId(), appId, yearMonth, treasureBean.getCustomerId(), treasureBean.getType());

        //对所有的queries查询 计算分析 写入数据库
        List<ICloudQuery> queries = getQueries(objectMap, queryList);
        for (ICloudQuery query : queries) {
            query.calculate(appId, yearMonth);
        }

        CloudLogUtils.logStartJob(treasureBean.getClientId(), treasureBean.getClientAppId(), treasureBean.getYearMonth(), treasureBean.getVersion(), "send message");
        TreasureAnalysisBean treasureAnalysisBean = new TreasureAnalysisBean(treasureBean.getClientId(), treasureBean.getClientAppId()
                , treasureBean.getYearMonth(), treasureBean.getVersion(), treasureBean.getType(), treasureBean.getCustomerId());
        messageProducer.produce(CloudProperty.getProperty().getTreasureAnalysisTopic(), treasureAnalysisBean);
        return true;
    }

    @Override
    public TreasureBean serializedTag() {
        return treasureBean;
    }

    public static List<ICloudQuery> getQueries(Map<String, Object> objectMap, List<String> queryList) {
        List<ICloudQuery> queries = new ArrayList<>();
        for (Map.Entry<String, Object> entry : objectMap.entrySet()) {
            CloudQuery cloudQuery = entry.getValue().getClass().getAnnotation(CloudQuery.class);
            if (queryList.contains(cloudQuery.name())) {
                queries.add((ICloudQuery) entry.getValue());
            }
        }
        return queries;
    }

    public static void deleteIfExisting(String appId, String yearMonth, String tableName) throws Exception {
        Date date = TimeUtils.yearMonth2Date(yearMonth);
        Session session = ArchiveDBManager.INSTANCE.getFactory().openSession();
        try {
            Transaction transaction = session.beginTransaction();
            Query query = session.createQuery(deleteSql(tableName));
            query.setParameter("appId", appId);
            query.setParameter("yearMonth", date);
            query.executeUpdate();
            transaction.commit();
        } catch (Exception ignored) {
        }
        session.close();
    }

    public static void saveCustomerInfo(String clientId, String appId, String yearMonth, String customerId, String type) {
        if (isExisted(clientId, appId)) {
            return;
        }
        Session session = ArchiveDBManager.INSTANCE.getFactory().openSession();
        try {
            Transaction transaction = session.beginTransaction();
            CustomerInfo customerInfo = new CustomerInfo(clientId, customerId, appId, yearMonth, type);
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
