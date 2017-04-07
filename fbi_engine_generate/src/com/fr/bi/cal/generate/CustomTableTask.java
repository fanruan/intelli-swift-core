package com.fr.bi.cal.generate;

import com.fr.bi.cal.utils.Collection2StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lucifer on 2017-4-1.
 * 自定义更新表task
 *
 * @author Lucifer
 * @since Advanced FineBI Analysis 1.0
 */
public class CustomTableTask {
    private long userId;
    private List<String> baseTableSourceIdList;
    private List<Integer> updateTypeList;

    public CustomTableTask(long userId, String baseTableSourceId, Integer updateType) {
        this.userId = userId;
        if (baseTableSourceIdList == null || updateTypeList == null) {
            baseTableSourceIdList = new ArrayList<String>();
            updateTypeList = new ArrayList<Integer>();
        }
        baseTableSourceIdList.add(baseTableSourceId);
        updateTypeList.add(updateType);
    }

    public CustomTableTask(long userId, List<String> baseTableSourceIdList, List<Integer> updateTypeList) {
        this.userId = userId;

        if (this.baseTableSourceIdList == null || this.updateTypeList == null) {
            this.baseTableSourceIdList = baseTableSourceIdList;
            this.updateTypeList = updateTypeList;
        } else {
            this.baseTableSourceIdList.addAll(baseTableSourceIdList);
            this.updateTypeList.addAll(updateTypeList);
        }
    }

    public CustomTableTask taskMerge(long userId, String baseTableSourceId, Integer updateType) {
        if (baseTableSourceIdList == null || updateTypeList == null) {
            baseTableSourceIdList = new ArrayList<String>();
            updateTypeList = new ArrayList<Integer>();
        }
        baseTableSourceIdList.add(baseTableSourceId);
        updateTypeList.add(updateType);
        return this;
    }

    public CustomTableTask taskMerge(long userId, List<String> baseTableSourceIds, List<Integer> updateTypes) {
        if (baseTableSourceIdList == null || updateTypeList == null) {
            baseTableSourceIdList = new ArrayList<String>();
            updateTypeList = new ArrayList<Integer>();
        }
        baseTableSourceIdList.addAll(baseTableSourceIds);
        updateTypeList.addAll(updateTypes);
        return this;
    }

    public long getUserId() {
        return userId;
    }

    public List<String> getBaseTableSourceIdList() {
        return baseTableSourceIdList;
    }

    public List<Integer> getUpdateTypeList() {
        return updateTypeList;
    }

    public String baseTableSourceIdToString() {
        return Collection2StringUtils.collection2String(baseTableSourceIdList);
    }
}