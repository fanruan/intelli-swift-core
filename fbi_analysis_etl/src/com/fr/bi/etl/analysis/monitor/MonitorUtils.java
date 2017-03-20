package com.fr.bi.etl.analysis.monitor;

import com.finebi.cube.conf.table.BusinessTable;
import com.fr.bi.conf.utils.BIModuleUtils;
import com.fr.bi.etl.analysis.data.AnalysisCubeTableSource;
import com.fr.bi.etl.analysis.manager.BIAnalysisETLManagerCenter;
import com.fr.bi.stable.data.BITableID;
import com.fr.bi.stable.exception.BITableAbsentException;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;

import java.util.*;

/**
 * Created by daniel on 2017/1/22.
 */
public class MonitorUtils {


    public static List<TableRelation> getTableRelations(SimpleTable st, long userId){
        List<TableRelation> list = new ArrayList<TableRelation>();
        Set<SimpleTable> set = new HashSet<SimpleTable>();
        BusinessTable table = null;
        try {
            table = BIAnalysisETLManagerCenter.getBusiPackManager().getTable(st.getId(), userId);
            ((AnalysisCubeTableSource)table.getTableSource()).getParentAnalysisBaseTableIds(set);
            for(SimpleTable simpleTable : set) {
                TableRelation relation = new TableRelation(st).setNext(new TableRelation(simpleTable));
                list.add(relation);
                List<TableRelation> parentRelations = getTableRelations(simpleTable, userId);
                for(TableRelation r : parentRelations) {
                    TableRelation cloneRelation = relation.clone().setNext(r);
                    list.add(cloneRelation);
                }

            }
        } catch (BITableAbsentException e){
        }
        return list;
    }

    public static JSONObject calculateTablePosition(String id, long userId) throws Exception {
        BusinessTable bt = BIModuleUtils.getBusinessTableById(new BITableID(id));
        if(bt != null && bt.getTableSource() != null){
            TableRelationTree relations = ((AnalysisCubeTableSource)bt.getTableSource()).getAllProcessAnalysisTablesWithRelation();
            Map<SimpleTable, List<TableRelation>> relationMap = new LinkedHashMap<SimpleTable, List<TableRelation>>();
            relations.createTableRelations(relationMap);
            List<BITablePosition> res = new ArrayList<BITablePosition>();
            Map<Integer, List<BITablePosition>> levelHelper = new HashMap<Integer, List<BITablePosition>>();
            Map<SimpleTable, BITablePosition> positionHelper = new HashMap<SimpleTable, BITablePosition>();
            int maxLevel = extraRelation(res, positionHelper, levelHelper, relationMap);
            PositionCalculator calculator = new PositionCalculator(positionHelper, relationMap,  levelHelper, maxLevel);
            calculator.sort();
            calHealth(maxLevel, levelHelper, userId, relationMap);
            JSONObject jo = JSONObject.create();
            JSONArray jsonArray = getJsonArray(userId, res);
            JSONArray relationArray = getRelationArray(relationMap);
            jo.put("data", jsonArray);
            jo.put("relation", relationArray);
            return jo;
        }
        return JSONObject.create();
    }

    private static int extraRelation(List<BITablePosition> res, Map<SimpleTable, BITablePosition> positionHelper,  Map<Integer, List<BITablePosition>> levelHelper, Map<SimpleTable, List<TableRelation>> relationMap) {
        int maxLevel = 0;
        for(Map.Entry<SimpleTable, List<TableRelation>> entry : relationMap.entrySet()){
            BITablePosition tp = new BITablePosition(entry.getKey());
            res.add(tp);
            positionHelper.put(tp.getTable(), tp);
            for(TableRelation relation : entry.getValue()){
                tp.setColumn(Math.max(tp.getColumn(), relation.getDeep()));
            }
            List<BITablePosition> helper = levelHelper.get(tp.getColumn());
            if(helper == null){
                helper = new ArrayList<BITablePosition>();
                levelHelper.put(tp.getColumn(), helper);
            }
            helper.add(tp);
            maxLevel = Math.max(maxLevel, tp.getColumn());
        }
        return maxLevel;
    }

