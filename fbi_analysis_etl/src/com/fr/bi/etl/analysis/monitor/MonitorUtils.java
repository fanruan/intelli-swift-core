package com.fr.bi.etl.analysis.monitor;

import com.finebi.cube.conf.table.BusinessTable;
import com.fr.bi.etl.analysis.data.AnalysisCubeTableSource;
import com.fr.bi.etl.analysis.manager.BIAnalysisETLManagerCenter;
import com.fr.bi.stable.exception.BITableAbsentException;

import java.util.*;

/**
 * Created by daniel on 2017/1/22.
 */
public class MonitorUtils {


    public static List<TableRelation> getTableRelations(String tableId, long userId){
        List<TableRelation> list = new ArrayList<TableRelation>();
        Set<String> set = new HashSet<String>();
        BusinessTable table = null;
        try {
            table = BIAnalysisETLManagerCenter.getBusiPackManager().getTable(tableId, userId);
            ((AnalysisCubeTableSource)table.getTableSource()).getParentAnalysisBaseTableIds(set);
            for(String id : set) {
                TableRelation relation = new TableRelation(tableId).setNext(new TableRelation(id));
                list.add(relation);
                List<TableRelation> parentRelations = getTableRelations(id, userId);
                for(TableRelation r : parentRelations) {
                    TableRelation cloneRelation = relation.clone().setNext(r);
                    list.add(cloneRelation);
                }

            }
        } catch (BITableAbsentException e){
        }
        return list;
    }

    public static List<BITablePosition> calculateTablePosition(long userId) {
        List<BITablePosition> res = new ArrayList<BITablePosition>();
        Set<BusinessTable> allTables =  BIAnalysisETLManagerCenter.getBusiPackManager().getAllTables(userId);
        Map<SimpleTable, List<TableRelation>> relationMap = new HashMap<SimpleTable, List<TableRelation>>();
        int maxLevel = 0;
        Map<Integer, List<BITablePosition>> levelHelper = new HashMap<Integer, List<BITablePosition>>();
        Map<SimpleTable, BITablePosition> positionHelper = new HashMap<SimpleTable, BITablePosition>();
        for(BusinessTable table : allTables) {
            String tableId = table.getID().getIdentityValue();
            BITablePosition tp = new BITablePosition(new SimpleTable(tableId));
            res.add(tp);
            positionHelper.put(tp.getTable(), tp);
            List<TableRelation> relation = getTableRelations(tp.getTable().getId(), userId);
            for(TableRelation r : relation){
                int deep = r.getDeep();
                maxLevel = Math.max(maxLevel, deep);
                tp.setColumn(Math.max(tp.getColumn(), deep));
            }
            List<BITablePosition> helper = levelHelper.get(tp.getColumn());
            if(helper == null){
                helper = new ArrayList<BITablePosition>();
                levelHelper.put(tp.getColumn(), helper);
            }
            helper.add(tp);
            relationMap.put(tp.getTable(), relation);
        }
        PositionCalculator calculator = new PositionCalculator(positionHelper, relationMap,  levelHelper, maxLevel);
        calculator.sortBaseLine();
        calculator.moveCenter();
        return res;
    }



}
