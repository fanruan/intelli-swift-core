package com.fr.swift.generate;

import com.fr.config.DBEnv;
import com.fr.config.dao.DaoContext;
import com.fr.config.dao.impl.HibernateClassHelperDao;
import com.fr.config.dao.impl.HibernateEntityDao;
import com.fr.config.dao.impl.HibernateXmlEnityDao;
import com.fr.config.entity.ClassHelper;
import com.fr.config.entity.Entity;
import com.fr.config.entity.XmlEntity;
import com.fr.general.ComparatorUtils;
import com.fr.stable.db.DBContext;
import com.fr.stable.db.option.DBOption;
import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.cube.queue.CubeTasks;
import com.fr.swift.cube.task.SchedulerTask;
import com.fr.swift.cube.task.Task;
import com.fr.swift.cube.task.Task.Result;
import com.fr.swift.cube.task.TaskKey;
import com.fr.swift.cube.task.WorkerTask;
import com.fr.swift.cube.task.impl.BaseWorker;
import com.fr.swift.cube.task.impl.CubeTaskManager;
import com.fr.swift.cube.task.impl.Operation;
import com.fr.swift.cube.task.impl.SchedulerTaskPool;
import com.fr.swift.cube.task.impl.WorkerTaskImpl;
import com.fr.swift.cube.task.impl.WorkerTaskPool;
import com.fr.swift.generate.history.index.MultiRelationIndexer;
import com.fr.swift.generate.history.TableBuilder;
import com.fr.swift.manager.LocalSegmentProvider;
import com.fr.swift.relation.CubeMultiRelation;
import com.fr.swift.relation.utils.MultiRelationHelper;
import com.fr.swift.segment.HistorySegmentImpl;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.DetailColumn;
import com.fr.swift.segment.relation.RelationIndex;
import com.fr.swift.service.LocalSwiftServerService;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.RelationSource;
import com.fr.swift.source.db.TableDBSource;
import com.fr.swift.source.db.TestConnectionProvider;
import com.fr.swift.source.relation.RelationSourceImpl;
import com.fr.swift.structure.Pair;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author yee
 * @date 2018/2/5
 */
public class MultiRelationIndexerTest extends TestCase {

    CountDownLatch latch = new CountDownLatch(1);
    DataSource dataSource = null;
    DataSource contract = null;

    @Override
    protected void setUp() throws Exception {
        new LocalSwiftServerService().start();
        TestConnectionProvider.createConnection();
        SchedulerTaskPool.getInstance().initListener();
        WorkerTaskPool.getInstance().initListener();
        WorkerTaskPool.getInstance().setGenerator(pair -> {
            TaskKey taskKey = pair.getKey();
            if (taskKey.operation() == Operation.NULL) {
                WorkerTask wt = new WorkerTaskImpl(taskKey);
                wt.setWorker(BaseWorker.nullWorker());
                return wt;
            }

            Object o = pair.getValue();
            if (o instanceof DataSource) {
                DataSource ds = ((DataSource) o);
                WorkerTask wt = new WorkerTaskImpl(taskKey);
                wt.setWorker(new TableBuilder(ds));
                return wt;
            } else {
                return null;
            }
        });
        CubeTaskManager.getInstance().initListener();
        dataSource = new TableDBSource("DEMO_CONTRACT", "allTest");
        contract = new TableDBSource("DEMO_CAPITAL_RETURN", "allTest");
        initConfigDB();
    }

    private void initConfigDB() throws Exception {
        DBOption dbOption = new DBOption();
        dbOption.setPassword("");
        dbOption.setDialectClass("com.fr.third.org.hibernate.dialect.H2Dialect");
        dbOption.setDriverClass("org.h2.Driver");
        dbOption.setUsername("sa");
        dbOption.setUrl("jdbc:h2:~/config");
        dbOption.addRawProperty("hibernate.show_sql", false)
                .addRawProperty("hibernate.format_sql", true).addRawProperty("hibernate.connection.autocommit", true);
        DBContext dbProvider = DBContext.create();
        dbProvider.addEntityClass(Entity.class);
        dbProvider.addEntityClass(XmlEntity.class);
        dbProvider.addEntityClass(ClassHelper.class);
        dbProvider.init(dbOption);
        DBEnv.setDBContext(dbProvider);
        DaoContext.setClassHelperDao(new HibernateClassHelperDao());
        DaoContext.setXmlEntityDao(new HibernateXmlEnityDao());
        DaoContext.setEntityDao(new HibernateEntityDao());
    }

