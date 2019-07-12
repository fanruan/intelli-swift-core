package com.fr.swift.cloud.task;

import com.fr.swift.SwiftContext;
import com.fr.swift.cloud.CloudProperty;
import com.fr.swift.cloud.analysis.CloudQuery;
import com.fr.swift.cloud.analysis.ICloudQuery;
import com.fr.swift.cloud.bean.TreasureAnalysisBean;
import com.fr.swift.cloud.bean.TreasureBean;
import com.fr.swift.cloud.kafka.MessageProducer;
import com.fr.swift.cloud.result.ArchiveDBManager;
import com.fr.swift.cloud.result.table.CustomerInfo;
import com.fr.swift.cloud.util.CloudLogUtils;
import com.fr.swift.cloud.util.TimeUtils;
import com.fr.swift.executor.task.job.BaseJob;
import com.fr.swift.log.SwiftLoggers;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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

        saveCustomerInfo(treasureBean.getClientId(), appId, yearMonth, treasureBean.getCustomerId(), treasureBean.getType());
        //对所有的queries查询 计算分析 写入数据库
        Map<ICloudQuery, String[]> queryMap = getQueries(objectMap, queryList);
        for (Map.Entry<ICloudQuery, String[]> queryEntry : queryMap.entrySet()) {
            deleteIfExisting(appId, yearMonth, queryEntry.getValue());
            queryEntry.getKey().queryAndSave(appId, yearMonth);
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

    public static Map<ICloudQuery, String[]> getQueries(Map<String, Object> objectMap, List<String> queryList) {
        Map<ICloudQuery, String[]> queryMap = new HashMap<>();
        for (Map.Entry<String, Object> entry : objectMap.entrySet()) {
            CloudQuery cloudQuery = entry.getValue().getClass().getAnnotation(CloudQuery.class);
            if (queryList.contains(cloudQuery.name())) {
                queryMap.put((ICloudQuery) entry.getValue(), cloudQuery.tables());
            }
        }
        return queryMap;
    }

    public static void deleteIfExisting(String appId, String yearMonth, String... tableNames) throws Exception {
        Date date = TimeUtils.yearMonth2Date(yearMonth);
        Session session = ArchiveDBManager.INSTANCE.getFactory().openSession();
        try {
            Transaction transaction = session.beginTransaction();
            for (String tableName : tableNames) {
                SwiftLoggers.getLogger().info("Delete table {} with appId: {}, yearMonth: {}!", tableName, appId, yearMonth);
                Query query = session.createSQLQuery(deleteSql(tableName, appId, new java.sql.Date(date.getTime())));
                query.executeUpdate();
            }
            transaction.commit();
        } catch (Exception e) {
            SwiftLoggers.getLogger().error("Delete table error with appId: {}, yearMonth: {}!", appId, yearMonth, e);
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

    private static String deleteSql(String tableName, String appId, Date yearMonth) {
        return "delete from " + tableName + " where appId = '" + appId + "' and yearMonth = '" + yearMonth + "'";
    }
}
