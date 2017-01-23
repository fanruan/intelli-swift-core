package com.fr.bi.etl.analysis.monitor;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Created by daniel on 2017/1/23.
 */
public final class PositionCalculator {

    private java.util.Map<SimpleTable, BITablePosition> positionHelper;
    private Map<SimpleTable, List<TableRelation>> relationMap;
    private Map<Integer, List<BITablePosition>> levelHelper;
    private int maxLevel;

    PositionCalculator(Map<SimpleTable, BITablePosition> positionHelper, Map<SimpleTable, List<TableRelation>> relationMap,  Map<Integer, List<BITablePosition>> levelHelper, int maxLevel) {
        this.positionHelper = positionHelper;
        this.relationMap = relationMap;
        this.levelHelper = levelHelper;
        this.maxLevel = maxLevel;
    }


    public void sortBaseLine() {
        List<BITablePosition> biTablePositions = levelHelper.get(0);


    }

    public void moveCenter() {
        for(int i = 1; i < maxLevel; i++) {
            List<BITablePosition> biTablePositions = levelHelper.get(i);
            for(BITablePosition tp : biTablePositions) {
                List<TableRelation> parent = relationMap.get(tp.getTable());
                int row = 0;
                int count = parent.size();
                for(TableRelation relation : parent) {
                    TableRelation next = relation.getNext();
                    if(next == null){
                        count--;
                        continue;
                    }
                    row += positionHelper.get(next.getTable()).getColumn();
                }
                row/=count;
                tp.setColumn(row);
            }
        }

        for(int i = 1; i < maxLevel; i++) {
            List<BITablePosition> biTablePositions = levelHelper.get(i);
            Collections.sort(biTablePositions, new Comparator<BITablePosition>() {
                public int compare(BITablePosition o1, BITablePosition o2) {
                    return Integer.compare(o1.getColumn(), o2.getColumn());
                }
            });
            BITablePosition last = null;
            for(BITablePosition tp : biTablePositions) {
                if(last != null){
                    while(tp.getColumn() <= last.getColumn()){
                        tp.setColumn(tp.getColumn() + 1);
                    }
                }
                last = tp;
            }
        }
    }
}
