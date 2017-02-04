package com.fr.bi.etl.analysis.monitor;

import com.fr.general.ComparatorUtils;
import com.fr.json.JSONCreator;
import com.fr.json.JSONObject;

import java.util.Set;

/**
 * Created by daniel on 2017/1/22.
 */
public class TableRelation implements JSONCreator{
    private SimpleTable table;
    private TableRelation next;

    public TableRelation(String id) {
        this.table = new SimpleTable(id);
    }

    public TableRelation(SimpleTable table) {
        this.table = table;
    }

    public TableRelation setNext(TableRelation next) {
        this.next = next;
        return this;
    }

    public String toString(){
        return next == null ? table.toString() : (table.toString() + "," + next.toString());
    }

    public int getDeep(){
        int deep = -1;
        if(next != null){
            deep =  next.getDeep();
        }
        return  ++deep;
    }

    public TableRelation getNext() {
        return next;
    }

    public SimpleTable getTop() {
        return  next == null ? table : next.getTop();
    }

    public SimpleTable getTable() {
        return table;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TableRelation that = (TableRelation) o;
        return ComparatorUtils.equals(table, that.table) && ComparatorUtils.equals(next, ((TableRelation) o).next);
    }

    @Override
    public int hashCode() {
        int result = table != null ? table.hashCode() : 0;
        result = 31 * result + (next != null ? next.hashCode() : 0);
        return result;
    }

    public TableRelation clone() {
        try {
            return (TableRelation) super.clone();
        } catch (CloneNotSupportedException e) {
            return new TableRelation(table);
        }
    }

    public JSONObject createJSON() throws Exception {
        JSONObject json = JSONObject.create();
        json.put("id", table.getId());
        if(next != null) {
            json.put("pid", next.getTable().getId());
        }
        return json;
    }

    public void checkDelete (Set<SimpleTable> all, Set<SimpleTable> delete) {
        if(!all.contains(table)){
            delete.add(table);
        }
        if(next != null){
            next.checkDelete(all, delete);
        }
    }
}
