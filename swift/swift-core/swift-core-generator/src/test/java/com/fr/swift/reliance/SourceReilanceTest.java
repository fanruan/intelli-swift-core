//package com.fr.swift.reliance;
//
//import com.fr.swift.source.ColumnTypeConstants;
//import com.fr.swift.source.DataSource;
//import com.fr.swift.source.EtlDataSource;
//import com.fr.swift.source.SourceKey;
//import com.fr.swift.source.db.QueryDBSource;
//import com.fr.swift.source.db.TableDBSource;
//import com.fr.swift.source.etl.ETLOperator;
//import com.fr.swift.source.etl.EtlSource;
//import com.fr.swift.source.etl.formula.ColumnFormulaOperator;
//import com.fr.swift.util.DataSourceUtils;
//import junit.framework.TestCase;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * This class created on 2018/4/9
// *
// * @author Lucifer
// * @description
// * @since Advanced FineBI Analysis 1.0
// */
//public class SourceReilanceTest extends TestCase {
//
//
//    /**
//     * A            A
//     * \            \
//     * A-formula1   A-formula2
//     * A-formula1和A-formula2新增字段名不同但公式相同
//     * result:
//     * origin 1
//     * reliance 2
//     */
//    public void testSimpleReliance() {
//        DataSource dataSource = new QueryDBSource("select 付款金额,付款时间,now() as time2 from DEMO_CAPITAL_RETURN", "allTest");
//
//        ETLOperator formulaOperator1 = new ColumnFormulaOperator("addField", ColumnTypeConstants.ColumnType.NUMBER, "${付款金额} + ${付款金额}");
//        List<DataSource> baseDataSources1 = new ArrayList<DataSource>();
//        baseDataSources1.add(dataSource);
//        EtlSource etlSource1 = new EtlSource(baseDataSources1, formulaOperator1);
//
//        ETLOperator formulaOperator2 = new ColumnFormulaOperator("addField", ColumnTypeConstants.ColumnType.NUMBER, "${付款金额} + ${付款金额}");
//        List<DataSource> baseDataSources2 = new ArrayList<DataSource>();
//        baseDataSources2.add(dataSource);
//        EtlSource etlSource2 = new EtlSource(baseDataSources2, formulaOperator2);
//
//        List<DataSource> dataSources = new ArrayList<DataSource>();
//        dataSources.add(etlSource1);
//        dataSources.add(etlSource2);
//
//        List<DataSource> relianceSources = DataSourceUtils.calculateReliance(dataSource.getSourceKey(), dataSources);
//        SourceReliance sourceReliance = new SourceReliance(dataSources, relianceSources);
//        assertEquals(sourceReliance.getOrigins().size(), 1);
//        assertEquals(sourceReliance.getReliances().size(), 2);
//
//
//        List<SourceKey> sourceKeyList = new ArrayList<SourceKey>();
//        sourceKeyList.add(dataSource.getSourceKey());
//        relianceSources = DataSourceUtils.calculateReliances(sourceKeyList, dataSources);
//        sourceReliance = new SourceReliance(dataSources, relianceSources);
//        assertEquals(sourceReliance.getOrigins().size(), 1);
//        assertEquals(sourceReliance.getReliances().size(), 2);
//
//    }
//
//    /**
//     * A            A
//     * \            \
//     * A-formula1   A-formula2
//     * A-formula1和A-formula2公式不同
//     * result:
//     * origin 2
//     * reliance 3
//     */
//    public void testSimpleReliance2() {
//        DataSource dataSource = new QueryDBSource("select 付款金额,付款时间,now() as time2 from DEMO_CAPITAL_RETURN", "allTest");
//
//        ETLOperator formulaOperator1 = new ColumnFormulaOperator("addField", ColumnTypeConstants.ColumnType.NUMBER, "${付款金额} + ${付款金额}");
//        List<DataSource> baseDataSources1 = new ArrayList<DataSource>();
//        baseDataSources1.add(dataSource);
//        EtlSource etlSource1 = new EtlSource(baseDataSources1, formulaOperator1);
//
//        ETLOperator formulaOperator2 = new ColumnFormulaOperator("addField", ColumnTypeConstants.ColumnType.NUMBER, "${付款金额} + ${付款金额}+${付款金额}");
//        List<DataSource> baseDataSources2 = new ArrayList<DataSource>();
//        baseDataSources2.add(dataSource);
//        EtlSource etlSource2 = new EtlSource(baseDataSources2, formulaOperator2);
//
//        List<DataSource> dataSources = new ArrayList<DataSource>();
//        dataSources.add(etlSource1);
//        dataSources.add(etlSource2);
//
//        List<DataSource> relianceSources = DataSourceUtils.calculateReliance(dataSource.getSourceKey(), dataSources);
//        SourceReliance sourceReliance = new SourceReliance(dataSources, relianceSources);
//        assertEquals(sourceReliance.getOrigins().size(), 2);
//        assertEquals(sourceReliance.getReliances().size(), 3);
//
//        List<SourceKey> sourceKeyList = new ArrayList<SourceKey>();
//        sourceKeyList.add(dataSource.getSourceKey());
//        relianceSources = DataSourceUtils.calculateReliances(sourceKeyList, dataSources);
//        sourceReliance = new SourceReliance(dataSources, relianceSources);
//        assertEquals(sourceReliance.getOrigins().size(), 2);
//        assertEquals(sourceReliance.getReliances().size(), 3);
//    }
//
//    /**
//     * A  B   C  D
//     * \__|   \__|
//     * \      \
//     * E      F
//     * \_____|
//     * \
//     * G
//     * \
//     * G-formula
//     * <p>
//     * result:
//     * origin 1
//     * reliance 4
//     */
//    public void testComplexReliance() {
//        DataSource dataSourceA = new TableDBSource("A", "allTest");
//        DataSource dataSourceB = new TableDBSource("B", "allTest");
//        DataSource dataSourceC = new TableDBSource("C", "allTest");
//        DataSource dataSourceD = new TableDBSource("D", "allTest");
//
//        List<DataSource> dataSourceAB = new ArrayList<DataSource>();
//        dataSourceAB.add(dataSourceA);
//        dataSourceAB.add(dataSourceB);
//        List<DataSource> dataSourceCD = new ArrayList<DataSource>();
//        dataSourceCD.add(dataSourceC);
//        dataSourceCD.add(dataSourceD);
//        EtlDataSource etlDataSourceAB = new EtlSource(dataSourceAB, new TestOperator());
//        EtlDataSource etlDataSourceCD = new EtlSource(dataSourceCD, new TestOperator());
//
//        List<DataSource> dataSourceABCD = new ArrayList<DataSource>();
//        dataSourceABCD.add(etlDataSourceAB);
//        dataSourceABCD.add(etlDataSourceCD);
//        EtlDataSource etlDataSourceABCD = new EtlSource(dataSourceABCD, new TestOperator());
//
//        List<DataSource> etlFormulaSource = new ArrayList<DataSource>();
//        etlFormulaSource.add(etlDataSourceABCD);
//        EtlDataSource etlFormula = new EtlSource(etlFormulaSource, new TestOperator());
//
//
//        List<DataSource> dataSources = new ArrayList<DataSource>();
//        dataSources.addAll(dataSourceAB);
//        dataSources.addAll(dataSourceCD);
//        dataSources.addAll(dataSourceABCD);
//        dataSources.addAll(etlFormulaSource);
//        dataSources.add(etlFormula);
//
//        List<DataSource> relianceSources = DataSourceUtils.calculateReliance(dataSourceA.getSourceKey(), dataSources);
//        List<DataSource> origins = new ArrayList<DataSource>();
//        origins.add(dataSourceA);
//        SourceReliance sourceReliance = new SourceReliance(origins, relianceSources);
//        assertEquals(dataSources.size(), 8);
//        assertEquals(sourceReliance.getOrigins().size(), 1);
//        assertEquals(sourceReliance.getReliances().size(), 4);
//
//        List<SourceKey> sourceKeyList = new ArrayList<SourceKey>();
//        sourceKeyList.add(dataSourceA.getSourceKey());
//        relianceSources = DataSourceUtils.calculateReliances(sourceKeyList, dataSources);
//        sourceReliance = new SourceReliance(origins, relianceSources);
//        assertEquals(sourceReliance.getOrigins().size(), 1);
//        assertEquals(sourceReliance.getReliances().size(), 4);
//    }
//}
//
