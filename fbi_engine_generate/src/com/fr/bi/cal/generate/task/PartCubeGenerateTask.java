package com.fr.bi.cal.generate.task;

import com.finebi.cube.conf.ICubeGenerateTask;
import com.finebi.cube.conf.ITaskCalculator;
import com.fr.bi.cal.generate.task.calculator.PartTaskCalculator;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Lucifer on 2017-5-19.
 *
 * @author Lucifer
 * @since Advanced FineBI Analysis 1.0
 */
public class PartCubeGenerateTask implements ICubeGenerateTask {

    private String taskType = "PartCubeGenerateTask";

    private long userId;

    public PartCubeGenerateTask(long userId) {
        this.userId = userId;
    }

    @Override
    public long getUserId() {
        return userId;
    }

    @Override
    public String getTableSourceId() {
        return null;
    }

    @Override
    public Integer getUpdateType() {
        return null;
    }

    @Override
    public ITaskCalculator getTaskCalculator() {
        return new PartTaskCalculator(new CustomCubeGenerateTask(userId));
    }

    @Override
    public boolean isOk2Merge() {
        return false;
    }

    @Override
    public ICubeGenerateTask merge(ICubeGenerateTask mergeCubeGenerateTask) {
        return this;
    }

    @Override
    public String getTaskInfo() {
        return taskType;
    }

    @Override
    public Set<String> getAllsSourceIds() {
        return new HashSet<String>();
    }
}