    public void buildMultiRelationIndex() throws Exception {


        List<Pair<TaskKey, Object>> l = new ArrayList<>();

        SchedulerTask start = CubeTasks.newStartTask(),
                end = CubeTasks.newEndTask();
        l.add(new Pair<>(start.key(), null));
        l.add(new Pair<>(end.key(), null));


        List<DataSource> dataSources = new ArrayList<DataSource>();
        dataSources.add(dataSource);
        dataSources.add(contract);


        for (DataSource updateDataSource : dataSources) {
            SchedulerTask task = CubeTasks.newTableTask(updateDataSource);
            start.addNext(task);
            task.addNext(end);
            l.add(new Pair<>(task.key(), updateDataSource));
        }
        end.addStatusChangeListener((prev, now) -> {
            if (now == Task.Status.DONE) {
                latch.countDown();
            }
        });
        CubeTasks.sendTasks(l);
        start.triggerRun();
        latch.await();
        if (end.result() != Result.SUCCEEDED) {
            fail();
        }

        MultiRelationIndexer indexer = new MultiRelationIndexer(MultiRelationHelper.convert2CubeRelation(createRelation()), LocalSegmentProvider.getInstance());
        SchedulerTask relationTask = CubeTasks.newRelationTask(createRelation());
        WorkerTask task = new WorkerTaskImpl(relationTask.key());
        task.setWorker(indexer);
        task.addStatusChangeListener((prev, now) -> {
            if (now == Task.Status.DONE) {
                latch.countDown();
            }
        });
        task.run();
        latch.await();

    }

    private RelationSource createRelation() {
        List<String> primaryFields = new ArrayList<>();
        List<String> foreignFields = new ArrayList<>();
        primaryFields.add("合同ID");
        primaryFields.add("总金额");
        foreignFields.add("合同ID");
        foreignFields.add("付款金额");
        return new RelationSourceImpl(dataSource.getSourceKey(), contract.getSourceKey(), primaryFields, foreignFields);
    }

    /**
     * 全量更新取数索引
     *
     * @throws Exception
     */
    public void testTransport() throws Exception {
        buildMultiRelationIndex();
        List<Segment> segmentList = LocalSegmentProvider.getInstance().getSegment(dataSource.getSourceKey());
        List<Segment> foreignList = LocalSegmentProvider.getInstance().getSegment(contract.getSourceKey());
        assertEquals(segmentList.size(), 1);
        assertEquals(foreignList.size(), 1);
        Segment segment = segmentList.get(0);
        Segment foreign = foreignList.get(0);
        assertTrue(segment instanceof HistorySegmentImpl);
        assertTrue(foreign instanceof HistorySegmentImpl);

        CubeMultiRelation relation = MultiRelationHelper.convert2CubeRelation(createRelation());
        RelationIndex index = foreign.getRelation(relation);
        int primaryRow = segment.getRowCount();
        int foreignRow = foreign.getRowCount();
        ImmutableBitMap nullIndex = index.getNullIndex();
        List<ColumnKey> primaryKeys = relation.getPrimaryField().getKeyFields();
        List<ColumnKey> foreignKeys = relation.getForeignField().getKeyFields();
        DetailColumn primary1 = segment.getColumn(primaryKeys.get(0)).getDetailColumn();
        DetailColumn primary2 = segment.getColumn(primaryKeys.get(1)).getDetailColumn();
        DetailColumn foreign1 = foreign.getColumn(foreignKeys.get(0)).getDetailColumn();
        DetailColumn foreign2 = foreign.getColumn(foreignKeys.get(1)).getDetailColumn();
        for (int i = 0; i < primaryRow; i++) {
            Object p1 = primary1.get(i);
            Object p2 = primary2.get(i);
            ImmutableBitMap targetIndex = index.getIndex(i + 1);
            for (int j = 0; j < foreignRow; j++) {
                Object f1 = foreign1.get(j);
                Object f2 = foreign2.get(j);
                if (ComparatorUtils.equals(p1, f1) && ComparatorUtils.equals(p2, f2)) {
                    assertTrue(targetIndex.contains(j));
                    assertFalse(nullIndex.contains(j));
                }
            }
        }
    }

}