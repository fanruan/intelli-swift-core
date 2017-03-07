package com.fr.bi.cal.analyze.cal.sssecret;


import com.fr.bi.cal.analyze.cal.store.GroupKey;
import com.fr.bi.stable.report.key.TargetGettingKey;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PageIteratorGroup implements Serializable {

    private static final long serialVersionUID = -5143454742110936232L;
    private Map<GroupKey, IRootDimensionGroup> rowMap = new ConcurrentHashMap<GroupKey, IRootDimensionGroup>();

    private Map<GroupKey, IRootDimensionGroup> columnMap = new ConcurrentHashMap<GroupKey, IRootDimensionGroup>();

    public PageIteratorGroup(Map<GroupKey, IRootDimensionGroup> rowMap) {
        this.rowMap = rowMap;
    }

    public PageIteratorGroup(Map<GroupKey, IRootDimensionGroup> rowMap, Map<GroupKey, IRootDimensionGroup> columnMap) {
        this(rowMap);
        this.columnMap = columnMap;
    }

    public Map<GroupKey, IRootDimensionGroup> getRowGroup() {
        return rowMap;
    }

    public Map<GroupKey, IRootDimensionGroup> getColumnGroup() {
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