package com.fr.bi.conf.records;

import com.finebi.cube.relation.BITableSourceRelationPath;
import com.fr.bi.conf.log.BIConnectionErrorLog;
import com.fr.bi.conf.log.BITableErrorLog;
import com.fr.bi.stable.constant.Status;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.engine.CubeTaskType;

import java.util.Set;

/**
 * This class created on 16-12-9.
 *
 * @author Kary
 * @since Advanced FineBI Analysis 1.0
 */
public class SingleUserBICubeTaskRecordManager {
    private CubeTaskType taskType;
    private long startTime;
    private long costTime;
    private Status finalResult;
    private Set<CubeTableSource> allSingleSourceLayers;
    private Set<BITableSourceRelationPath> allRelationPaths;
private Set<BITableErrorLog> errorTableLogs;

    public Set<BIConnectionErrorLog> getErrorPathLogs() {
        return errorPathLogs;
    }

    public void setErrorPathLogs(Set<BIConnectionErrorLog> errorPathLogs) {
        this.errorPathLogs = errorPathLogs;
    }

    private Set<BIConnectionErrorLog> errorPathLogs;
    public CubeTaskType getTaskType() {
        return taskType;
    }

    public void setTaskType(CubeTaskType taskType) {
        this.taskType = taskType;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getCostTime() {
        return costTime;
    }

    public void setCostTime(long costTime) {
        this.costTime = costTime;
    }

    public Status getFinalResult() {
        return finalResult;
    }

    public void setFinalResult(Status finalResult) {
        this.finalResult = finalResult;
    }

    public Set<CubeTableSource> getAllSingleSourceLayers() {
        return allSingleSourceLayers;
    }

    public void setAllSingleSourceLayers(Set<CubeTableSource> allSingleSourceLayers) {
        this.allSingleSourceLayers = allSingleSourceLayers;
    }

    public Set<BITableSourceRelationPath> getAllRelationPaths() {
        return allRelationPaths;
    }

    public void setAllRelationPaths(Set<BITableSourceRelationPath> allRelationPaths) {
        this.allRelationPaths = allRelationPaths;
    }


    public void setErrorTableLogs(Set<BITableErrorLog> errorTableLogs) {
        this.errorTableLogs = errorTableLogs;
    }

    public Set<BITableErrorLog> getErrorTableLogs() {
        return errorTableLogs;
    }

    public void clear() {
        synchronized (this) {
            allRelationPaths.clear();
            allSingleSourceLayers.clear();
            errorPathLogs.clear();
            errorTableLogs.clear();
        }
    }
}
