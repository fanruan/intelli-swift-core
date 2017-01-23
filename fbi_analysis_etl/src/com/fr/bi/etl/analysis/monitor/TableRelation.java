package com.fr.bi.etl.analysis.monitor;

import com.fr.general.ComparatorUtils;

/**
 * Created by daniel on 2017/1/22.
 */
public class TableRelation {
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
        int deep = 0;
        if(next != null){
            deep =  next.getDeep();
        }
        return  ++deep;
    }

    public TableRelation getNext() {
        return next;
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
}