    public static JSONObject calculateTablePosition(long userId) throws Exception {
        List<BITablePosition> res = new ArrayList<BITablePosition>();
        Set<BusinessTable> allTables =  BIAnalysisETLManagerCenter.getBusiPackManager().getAllTables(userId);
        Map<SimpleTable, List<TableRelation>> relationMap = new HashMap<SimpleTable, List<TableRelation>>();
        int maxLevel = 0;
        Map<Integer, List<BITablePosition>> levelHelper = new HashMap<Integer, List<BITablePosition>>();
        Map<SimpleTable, BITablePosition> positionHelper = new HashMap<SimpleTable, BITablePosition>();
        Set<SimpleTable> totalTables = new HashSet<SimpleTable>();
        Set<SimpleTable> deletedTables = new HashSet<SimpleTable>();
        for(BusinessTable table : allTables) {
            totalTables.add(new SimpleTable( table.getID().getIdentityValue()));
        }
        maxLevel = getMaxLevel(userId, res, allTables, relationMap, maxLevel, levelHelper, positionHelper, totalTables, deletedTables);
        maxLevel = getMaxLevelWithDelete(userId, res, relationMap, maxLevel, levelHelper, positionHelper, deletedTables);
        PositionCalculator calculator = new PositionCalculator(positionHelper, relationMap,  levelHelper, maxLevel);
        calculator.sort();
        calHealth(maxLevel, levelHelper, userId, relationMap);
        JSONObject jo = JSONObject.create();
        JSONArray jsonArray = getJsonArray(userId, res);
        JSONArray relationArray = getRelationArray(relationMap);
        jo.put("data", jsonArray);
        jo.put("relation", relationArray);
        return jo;
    }


    private static int getMaxLevelWithDelete(long userId, List<BITablePosition> res, Map<SimpleTable, List<TableRelation>> relationMap, int maxLevel, Map<Integer, List<BITablePosition>> levelHelper, Map<SimpleTable, BITablePosition> positionHelper, Set<SimpleTable> deletedTables) {
        for(SimpleTable table : deletedTables) {
            BITablePosition tp = new BITablePosition(table);
            res.add(tp);
            positionHelper.put(tp.getTable(), tp);
            List<TableRelation> relation = getTableRelations(tp.getTable(), userId);
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
        return maxLevel;
    }

    private static int getMaxLevel(long userId, List<BITablePosition> res, Set<BusinessTable> allTables, Map<SimpleTable, List<TableRelation>> relationMap, int maxLevel, Map<Integer, List<BITablePosition>> levelHelper, Map<SimpleTable, BITablePosition> positionHelper, Set<SimpleTable> totalTables, Set<SimpleTable> deletedTables) {
        for(BusinessTable table : allTables) {
            BITablePosition tp = new BITablePosition(new SimpleTable( table.getID().getIdentityValue()));
            res.add(tp);
            positionHelper.put(tp.getTable(), tp);
            List<TableRelation> relation = getTableRelations(tp.getTable(), userId);
            for(TableRelation r : relation){
                r.checkDelete(totalTables, deletedTables);
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
        return maxLevel;
    }

    private static JSONArray getRelationArray(Map<SimpleTable, List<TableRelation>> relationMap) throws Exception {
        JSONArray relationArray = JSONArray.create();
        for(Map.Entry<SimpleTable, List<TableRelation>> entry : relationMap.entrySet()){
            List<TableRelation> tr = entry.getValue();
            for(TableRelation tableRelation : tr) {
                relationArray.put(tableRelation.createJSON());
            }
        }
        return relationArray;
    }


    private static void calHealth(int maxLevel, Map<Integer, List<BITablePosition>> levelHelper, long userId, Map<SimpleTable, List<TableRelation>> relationMap) {
        for(int i =0; i< maxLevel + 1; i++) {
            List<BITablePosition> ps = levelHelper.get(i);
            if(ps != null){
                for(BITablePosition bp : ps){
                    bp.getTable().calHealth(relationMap, userId);
                }
            }
        }
    }

    private static JSONArray getJsonArray(long userId, List<BITablePosition> res) throws Exception {
        JSONArray jsonArray = JSONArray.create();
        for(BITablePosition p : res){
            JSONObject j = p.createJSON(userId);
            j.put("h", p.getTable().getHealth());
            jsonArray.put(j);
        }
        return jsonArray;
    }


}
