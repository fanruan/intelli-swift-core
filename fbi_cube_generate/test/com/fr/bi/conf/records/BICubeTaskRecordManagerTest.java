package com.fr.bi.conf.records;

import com.fr.bi.conf.log.BIConnectionErrorLog;
import com.fr.bi.conf.provider.BICubeTaskRecordProvider;
import com.fr.bi.conf.provider.BILogManagerProvider;
import com.fr.bi.stable.constant.Status;
import com.fr.bi.stable.engine.CubeTaskType;
import com.fr.stable.bridge.StableFactory;
import junit.framework.TestCase;

/**
 * This class created on 16-12-12.
 *
 * @author Kary
 * @since Advanced FineBI Analysis 1.0
 */
public class BICubeTaskRecordManagerTest extends TestCase {

    protected BICubeTaskRecordProvider records = new BICubeTaskRecordManager();
    protected long userId;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        StableFactory.registerMarkedObject(BICubeTaskRecordProvider.XML_TAG, new BICubeTaskRecordManager());
        userId = -999;
    }

    public void testCorrectness() throws Exception {
        records.clear(userId);
        BILogManagerProvider biLog = BICubeTaskRecordManagerTestFactory.generateBILogManagerTest(userId);
        records.saveCubeTaskRecord(userId, BICubeTaskRecordManagerTestFactory.generateBILogRecord(userId, biLog, CubeTaskType.ALL, Status.WRONG));
        for (BICubeTaskRecord record : records.getAllTaskRecords()) {
            checkSizes(record, biLog);
            checkErrorText(record, biLog);
        }
    }

    public void testFailedResult() throws Exception {
        records.clear(userId);
        BILogManagerProvider biLog = BICubeTaskRecordManagerTestFactory.generateBILogManagerTest(userId);
        records.saveCubeTaskRecord(userId, BICubeTaskRecordManagerTestFactory.generateBILogRecord(userId, biLog, CubeTaskType.ALL, Status.WRONG));
        assertTrue(records.getAllTaskRecords().get(0).getFinalResult().name() == Status.WRONG.name());
    }

    public void testSuccessResult() throws Exception {
        records.clear(userId);
        BILogManagerProvider biLog = BICubeTaskRecordManagerTestFactory.generateBILogManagerTest(userId);
        records.saveCubeTaskRecord(userId, BICubeTaskRecordManagerTestFactory.generateBILogRecord(userId, biLog, CubeTaskType.ALL, Status.END));
        assertTrue(records.getAllTaskRecords().get(0).getFinalResult().name() == Status.END.name());
    }

    private void checkErrorText(BICubeTaskRecord record, BILogManagerProvider bilogs) {
        for (BIConnectionErrorLog recordLog : record.getErrorPathLogs()) {
            boolean matched = false;
            for (BIConnectionErrorLog biLog : bilogs.getErrorPaths(userId)) {
                if (recordLog.getColumnFieldKey().getRelations().equals(biLog.getColumnFieldKey().getRelations())) {
                    assertTrue(recordLog.getError_text().equals(biLog.getError_text()));
                    matched = true;
                }
            }
            assertTrue(matched);
        }
    }

    private void checkSizes(BICubeTaskRecord record, BILogManagerProvider bilog) {
        assertTrue(record.getStartTime() == bilog.getStart(userId));
        assertTrue(record.getEndTime() == bilog.getEndTime(userId));
        assertTrue(record.getAllSingleSourceLayers() == bilog.getAllTableSourceSet(userId));
        assertTrue(record.getAllRelationPaths().size() == bilog.getAllRelationPathSet(userId).size());
        assertTrue(record.getErrorPathLogs().size() == bilog.getErrorPaths(userId).size());
        assertTrue(record.getErrorTableLogs().size() == bilog.getErrorTables(userId).size());
    }

}