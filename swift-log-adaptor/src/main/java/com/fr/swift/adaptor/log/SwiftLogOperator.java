package com.fr.swift.adaptor.log;

import com.fr.general.DataList;
import com.fr.general.LogOperator;
import com.fr.stable.query.condition.QueryCondition;
import com.fr.swift.cal.QueryInfo;
import com.fr.swift.db.Database;
import com.fr.swift.db.Table;
import com.fr.swift.db.impl.SwiftDatabase;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.service.QueryRunnerProvider;
import com.fr.swift.source.Row;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftResultSet;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author anchore
 * @date 2018/4/26
 */
public class SwiftLogOperator implements LogOperator {
    private static final int USE_IMPORT_THRESHOLD = 100000;

    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(SwiftLogOperator.class);

    private final Database db = SwiftDatabase.getInstance();

    private Map<Class<?>, List<Object>> dataMap = new ConcurrentHashMap<Class<?>, List<Object>>();

    public static final int FLUSH_INTERVAL_THRESHOLD = 60000;

    public static final int FLUSH_SIZE_THRESHOLD = 10000;

    private long lastFlushTime = System.currentTimeMillis();

    @Override
    public <T> DataList<T> find(Class<T> entity, QueryCondition queryCondition) {
        DataList<T> dataList = new DataList<T>();
        try {
            Table table = db.getTable(new SourceKey(SwiftMetaAdaptor.getTableName(entity)));
            QueryInfo queryInfo = QueryConditionAdaptor.adaptCondition(queryCondition, table);
            SwiftResultSet resultSet = QueryRunnerProvider.getInstance().executeQuery(queryInfo);

            List<T> tList = new ArrayList<T>();
            while (resultSet.next()) {
                Row row = resultSet.getRowData();
                DecisionRowAdaptor adaptor = new DecisionRowAdaptor(entity, table.getMeta());
                T t = (T) adaptor.apply(row);
                tList.add(t);
            }
            dataList.list(tList);
            dataList.setTotalCount(tList.size());
        } catch (Exception e) {
            LOGGER.error(e);
        }
        return dataList;
    }

    @Override
    public <T> DataList<T> find(Class<T> entity, QueryCondition queryCondition, String s) {
        return find(entity, queryCondition);
    }

    @Override
    public void recordInfo(Object o) throws Exception {
        if (o == null) {
            return;
        }
        record(Collections.singletonList(o));
    }

    private synchronized void record(List<Object> data) throws Exception {
        Object first = data.get(0);
        Class<?> entity = first.getClass();
        if (!dataMap.containsKey(entity)) {
            dataMap.put(entity, new ArrayList<Object>());
        }
        List<Object> curData = dataMap.get(entity);
        curData.addAll(data);

        if (curData.size() < FLUSH_SIZE_THRESHOLD && System.currentTimeMillis() - lastFlushTime < FLUSH_INTERVAL_THRESHOLD) {
            return;
        }

        Table table = db.getTable(new SourceKey(SwiftMetaAdaptor.getTableName(entity)));
        SwiftResultSet rowSet = new LogRowSet(table.getMeta(), curData, entity);

//        if (curData.size() < USE_IMPORT_THRESHOLD) {
        table.insert(rowSet);
//        } else {
//            table.importFrom(rowSet);
//        }

        curData.clear();
        lastFlushTime = System.currentTimeMillis();
    }

    @Override
    public void recordInfo(List<Object> list) throws Exception {
        if (list == null || list.isEmpty()) {
            return;
        }

        record(list);
    }

    @Override
    public void initTables(List<Class> list) throws SQLException {
        for (Class table : list) {
            SwiftMetaData meta = SwiftMetaAdaptor.adapt(table);
            SourceKey tableKey = new SourceKey(meta.getTableName());
            synchronized (db) {
                if (!db.existsTable(tableKey)) {
                    db.createTable(tableKey, meta);
                }
            }
        }
    }
}