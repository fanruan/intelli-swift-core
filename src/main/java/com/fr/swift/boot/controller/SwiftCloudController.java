package com.fr.swift.boot.controller;

import com.fr.swift.SwiftContext;
import com.fr.swift.annotation.Inside;
import com.fr.swift.cloud.bean.TreasureBean;
import com.fr.swift.cloud.load.CloudVersionProperty;
import com.fr.swift.cloud.task.TreasureAnalysisTask;
import com.fr.swift.config.service.SwiftMetaDataService;
import com.fr.swift.executor.TaskProducer;
import com.fr.swift.executor.config.ExecutorTaskService;
import com.fr.swift.executor.task.ExecutorTask;
import com.fr.swift.executor.type.DBStatusType;
import com.fr.swift.source.SwiftMetaData;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.Query;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yee
 * @date 2019-02-25
 */
@RestController
public class SwiftCloudController {

    @ResponseBody
    @RequestMapping(value = "/cloud/metadata/update/{version}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public Map<String, Boolean> updateMetadata(@PathVariable("version") String version) throws Exception {
        Map<String, SwiftMetaData> metaDataMap = CloudVersionProperty.getProperty().getMetadataMapByVersion(version);
        Map<String, Boolean> resultMap = new HashMap<>();
        if (metaDataMap != null && !metaDataMap.isEmpty()) {
            SwiftMetaDataService metaDataService = SwiftContext.get().getBean(SwiftMetaDataService.class);
            for (Map.Entry<String, SwiftMetaData> entry : metaDataMap.entrySet()) {
                boolean updateResult = metaDataService.saveOrUpdate(entry.getValue());
                resultMap.put(entry.getValue().getTableName(), updateResult);
            }
        }
        return resultMap;
    }

    @ResponseBody
    @RequestMapping(value = "/cloud/retrigger/task/{taskId}", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public boolean reTriggerTask(@PathVariable("taskId") String taskId) throws Exception {
        ExecutorTaskService executorTaskService = SwiftContext.get().getBean(ExecutorTaskService.class);
        ExecutorTask executorTask = executorTaskService.getExecutorTask(taskId);
        if (executorTask == null) {
            throw new Exception(String.format("%s is not existed", taskId));
        }
        if (executorTask.getDbStatusType() == DBStatusType.ACTIVE) {
            throw new Exception(String.format("%s is ACTIVE", taskId));
        }
        executorTask.setDbStatusType(DBStatusType.ACTIVE);
        return TaskProducer.produceTask(executorTask);
    }

    @ResponseBody
    @RequestMapping(value = "/cloud/tpl/analyse", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @Inside
    public boolean reAnalyseTpl(@RequestBody Map<String, String> map) throws Exception {
        String appId = map.get("appId");
        String clientId = map.get("clientId");
        String yearMonth = map.get("yearMonth");
        TreasureBean treasureBean = new TreasureBean("__fine_intelli_treasure_upload__", null, null, clientId, appId, yearMonth, "2.0", System.currentTimeMillis(), "TreasureAnalyze");
        TreasureAnalysisTask treasureAnalysisTask = new TreasureAnalysisTask(treasureBean);
        treasureAnalysisTask.getJob().call();
        return true;
    }

    @ResponseBody
    @RequestMapping(value = "/cloud/query/sql", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @Inside
    public Object querySql(@RequestBody Map<String, String> map) throws Exception {
        String url = map.get("url");
        String sql = map.get("sql");
        Class.forName("com.fr.swift.jdbc.Driver");
        Connection connection = DriverManager.getConnection(url);
        Statement statement = connection.createStatement();
        try {
            ResultSet resultSet = statement.executeQuery(sql);
            int columnCount = resultSet.getMetaData().getColumnCount();
            List<Map<String, Object>> resultMap = new ArrayList<>();
            while (resultSet.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.put(resultSet.getMetaData().getColumnName(i), resultSet.getObject(i));
                }
                resultMap.add(row);
            }
            return resultMap;
        } finally {
            statement.close();
            connection.close();
        }
    }

    @Negative(until = "2019-06-30")
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

    @Negative(until = "2019-06-30")
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

    @Negative(until = "2019-06-30")
    private String sql(String tableName) {
        return "select 1 from " + tableName + " where clientId = :clientId and appId = :appId";
    }
}