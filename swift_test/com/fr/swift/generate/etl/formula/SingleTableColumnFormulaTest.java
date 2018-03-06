package com.fr.swift.generate.etl.formula;

import com.fr.swift.generate.BaseTest;
import com.fr.swift.generate.history.ColumnIndexer;
import com.fr.swift.generate.history.DataTransporter;
import com.fr.swift.manager.LocalSegmentProvider;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.service.LocalSwiftServerService;
import com.fr.swift.source.ColumnTypeConstants;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.db.QueryDBSource;
import com.fr.swift.source.etl.ETLOperator;
import com.fr.swift.source.etl.ETLSource;
import com.fr.swift.source.etl.formula.ColumnFormulaOperator;

import java.util.ArrayList;
import java.util.List;

public class SingleTableColumnFormulaTest extends BaseTest {

    /**
     * A
     * \
     * A-formula
     *
     * @throws Exception
     */
    public void testOneColumnFormula() throws Exception {
        DataSource dataSource = new QueryDBSource("select * from DEMO_CAPITAL_RETURN", "allTest");
        DataTransporter dataTransporter = new DataTransporter(dataSource);
        dataTransporter.work();
        for (int i = 1; i <= dataSource.getMetadata().getColumnCount(); i++) {
            ColumnIndexer<?> indexer = new ColumnIndexer(dataSource, new ColumnKey(dataSource.getMetadata().getColumnName(i)));
            indexer.work();
        }
        ETLOperator formulaOperator = new ColumnFormulaOperator("addField", ColumnTypeConstants.ColumnType.NUMBER, "${付款金额} + ${付款金额}");
        List<DataSource> baseDataSources = new ArrayList<DataSource>();
        baseDataSources.add(dataSource);
        ETLSource etlSource = new ETLSource(baseDataSources, formulaOperator);
        DataTransporter dataTransporter1 = new DataTransporter(etlSource);
        dataTransporter1.work();
        for (int i = 1; i <= 1; i++) {
            ColumnIndexer<?> indexer = new ColumnIndexer(etlSource, new ColumnKey(etlSource.getMetadata().getColumnName(i)));
            indexer.work();
        }
        Segment dataSourceSegment = LocalSegmentProvider.getInstance().getSegment(dataSource.getSourceKey()).get(0);
        Segment etlSourceSegment = LocalSegmentProvider.getInstance().getSegment(etlSource.getSourceKey()).get(0);
        assertEquals(dataSourceSegment.getLocation().getPath(), etlSourceSegment.getLocation().getPath());

        Column originalColumn1 = dataSourceSegment.getColumn(new ColumnKey("付款金额"));
        Column originalColumn2 = etlSourceSegment.getColumn(new ColumnKey("付款金额"));
        Column addColumn = etlSourceSegment.getColumn(new ColumnKey("addField"));

        assertEquals(dataSourceSegment.getRowCount(), etlSourceSegment.getRowCount());

        for (int i = 0; i < dataSourceSegment.getRowCount(); i++) {
            assertEquals(originalColumn1.getDetailColumn().get(i), originalColumn2.getDetailColumn().get(i));
            assertEquals((((Long) originalColumn1.getDetailColumn().get(i)).doubleValue() * 2), addColumn.getDetailColumn().get(i));
        }
        assertTrue(true);
    }

    /**
     * A
     * \
     * A-formula
     * \
     * A-formula-formula
     *
     * @throws Exception
     */
    public void testTwoColumnFormula() throws Exception {
        DataSource dataSource = new QueryDBSource("select 付款金额,付款时间,合同ID from DEMO_CAPITAL_RETURN", "allTest");
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
        DataTransporter dataTransporter1 = new DataTransporter(etlSource1);
        dataTransporter1.work();
        for (int i = 1; i <= 1; i++) {
            ColumnIndexer<?> indexer = new ColumnIndexer(etlSource1, new ColumnKey(etlSource1.getMetadata().getColumnName(i)));
            indexer.work();
        }

        ETLOperator formulaOperator2 = new ColumnFormulaOperator("addField2", ColumnTypeConstants.ColumnType.NUMBER, "${addField} + ${addField}");
        List<DataSource> baseDataSources2 = new ArrayList<DataSource>();
        baseDataSources2.add(etlSource1);
        ETLSource etlSource2 = new ETLSource(baseDataSources2, formulaOperator2);
        DataTransporter dataTransporter2 = new DataTransporter(etlSource2);
        dataTransporter2.work();
        for (int i = 1; i <= 1; i++) {
            ColumnIndexer<?> indexer = new ColumnIndexer(etlSource2, new ColumnKey(etlSource2.getMetadata().getColumnName(i)));
            indexer.work();
        }

        Segment dataSourceSegment = LocalSegmentProvider.getInstance().getSegment(dataSource.getSourceKey()).get(0);
        Segment etlSourceSegment = LocalSegmentProvider.getInstance().getSegment(etlSource1.getSourceKey()).get(0);
        Segment etlSourceSegment2 = LocalSegmentProvider.getInstance().getSegment(etlSource2.getSourceKey()).get(0);

        assertEquals(dataSourceSegment.getLocation().getPath(), etlSourceSegment.getLocation().getPath());
        assertEquals(dataSourceSegment.getLocation().getPath(), etlSourceSegment2.getLocation().getPath());

        Column originalColumn1 = dataSourceSegment.getColumn(new ColumnKey("付款金额"));
        Column originalColumn2 = etlSourceSegment.getColumn(new ColumnKey("付款金额"));
        Column originalColumn3 = etlSourceSegment2.getColumn(new ColumnKey("付款金额"));

        Column addColumn = etlSourceSegment.getColumn(new ColumnKey("addField"));
        Column addColumn2 = etlSourceSegment2.getColumn(new ColumnKey("addField2"));

        assertEquals(dataSourceSegment.getRowCount(), etlSourceSegment.getRowCount());
        assertEquals(dataSourceSegment.getRowCount(), etlSourceSegment2.getRowCount());

        for (int i = 0; i < dataSourceSegment.getRowCount(); i++) {
            assertEquals(originalColumn1.getDetailColumn().get(i), originalColumn2.getDetailColumn().get(i));
            assertEquals(originalColumn1.getDetailColumn().get(i), originalColumn3.getDetailColumn().get(i));

            assertEquals((((Long) originalColumn1.getDetailColumn().get(i)).doubleValue() * 2), addColumn.getDetailColumn().get(i));
            assertEquals(((double) addColumn.getDetailColumn().get(i)) * 2, addColumn2.getDetailColumn().get(i));

        }
        assertTrue(true);
    }
}
