package com.fr.swift.query.info.bean.element.aggregation.funnel;

import com.fr.swift.base.json.annotation.JsonProperty;

/**
 * Created by lyon on 2018/12/28.
 */
public class DayFilterBean {

    @JsonProperty
    private String column;
    @JsonProperty
    private String dayStart;
    @JsonProperty
    private int numberOfDays;

    public DayFilterBean() {
    }

    public DayFilterBean(String column, String dayStart, int numberOfDays) {
        this.column = column;
        this.dayStart = dayStart;
        this.numberOfDays = numberOfDays;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public String getDayStart() {
        return dayStart;
    }

    public void setDayStart(String dayStart) {
        this.dayStart = dayStart;
    }

    public int getNumberOfDays() {
        return numberOfDays;
    }

    public void setNumberOfDays(int numberOfDays) {
        this.numberOfDays = numberOfDays;
    }
}
