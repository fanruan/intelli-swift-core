package com.fr.bi.cal.analyze.cal.sssecret;

import java.util.Comparator;

public class GroupConnectionValue {

    private GroupConnectionValue parent;

    private GroupConnectionValue child;

    private Object data;

    private Comparator c;

    private NoneDimensionGroup currentValue;

    public GroupConnectionValue(Object data, Comparator c, NoneDimensionGroup currentValue) {
        this.data = data;
        this.c = c;
        this.currentValue = currentValue;
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