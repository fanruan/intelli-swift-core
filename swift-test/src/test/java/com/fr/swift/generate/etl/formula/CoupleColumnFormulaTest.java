package com.fr.swift.generate.etl.formula;

import com.fr.swift.context.SwiftContext;
import com.fr.swift.generate.BaseTest;
import com.fr.swift.generate.TestIndexer;
import com.fr.swift.generate.TestTransport;
import com.fr.swift.manager.LocalSegmentProvider;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SwiftSegmentManager;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.service.LocalSwiftServerService;
import com.fr.swift.source.ColumnTypeConstants;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.db.QueryDBSource;
import com.fr.swift.source.db.TestConnectionProvider;
import com.fr.swift.source.etl.ETLOperator;
import com.fr.swift.source.etl.EtlSource;
import com.fr.swift.source.etl.formula.ColumnFormulaOperator;
import com.fr.swift.test.Preparer;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

public class CoupleColumnFormulaTest extends BaseTest {

    private SwiftSegmentManager segmentProvider;

    @BeforeClass
    public static void boot() {
        Preparer.prepareFrEnv();
        Preparer.prepareContext();
        new LocalSwiftServerService().start();
        TestConnectionProvider.createConnection();
    }

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
        segmentProvider = SwiftContext.getInstance().getBean(LocalSegmentProvider.class);
    }

    /**
     * A                  A
     * \                  \
     * A-formula1         A-formula2
     *
     * @throws Exception
     * @description A表做两个不同的公式，新增列名也不同
     * @expect 新增列为两个不同字段
     */
    @Test
    public void testBothNotSame() throws Exception {
        DataSource dataSource = new QueryDBSource("select 付款金额,付款时间 from DEMO_CAPITAL_RETURN", "allTest");

        ETLOperator formulaOperator1 = new ColumnFormulaOperator("addField", ColumnTypeConstants.ColumnType.NUMBER, "${付款金额} + ${付款金额}");
        List<DataSource> baseDataSources1 = new ArrayList<DataSource>();
        baseDataSources1.add(dataSource);
        EtlSource etlSource1 = new EtlSource(baseDataSources1, formulaOperator1);

        ETLOperator formulaOperator2 = new ColumnFormulaOperator("addField2", ColumnTypeConstants.ColumnType.NUMBER, "${付款金额} + ${付款金额} + ${付款金额}");
        List<DataSource> baseDataSources2 = new ArrayList<DataSource>();
        baseDataSources2.add(dataSource);
        EtlSource etlSource2 = new EtlSource(baseDataSources2, formulaOperator2);

        TestIndexer.historyIndex(dataSource, TestTransport.historyTransport(dataSource));
        TestIndexer.historyIndex(etlSource1, TestTransport.historyTransport(etlSource1));
        TestIndexer.historyIndex(etlSource2, TestTransport.historyTransport(etlSource2));

        Segment dataSourceSegment = segmentProvider.getSegment(dataSource.getSourceKey()).get(0);
        Segment etlSourceSegment1 = segmentProvider.getSegment(etlSource1.getSourceKey()).get(0);
        Segment etlSourceSegment2 = segmentProvider.getSegment(etlSource2.getSourceKey()).get(0);

        assertEquals(dataSourceSegment.getLocation().getPath(), etlSourceSegment1.getLocation().getPath());
        assertEquals(dataSourceSegment.getLocation().getPath(), etlSourceSegment2.getLocation().getPath());

        Column originalColumn1 = dataSourceSegment.getColumn(new ColumnKey("付款金额"));
        Column originalColumn2 = etlSourceSegment1.getColumn(new ColumnKey("付款金额"));
        Column originalColumn3 = etlSourceSegment2.getColumn(new ColumnKey("付款金额"));

        Column addColumn1 = etlSourceSegment1.getColumn(new ColumnKey("addField"));
        Column addColumn2 = etlSourceSegment2.getColumn(new ColumnKey("addField2"));

        assertNotSame(addColumn1.getLocation().getPath(), addColumn2.getLocation().getPath());


        assertEquals(dataSourceSegment.getRowCount(), etlSourceSegment1.getRowCount());
        assertEquals(dataSourceSegment.getRowCount(), etlSourceSegment2.getRowCount());

        for (int i = 0; i < dataSourceSegment.getRowCount(); i++) {
            assertEquals(originalColumn1.getDetailColumn().get(i), originalColumn2.getDetailColumn().get(i));
            assertEquals(originalColumn1.getDetailColumn().get(i), originalColumn3.getDetailColumn().get(i));

            assertEquals((((Long) originalColumn1.getDetailColumn().get(i)).doubleValue() * 2), addColumn1.getDetailColumn().get(i));
            assertEquals((((Long) originalColumn1.getDetailColumn().get(i)).doubleValue() * 3), addColumn2.getDetailColumn().get(i));
        }
    }

    /**
     * A                  A
     * \                  \
     * A-formula1         A-formula2
     *
     * @throws Exception
     * @description 新增列字段名相同，公式不同
     * @expect 新增列为两个不同字段
     */
    @Test
    public void testFieldSameFormulaNotSame() throws Exception {
        DataSource dataSource = new QueryDBSource("select 付款金额,付款时间,now() as time from DEMO_CAPITAL_RETURN", "allTest");

        ETLOperator formulaOperator1 = new ColumnFormulaOperator("addField", ColumnTypeConstants.ColumnType.NUMBER, "${付款金额} + ${付款金额}");
        List<DataSource> baseDataSources1 = new ArrayList<DataSource>();
        baseDataSources1.add(dataSource);
        EtlSource etlSource1 = new EtlSource(baseDataSources1, formulaOperator1);

        ETLOperator formulaOperator2 = new ColumnFormulaOperator("addField", ColumnTypeConstants.ColumnType.NUMBER, "${付款金额} + ${付款金额} + ${付款金额}");
        List<DataSource> baseDataSources2 = new ArrayList<DataSource>();
        baseDataSources2.add(dataSource);
        EtlSource etlSource2 = new EtlSource(baseDataSources2, formulaOperator2);

        TestIndexer.historyIndex(dataSource, TestTransport.historyTransport(dataSource));
        TestIndexer.historyIndex(etlSource1, TestTransport.historyTransport(etlSource1));
        TestIndexer.historyIndex(etlSource2, TestTransport.historyTransport(etlSource2));

        Segment dataSourceSegment = segmentProvider.getSegment(dataSource.getSourceKey()).get(0);
        Segment etlSourceSegment1 = segmentProvider.getSegment(etlSource1.getSourceKey()).get(0);
        Segment etlSourceSegment2 = segmentProvider.getSegment(etlSource2.getSourceKey()).get(0);

        assertEquals(dataSourceSegment.getLocation().getPath(), etlSourceSegment1.getLocation().getPath());
        assertEquals(dataSourceSegment.getLocation().getPath(), etlSourceSegment2.getLocation().getPath());

        Column originalColumn1 = dataSourceSegment.getColumn(new ColumnKey("付款金额"));
        Column originalColumn2 = etlSourceSegment1.getColumn(new ColumnKey("付款金额"));
        Column originalColumn3 = etlSourceSegment2.getColumn(new ColumnKey("付款金额"));

        Column addColumn1 = etlSourceSegment1.getColumn(new ColumnKey("addField"));
        Column addColumn2 = etlSourceSegment2.getColumn(new ColumnKey("addField"));

        assertNotSame(addColumn1.getLocation().getPath(), addColumn2.getLocation().getPath());

        assertEquals(dataSourceSegment.getRowCount(), etlSourceSegment1.getRowCount());
        assertEquals(dataSourceSegment.getRowCount(), etlSourceSegment2.getRowCount());

        for (int i = 0; i < dataSourceSegment.getRowCount(); i++) {
            assertEquals(originalColumn1.getDetailColumn().get(i), originalColumn2.getDetailColumn().get(i));
            assertEquals(originalColumn1.getDetailColumn().get(i), originalColumn3.getDetailColumn().get(i));

            assertEquals((((Long) originalColumn1.getDetailColumn().get(i)).doubleValue() * 2), addColumn1.getDetailColumn().get(i));
            assertEquals((((Long) originalColumn1.getDetailColumn().get(i)).doubleValue() * 3), addColumn2.getDetailColumn().get(i));
        }
    }

    /**
     * A                  A
     * \                  \
     * A-formula1         A-formula2
     *
     * @throws Exception
     * @description 新增列字段名相同，也相同
     * @expect 新增列为两个相同字段
     */
    @Test
    public void testBothSame() throws Exception {
        DataSource dataSource = new QueryDBSource("select 付款金额,付款时间,now() as time2 from DEMO_CAPITAL_RETURN", "allTest");

        ETLOperator formulaOperator1 = new ColumnFormulaOperator("addField", ColumnTypeConstants.ColumnType.NUMBER, "${付款金额} + ${付款金额}");
        List<DataSource> baseDataSources1 = new ArrayList<DataSource>();
        baseDataSources1.add(dataSource);
        EtlSource etlSource1 = new EtlSource(baseDataSources1, formulaOperator1);

        ETLOperator formulaOperator2 = new ColumnFormulaOperator("addField", ColumnTypeConstants.ColumnType.NUMBER, "${付款金额} + ${付款金额}");
        List<DataSource> baseDataSources2 = new ArrayList<DataSource>();
        baseDataSources2.add(dataSource);
        EtlSource etlSource2 = new EtlSource(baseDataSources2, formulaOperator2);

        TestIndexer.historyIndex(dataSource, TestTransport.historyTransport(dataSource));
        TestIndexer.historyIndex(etlSource1, TestTransport.historyTransport(etlSource1));
        TestIndexer.historyIndex(etlSource2, TestTransport.historyTransport(etlSource2));

        Segment dataSourceSegment = segmentProvider.getSegment(dataSource.getSourceKey()).get(0);
        Segment etlSourceSegment1 = segmentProvider.getSegment(etlSource1.getSourceKey()).get(0);
        Segment etlSourceSegment2 = segmentProvider.getSegment(etlSource2.getSourceKey()).get(0);

        assertEquals(dataSourceSegment.getLocation().getPath(), etlSourceSegment1.getLocation().getPath());
        assertEquals(dataSourceSegment.getLocation().getPath(), etlSourceSegment2.getLocation().getPath());

        Column originalColumn1 = dataSourceSegment.getColumn(new ColumnKey("付款金额"));
        Column originalColumn2 = etlSourceSegment1.getColumn(new ColumnKey("付款金额"));
        Column originalColumn3 = etlSourceSegment2.getColumn(new ColumnKey("付款金额"));

        Column addColumn1 = etlSourceSegment1.getColumn(new ColumnKey("addField"));
        Column addColumn2 = etlSourceSegment2.getColumn(new ColumnKey("addField"));

        assertEquals(addColumn1.getLocation().getPath(), addColumn2.getLocation().getPath());

        assertEquals(dataSourceSegment.getRowCount(), etlSourceSegment1.getRowCount());
        assertEquals(dataSourceSegment.getRowCount(), etlSourceSegment2.getRowCount());

        for (int i = 0; i < dataSourceSegment.getRowCount(); i++) {
            assertEquals(originalColumn1.getDetailColumn().get(i), originalColumn2.getDetailColumn().get(i));
            assertEquals(originalColumn1.getDetailColumn().get(i), originalColumn3.getDetailColumn().get(i));

            assertEquals((((Long) originalColumn1.getDetailColumn().get(i)).doubleValue() * 2), addColumn1.getDetailColumn().get(i));
            assertEquals((((Long) originalColumn1.getDetailColumn().get(i)).doubleValue() * 2), addColumn2.getDetailColumn().get(i));
        }
    }

    /**
     * A                  A
     * \                  \
     * A-formula1         A-formula2
     *
     * @throws Exception
     * @description 新增列字段不相同，公式相同
     * @expect 新增列为两个相同字段
     */
    @Test
    public void testFormulaSameFieldNotSame() throws Exception {
        DataSource dataSource = new QueryDBSource("select 付款金额,付款时间,now() as time3 from DEMO_CAPITAL_RETURN", "allTest");

        ETLOperator formulaOperator1 = new ColumnFormulaOperator("addField", ColumnTypeConstants.ColumnType.NUMBER, "${付款金额} + ${付款金额}");
        List<DataSource> baseDataSources1 = new ArrayList<DataSource>();
        baseDataSources1.add(dataSource);
        EtlSource etlSource1 = new EtlSource(baseDataSources1, formulaOperator1);

        ETLOperator formulaOperator2 = new ColumnFormulaOperator("addField2", ColumnTypeConstants.ColumnType.NUMBER, "${付款金额} + ${付款金额}");
        List<DataSource> baseDataSources2 = new ArrayList<DataSource>();
        baseDataSources2.add(dataSource);
        EtlSource etlSource2 = new EtlSource(baseDataSources2, formulaOperator2);

        TestIndexer.historyIndex(dataSource, TestTransport.historyTransport(dataSource));
        TestIndexer.historyIndex(etlSource1, TestTransport.historyTransport(etlSource1));
        TestIndexer.historyIndex(etlSource2, TestTransport.historyTransport(etlSource2));

        Segment dataSourceSegment = segmentProvider.getSegment(dataSource.getSourceKey()).get(0);
        Segment etlSourceSegment1 = segmentProvider.getSegment(etlSource1.getSourceKey()).get(0);
        Segment etlSourceSegment2 = segmentProvider.getSegment(etlSource2.getSourceKey()).get(0);

        assertEquals(dataSourceSegment.getLocation().getPath(), etlSourceSegment1.getLocation().getPath());
        assertEquals(dataSourceSegment.getLocation().getPath(), etlSourceSegment2.getLocation().getPath());

        Column originalColumn1 = dataSourceSegment.getColumn(new ColumnKey("付款金额"));
        Column originalColumn2 = etlSourceSegment1.getColumn(new ColumnKey("付款金额"));
        Column originalColumn3 = etlSourceSegment2.getColumn(new ColumnKey("付款金额"));

        Column addColumn1 = etlSourceSegment1.getColumn(new ColumnKey("addField"));
        Column addColumn2 = etlSourceSegment2.getColumn(new ColumnKey("addField2"));

        assertEquals(addColumn1.getLocation().getPath(), addColumn2.getLocation().getPath());

        assertEquals(dataSourceSegment.getRowCount(), etlSourceSegment1.getRowCount());
        assertEquals(dataSourceSegment.getRowCount(), etlSourceSegment2.getRowCount());

        for (int i = 0; i < dataSourceSegment.getRowCount(); i++) {
            assertEquals(originalColumn1.getDetailColumn().get(i), originalColumn2.getDetailColumn().get(i));
            assertEquals(originalColumn1.getDetailColumn().get(i), originalColumn3.getDetailColumn().get(i));

            assertEquals((((Long) originalColumn1.getDetailColumn().get(i)).doubleValue() * 2), addColumn1.getDetailColumn().get(i));
            assertEquals((((Long) originalColumn1.getDetailColumn().get(i)).doubleValue() * 2), addColumn2.getDetailColumn().get(i));
        }
    }
}
