package com.fr.bi.conf.records;

import com.finebi.cube.conf.BILogManagerTestFactory;
import com.finebi.cube.relation.BITableSourceRelation;
import com.finebi.cube.relation.BITableSourceRelationPath;
import com.finebi.cube.tools.BIMemoryDataSourceFactory;
import com.finebi.cube.tools.BITableSourceRelationTestTool;
import com.fr.bi.conf.provider.BILogManagerProvider;
import com.fr.bi.stable.constant.Status;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.engine.CubeTaskType;

import java.util.HashSet;
import java.util.Set;

/**
 * This class created on 16-12-12.
 *
 * @author Kary
 * @since Advanced FineBI Analysis 1.0
 */
public class BICubeTaskRecordManagerTestFactory {
    public static BICubeTaskRecord generateBILogRecord(long userId,BILogManagerProvider logManager) {
        BICubeTaskRecord record = new BICubeTaskRecord();
        record.setErrorTableLogs(logManager.getErrorTables(userId));
        Set<BITableSourceRelationPath> allRelationPathSet = logManager.getAllRelationPathSet(userId);
        record.setAllRelationPaths(allRelationPathSet);
        record.setErrorPathLogs(logManager.getErrorPaths(userId));
        Set<CubeTableSource> allTableSourceSet = logManager.getAllTableSourceSet(userId);
        record.setAllSingleSourceLayers(allTableSourceSet);
        return record;
    }
    public static BICubeTaskRecord generateBILogRecord(long userId,BILogManagerProvider logManager,CubeTaskType cubeTaskType,Status status) {
        BICubeTaskRecord record = new BICubeTaskRecord(cubeTaskType, logManager.getStart(userId), logManager.getEndTime(userId), status);
        record.setErrorTableLogs(logManager.getErrorTables(userId));
        Set<BITableSourceRelationPath> allRelationPathSet = logManager.getAllRelationPathSet(userId);
        record.setAllRelationPaths(allRelationPathSet);
        record.setErrorPathLogs(logManager.getErrorPaths(userId));
        Set<CubeTableSource> allTableSourceSet = logManager.getAllTableSourceSet(userId);
        record.setAllSingleSourceLayers(allTableSourceSet);
        return record;
    }

    public static BILogManagerProvider generateBILogManagerTest(long userId) {
        Set<CubeTableSource> correctSources = BIMemoryDataSourceFactory.getDataSourceSetWithBC();
        Set<CubeTableSource> errSources = new HashSet<CubeTableSource>();
        errSources.add(BIMemoryDataSourceFactory.generateTableA());
        Set<BITableSourceRelation> correctPaths = new HashSet<BITableSourceRelation>();
        correctPaths.add(BITableSourceRelationTestTool.getABWithBICubeFieldSource());
        Set<BITableSourceRelation> errorPaths = new HashSet<BITableSourceRelation>();
        errorPaths.add(BITableSourceRelationTestTool.getBCWithBICubeFieldSource());
        return BILogManagerTestFactory.generateBILogs(userId, correctSources, errSources, correctPaths, errorPaths);
    }
}
