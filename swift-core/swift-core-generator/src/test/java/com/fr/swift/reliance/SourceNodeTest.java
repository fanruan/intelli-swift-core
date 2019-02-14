//package com.fr.swift.reliance;
//
//import com.fr.swift.source.DataSource;
//import com.fr.swift.source.EtlDataSource;
//import com.fr.swift.source.SourceKey;
//import com.fr.swift.source.db.TableDBSource;
//import com.fr.swift.source.etl.EtlSource;
//import com.fr.swift.util.SourceNodeUtils;
//import junit.framework.TestCase;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
///**
// * This class created on 2018/4/11
// *
// * @author Lucifer
// * @description
// * @since Advanced FineBI 5.0
// */
//public class SourceNodeTest extends TestCase {
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
//    public void testSourceNode() {
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
//        List<DataSource> origins = new ArrayList<DataSource>();
//        origins.add(dataSourceA);
//        List<DataSource> reliances = new ArrayList<DataSource>();
//        reliances.add(etlFormula);
//        reliances.add(etlDataSourceABCD);
//        reliances.add(etlDataSourceAB);
//        reliances.add(dataSourceA);
//
//        SourceReliance sourceReliance = new SourceReliance(origins, reliances);
//        SourceNodeUtils.calculateSourceNode(sourceReliance);
//        Map<SourceKey, SourceNode> map = sourceReliance.getHeadNodes();
//        assertEquals(map.size(), 1);
//        assertTrue(map.containsKey(dataSourceA.getSourceKey()));
//
//        assertEquals(map.get(dataSourceA.getSourceKey()).next().size(), 1);
//        assertEquals(map.get(dataSourceA.getSourceKey()).next().get(0).getSourceKey(), etlDataSourceAB.getSourceKey());
//
//        assertEquals(map.get(dataSourceA.getSourceKey()).next().get(0).next().size(), 1);
//        assertEquals(map.get(dataSourceA.getSourceKey()).next().get(0).next().get(0).getSourceKey(), etlDataSourceABCD.getSourceKey());
//
//        assertEquals(map.get(dataSourceA.getSourceKey()).next().get(0).next().get(0).next().size(), 1);
//        assertEquals(map.get(dataSourceA.getSourceKey()).next().get(0).next().get(0).next().get(0).getSourceKey(), etlFormula.getSourceKey());
//
//        assertEquals(map.get(dataSourceA.getSourceKey()).next().get(0).next().get(0).next().get(0).next().size(), 0);
//    }
//}
