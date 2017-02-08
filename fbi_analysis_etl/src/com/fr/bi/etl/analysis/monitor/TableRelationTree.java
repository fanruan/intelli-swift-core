package com.fr.bi.etl.analysis.monitor;

import com.fr.stable.StringUtils;
import com.fr.stable.core.UUID;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by daniel on 2017/2/7.
 */
public class TableRelationTree {

    public static final TableRelationTree EMPTY = new TableRelationTree(new SimpleTable(StringUtils.EMPTY));

    private List<TableRelationTree> parents = new ArrayList<TableRelationTree>();

    private SimpleTable table;

    public TableRelationTree(SimpleTable table) {
        this.table = table;
    }

    public void addParent(TableRelationTree relationTree){
        parents.add(relationTree);
    }


    public void createTableRelations(Map<SimpleTable, List<TableRelation>> relationMap) {
        relationMap.put(table, createTableRelationList());
        for(TableRelationTree tree : parents){
            tree.createTableRelations(relationMap);
        }
    }


    private List<TableRelation> createTableRelationList(){
        List<TableRelation> relations = new ArrayList<TableRelation>();
        for(TableRelationTree tree : parents){
            List<TableRelation> relation = tree.createTableRelationList();
            if(relation.isEmpty()) {
                TableRelation tr = new TableRelation(table);
                tr.setNext(new TableRelation(tree.table));
                relations.add(tr);
            } else {
                for (TableRelation t : relation) {
                    TableRelation tr = new TableRelation(table);
                    tr.setNext(t);
                    relations.add(tr);
                }
            }
        }
        return relations;
    }


}
