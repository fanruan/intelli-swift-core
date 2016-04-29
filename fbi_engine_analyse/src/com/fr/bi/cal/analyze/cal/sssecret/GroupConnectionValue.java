package com.fr.bi.cal.analyze.cal.sssecret;

import com.fr.bi.stable.report.result.DimensionCalculator;

import java.util.Comparator;

public class GroupConnectionValue {

    private GroupConnectionValue parent;

    private GroupConnectionValue child;

    private Object data;

    private Object key;

    private int groupRow;

    private Comparator c;

    private NoneDimensionGroup currentValue;

    private DimensionCalculator ck;


    public GroupConnectionValue(DimensionCalculator ck, Object data, Comparator c, NoneDimensionGroup currentValue) {
        this.ck = ck;
        this.data = data;
        if (data instanceof ComparableLinkedHashSet) {
            setSortKey(data.toString());
        }
        this.c = c;
        this.currentValue = currentValue;
    }

    public Object getKey() {
        if (key == null) {
            setSortKey(data == null ? null : data.toString());
        }
        return key;
    }

    public void setSortKey(Object key) {
        this.key = key;
    }

    public int getGroupRow() {
        return groupRow;
    }

    public void setGroupRow(int groupRow) {
        this.groupRow = groupRow;
    }

    public DimensionCalculator getCk() {
        return ck;
    }

    public Object getData() {
        return data;
    }

    public Comparator getComparator() {
        return c;
    }

    public void setComparator(Comparator c) {
        this.c = c;
    }

    public NoneDimensionGroup getCurrentValue() {
        return currentValue;
    }

    public GroupConnectionValue getChild() {
        return child;
    }

    public GroupConnectionValue getParent() {
        return parent;
    }

    public void setParent(GroupConnectionValue parent) {
        this.parent = parent;
        this.parent.child = this;
    }

}