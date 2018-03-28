package com.fr.swift.generate.increment;

import com.fr.annotation.Test;
import com.fr.swift.flow.FlowRuleController;
import com.fr.swift.generate.BaseTest;
import com.fr.swift.generate.history.index.ColumnIndexer;
import com.fr.swift.generate.history.transport.TableTransporter;
import com.fr.swift.generate.realtime.RealtimeColumnIndexer;
import com.fr.swift.generate.realtime.RealtimeDataTransporter;
import com.fr.swift.increase.IncrementImpl;
import com.fr.swift.increment.Increment;
import com.fr.swift.manager.LocalSegmentProvider;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.db.QueryDBSource;

import java.util.List;

/**
 * This class created on 2018/3/20
 *
 * @author Lucifer
 * @description
 * todo 合并还要重做下。。
 * @since Advanced FineBI Analysis 1.0
 */
public class RealtimeMergeTest extends BaseTest {

    @Override
    public void setUp() throws Exception {
        super.setUp();
//        DBOption dbOption = new DBOption();
//        dbOption.setPassword("root123");
//        dbOption.setDialectClass("com.fr.third.org.hibernate.dialect.MySQL5InnoDBDialect");
//        dbOption.setDriverClass("com.mysql.jdbc.Driver");
//        dbOption.setUsername("root");
//        dbOption.setUrl("jdbc:mysql://localhost:3306/lucifer");
//        dbOption.addRawProperty("hibernate.show_sql", true)
//                .addRawProperty("hibernate.format_sql", true).addRawProperty("hibernate.connection.autocommit", true);
//        DBContext dbProvider = DBContext.create();
//        dbProvider.addEntityClass(Entity.class);
//        dbProvider.addEntityClass(XmlEntity.class);
//        dbProvider.addEntityClass(ClassHelper.class);
//        dbProvider.init(dbOption);
//        DBEnv.setDBContext(dbProvider);
//        DaoContext.setClassHelperDao(new HibernateClassHelperDao());
//        DaoContext.setXmlEntityDao(new HibernateXmlEnityDao());
//        DaoContext.setEntityDao(new HibernateEntityDao());
    }

    @Test
    public void testMergeTransport() {
        try {
            DataSource dataSource1 = new QueryDBSource("select * from DEMO_CAPITAL_RETURN where 记录人 in ('杨文蔚','洪清','王可')", "IncreaseTest");
            DataSource dataSource2 = new QueryDBSource("select * from DEMO_CAPITAL_RETURN where 记录人 in ('庆芳')", "IncreaseTest");
            DataSource dataSource3 = new QueryDBSource("select * from DEMO_CAPITAL_RETURN where 记录人 in ('徐佳')", "IncreaseTest");
            DataSource dataSource4 = new QueryDBSource("select * from DEMO_CAPITAL_RETURN where 记录人 in ('徐弘')", "IncreaseTest");

            TableTransporter tableTransporter = new TableTransporter(dataSource1);
            tableTransporter.transport();
            for (int i = 1; i <= dataSource1.getMetadata().getColumnCount(); i++) {
                ColumnIndexer columnIndexer = new ColumnIndexer(dataSource1, new ColumnKey(dataSource1.getMetadata().getColumnName(i)));
                columnIndexer.work();
            }
            List<Segment> segmentList = LocalSegmentProvider.getInstance().getSegment(dataSource1.getSourceKey());
            assertEquals(segmentList.size(), 1);

            Increment increment1 = new IncrementImpl("select * from DEMO_CAPITAL_RETURN where 记录人 in ('庆芳')", null, null, dataSource1.getSourceKey(), "IncreaseTest");
            RealtimeDataTransporter transport1 = new RealtimeDataTransporter(dataSource1, increment1, new FlowRuleController());
            transport1.work();
            for (int i = 1; i <= dataSource1.getMetadata().getColumnCount(); i++) {
                RealtimeColumnIndexer<?> indexer = new RealtimeColumnIndexer(dataSource1, new ColumnKey(dataSource1.getMetadata().getColumnName(i)));
                indexer.work();
            }
            int count1 = LocalSegmentProvider.getInstance().getSegment(dataSource1.getSourceKey()).get(1).getRowCount();
            assertEquals(LocalSegmentProvider.getInstance().getSegment(dataSource1.getSourceKey()).size(), 2);


            Increment increment2 = new IncrementImpl("select * from DEMO_CAPITAL_RETURN where 记录人 in ('徐佳')", null, null, dataSource1.getSourceKey(), "IncreaseTest");
            RealtimeDataTransporter transport2 = new RealtimeDataTransporter(dataSource1, increment2, new FlowRuleController());
            transport2.work();
            for (int i = 1; i <= dataSource1.getMetadata().getColumnCount(); i++) {
                RealtimeColumnIndexer<?> indexer = new RealtimeColumnIndexer(dataSource1, new ColumnKey(dataSource1.getMetadata().getColumnName(i)));
                indexer.work();
            }
            int count2 = LocalSegmentProvider.getInstance().getSegment(dataSource1.getSourceKey()).get(2).getRowCount();
            assertEquals(LocalSegmentProvider.getInstance().getSegment(dataSource1.getSourceKey()).size(), 3);


            Increment increment3 = new IncrementImpl("select * from DEMO_CAPITAL_RETURN where 记录人 in ('徐弘')", null, null, dataSource1.getSourceKey(), "IncreaseTest");
            RealtimeDataTransporter transport3 = new RealtimeDataTransporter(dataSource1, increment3, new FlowRuleController());
            transport3.work();
            for (int i = 1; i <= dataSource1.getMetadata().getColumnCount(); i++) {
                RealtimeColumnIndexer<?> indexer = new RealtimeColumnIndexer(dataSource1, new ColumnKey(dataSource1.getMetadata().getColumnName(i)));
                indexer.work();
            }
            int count3 = LocalSegmentProvider.getInstance().getSegment(dataSource1.getSourceKey()).get(3).getRowCount();
            assertEquals(LocalSegmentProvider.getInstance().getSegment(dataSource1.getSourceKey()).size(), 4);

            //合并块
//            MergeSegmentOperator mergeSegmentOperator = new MergeSegmentOperator(dataSource1.getSourceKey(), dataSource1.getMetadata(),
//                    DataSourceUtils.getSwiftSourceKey(dataSource1));
//            mergeSegmentOperator.transport();
//            mergeSegmentOperator.finishTransport();
//            for (int i = 1; i <= dataSource1.getMetadata().getColumnCount(); i++) {
//                ColumnIndexer columnIndexer = new ColumnIndexer(dataSource1, new ColumnKey(dataSource1.getMetadata().getColumnName(i)));
//                columnIndexer.work();
//            }

            segmentList = LocalSegmentProvider.getInstance().getSegment(dataSource1.getSourceKey());

            assertEquals(segmentList.size(), 2);
            assertEquals(segmentList.get(1).getRowCount(), count1 + count2 + count3);
            for (int i = 0; i < count1 + count2 + count3; i++) {
               assertNotNull(segmentList.get(1).getColumn(new ColumnKey("付款时间")).getDetailColumn().get(i));
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            assertTrue(false);
        }
    }
}
