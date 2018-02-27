package com.fr.swift.generate.etl.formula;

import com.fr.swift.generate.BaseTest;
import com.fr.swift.generate.history.ColumnIndexer;
import com.fr.swift.generate.history.DataTransporter;
import com.fr.swift.manager.LocalSegmentProvider;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.ColumnTypeConstants;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.db.QueryDBSource;
import com.fr.swift.source.etl.ETLOperator;
import com.fr.swift.source.etl.ETLSource;
import com.fr.swift.source.etl.formula.ColumnFormulaOperator;

import java.util.ArrayList;
import java.util.List;

public class CoupleColumnFormulaTest extends BaseTest {

    /**
     * A                  A
     * \                  \
     * A-formula1         A-formula2
     *
     * @throws Exception
     * @description A表做两个不同的公式，新增列名也不同
     * @expect 新增列为两个不同字段
     */
    public void testBothNotSame() throws Exception {
        DataSource dataSource = new QueryDBSource("select 付款金额,付款时间 from DEMO_CAPITAL_RETURN", "allTest");
        DataTransporter dataTransporter = new DataTransporter(dataSource);
        dataTransporter.work();
        for (int i = 1; i <= dataSource.getMetadata().getColumnCount(); i++) {
            ColumnIndexer<?> indexer = new ColumnIndexer(dataSource, new ColumnKey(dataSource.getMetadata().getColumnName(i)));
            indexer.work();
        }

        ETLOperator formulaOperator1 = new ColumnFormulaOperator("addField", ColumnTypeConstants.ColumnType.NUMBER, "${付款金额} + ${付款金额}");
        List<DataSource> baseDataSources1 = new ArrayList<DataSource>();
        baseDataSources1.add(dataSource);
        ETLSource etlSource1 = new ETLSource(baseDataSources1, formulaOperator1);
        etlSource1.getSourceKey();
        DataTransporter dataTransporter1 = new DataTransporter(etlSource1);
        dataTransporter1.work();
        for (int i = 1; i <= 1; i++) {
            ColumnIndexer<?> indexer = new ColumnIndexer(etlSource1, new ColumnKey(etlSource1.getMetadata().getColumnName(i)));
            indexer.work();
        }

        ETLOperator formulaOperator2 = new ColumnFormulaOperator("addField2", ColumnTypeConstants.ColumnType.NUMBER, "${付款金额} + ${付款金额} + ${付款金额}");
        List<DataSource> baseDataSources2 = new ArrayList<DataSource>();
        baseDataSources2.add(dataSource);
        ETLSource etlSource2 = new ETLSource(baseDataSources2, formulaOperator2);
        DataTransporter dataTransporter2 = new DataTransporter(etlSource2);
        dataTransporter2.work();
        for (int i = 1; i <= 1; i++) {
            ColumnIndexer<?> indexer = new ColumnIndexer(etlSource2, new ColumnKey(etlSource2.getMetadata().getColumnName(i)));
            indexer.work();
        }

        Segment dataSourceSegment = LocalSegmentProvider.getInstance().getSegment(dataSource.getSourceKey()).get(0);
        Segment etlSourceSegment1 = LocalSegmentProvider.getInstance().getSegment(etlSource1.getSourceKey()).get(0);
        Segment etlSourceSegment2 = LocalSegmentProvider.getInstance().getSegment(etlSource2.getSourceKey()).get(0);

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
        assertTrue(true);
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
    public void testFieldSameFormulaNotSame() throws Exception {
        DataSource dataSource = new QueryDBSource("select 付款金额,付款时间,now() as time from DEMO_CAPITAL_RETURN", "allTest");
        DataTransporter dataTransporter = new DataTransporter(dataSource);
        dataTransporter.work();
        for (int i = 1; i <= dataSource.getMetadata().getColumnCount(); i++) {
            ColumnIndexer<?> indexer = new ColumnIndexer(dataSource, new ColumnKey(dataSource.getMetadata().getColumnName(i)));
            indexer.work();
        }

        ETLOperator formulaOperator1 = new ColumnFormulaOperator("addField", ColumnTypeConstants.ColumnType.NUMBER, "${付款金额} + ${付款金额}");
        List<DataSource> baseDataSources1 = new ArrayList<DataSource>();
        baseDataSources1.add(dataSource);
        ETLSource etlSource1 = new ETLSource(baseDataSources1, formulaOperator1);
        etlSource1.getSourceKey();
        DataTransporter dataTransporter1 = new DataTransporter(etlSource1);
        dataTransporter1.work();
        for (int i = 1; i <= 1; i++) {
            ColumnIndexer<?> indexer = new ColumnIndexer(etlSource1, new ColumnKey(etlSource1.getMetadata().getColumnName(i)));
            indexer.work();
        }

        ETLOperator formulaOperator2 = new ColumnFormulaOperator("addField", ColumnTypeConstants.ColumnType.NUMBER, "${付款金额} + ${付款金额} + ${付款金额}");
        List<DataSource> baseDataSources2 = new ArrayList<DataSource>();
        baseDataSources2.add(dataSource);
        ETLSource etlSource2 = new ETLSource(baseDataSources2, formulaOperator2);
        DataTransporter dataTransporter2 = new DataTransporter(etlSource2);
        dataTransporter2.work();
        for (int i = 1; i <= 1; i++) {
            ColumnIndexer<?> indexer = new ColumnIndexer(etlSource2, new ColumnKey(etlSource2.getMetadata().getColumnName(i)));
            indexer.work();
        }

        Segment dataSourceSegment = LocalSegmentProvider.getInstance().getSegment(dataSource.getSourceKey()).get(0);
        Segment etlSourceSegment1 = LocalSegmentProvider.getInstance().getSegment(etlSource1.getSourceKey()).get(0);
        Segment etlSourceSegment2 = LocalSegmentProvider.getInstance().getSegment(etlSource2.getSourceKey()).get(0);

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
        assertTrue(true);
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
    public void testBothSame() throws Exception {
        DataSource dataSource = new QueryDBSource("select 付款金额,付款时间,now() as time2 from DEMO_CAPITAL_RETURN", "allTest");
        DataTransporter dataTransporter = new DataTransporter(dataSource);
        dataTransporter.work();
        for (int i = 1; i <= dataSource.getMetadata().getColumnCount(); i++) {
            ColumnIndexer<?> indexer = new ColumnIndexer(dataSource, new ColumnKey(dataSource.getMetadata().getColumnName(i)));
            indexer.work();
        }

        ETLOperator formulaOperator1 = new ColumnFormulaOperator("addField", ColumnTypeConstants.ColumnType.NUMBER, "${付款金额} + ${付款金额}");
        List<DataSource> baseDataSources1 = new ArrayList<DataSource>();
        baseDataSources1.add(dataSource);
        ETLSource etlSource1 = new ETLSource(baseDataSources1, formulaOperator1);
        etlSource1.getSourceKey();
        DataTransporter dataTransporter1 = new DataTransporter(etlSource1);
        dataTransporter1.work();
        for (int i = 1; i <= 1; i++) {
            ColumnIndexer<?> indexer = new ColumnIndexer(etlSource1, new ColumnKey(etlSource1.getMetadata().getColumnName(i)));
            indexer.work();
        }

        ETLOperator formulaOperator2 = new ColumnFormulaOperator("addField", ColumnTypeConstants.ColumnType.NUMBER, "${付款金额} + ${付款金额}");
        List<DataSource> baseDataSources2 = new ArrayList<DataSource>();
        baseDataSources2.add(dataSource);
        ETLSource etlSource2 = new ETLSource(baseDataSources2, formulaOperator2);
        DataTransporter dataTransporter2 = new DataTransporter(etlSource2);
        dataTransporter2.work();
        for (int i = 1; i <= 1; i++) {
            ColumnIndexer<?> indexer = new ColumnIndexer(etlSource2, new ColumnKey(etlSource2.getMetadata().getColumnName(i)));
            indexer.work();
        }

        Segment dataSourceSegment = LocalSegmentProvider.getInstance().getSegment(dataSource.getSourceKey()).get(0);
        Segment etlSourceSegment1 = LocalSegmentProvider.getInstance().getSegment(etlSource1.getSourceKey()).get(0);
        Segment etlSourceSegment2 = LocalSegmentProvider.getInstance().getSegment(etlSource2.getSourceKey()).get(0);

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
        assertTrue(true);
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
    public void testFormulaSameFieldNotSame() throws Exception {
        DataSource dataSource = new QueryDBSource("select 付款金额,付款时间,now() as time3 from DEMO_CAPITAL_RETURN", "allTest");
        DataTransporter dataTransporter = new DataTransporter(dataSource);
        dataTransporter.work();
        for (int i = 1; i <= dataSource.getMetadata().getColumnCount(); i++) {
            ColumnIndexer<?> indexer = new ColumnIndexer(dataSource, new ColumnKey(dataSource.getMetadata().getColumnName(i)));
            indexer.work();
        }

        ETLOperator formulaOperator1 = new ColumnFormulaOperator("addField", ColumnTypeConstants.ColumnType.NUMBER, "${付款金额} + ${付款金额}");
        List<DataSource> baseDataSources1 = new ArrayList<DataSource>();
        baseDataSources1.add(dataSource);
        ETLSource etlSource1 = new ETLSource(baseDataSources1, formulaOperator1);
        etlSource1.getSourceKey();
        DataTransporter dataTransporter1 = new DataTransporter(etlSource1);
        dataTransporter1.work();
        for (int i = 1; i <= 1; i++) {
            ColumnIndexer<?> indexer = new ColumnIndexer(etlSource1, new ColumnKey(etlSource1.getMetadata().getColumnName(i)));
            indexer.work();
        }

        ETLOperator formulaOperator2 = new ColumnFormulaOperator("addField2", ColumnTypeConstants.ColumnType.NUMBER, "${付款金额} + ${付款金额}");
        List<DataSource> baseDataSources2 = new ArrayList<DataSource>();
        baseDataSources2.add(dataSource);
        ETLSource etlSource2 = new ETLSource(baseDataSources2, formulaOperator2);
        DataTransporter dataTransporter2 = new DataTransporter(etlSource2);
        dataTransporter2.work();
        for (int i = 1; i <= 1; i++) {
            ColumnIndexer<?> indexer = new ColumnIndexer(etlSource2, new ColumnKey(etlSource2.getMetadata().getColumnName(i)));
            indexer.work();
        }

        Segment dataSourceSegment = LocalSegmentProvider.getInstance().getSegment(dataSource.getSourceKey()).get(0);
        Segment etlSourceSegment1 = LocalSegmentProvider.getInstance().getSegment(etlSource1.getSourceKey()).get(0);
        Segment etlSourceSegment2 = LocalSegmentProvider.getInstance().getSegment(etlSource2.getSourceKey()).get(0);

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
        assertTrue(true);
    }
}
