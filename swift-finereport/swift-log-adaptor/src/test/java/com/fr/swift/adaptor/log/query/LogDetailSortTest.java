//package com.fr.swift.adaptor.log.query;
//
//import com.fr.stable.query.QueryFactory;
//import com.fr.stable.query.condition.QueryCondition;
//import com.fr.swift.adaptor.log.QueryConditionAdaptor;
//import com.fr.swift.basics.base.selector.ProxySelector;
//import com.fr.swift.db.Database;
//import com.fr.swift.db.Table;
//import com.fr.swift.db.impl.SwiftDatabase;
//import com.fr.swift.query.info.bean.query.QueryBeanFactory;
//import com.fr.swift.query.info.bean.query.QueryInfoBean;
//import com.fr.swift.service.AnalyseService;
//import com.fr.swift.source.DataSource;
//import com.fr.swift.source.Row;
//import com.fr.swift.source.SourceKey;
//import com.fr.swift.result.SwiftResultSet;
//import com.fr.swift.source.db.QueryDBSource;
//import org.junit.Before;
//import org.junit.Test;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import static junit.framework.TestCase.assertTrue;
//
///**
// * This class created on 2018/4/27
// *
// * @author Lucifer
// * @description
// * @since Advanced FineBI 5.0
// */
//public class LogDetailSortTest extends LogBaseTest {
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
//    public void testSortedAsc() {
//        try {
//            DataSource dataSource = new QueryDBSource("select * from DEMO_CONTRACT", "DBtestSortedAsc");
//            if (!db.existsTable(new SourceKey("testSortedAsc"))) {
//                db.createTable(new SourceKey("testSortedAsc"), dataSource.getMetadata());
//            }
//            Table table = db.getTable(new SourceKey("testSortedAsc"));
//            transportAndIndex(dataSource, table);
//
//            QueryCondition sortQueryCondition = QueryFactory.create().addSort("总金额");
//
//            QueryInfoBean queryBean = QueryConditionAdaptor.adaptCondition(sortQueryCondition, table);
//            SwiftResultSet sortResultSet = service.getQueryResult(QueryBeanFactory.queryBean2String(queryBean));
//            int sortindex = table.getMeta().getColumnIndex("总金额");
//            List<Double> dataList = new ArrayList<Double>();
//            while (sortResultSet.hasNext()) {
//                Row row = sortResultSet.getNextRow();
//                dataList.add(((Long) row.getValue(sortindex - 1)).doubleValue());
//            }
//            for (int i = 0; i < dataList.size() - 1; i++) {
//                assertTrue(dataList.get(i) <= dataList.get(i + 1));
//            }
//        } catch (Exception e) {
//            LOGGER.error(e);
//            assertTrue(false);
//        }
//    }
//
//    @Test
//    public void testSortedDesc() {
//        try {
//            DataSource dataSource = new QueryDBSource("select * from DEMO_CONTRACT", "DBtestSortedDesc");
//            if (!db.existsTable(new SourceKey("testSortedDesc"))) {
//                db.createTable(new SourceKey("testSortedDesc"), dataSource.getMetadata());
//            }
//            Table table = db.getTable(new SourceKey("testSortedDesc"));
//            transportAndIndex(dataSource, table);
//
//            QueryCondition sortQueryCondition = QueryFactory.create().addSort("总金额", true);
//
//            QueryInfoBean queryBean = QueryConditionAdaptor.adaptCondition(sortQueryCondition, table);
//            SwiftResultSet sortResultSet = service.getQueryResult(QueryBeanFactory.queryBean2String(queryBean));
//            int sortindex = table.getMeta().getColumnIndex("总金额");
//            List<Double> dataList = new ArrayList<Double>();
//            while (sortResultSet.hasNext()) {
//                Row row = sortResultSet.getNextRow();
//                dataList.add(((Long) row.getValue(sortindex - 1)).doubleValue());
//            }
//            for (int i = 0; i < dataList.size() - 1; i++) {
//                assertTrue(dataList.get(i) >= dataList.get(i + 1));
//            }
//        } catch (Exception e) {
//            LOGGER.error(e);
//            assertTrue(false);
//        }
//    }
//}
