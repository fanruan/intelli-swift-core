//package com.fr.swift.query.index;
//
//import com.fr.swift.SwiftContext;
//import com.fr.swift.bitmap.ImmutableBitMap;
//import com.fr.swift.db.Database;
//import com.fr.swift.db.Table;
//import com.fr.swift.db.Where;
//import com.fr.swift.db.impl.SwiftDatabase;
//import com.fr.swift.db.impl.SwiftWhere;
//import com.fr.swift.manager.LocalSegmentProvider;
//import com.fr.swift.query.info.bean.element.filter.impl.InFilterBean;
//import com.fr.swift.query.info.bean.element.filter.impl.NumberInRangeFilterBean;
//import com.fr.swift.query.info.bean.element.filter.impl.value.RangeFilterValueBean;
//import com.fr.swift.query.query.FilterBean;
//import com.fr.swift.segment.Segment;
//import com.fr.swift.segment.SegmentKey;
//import com.fr.swift.segment.column.Column;
//import com.fr.swift.segment.column.ColumnKey;
//import com.fr.swift.segment.operator.delete.WhereDeleter;
//import com.fr.swift.service.AnalyseService;
//import com.fr.swift.source.DataSource;
//import com.fr.swift.source.SourceKey;
//import com.fr.swift.result.SwiftResultSet;
//import com.fr.swift.source.SwiftSourceTransfer;
//import com.fr.swift.source.DefaultSourceTransferProvider;
//import com.fr.swift.source.db.QueryDBSource;
//import com.fr.swift.test.Preparer;
//import junit.framework.TestCase;
//import org.junit.Before;
//import org.junit.Test;
//
//import java.util.Collections;
//
//import static org.junit.Assert.assertNotEquals;
//
///**
// * This class created on 2018/4/27
// *
// * @author Lucifer
// * @description
// * @since Advanced FineBI 5.0
// */
//public class DeleteTest extends TestCase {
//    private final Database db = SwiftDatabase.getInstance();
//
//    private LocalSegmentProvider localSegmentProvider = (LocalSegmentProvider) SwiftContext.get().getBean("localSegmentProvider");
//
//    @Override
//    @Before
//    public void setUp() throws Exception {
//        super.setUp();
//        Preparer.prepareCubeBuild(getClass());
//        SwiftContext.get().getBean("swiftAnalyseService", AnalyseService.class).start();
//    }
//
//    private static FilterBean createEqualFilter(String fieldName, String value) {
//        InFilterBean bean = new InFilterBean();
//        bean.setColumn(fieldName);
//        bean.setFilterValue(Collections.singleton(value));
//        return bean;
//    }
//
//    private static FilterBean createGTFilter(String fieldName, int value) {
//        NumberInRangeFilterBean filterInfoBean = new NumberInRangeFilterBean();
//        filterInfoBean.setColumn(fieldName);
//        RangeFilterValueBean gtValue = new RangeFilterValueBean();
//        gtValue.setStart(String.valueOf(value));
//        filterInfoBean.setFilterValue(gtValue);
//        return filterInfoBean;
//    }
//
//    @Test
//    public void testEQHis() throws Exception {
//        DataSource dataSource = new QueryDBSource("select * from DEMO_CONTRACT", "testEQHis");
//        if (!db.existsTable(new SourceKey("testEQHis"))) {
//            db.createTable(new SourceKey("testEQHis"), dataSource.getMetadata());
//        }
//        Table table = db.getTable(new SourceKey("testEQHis"));
//        transportHisAndIndex(dataSource, table);
//        SegmentKey segmentKey = localSegmentProvider.getSegmentKeys(new SourceKey("testEQHis")).get(0);
//        Segment segment = localSegmentProvider.getSegment(new SourceKey("testEQHis")).get(0);
//
//        Where where = new SwiftWhere(createEqualFilter("合同类型", "购买合同"));
//
//        ((WhereDeleter) SwiftContext.get().getBean("decrementer", segmentKey)).delete(where);
//
//        Column column = segment.getColumn(new ColumnKey("合同类型"));
//        for (int i = 0; i < segment.getRowCount(); i++) {
//            if (segment.getAllShowIndex().contains(i)) {
//                assertNotEquals(column.getDetailColumn().get(i), "购买合同");
//            }
//        }
//    }
//
//    @Test
//    public void testEQReal() throws Exception {
//        DataSource dataSource = new QueryDBSource("select * from DEMO_CONTRACT", "testEQReal");
//        if (!db.existsTable(new SourceKey("testEQReal"))) {
//            db.createTable(new SourceKey("testEQReal"), dataSource.getMetadata());
//        }
//        Table table = db.getTable(new SourceKey("testEQReal"));
//        transportRealAndIndex(dataSource, table);
//        SegmentKey segmentKey = localSegmentProvider.getSegmentKeys(new SourceKey("testEQReal")).get(0);
//        Segment segment = localSegmentProvider.getSegment(new SourceKey("testEQReal")).get(0);
//
//        Where where = new SwiftWhere(createEqualFilter("合同类型", "购买合同"));
//        ((WhereDeleter) SwiftContext.get().getBean("decrementer", segmentKey)).delete(where);
//        Column column = segment.getColumn(new ColumnKey("合同类型"));
//        for (int i = 0; i < segment.getRowCount(); i++) {
//            if (segment.getAllShowIndex().contains(i)) {
//                assertNotEquals(column.getDetailColumn().get(i), "购买合同");
//            }
//        }
//    }
//
//    protected void transportHisAndIndex(DataSource dataSource, Table table) throws Exception {
//        SwiftSourceTransfer transfer = DefaultSourceTransferProvider.createSourceTransfer(dataSource);
//        SwiftResultSet resultSet = transfer.createResultSet();
//        table.importFrom(resultSet);
//    }
//
//    protected void transportRealAndIndex(DataSource dataSource, Table table) throws Exception {
//        SwiftSourceTransfer transfer = DefaultSourceTransferProvider.createSourceTransfer(dataSource);
//        SwiftResultSet resultSet = transfer.createResultSet();
//        table.insert(resultSet);
//    }
//
//    @Test
//    public void testGTReal() throws Exception {
//        DataSource dataSource = new QueryDBSource("select * from DEMO_CONTRACT", "testEQReal");
//        if (!db.existsTable(new SourceKey("testGTReal"))) {
//            db.createTable(new SourceKey("testGTReal"), dataSource.getMetadata());
//        }
//        Table table = db.getTable(new SourceKey("testGTReal"));
//        transportRealAndIndex(dataSource, table);
//        Segment segment = localSegmentProvider.getSegment(new SourceKey("testGTReal")).get(0);
//        SegmentKey segmentKey = localSegmentProvider.getSegmentKeys(new SourceKey("testGTReal")).get(0);
//
//        Where where = new SwiftWhere(createGTFilter("总金额", 100000));
//        ((WhereDeleter) SwiftContext.get().getBean("decrementer", segmentKey)).delete(where);
//        Column column = segment.getColumn(new ColumnKey("总金额"));
//        for (int i = 0; i < segment.getRowCount(); i++) {
//            if (segment.getAllShowIndex().contains(i)) {
//                assertTrue((long) column.getDetailColumn().get(i) <= 100000);
//            }
//        }
//    }
//
//    @Test
//    public void testGTRealByWhere() throws Exception {
//        DataSource dataSource = new QueryDBSource("select * from DEMO_CONTRACT", "testGTRealByWhere");
//        if (!db.existsTable(new SourceKey("testGTRealByWhere"))) {
//            db.createTable(new SourceKey("testGTRealByWhere"), dataSource.getMetadata());
//        }
//        Table table = db.getTable(new SourceKey("testGTRealByWhere"));
//        transportRealAndIndex(dataSource, table);
//
//        Where where = new SwiftWhere(createGTFilter("总金额", 100000));
//        Segment segment = localSegmentProvider.getSegment(new SourceKey("testGTRealByWhere")).get(0);
//        ImmutableBitMap indexAfterFilter = where.createWhereIndex(table, segment);
//
//        Column column = segment.getColumn(new ColumnKey("总金额"));
//        for (int i = 0; i < segment.getRowCount(); i++) {
//            if (indexAfterFilter.contains(i)) {
//                assertTrue((long) column.getDetailColumn().get(i) > 100000);
//            }
//        }
//    }
//}
