//package com.fr.swift.query.info.bean.parser;
//
//import com.fr.swift.config.service.IndexingConfService;
//import com.fr.swift.db.Database;
//import com.fr.swift.query.info.bean.element.DimensionBean;
//import com.fr.swift.query.info.bean.element.MetricBean;
//import com.fr.swift.query.info.bean.element.SortBean;
//import com.fr.swift.query.info.bean.query.DetailQueryInfoBean;
//import com.fr.swift.query.info.bean.query.GroupQueryInfoBean;
//import com.fr.swift.query.info.bean.query.QueryBeanFactory;
//import com.fr.swift.query.info.detail.DetailQueryInfo;
//import com.fr.swift.query.info.group.GroupQueryInfo;
//import com.fr.swift.query.query.QueryType;
//import com.fr.swift.query.sort.SortType;
//import com.fr.swift.source.DataSource;
//import com.fr.swift.source.SourceKey;
//import com.fr.swift.source.db.QueryDBSource;
//import org.junit.Before;
//import org.junit.Test;
//
//import java.io.File;
//import java.io.IOException;
//import java.sql.SQLException;
//import java.util.List;
//
//import static junit.framework.TestCase.assertTrue;
//import static junit.framework.TestCase.fail;
//import static org.junit.Assert.assertEquals;
//
///**
// * Created by Lyon on 2018/6/7.
// */
//public class QueryInfoParserTest {
//
//    private Database db;
//    private IndexingConfService service;
//
//    @Before
//    public void setUp() throws Exception {
//        // TODO: 2018/11/29 mock test
////        Preparer.prepareCubeBuild(getClass());
////        db = SwiftDatabase.getInstance();
////        TestConfDb.setConfDb(SwiftTableIndexingConf.class, SwiftColumnIndexingConf.class);
////        service = SwiftContext.get().getBean(IndexingConfService.class);
////        SourceKey a = new SourceKey("DEMO_CONTRACT");
////        TableAllotConf tableConf = new SwiftTableIndexingConf(a, new LineAllotRule());
////        service.setTableConf(tableConf);
//    }
//
//    @Test
//    public void testGroupQueryInfoBean() throws Exception {
//        DataSource dataSource = new QueryDBSource("select * from DEMO_CONTRACT", "DEMO_CONTRACT");
//        if (!db.existsTable(new SourceKey("DEMO_CONTRACT"))) {
//            db.createTable(new SourceKey("DEMO_CONTRACT"), dataSource.getMetadata());
//        }
//        String path = ResourceUtils.getFileAbsolutePath("json");
//        String filePath = path + File.separator + "group.json";
//        assertTrue(new File(filePath).exists());
//        GroupQueryInfoBean queryBean = null;
//        try {
//            queryBean = (GroupQueryInfoBean) QueryBeanFactory.create(new File(filePath).toURI().toURL());
//        } catch (IOException e) {
//            fail();
//        }
//        List<DimensionBean> dimensionBeans = queryBean.getDimensions();
//        assertEquals(1, dimensionBeans.size());
//        assertEquals("合同类型", dimensionBeans.get(0).getColumn());
//        assertEquals("合同类型-转义", dimensionBeans.get(0).getAlias());
//        List<MetricBean> metricBeans = queryBean.getAggregations();
//        assertEquals(1, metricBeans.size());
//        assertEquals("购买数量", metricBeans.get(0).getColumn());
//        assertEquals("购买数量-转义", metricBeans.get(0).getAlias());
//        GroupQueryInfo info = (GroupQueryInfo) QueryInfoParser.parse(queryBean);
//        assertEquals(1, info.getDimensions().size());
//        assertEquals(1, info.getMetrics().size());
//        String queryString = QueryBeanFactory.queryBean2String(queryBean);
//        queryBean = (GroupQueryInfoBean) QueryBeanFactory.create(queryString);
//    }
//
//    @Test
//    public void testDetailQueryInfoBean() throws SQLException {
//        DataSource dataSource = new QueryDBSource("select * from DEMO_CONTRACT", "DEMO_CONTRACT");
//        if (!db.existsTable(new SourceKey("DEMO_CONTRACT"))) {
//            db.createTable(new SourceKey("DEMO_CONTRACT"), dataSource.getMetadata());
//        }
//        String path = ResourceUtils.getFileAbsolutePath("json");
//        String filePath = path + File.separator + "detail.json";
//        assertTrue(new File(filePath).exists());
//        DetailQueryInfoBean queryBean = null;
//        try {
//            queryBean = (DetailQueryInfoBean) QueryBeanFactory.create(new File(filePath).toURI().toURL());
//        } catch (IOException e) {
//            fail();
//        }
//        assertEquals(QueryType.DETAIL, queryBean.getQueryType());
//        assertEquals(4, queryBean.getDimensions().size());
//        List<DimensionBean> dimensionBeanList = queryBean.getDimensions();
//        assertEquals("合同类型", dimensionBeanList.get(0).getColumn());
//        assertEquals("购买数量", dimensionBeanList.get(1).getColumn());
//        assertEquals("总金额", dimensionBeanList.get(2).getColumn());
//        assertEquals("购买的产品", dimensionBeanList.get(3).getColumn());
//        List<SortBean> sortBeans = queryBean.getSorts();
//        assertEquals(1, sortBeans.size());
//        assertEquals("购买数量", sortBeans.get(0).getName());
//        assertEquals(SortType.DESC, sortBeans.get(0).getType());
//
//        DetailQueryInfo info = (DetailQueryInfo) QueryInfoParser.parse(queryBean);
//        assertEquals(4, info.getDimensions().size());
//        assertEquals(1, info.getSorts().size());
//    }
//}