package com.fr.bi.cal.generate.task;

import com.finebi.cube.conf.ICubeGenerateTask;
import com.finebi.cube.conf.ITaskCalculator;
import com.finebi.cube.relation.BITableRelation;
import com.fr.bi.cal.generate.task.calculator.CustomTaskCalculator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Lucifer on 2017-5-24.
 *
 * @author Lucifer
 * @since Advanced FineBI Analysis 1.0
 */
public class CustomCubeGenerateTask implements ICubeGenerateTask {

    private String taskType = "CustomCubeGenerateTask";
    private long userId;
    private Map<String, List<Integer>> sourceIdUpdateTypeMap;
    private List<BITableRelation> tableRelations;

    public CustomCubeGenerateTask(long userId) {
        this.userId = userId;
    }

    public CustomCubeGenerateTask(long userId, Map<String, List<Integer>> sourceIdUpdateTypeMap, List<BITableRelation> tableRelations) {
        this.userId = userId;
        this.sourceIdUpdateTypeMap = sourceIdUpdateTypeMap;
        this.tableRelations = tableRelations;
    }

    public CustomCubeGenerateTask(long userId, String tableSourceId, Integer updateType) {
        this.userId = userId;
        addTable(tableSourceId, updateType);
    }

    public void addTable(String tableSourceId, Integer updateType) {
        if (sourceIdUpdateTypeMap == null) {
            sourceIdUpdateTypeMap = new HashMap<String, List<Integer>>();
        }
        if (!sourceIdUpdateTypeMap.containsKey(tableSourceId)) {
            sourceIdUpdateTypeMap.put(tableSourceId, new ArrayList<Integer>());
        }
        sourceIdUpdateTypeMap.get(tableSourceId).add(updateType);
    }

    public void addTables(Map<String, List<Integer>> sourceIdUpdateTypeMap) {
        if (sourceIdUpdateTypeMap == null) {
            this.sourceIdUpdateTypeMap = sourceIdUpdateTypeMap;
            return;
        }
        for (Map.Entry<String, List<Integer>> entry : sourceIdUpdateTypeMap.entrySet()) {
            if (this.sourceIdUpdateTypeMap == null) {
                this.sourceIdUpdateTypeMap = new HashMap<String, List<Integer>>();
            }
            if (!this.sourceIdUpdateTypeMap.containsKey(entry.getKey())) {
                this.sourceIdUpdateTypeMap.put(entry.getKey(), entry.getValue());
            } else {
                this.sourceIdUpdateTypeMap.get(entry.getKey()).addAll(entry.getValue());
            }
        }
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
        return new CustomTaskCalculator(this);
    }

    public Map<String, List<Integer>> getSourceIdUpdateTypeMap() {
        if (sourceIdUpdateTypeMap == null) {
            sourceIdUpdateTypeMap = new HashMap<String, List<Integer>>();
        }
        return sourceIdUpdateTypeMap;
    }

    public List<BITableRelation> getTableRelations() {
        if (tableRelations == null) {
            tableRelations = new ArrayList<BITableRelation>();
        }
        return tableRelations;
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
            addTable(mergeCubeGenerateTask.getTableSourceId(), mergeCubeGenerateTask.getUpdateType());
        } else if (mergeCubeGenerateTask instanceof CustomCubeGenerateTask) {
            addTables(((CustomCubeGenerateTask) mergeCubeGenerateTask).getSourceIdUpdateTypeMap());
        }
        return this;
    }

    @Override
    public String getTaskInfo() {
        Set<String> suorceIds = sourceIdUpdateTypeMap.keySet();
        StringBuffer buffer = new StringBuffer(taskType);
        buffer.append("  ").append("sourceId:[");
        for (String sourceId : suorceIds) {
            buffer.append(sourceId).append(",");
        }
        buffer.append("]");
        return buffer.toString();
    }

    @Override
    public Set<String> getAllsSourceIds() {
        Set<String> sourceIds = new HashSet<String>();
        sourceIds.addAll(sourceIdUpdateTypeMap.keySet());
        return sourceIds;
    }
}
