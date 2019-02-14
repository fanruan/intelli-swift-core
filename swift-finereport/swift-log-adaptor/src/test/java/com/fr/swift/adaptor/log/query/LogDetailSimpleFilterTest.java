//package com.fr.swift.adaptor.log.query;
//
//import com.fr.general.ComparatorUtils;
//import com.fr.stable.query.QueryFactory;
//import com.fr.stable.query.condition.QueryCondition;
//import com.fr.stable.query.restriction.RestrictionFactory;
//import com.fr.swift.adaptor.log.QueryConditionAdaptor;
//import com.fr.swift.basics.base.selector.ProxySelector;
//import com.fr.swift.db.Database;
//import com.fr.swift.db.Table;
//import com.fr.swift.db.impl.SwiftDatabase;
//import com.fr.swift.query.info.bean.query.QueryBeanFactory;
//import com.fr.swift.query.info.bean.query.QueryInfoBean;
//import com.fr.swift.query.query.QueryBean;
//import com.fr.swift.service.AnalyseService;
//import com.fr.swift.source.DataSource;
//import com.fr.swift.source.Row;
//import com.fr.swift.source.SourceKey;
//import com.fr.swift.result.SwiftResultSet;
//import com.fr.swift.source.db.QueryDBSource;
//import org.junit.Before;
//import org.junit.Test;
//
//import java.util.HashSet;
//import java.util.Set;
//
//import static junit.framework.TestCase.assertEquals;
//import static junit.framework.TestCase.assertNotSame;
//import static junit.framework.TestCase.assertTrue;
//
///**
// * This class created on 2018/4/27
// *
// * @author Lucifer
// * @description
// * @since Advanced FineBI 5.0
// */
//public class LogDetailSimpleFilterTest extends LogBaseTest {
//
//    private final Database db = SwiftDatabase.getInstance();
//    private AnalyseService service;
//
//    @Override
//    @Before
//    public void setUp() throws Exception {
//        service = ProxySelector.getInstance().getFactory().getProxy(AnalyseService.class);
//    }
//
//    @Test
//    public void testEQ() {
//        try {
//            DataSource dataSource = new QueryDBSource("select * from DEMO_CONTRACT", "DBtestEQ");
//            if (!db.existsTable(new SourceKey("testEQ"))) {
//                db.createTable(new SourceKey("testEQ"), dataSource.getMetadata());
//            }
//            Table table = db.getTable(new SourceKey("testEQ"));
//            transportAndIndex(dataSource, table);
//            //eq
//            QueryCondition eqQueryCondition = QueryFactory.create().addRestriction(RestrictionFactory.eq("合同类型", "购买合同"));
//            QueryBean queryBean = QueryConditionAdaptor.adaptCondition(eqQueryCondition, table);
//            SwiftResultSet eqResultSet = service.getQueryResult(QueryBeanFactory.queryBean2String(queryBean));
//            int eqindex = table.getMeta().getColumnIndex("合同类型") - 1;
//            while (eqResultSet.hasNext()) {
//                Row row = eqResultSet.getNextRow();
//                assertEquals(row.getValue(eqindex), "购买合同");
//            }
//
//        } catch (Exception e) {
//            LOGGER.error(e);
//            assertTrue(false);
//        }
//    }
//
//    @Test
//    public void testNEQ() {
//        try {
//            DataSource dataSource = new QueryDBSource("select * from DEMO_CONTRACT", "DBtestNEQ");
//            if (!db.existsTable(new SourceKey("testNEQ"))) {
//                db.createTable(new SourceKey("testNEQ"), dataSource.getMetadata());
//            }
//            Table table = db.getTable(new SourceKey("testNEQ"));
//
//            transportAndIndex(dataSource, table);
//            QueryCondition neqQueryCondition = QueryFactory.create().addRestriction(RestrictionFactory.neq("合同类型", "购买合同"));
//            QueryBean queryBean = QueryConditionAdaptor.adaptCondition(neqQueryCondition, table);
//            SwiftResultSet neqResultSet = service.getQueryResult(QueryBeanFactory.queryBean2String(queryBean));
//            int neqindex = table.getMeta().getColumnIndex("合同类型") - 1;
//            while (neqResultSet.hasNext()) {
//                Row row = neqResultSet.getNextRow();
//                assertNotSame(row.getValue(neqindex), "购买合同");
//            }
//        } catch (Exception e) {
//            LOGGER.error(e);
//            assertTrue(false);
//        }
//    }
//
//    @Test
//    public void testGT() {
//        try {
//            DataSource dataSource = new QueryDBSource("select * from DEMO_CONTRACT", "DBtestGT");
//            if (!db.existsTable(new SourceKey("testGT"))) {
//                db.createTable(new SourceKey("testGT"), dataSource.getMetadata());
//            }
//            Table table = db.getTable(new SourceKey("testGT"));
//            transportAndIndex(dataSource, table);
//
//            QueryCondition gtQueryCondition = QueryFactory.create().addRestriction(RestrictionFactory.gt("总金额", 1000000));
//            QueryBean queryBean = QueryConditionAdaptor.adaptCondition(gtQueryCondition, table);
//            SwiftResultSet gtResultSet = service.getQueryResult(QueryBeanFactory.queryBean2String(queryBean));
//            int gtindex = table.getMeta().getColumnIndex("总金额") - 1;
//            int count = 0;
//            while (gtResultSet.hasNext()) {
//                Row row = gtResultSet.getNextRow();
//                assertTrue(((Long) row.getValue(gtindex)).doubleValue() > 1000000);
//                if (((Long) row.getValue(gtindex)).doubleValue() == 1000000) {
//                    count++;
//                }
//            }
//            assertTrue(count == 0);
//
//        } catch (Exception e) {
//            LOGGER.error(e);
//            assertTrue(false);
//        }
//    }
//
//    @Test
//    public void testGTE() {
//        try {
//            DataSource dataSource = new QueryDBSource("select * from DEMO_CONTRACT", "DBtestGTE");
//            if (!db.existsTable(new SourceKey("testGTE"))) {
//                db.createTable(new SourceKey("testGTE"), dataSource.getMetadata());
//            }
//            Table table = db.getTable(new SourceKey("testGTE"));
//            transportAndIndex(dataSource, table);
//
//            QueryCondition gteQueryCondition = QueryFactory.create().addRestriction(RestrictionFactory.gte("总金额", 1000000));
//            QueryBean queryBean = QueryConditionAdaptor.adaptCondition(gteQueryCondition, table);
//            SwiftResultSet gteResultSet = service.getQueryResult(QueryBeanFactory.queryBean2String(queryBean));
//            int gteindex = table.getMeta().getColumnIndex("总金额") - 1;
//            int count = 0;
//            while (gteResultSet.hasNext()) {
//                Row row = gteResultSet.getNextRow();
//                assertTrue(((Long) row.getValue(gteindex)).doubleValue() >= 1000000);
//                if (((Long) row.getValue(gteindex)).doubleValue() == 1000000) {
//                    count++;
//                }
//            }
//            assertTrue(count == 13);
//
//        } catch (Exception e) {
//            LOGGER.error(e);
//            assertTrue(false);
//        }
//    }
//
//    @Test
//    public void testLT() {
//        try {
//            DataSource dataSource = new QueryDBSource("select * from DEMO_CONTRACT", "DBtestLT");
//            if (!db.existsTable(new SourceKey("testLT"))) {
//                db.createTable(new SourceKey("testLT"), dataSource.getMetadata());
//            }
//            Table table = db.getTable(new SourceKey("testLT"));
//            transportAndIndex(dataSource, table);
//
//            QueryCondition ltQueryCondition = QueryFactory.create().addRestriction(RestrictionFactory.lt("总金额", 1000000));
//            QueryInfoBean queryBean = QueryConditionAdaptor.adaptCondition(ltQueryCondition, table);
//            SwiftResultSet ltResultSet = service.getQueryResult(QueryBeanFactory.queryBean2String(queryBean));
//            int ltindex = table.getMeta().getColumnIndex("总金额") - 1;
//            int count = 0;
//            while (ltResultSet.hasNext()) {
//                Row row = ltResultSet.getNextRow();
//                assertTrue(((Long) row.getValue(ltindex)).doubleValue() < 1000000);
//                if (((Long) row.getValue(ltindex)).doubleValue() == 1000000) {
//                    count++;
//                }
//            }
//            assertTrue(count == 0);
//
//        } catch (Exception e) {
//            LOGGER.error(e);
//            assertTrue(false);
//        }
//    }
//
//    @Test
//    public void testLTE() {
//        try {
//            DataSource dataSource = new QueryDBSource("select * from DEMO_CONTRACT", "DBtestLTE");
//            if (!db.existsTable(new SourceKey("testLTE"))) {
//                db.createTable(new SourceKey("testLTE"), dataSource.getMetadata());
//            }
//            Table table = db.getTable(new SourceKey("testLTE"));
//            transportAndIndex(dataSource, table);
//
//            QueryCondition lteQueryCondition = QueryFactory.create().addRestriction(RestrictionFactory.lte("总金额", 1000000));
//            QueryInfoBean queryBean = QueryConditionAdaptor.adaptCondition(lteQueryCondition, table);
//            SwiftResultSet lteResultSet = service.getQueryResult(QueryBeanFactory.queryBean2String(queryBean));
//            int lteindex = table.getMeta().getColumnIndex("总金额") - 1;
//            int count = 0;
//            while (lteResultSet.hasNext()) {
//                Row row = lteResultSet.getNextRow();
//                assertTrue(((Long) row.getValue(lteindex)).doubleValue() <= 1000000);
//                if (((Long) row.getValue(lteindex)).doubleValue() == 1000000) {
//                    count++;
//                }
//            }
//            assertTrue(count == 13);
//
//        } catch (Exception e) {
//            LOGGER.error(e);
//            assertTrue(false);
//        }
//    }
//
//    @Test
//    public void testIn() {
//        try {
//            DataSource dataSource = new QueryDBSource("select * from DEMO_CONTRACT", "DBtestIn");
//            if (!db.existsTable(new SourceKey("testIn"))) {
//                db.createTable(new SourceKey("testIn"), dataSource.getMetadata());
//            }
//            Table table = db.getTable(new SourceKey("testIn"));
//            transportAndIndex(dataSource, table);
//
//            Set<String> set = new HashSet<String>();
//            set.add("长期协议");
//            set.add("长期协议订单");
//            QueryCondition inQueryCondition = QueryFactory.create().addRestriction(RestrictionFactory.in("合同类型", set));
//            QueryInfoBean queryBean = QueryConditionAdaptor.adaptCondition(inQueryCondition, table);
//            SwiftResultSet inResultSet = service.getQueryResult(QueryBeanFactory.queryBean2String(queryBean));
//            int inindex = table.getMeta().getColumnIndex("合同类型") - 1;
//            while (inResultSet.hasNext()) {
//                Row row = inResultSet.getNextRow();
//                assertTrue(ComparatorUtils.equals(row.getValue(inindex), "长期协议")
//                        || ComparatorUtils.equals(row.getValue(inindex), "长期协议订单"));
//            }
//
//        } catch (Exception e) {
//            LOGGER.error(e);
//            assertTrue(false);
//        }
//    }
//
//    @Test
//    public void testNotIn() {
//        try {
//            DataSource dataSource = new QueryDBSource("select * from DEMO_CONTRACT", "DBtestNotIn");
//            if (!db.existsTable(new SourceKey("testNotIn"))) {
//                db.createTable(new SourceKey("testNotIn"), dataSource.getMetadata());
//            }
//            Table table = db.getTable(new SourceKey("testNotIn"));
//            transportAndIndex(dataSource, table);
//
//            Set<String> set = new HashSet<String>();
//            set.add("长期协议");
//            set.add("长期协议订单");
//            QueryCondition notinQueryCondition = QueryFactory.create().addRestriction(RestrictionFactory.notIn("合同类型", set));
//            QueryInfoBean queryBean = QueryConditionAdaptor.adaptCondition(notinQueryCondition, table);
//            SwiftResultSet notinResultSet = service.getQueryResult(QueryBeanFactory.queryBean2String(queryBean));
//            int notinindex = table.getMeta().getColumnIndex("合同类型") - 1;
//            while (notinResultSet.hasNext()) {
//                Row row = notinResultSet.getNextRow();
//                assertTrue(!ComparatorUtils.equals(row.getValue(notinindex), "长期协议")
//                        && !ComparatorUtils.equals(row.getValue(notinindex), "长期协议订单"));
//            }
//
//        } catch (Exception e) {
//            LOGGER.error(e);
//            assertTrue(false);
//        }
//    }
//
//    @Test
//    public void testLike() {
//        try {
//            DataSource dataSource = new QueryDBSource("select * from DEMO_CONTRACT", "DBtestLike");
//            if (!db.existsTable(new SourceKey("testLike"))) {
//                db.createTable(new SourceKey("testLike"), dataSource.getMetadata());
//            }
//            Table table = db.getTable(new SourceKey("testLike"));
//            transportAndIndex(dataSource, table);
//
//            QueryCondition likeQueryCondition = QueryFactory.create().addRestriction(RestrictionFactory.like("合同类型", "协议"));
//            QueryInfoBean queryBean = QueryConditionAdaptor.adaptCondition(likeQueryCondition, table);
//            SwiftResultSet likeResultSet = service.getQueryResult(QueryBeanFactory.queryBean2String(queryBean));
//            int likeindex = table.getMeta().getColumnIndex("合同类型") - 1;
//            while (likeResultSet.hasNext()) {
//                Row row = likeResultSet.getNextRow();
//                assertTrue(row.getValue(likeindex).toString().contains("协议"));
//            }
//
//        } catch (Exception e) {
//            LOGGER.error(e);
//            assertTrue(false);
//        }
//    }
//
//    @Test
//    public void testStartWith() {
//        try {
//            DataSource dataSource = new QueryDBSource("select * from DEMO_CONTRACT", "DBtestStartWith");
//            if (!db.existsTable(new SourceKey("testStartWith"))) {
//                db.createTable(new SourceKey("testStartWith"), dataSource.getMetadata());
//            }
//            Table table = db.getTable(new SourceKey("testStartWith"));
//            transportAndIndex(dataSource, table);
//
//            QueryCondition startwithCondition = QueryFactory.create().addRestriction(RestrictionFactory.startWith("合同类型", "长期"));
//            QueryInfoBean queryBean = QueryConditionAdaptor.adaptCondition(startwithCondition, table);
//            SwiftResultSet startwithResultSet = service.getQueryResult(QueryBeanFactory.queryBean2String(queryBean));
//            int startwithindex = table.getMeta().getColumnIndex("合同类型") - 1;
//            while (startwithResultSet.hasNext()) {
//                Row row = startwithResultSet.getNextRow();
//                assertTrue(row.getValue(startwithindex).toString().startsWith("长期"));
//            }
//
//        } catch (Exception e) {
//            LOGGER.error(e);
//            assertTrue(false);
//        }
//    }
//
//    @Test
//    public void testEndWith() {
//        try {
//            DataSource dataSource = new QueryDBSource("select * from DEMO_CONTRACT", "DBtestEndWith");
//            if (!db.existsTable(new SourceKey("testEndWith"))) {
//                db.createTable(new SourceKey("testEndWith"), dataSource.getMetadata());
//            }
//            Table table = db.getTable(new SourceKey("testEndWith"));
//            transportAndIndex(dataSource, table);
//
//            QueryCondition endwithCondition = QueryFactory.create().addRestriction(RestrictionFactory.startWith("合同类型", "合同"));
//            QueryInfoBean queryBean = QueryConditionAdaptor.adaptCondition(endwithCondition, table);
//            SwiftResultSet endwithResultSet = service.getQueryResult(QueryBeanFactory.queryBean2String(queryBean));
//            int endwithindex = table.getMeta().getColumnIndex("合同类型") - 1;
//            while (endwithResultSet.hasNext()) {
//                Row row = endwithResultSet.getNextRow();
//                assertTrue(row.getValue(endwithindex).toString().endsWith("合同"));
//            }
//
//        } catch (Exception e) {
//            LOGGER.error(e);
//            assertTrue(false);
//        }
//    }
//}
