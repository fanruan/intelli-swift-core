package com.fr.swift.adaptor.log;

import com.fr.intelli.record.MetricException;
import com.fr.intelli.record.scene.Metric;
import com.fr.intelli.record.scene.impl.BaseMetric;
import com.fr.stable.query.condition.QueryCondition;
import com.fr.stable.query.data.DataColumn;
import com.fr.stable.query.data.DataList;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.db.Database;
import com.fr.swift.db.SwiftDatabase;
import com.fr.swift.db.Table;
import com.fr.swift.db.Where;
import com.fr.swift.db.impl.AddColumnAction;
import com.fr.swift.db.impl.DropColumnAction;
import com.fr.swift.db.impl.MetadataDiffer;
import com.fr.swift.db.impl.SwiftWhere;
import com.fr.swift.event.ClusterEvent;
import com.fr.swift.event.ClusterEventListener;
import com.fr.swift.event.ClusterEventType;
import com.fr.swift.event.ClusterListenerHandler;
import com.fr.swift.event.global.DeleteEvent;
import com.fr.swift.jdbc.result.EmptyResultSet;
import com.fr.swift.jdbc.result.ResultSetWrapper;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.query.query.FilterBean;
import com.fr.swift.query.query.QueryBean;
import com.fr.swift.result.DetailResultSet;
import com.fr.swift.selector.ClusterSelector;
import com.fr.swift.service.AnalyseService;
import com.fr.swift.service.RealtimeService;
import com.fr.swift.service.cluster.ClusterAnalyseService;
import com.fr.swift.service.cluster.ClusterRealTimeService;
import com.fr.swift.service.listener.SwiftServiceListenerManager;
import com.fr.swift.source.Row;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.structure.Pair;
import com.fr.swift.util.JpaAdaptor;
import com.fr.swift.util.Util;
import com.fr.swift.util.concurrent.CommonExecutor;
import com.fr.swift.util.concurrent.PoolThreadFactory;
import com.fr.swift.util.concurrent.SwiftExecutors;
import com.fr.swift.utils.ClusterCommonUtils;

import java.sql.ResultSet;
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

    private RealtimeService realtimeService;
    private AnalyseService analyseService;
    private Sync sync;

    private MetricProxy() {
        realtimeService = SwiftContext.get().getBean("swiftRealtimeService", RealtimeService.class);
        analyseService = SwiftContext.get().getBean("swiftAnalyseService", AnalyseService.class);
        sync = new Sync();
        ClusterListenerHandler.addListener(new ClusterEventListener() {
            @Override
            public void handleEvent(ClusterEvent clusterEvent) {
                if (clusterEvent.getEventType() == ClusterEventType.JOIN_CLUSTER) {
                    realtimeService = SwiftContext.get().getBean(ClusterRealTimeService.class);
                    analyseService = SwiftContext.get().getBean(ClusterAnalyseService.class);
                } else if (clusterEvent.getEventType() == ClusterEventType.LEFT_CLUSTER) {
                    realtimeService = SwiftContext.get().getBean("swiftRealtimeService", RealtimeService.class);
                    analyseService = SwiftContext.get().getBean("swiftAnalyseService", AnalyseService.class);
                }
            }
        });
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
            SwiftResultSet resultSet = analyseService.getQueryResult(queryBean);
            List<Row> page = LogQueryUtils.getPage(resultSet, queryCondition);
            for (Row row : page) {
                tList.add(adaptor.apply(row));
            }
            dataList.list(tList);
            dataList.setTotalCount(((DetailResultSet) resultSet).getRowCount());
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
        }
        return dataList;
    }

    @Override
    public <T> ResultSet findWithMetaData(Class<T> aClass, QueryCondition queryCondition, List<DataColumn> list) throws MetricException {
        try {
            QueryBean queryBean = LogQueryUtils.query(aClass, queryCondition, list);
            SwiftResultSet resultSet = analyseService.getQueryResult(queryBean);
            return new ResultSetWrapper(resultSet);
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
        }
        return new ResultSetWrapper(EmptyResultSet.INSTANCE);
    }

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
                    ClusterCommonUtils.asyncCallMaster(event);
                } else {
                    SwiftServiceListenerManager.getInstance().triggerEvent(event);
                }
            }
        }
    }

    private static final Metric INSTANCE = new MetricProxy();

    public static Metric getInstance() {
        return INSTANCE;
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
                    realtimeService.insert(table.getSourceKey(), new LogRowSet(table.getMetadata(), data, entity));
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
}