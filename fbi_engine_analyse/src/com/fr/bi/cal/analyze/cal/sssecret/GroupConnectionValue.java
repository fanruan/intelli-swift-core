package com.fr.bi.cal.analyze.cal.sssecret;

public class GroupConnectionValue {

    private GroupConnectionValue parent;

    private GroupConnectionValue child;

    private Object data;

    private NoneDimensionGroup currentValue;

    public GroupConnectionValue(Object data, NoneDimensionGroup currentValue) {
        this.data = data;
        this.currentValue = currentValue;
    }

    public Object getData() {
        return data;
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