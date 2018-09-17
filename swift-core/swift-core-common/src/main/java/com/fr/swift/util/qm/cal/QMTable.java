package com.fr.swift.util.qm.cal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Lyon on 2018/7/6.
 */
class QMTable {

    // 根据Row#numberOf1s对行进行分组
    private Map<Integer, Set<Row>> groupMap;

    QMTable(Map<Integer, Set<Row>> groupMap) {
        this.groupMap = groupMap;
    }

    public List<Integer> getGroupValues() {
        return new ArrayList<Integer>(groupMap.keySet());
    }

    public Set<Row> getRowList(int numberOf1s) {
        return groupMap.get(numberOf1s);
    }
}
