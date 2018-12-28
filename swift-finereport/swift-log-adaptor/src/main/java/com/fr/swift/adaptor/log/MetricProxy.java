package com.fr.swift.adaptor.log;

import com.fr.intelli.record.scene.impl.BaseMetric;
import com.fr.stable.query.condition.QueryCondition;
import com.fr.stable.query.data.DataList;
import com.fr.swift.basics.base.selector.ProxySelector;
import com.fr.swift.db.Database;
import com.fr.swift.db.SwiftDatabase;
import com.fr.swift.db.Table;
import com.fr.swift.db.Where;
import com.fr.swift.db.impl.AddColumnAction;
import com.fr.swift.db.impl.DropColumnAction;
import com.fr.swift.db.impl.MetadataDiffer;
import com.fr.swift.db.impl.SwiftWhere;
import com.fr.swift.event.global.DeleteEvent;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.query.info.bean.query.QueryBeanFactory;
import com.fr.swift.query.query.FilterBean;
import com.fr.swift.query.query.QueryBean;
import com.fr.swift.query.result.SwiftResultSetUtils;
import com.fr.swift.result.DetailQueryResultSet;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.result.qrs.QueryResultSet;
import com.fr.swift.selector.ClusterSelector;
import com.fr.swift.service.AnalyseService;
import com.fr.swift.service.RealtimeService;
import com.fr.swift.service.listener.RemoteSender;
import com.fr.swift.service.listener.SwiftServiceListenerManager;
import com.fr.swift.source.Row;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.structure.Pair;
import com.fr.swift.util.JpaAdaptor;
import com.fr.swift.util.Util;
import com.fr.swift.util.concurrent.CommonExecutor;
import com.fr.swift.util.concurrent.PoolThreadFactory;
import com.fr.swift.util.concurrent.SwiftExecutors;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * This class created on 2018/5/10
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class MetricProxy extends BaseMetric {

    //    private RealtimeService realtimeService;
//    private AnalyseService analyseService;
    private Sync sync;

    private MetricProxy() {
//        realtimeService = ProxySelector.getInstance().getFactory().getProxy(RealtimeService.class);
//        analyseService = ProxySelector.getInstance().getFactory().getProxy(AnalyseService.class);
        sync = new Sync();
    }

    private final Database db = com.fr.swift.db.impl.SwiftDatabase.getInstance();

    @Override
    public <T> DataList<T> find(Class<T> entity, QueryCondition queryCondition) {
        DataList<T> dataList = new DataList<T>();
        try {
            Table table = db.getTable(new SourceKey(JpaAdaptor.getTableName(entity)));
            DecisionRowAdaptor<T> adaptor = new DecisionRowAdaptor<T>(entity, table.getMeta());
            List<T> tList = new ArrayList<T>();

            QueryBean queryBean = LogQueryUtils.getDetailQueryBean(entity, queryCondition);
            // TODO: 2018/11/28  QueryResultSet to SwiftResultSet
            QueryResultSet queryResultSet = ProxySelector.getProxy(AnalyseService.class).getQueryResult(QueryBeanFactory.queryBean2String(queryBean));
            SwiftResultSet resultSet = SwiftResultSetUtils.toSwiftResultSet(queryResultSet, queryBean);
            List<Row> page = LogQueryUtils.getPage(resultSet, queryCondition);
            for (Row row : page) {
                tList.add(adaptor.apply(row));
            }
            dataList.list(tList);
            dataList.setTotalCount(((DetailQueryResultSet) queryResultSet).getRowCount());
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
        }
        return dataList;
    }

    @Override
    public <T> DataList<List<T>> find(String s) {
        return null;
    }

//    @Override
//    public <T> ResultSet findWithMetaData(Class<T> aClass, QueryCondition queryCondition, List<DataColumn> list) throws MetricException {
//        QueryBean queryBean = LogQueryUtils.groupQuery(aClass, queryCondition, list);
//        ResultSet ret = null;
//        try {
//            SwiftResultSet resultSet = analyseService.getQueryResult(queryBean);
//            ret = new ResultSetWrapper(resultSet);
//        } catch (Exception e) {
//            SwiftLoggers.getLogger().error(e);
//        }
//        return ret;
//    }

    @Override
    public void submit(Object o) {
        if (o == null) {
            return;
        }
        sync.stage(Collections.singletonList(o));
    }

    @Override
    public void submit(List<Object> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        sync.stage(list);
    }

    @Override
    public void pretreatment(List<Class> list) throws Exception {
        for (Class table : list) {
            initTable(table);
        }
    }

    private void initTable(Class table) throws SQLException {
        SwiftMetaData meta = JpaAdaptor.adapt(table, SwiftDatabase.DECISION_LOG);
        final SourceKey tableKey = new SourceKey(meta.getTableName());
        synchronized (db) {
            if (!db.existsTable(tableKey)) {
                db.createTable(tableKey, meta);
                return;
            }

            final MetadataDiffer differ = new MetadataDiffer(db.getTable(tableKey).getMetadata(), meta);
            if (!differ.hasDiff()) {
                return;
            }

            CommonExecutor.get().execute(new Runnable() {
                @Override
                public void run() {
                    for (SwiftMetaDataColumn columnMeta : differ.getAdded()) {
                        try {
                            com.fr.swift.db.impl.SwiftDatabase.getInstance().alterTable(tableKey, new AddColumnAction(columnMeta));
                        } catch (SQLException e) {
                            SwiftLoggers.getLogger().warn("add column {} failed: {}", columnMeta, Util.getRootCauseMessage(e));
                        }
                    }
                    for (SwiftMetaDataColumn columnMeta : differ.getDropped()) {
                        try {
                            com.fr.swift.db.impl.SwiftDatabase.getInstance().alterTable(tableKey, new DropColumnAction(columnMeta));
                        } catch (SQLException e) {
                            SwiftLoggers.getLogger().warn("drop column {} failed: {}", columnMeta, Util.getRootCauseMessage(e));
                        }
                    }
                }
            });
        }
    }

    @Override
    public void clean(QueryCondition condition) throws Exception {
        List<Table> tables = com.fr.swift.db.impl.SwiftDatabase.getInstance().getAllTables();
        FilterBean filterBean = QueryConditionAdaptor.restriction2FilterInfo(condition.getRestriction());
        for (Table table : tables) {
            if (table.getMeta().getSwiftDatabase() == SwiftDatabase.DECISION_LOG) {
                DeleteEvent event = new DeleteEvent(Pair.<SourceKey, Where>of(table.getSourceKey(), new SwiftWhere(filterBean)));
                if (ClusterSelector.getInstance().getFactory().isCluster()) {
                    ProxySelector.getInstance().getFactory().getProxy(RemoteSender.class).trigger(event);
                } else {
                    SwiftServiceListenerManager.getInstance().triggerEvent(event);
                }
            }
        }
    }

    class Sync implements Runnable {

        static final int FLUSH_SIZE_THRESHOLD = 10000;

        private ScheduledExecutorService scheduler = SwiftExecutors.newSingleThreadScheduledExecutor(new PoolThreadFactory(getClass()));

        private ConcurrentMap<Class<?>, List<Object>> dataMap = new ConcurrentHashMap<Class<?>, List<Object>>();

        private BufferedInsert bufferedInsert = new BufferedInsert();

        Sync() {
            scheduler.scheduleWithFixedDelay(this, 5, 5, TimeUnit.SECONDS);
        }

        @Override
        public void run() {
            for (Class<?> entity : dataMap.keySet()) {
                try {
                    record(entity);
                } catch (Exception e) {
                    SwiftLoggers.getLogger().error(e);
                }
            }
        }

        private synchronized void record(Class<?> entity) {
            List<Object> data = dataMap.remove(entity);
            if (data == null || data.isEmpty()) {
                return;
            }

            bufferedInsert.submit(entity, data);
        }

        private synchronized void stage(List<Object> data) {
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

    class BufferedInsert implements Runnable {

        private ExecutorService exec = SwiftExecutors.newSingleThreadExecutor(new PoolThreadFactory(getClass()));

        private BlockingQueue<List<Object>> dataQueue = new ArrayBlockingQueue<List<Object>>(1000);

        BufferedInsert() {
            exec.execute(this);
        }

        @Override
        public void run() {
            while (true) {
                try {
                    List<Object> data = dataQueue.take();

                    Object first = data.get(0);
                    Class<?> entity = first.getClass();
                    Table table = db.getTable(new SourceKey(JpaAdaptor.getTableName(entity)));
                    ProxySelector.getProxy(RealtimeService.class).insert(table.getSourceKey(), new LogRowSet(table.getMetadata(), data, entity));
                } catch (Exception e) {
                    SwiftLoggers.getLogger().error(e);
                }
            }
        }

        void submit(Class<?> entity, List<Object> data) {
            if (!dataQueue.offer(data)) {
                SwiftLoggers.getLogger().error("swift rejected {} {}", data.size(), entity.getSimpleName());
            }
        }
    }

    private static final MetricProxy INSTANCE = new MetricProxy();

    public static MetricProxy getInstance() {
        return INSTANCE;
    }
}