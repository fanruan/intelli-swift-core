package com.fr.bi.cal.analyze.cal.sssecret;


import com.fr.bi.stable.report.key.TargetGettingKey;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PageIteratorGroup {

    private Map<TargetGettingKey, IRootDimensionGroup> rowMap = new ConcurrentHashMap<TargetGettingKey, IRootDimensionGroup>();

    private Map<TargetGettingKey, IRootDimensionGroup> columnMap = new ConcurrentHashMap<TargetGettingKey, IRootDimensionGroup>();

    public PageIteratorGroup(Map<TargetGettingKey, IRootDimensionGroup> rowMap) {
        this.rowMap = rowMap;
    }

    public PageIteratorGroup(Map<TargetGettingKey, IRootDimensionGroup> rowMap, Map<TargetGettingKey, IRootDimensionGroup> columnMap) {
        this(rowMap);
        this.columnMap = columnMap;
    }

    public Map<TargetGettingKey, IRootDimensionGroup> getRowGroup() {
        return rowMap;
    }

    public Map<TargetGettingKey, IRootDimensionGroup> getColumnGroup() {
        return columnMap;
    }

    public void releaseIndex() {
        Iterator<IRootDimensionGroup> it = rowMap.values().iterator();
        while (it.hasNext()) {
            it.next().clearCache();
        }
        Iterator<IRootDimensionGroup> it1 = columnMap.values().iterator();
        while (it1.hasNext()) {
            it1.next().clearCache();
        }
    }

}