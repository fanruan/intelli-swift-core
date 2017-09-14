package com.fr.bi.cal.generate.task;

import com.finebi.cube.conf.ICubeGenerateTask;
import com.finebi.cube.conf.ITaskCalculator;
import com.fr.bi.cal.generate.task.calculator.EmptyTaskCalculator;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Lucifer on 2017-5-19.
 *
 * @author Lucifer
 * @since Advanced FineBI Analysis 1.0
 */
public class EmptyCubeGenerateTask implements ICubeGenerateTask {

    private String taskType = "EmptyCubeGenerateTask";

    private long userId;

    public EmptyCubeGenerateTask(long userId) {
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
        return new EmptyTaskCalculator(this);
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
