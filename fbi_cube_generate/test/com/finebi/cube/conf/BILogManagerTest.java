package com.finebi.cube.conf;

import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.relation.BITableSourceRelation;
import com.finebi.cube.tools.BIMemoryDataSourceFactory;
import com.finebi.cube.tools.BITableSourceRelationTestTool;
import com.fr.bi.conf.log.BITableErrorLog;
import com.fr.bi.conf.provider.BILogManagerProvider;
import com.fr.bi.stable.data.source.CubeTableSource;
import junit.framework.TestCase;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by kary on 2016/6/12.
 */
public class BILogManagerTest extends TestCase {
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testLog() {
        try {
            long userId = -999;
            Set<CubeTableSource> correctSources = BIMemoryDataSourceFactory.getDataSourceSetWithBC();
            Set<CubeTableSource> errSources = new HashSet<CubeTableSource>();
            errSources.add(BIMemoryDataSourceFactory.generateTableA());
            Set<BITableSourceRelation> correctPaths = new HashSet<BITableSourceRelation>();
            correctPaths.add(BITableSourceRelationTestTool.getABWithBICubeFieldSource());
            Set<BITableSourceRelation> errorPaths = new HashSet<BITableSourceRelation>();
            errorPaths.add(BITableSourceRelationTestTool.getBCWithBICubeFieldSource());
            BILogManagerProvider biLog = BILogManagerTestFactory.generateBILogs(userId, correctSources, errSources, correctPaths, errorPaths);
            checkSize(userId, correctSources, errSources, correctPaths, errorPaths, biLog);
            Set<CubeTableSource> allSources = getAllSources(correctSources, errSources);
            checkColumns(userId, biLog, allSources);
            checkErrorTables(userId, biLog, errSources);
        } catch (Exception e) {
            BILoggerFactory.getLogger(this.getClass()).error(e.getMessage(), e);
            assertFalse(true);
        }
    }

    private void checkErrorTables(long userId, BILogManagerProvider biLog, Set<CubeTableSource> errSources) {
        int size = 0;
        for (CubeTableSource errSource : errSources) {
            for (BITableErrorLog errorLog : biLog.getErrorTables(userId)) {
                if (errorLog.getPersistentTable().getTableName().equals(errSource.getTableName())) {
                    size++;
                }
            }
        }
        assertTrue(size==errSources.size());
    }

    private void checkColumns(long userId, BILogManagerProvider biLog, Set<CubeTableSource> allSources) {
        for (CubeTableSource source : allSources) {
            for (CubeTableSource sourceInLog : biLog.getAllTableSourceSet(userId)) {
                if (source.getSourceID().equals(sourceInLog.getSourceID())) {
                    assertTrue(source.getFacetFields(allSources).size() == sourceInLog.getFacetFields(allSources).size());
                }
            }
        }
    }

    private Set<CubeTableSource> getAllSources(Set<CubeTableSource> correctSources, Set<CubeTableSource> errSources) {
        Set<CubeTableSource> allSources = new HashSet<CubeTableSource>();
        allSources.addAll(correctSources);
        allSources.addAll(errSources);
        return allSources;
    }

    private void checkSize(long userId, Set<CubeTableSource> correctSources, Set<CubeTableSource> errSources, Set<BITableSourceRelation> correctPaths, Set<BITableSourceRelation> errorPaths, BILogManagerProvider biLog) {
        assertTrue(biLog.getEndTime(userId) > biLog.getStart(userId));
        assertTrue(biLog.getErrorTables(userId).size() == errSources.size());
        assertTrue(biLog.getErrorPaths(userId).size() == errorPaths.size());
        assertTrue(biLog.getAllRelationPathSet(userId).size() == correctPaths.size() + errorPaths.size());
        assertTrue(biLog.getAllTableSourceSet(userId).size() == correctSources.size() + errSources.size());
    }


}
