package com.fr.bi.cal.generate.task;

import com.finebi.cube.conf.ICubeGenerateTask;
import com.finebi.cube.conf.ITaskCalculator;
import com.fr.bi.cal.generate.task.calculator.SingleTaskCalculator;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Lucifer on 2017-5-19.
 *
 * @author Lucifer
 * @since Advanced FineBI Analysis 1.0
 */
public class SingleCubeGenerateTask implements ICubeGenerateTask {

    private String taskType = "SingleCubeGenerateTask";

    private String tableSourceId;
    private Integer updateType;
    private long userId;

    public SingleCubeGenerateTask(String tableSourceId, Integer updateType, long userId) {
        this.tableSourceId = tableSourceId;
        this.updateType = updateType;
        this.userId = userId;
    }

    @Override
    public String getTableSourceId() {
        return tableSourceId;
    }

    @Override
    public Integer getUpdateType() {
        return updateType;
    }

    @Override
    public long getUserId() {
        return userId;
    }

    @Override
    public ITaskCalculator getTaskCalculator() {
        return new SingleTaskCalculator(new CustomCubeGenerateTask(userId, tableSourceId, updateType));
    }

    @Override
    public boolean isOk2Merge() {
        return true;
    }

    @Override
    public ICubeGenerateTask merge(ICubeGenerateTask mergeCubeGenerateTask) {
        if (mergeCubeGenerateTask instanceof AllCubeGenerateTask) {
            return new AllCubeGenerateTask(userId);
        } else if (mergeCubeGenerateTask instanceof SingleCubeGenerateTask) {
            CustomCubeGenerateTask customCubeGenerateTask = new CustomCubeGenerateTask(userId, tableSourceId, updateType);
            customCubeGenerateTask.addTable(mergeCubeGenerateTask.getTableSourceId(), mergeCubeGenerateTask.getUpdateType());
            return customCubeGenerateTask;
        } else if (mergeCubeGenerateTask instanceof CustomCubeGenerateTask) {
            ((CustomCubeGenerateTask) mergeCubeGenerateTask).addTable(tableSourceId, updateType);
            return mergeCubeGenerateTask;
        }
        return this;
    }

    @Override
    public String getTaskInfo() {
        return taskType + "   sourceId:[" + tableSourceId + "],updateType:[" + updateType + "]";
    }

    @Override
    public Set<String> getAllsSourceIds() {
        Set<String> sourceIds = new HashSet<String>();
        sourceIds.add(tableSourceId);
        return sourceIds;
    }
}
