package com.fr.swift.adaptor.log;

import com.fr.general.DataList;
import com.fr.general.LogOperator;
import com.fr.stable.query.condition.QueryCondition;
import com.fr.swift.db.Database;
import com.fr.swift.db.Table;
import com.fr.swift.db.impl.SwiftDatabase;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.query.info.QueryInfo;
import com.fr.swift.service.QueryRunnerProvider;
import com.fr.swift.source.Row;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.util.concurrent.PoolThreadFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author anchore
 * @date 2018/4/26
 */
public class SwiftLogOperator implements LogOperator {
    private static final int USE_IMPORT_THRESHOLD = 100000;

    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(SwiftLogOperator.class);

    private final Database db = SwiftDatabase.getInstance();

    private Sync sync = new Sync();

    @Override
    public <T> DataList<T> find(Class<T> entity, QueryCondition queryCondition) {
        DataList<T> dataList = new DataList<T>();
        try {
            Table table = db.getTable(new SourceKey(SwiftMetaAdaptor.getTableName(entity)));
            DecisionRowAdaptor<T> adaptor = new DecisionRowAdaptor<T>(entity, table.getMeta());
            QueryInfo queryInfo = QueryConditionAdaptor.adaptCondition(queryCondition, table);
            SwiftResultSet resultSet = QueryRunnerProvider.getInstance().executeQuery(queryInfo);

            List<T> tList = new ArrayList<T>();
            while (resultSet.next()) {
                Row row = resultSet.getRowData();
                T t = adaptor.apply(row);
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
    public void recordInfo(Object o) {
        if (o == null) {
            return;
        }
        sync.stage(Collections.singletonList(o));
    }

    @Override
    public void recordInfo(List<Object> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        sync.stage(list);
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

    class Sync implements Runnable {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1, new PoolThreadFactory(getClass()));

        private Map<Class<?>, List<Object>> dataMap = new ConcurrentHashMap<Class<?>, List<Object>>();

        public static final int FLUSH_SIZE_THRESHOLD = 10000;

        Sync() {
            scheduler.scheduleAtFixedRate(this, 0, 30, TimeUnit.SECONDS);
        }

        @Override
        public void run() {
            try {
                for (Class<?> entity : dataMap.keySet()) {
                    record(entity);
                }
            } catch (Exception e) {
                SwiftLoggers.getLogger().error(e);
            }
        }

        synchronized
        private void record(Class<?> entity) {
            List<Object> data = dataMap.get(entity);
            if (data == null || data.isEmpty()) {
                return;
            }

            dataMap.remove(entity);
            try {
                Table table = db.getTable(new SourceKey(SwiftMetaAdaptor.getTableName(entity)));
                SwiftResultSet rowSet = new LogRowSet(table.getMeta(), data, entity);
                table.insert(rowSet);
            } catch (Exception e) {
                SwiftLoggers.getLogger().error(e);
            }
        }

        synchronized
        private void stage(List<Object> data) {
            Object first = data.get(0);
            Class<?> entity = first.getClass();
            if (!dataMap.containsKey(entity)) {
                dataMap.put(entity, new ArrayList<Object>());
            }
            List<Object> curData = dataMap.get(entity);
            curData.addAll(data);

            if (curData.size() > FLUSH_SIZE_THRESHOLD) {
                record(entity);
            }
        }
    }
}