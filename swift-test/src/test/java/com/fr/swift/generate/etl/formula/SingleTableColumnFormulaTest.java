//package com.fr.swift.generate.etl.formula;
//
//import com.fr.swift.context.SwiftContext;
//import com.fr.swift.generate.BaseTest;
//import com.fr.swift.generate.TestIndexer;
//import com.fr.swift.generate.TestTransport;
//import com.fr.swift.manager.LocalSegmentProvider;
//import com.fr.swift.segment.Segment;
//import com.fr.swift.segment.column.Column;
//import com.fr.swift.segment.column.ColumnKey;
//import com.fr.swift.source.ColumnTypeConstants;
//import com.fr.swift.source.DataSource;
//import com.fr.swift.source.db.QueryDBSource;
//import com.fr.swift.source.etl.ETLOperator;
//import com.fr.swift.source.etl.EtlSource;
//import com.fr.swift.source.etl.formula.ColumnFormulaOperator;
//import com.fr.swift.test.Preparer;
//import org.junit.Test;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.junit.Assert.assertEquals;
//
//public class SingleTableColumnFormulaTest extends BaseTest {
//
//    private LocalSegmentProvider segmentProvider;
//
//    @Override
//    public void setUp() throws Exception {
//        super.setUp();
//        Preparer.prepareCubeBuild(getClass());
//        segmentProvider = SwiftContext.get().getBean(LocalSegmentProvider.class);
//    }
//
//    /**
//     * A
//     * \
//     * A-formula
//     *
//     * @throws Exception
//     */
//    @Test
//    public void testOneColumnFormula() throws Exception {
//        TableBuildTestUtil.initGeneratorListener();
//
//        DataSource dataSource = new QueryDBSource("select * from DEMO_CAPITAL_RETURN", "allTest");
//        ETLOperator formulaOperator = new ColumnFormulaOperator("addField", ColumnTypeConstants.ColumnType.NUMBER, "${付款金额} + ${付款金额}");
//        List<DataSource> baseDataSources = new ArrayList<DataSource>();
//        baseDataSources.add(dataSource);
//        EtlSource etlSource = new EtlSource(baseDataSources, formulaOperator);
//
//        TestIndexer.historyIndex(dataSource, TestTransport.historyTransport(dataSource));
//        TestIndexer.historyIndex(etlSource, TestTransport.historyTransport(etlSource));
//
//        Segment dataSourceSegment = segmentProvider.getSegment(dataSource.getSourceKey()).get(0);
//        Segment etlSourceSegment = segmentProvider.getSegment(etlSource.getSourceKey()).get(0);
//        assertEquals(dataSourceSegment.getLocation().getPath(), etlSourceSegment.getLocation().getPath());
//
//        Column originalColumn1 = dataSourceSegment.getColumn(new ColumnKey("付款金额"));
//        Column originalColumn2 = etlSourceSegment.getColumn(new ColumnKey("付款金额"));
//        Column addColumn = etlSourceSegment.getColumn(new ColumnKey("addField"));
//
//        assertEquals(dataSourceSegment.getRowCount(), etlSourceSegment.getRowCount());
//
//        for (int i = 0; i < dataSourceSegment.getRowCount(); i++) {
//            assertEquals(originalColumn1.getDetailColumn().get(i), originalColumn2.getDetailColumn().get(i));
//            assertEquals((((Long) originalColumn1.getDetailColumn().get(i)).doubleValue() * 2), addColumn.getDetailColumn().get(i));
//        }
//    }
//
//    /**
//     * A
//     * \
//     * A-formula
//     * \
//     * A-formula-formula
//     *
//     * @throws Exception
//     */
//    @Test
//    public void testTwoColumnFormula() throws Exception {
//        DataSource dataSource = new QueryDBSource("select 付款金额,付款时间,合同ID from DEMO_CAPITAL_RETURN", "allTest");
//
//        ETLOperator formulaOperator1 = new ColumnFormulaOperator("addField", ColumnTypeConstants.ColumnType.NUMBER, "${付款金额} + ${付款金额}");
//        List<DataSource> baseDataSources1 = new ArrayList<DataSource>();
//        baseDataSources1.add(dataSource);
//        EtlSource etlSource1 = new EtlSource(baseDataSources1, formulaOperator1);
//
//        ETLOperator formulaOperator2 = new ColumnFormulaOperator("addField2", ColumnTypeConstants.ColumnType.NUMBER, "${addField} + ${addField}");
//        List<DataSource> baseDataSources2 = new ArrayList<DataSource>();
//        baseDataSources2.add(etlSource1);
//        EtlSource etlSource2 = new EtlSource(baseDataSources2, formulaOperator2);
//
//        TestIndexer.historyIndex(dataSource, TestTransport.historyTransport(dataSource));
//        TestIndexer.historyIndex(etlSource1, TestTransport.historyTransport(etlSource1));
//        TestIndexer.historyIndex(etlSource2, TestTransport.historyTransport(etlSource2));
//
//        Segment dataSourceSegment = segmentProvider.getSegment(dataSource.getSourceKey()).get(0);
//        Segment etlSourceSegment = segmentProvider.getSegment(etlSource1.getSourceKey()).get(0);
//        Segment etlSourceSegment2 = segmentProvider.getSegment(etlSource2.getSourceKey()).get(0);
//
//        assertEquals(dataSourceSegment.getLocation().getPath(), etlSourceSegment.getLocation().getPath());
//        assertEquals(dataSourceSegment.getLocation().getPath(), etlSourceSegment2.getLocation().getPath());
//
//        Column originalColumn1 = dataSourceSegment.getColumn(new ColumnKey("付款金额"));
//        Column originalColumn2 = etlSourceSegment.getColumn(new ColumnKey("付款金额"));
//        Column originalColumn3 = etlSourceSegment2.getColumn(new ColumnKey("付款金额"));
//
//        Column addColumn = etlSourceSegment.getColumn(new ColumnKey("addField"));
//        Column addColumn2 = etlSourceSegment2.getColumn(new ColumnKey("addField2"));
//
//        assertEquals(dataSourceSegment.getRowCount(), etlSourceSegment.getRowCount());
//        assertEquals(dataSourceSegment.getRowCount(), etlSourceSegment2.getRowCount());
//
//        for (int i = 0; i < dataSourceSegment.getRowCount(); i++) {
//            assertEquals(originalColumn1.getDetailColumn().get(i), originalColumn2.getDetailColumn().get(i));
//            assertEquals(originalColumn1.getDetailColumn().get(i), originalColumn3.getDetailColumn().get(i));
//
//            assertEquals((((Long) originalColumn1.getDetailColumn().get(i)).doubleValue() * 2), addColumn.getDetailColumn().get(i));
//            assertEquals(((double) addColumn.getDetailColumn().get(i)) * 2, addColumn2.getDetailColumn().get(i));
//
//        }
//    }
//}
