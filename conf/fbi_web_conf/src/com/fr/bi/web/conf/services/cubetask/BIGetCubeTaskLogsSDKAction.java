package com.fr.bi.web.conf.services.cubetask;

import com.finebi.cube.ICubeConfiguration;
import com.finebi.cube.conf.BICubeConfiguration;
import com.finebi.cube.conf.BICubeConfigureCenter;
import com.finebi.cube.conf.BICubeManagerProvider;
import com.finebi.cube.conf.CubeGenerationManager;
import com.finebi.cube.conf.table.BusinessTable;
import com.finebi.cube.utils.BITableKeyUtils;
import com.finebi.cube.utils.CubeUpdateUtils;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.conf.records.BICubeTaskRecord;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.engine.CubeTask;
import com.fr.bi.stable.utils.program.BIStringUtils;
import com.fr.bi.web.conf.AbstractBIConfigureAction;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


/**
 * This class created on 16-12-11.
 *
 * @author Kary
 * @since Advanced FineBI Analysis 1.0
 */
public class BIGetCubeTaskLogsSDKAction extends AbstractBIConfigureAction {

    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req, HttpServletResponse res) throws Exception {
        long userId = ServiceUtils.getCurrentUserID(req);
        printCurrentTasks(userId);
        printAllLogs(userId);
        printLastUpdateTimeByTableSource(userId);
        printSummary(userId);
        printTableUpdateInfo(userId, null);
    }

    private void printTableUpdateInfo(long userId, CubeTableSource cubeTableSource) {
        printTableUpdateStatus(userId, cubeTableSource);
        printTableUpdateHistory(userId, cubeTableSource);
    }

    /**
     * 表是否在更新
     *
     * @param userId
     * @param cubeTableSource
     */
    private boolean printTableUpdateStatus(long userId, CubeTableSource cubeTableSource) {
        String currentTaskId = printCurrentTasks(userId);
        if (!BIStringUtils.isEmptyString(currentTaskId)){
            if (currentTaskId.contains(cubeTableSource.getSourceID())){
                return true;
            }
        }
        return false;
    }

    /**
     * 获取特定表的更新历史
     * todo
     *
     * @param userId
     * @param cubeTableSource
     */
    private void printTableUpdateHistory(long userId, CubeTableSource cubeTableSource) {
        //获取所有cube更新的log，然后从里面找有没有相应的tableSource
    }

    /**
     * 获取当前正在进行的任务
     *
     * @param userId
     */
    private String printCurrentTasks(long userId) {
        BICubeManagerProvider cubeManager = CubeGenerationManager.getCubeManager();
        //正在跑的任务
        if (cubeManager.hasTask(userId)) {
            CubeTask currentTask = cubeManager.getGeneratingTask(userId);
//        taskId=BIStringUtils.append(specificTable.getSourceID(), specificBasicTableID)
            //获取是全局还是单表，要是单表的话是哪一张
            currentTask.getTaskId();
            Iterator<CubeTask> iterator = cubeManager.getWaitingTaskIterator(-999);
            while (iterator.hasNext()) {
                System.out.println(iterator.next().getTaskId());
            }
            return currentTask.getTaskId();
        }
        return null;
    }

    /**
     * 获取特定table上次更新时间
     *
     * @param userId
     */
    private void printLastUpdateTimeByTableSource(long userId) {
        ICubeConfiguration cubeConfiguration = BICubeConfiguration.getConf(Long.toString(userId));
        for (BusinessTable businessTable : BICubeConfigureCenter.getPackageManager().getAllTables(userId)) {
            CubeTableSource source = businessTable.getTableSource();
            Set<CubeTableSource> tableLayers = CubeUpdateUtils.toSet(source.createGenerateTablesList());
            for (CubeTableSource layer : tableLayers) {
                long updateTime = BITableKeyUtils.getLastUpdateTime(layer, cubeConfiguration);
                System.out.println(BIStringUtils.append("tableName:" + layer.getTableName(), "lastUpdateTime:" + new Date(updateTime)));
            }
        }

    }

    /**
     * 获取完整的log信息
     *
     * @param userId
     */
    private void printAllLogs(long userId) {
        List<BICubeTaskRecord> allLogs = BIConfigureManagerCenter.getCubeTaskRecordManager().getSingleUserBICubeTaskRecordManager(userId).getCubeTaskRecords();
        System.out.println(allLogs.toString());
    }

    /**
     * 获取简单的更新起止时间
     *
     * @param userId
     * @return
     * @throws JSONException
     */
    private JSONObject printSummary(long userId) throws JSONException {
        List<BICubeTaskRecord> recordList = BIConfigureManagerCenter.getCubeTaskRecordManager().getSingleUserBICubeTaskRecordManager(userId).getCubeTaskRecords();
//        CHECK(0), ALL(1), SINGLE(2), BUILD(3), INSTANT(4), EMPTY(5), PART(6);
//        Status:SUCCESS, WRONG, WAITING, NULL, PREPARING, PREPARED, START, RUNNING, LOADING, LOADED, REPLACING, END
        JSONObject jsonObject = new JSONObject();
        for (BICubeTaskRecord record : recordList) {
            JSONObject js = new JSONObject();
            js.put("startTime", new Date(record.getStartTime()));
            js.put("endTime", new Date(record.getEndTime()));
//            js.put("allSingleLayers", record.getAllSingleSourceLayers());
//            js.put("allPath", record.getAllRelationPaths());
//            if (record.getFinalResult() == Status.WRONG) {
//                Map tablesMap = new HashMap();
//                for (BITableErrorLog log : record.getErrorTableLogs()) {
//                    tablesMap.put(log.getPersistentTable().getTableName(), log.getError_text());
//                }
//                js.put("errorTable", tablesMap.toString());
//                Map errorPaths = new HashMap();
//                for (BIConnectionErrorLog errorLog : record.getErrorPathLogs()) {
//                    errorPaths.put(errorLog.getColumnFieldKey().getRelations().toString(), errorLog.getError_text());
//                }
//                js.put("errorPaths", errorPaths.toString());
//            }
            jsonObject.put(new Date(record.getStartTime()).toString(), js);
        }
        System.out.println(jsonObject.toString());
        return jsonObject;
    }

    @Override
    public String getCMD() {
        return "get_cube_records";
    }
}
