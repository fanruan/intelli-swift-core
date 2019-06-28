package com.fr.swift.query.info.bean.element.aggregation.funnel.group.time;

/**
 * @author yee
 * @date 2019-06-28
 */
public class ContinousTimeGroup implements TimeGroup {

    private TimeGroupType type;

    public ContinousTimeGroup(TimeGroupType type) {
        this.type = type;
    }

    public ContinousTimeGroup() {
    }

    @Override
    public TimeGroupType getType() {
        return type;
    }

    public void setType(TimeGroupType type) {
        this.type = type;
    }
}
