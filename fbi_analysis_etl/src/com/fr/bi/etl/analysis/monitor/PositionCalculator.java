package com.fr.bi.etl.analysis.monitor;

import com.fr.third.org.apache.poi.hssf.record.formula.functions.Int;

import java.util.*;

/**
 * Created by daniel on 2017/1/23.
 */
public final class PositionCalculator {

    private java.util.Map<SimpleTable, BITablePosition> positionHelper;
    private Map<SimpleTable, List<TableRelation>> relationMap;
    private Map<Integer, List<BITablePosition>> levelHelper;
    private Map<SimpleTable, Set<SimpleTable>> topHelper = new HashMap<SimpleTable, Set<SimpleTable>>();
    private int maxLevel;

    PositionCalculator(Map<SimpleTable, BITablePosition> positionHelper, Map<SimpleTable, List<TableRelation>> relationMap,  Map<Integer, List<BITablePosition>> levelHelper, int maxLevel) {
        this.positionHelper = positionHelper;
        this.relationMap = relationMap;
        this.levelHelper = levelHelper;
        this.maxLevel = maxLevel;
        buildTopHelper();
    }

    private void buildTopHelper() {
       for(Map.Entry<SimpleTable, List<TableRelation>> entry : relationMap.entrySet()) {
           SimpleTable table = entry.getKey();
           for(TableRelation relation : entry.getValue()) {
               SimpleTable top = relation.getTop();
               if(!table.equals(top)) {
                   Set<SimpleTable> simpleTables = topHelper.get(top);
                   if(simpleTables == null) {
                       simpleTables = new HashSet<SimpleTable>();
                       topHelper.put(top, simpleTables);
                   }
                   simpleTables.add(table);
               }
           }
        }
    }

    private Set<SimpleTable> findSameTables(Set<SimpleTable> a, List<BITablePosition> positions) {
        Set<SimpleTable> res = new HashSet<SimpleTable>();
        if(a == null || positions == null){
            return res;
        }
        for(BITablePosition p : positions){
            if(a.contains(p.getTable())) {
                res.add(p.getTable());
            }
        }

        return res;
    }

    private int compareTable(Set<SimpleTable> a, Set<SimpleTable> b, List<BITablePosition> positions){
        Set<SimpleTable> simpleTables1 = findSameTables(a, positions);
        Set<SimpleTable> simpleTables2 = findSameTables(b, positions);
        for(SimpleTable table : simpleTables2) {
            if(simpleTables1.contains(table)){
                return 0;
            }
        }
        if(simpleTables1.size() == simpleTables2.size()){
            if(simpleTables1.isEmpty()){
                return -1;
            }
            if(simpleTables2.isEmpty()) {
                return  1;
            }
            return simpleTables1.iterator().next().getId().compareTo(simpleTables2.iterator().next().getId());
        }
        return simpleTables1.size() - simpleTables2.size();
    }

    private void sortBaseLine() {
        List<BITablePosition> biTablePositions = levelHelper.get(0);
        biTablePositions.sort(new Comparator<BITablePosition>() {
            public int compare(BITablePosition o1, BITablePosition o2) {
                Set<SimpleTable> t1 = topHelper.get(o1.getTable());
                Set<SimpleTable> t2 = topHelper.get(o2.getTable());
                for(int i = maxLevel; i > 0 ; i--) {
                    List<BITablePosition> tablePositions = levelHelper.get(i);
                    int res =  compareTable(t1, t2, tablePositions);
                    if(res != 0){
                        return res;
                    }
                }
                if(t1.size() == t2.size()) {
                    return o1.getTable().getId().compareTo(o2.getTable().getId());
                }
                return  t1.size() - t2.size();
            }
        });
        int i = 0;
        for(BITablePosition tp : biTablePositions){
            tp.setRow(i++);
        }
    }

    private void moveCenter() {
        for(int i = 1; i < maxLevel + 1; i++) {
            List<BITablePosition> biTablePositions = levelHelper.get(i);
            for(BITablePosition tp : biTablePositions) {
                List<TableRelation> parent = relationMap.get(tp.getTable());
                double row = 0;
                int count = parent.size();
                for(TableRelation relation : parent) {
                    TableRelation next = relation.getNext();
                    if(next == null){
                        count--;
                        continue;
                    }
                    row += positionHelper.get(next.getTable()).getRow();
                }
                row/=count;
                tp.setRow(Math.max(tp.getRow(), row));
            }
        }

        for(int i = 1; i < maxLevel + 1; i++) {
            List<BITablePosition> biTablePositions = levelHelper.get(i);
            Collections.sort(biTablePositions, new Comparator<BITablePosition>() {
                public int compare(BITablePosition o1, BITablePosition o2) {
                    return Double.compare(o1.getRow(), o2.getRow());
                }
            });
            BITablePosition last = null;
            for(BITablePosition tp : biTablePositions) {
                if(last != null){
                    while(tp.getRow() - last.getRow() < 1){
                        tp.setRow(tp.getRow() + 0.5);
                    }
                }
                last = tp;
            }
        }
    }

    public void sort() {
        sortBaseLine();
        moveCenter();
        //做两次
        moveCenter();
    }
}
