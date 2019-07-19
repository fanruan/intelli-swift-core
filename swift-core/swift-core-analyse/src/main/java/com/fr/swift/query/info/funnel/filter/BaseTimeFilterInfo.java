package com.fr.swift.query.info.funnel.filter;

import com.fr.swift.base.json.annotation.JsonProperty;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.query.info.bean.element.filter.FilterInfoBean;
import com.fr.swift.query.info.bean.element.filter.impl.NumberInRangeFilterBean;
import com.fr.swift.query.info.bean.element.filter.impl.value.RangeFilterValueBean;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * @author yee
 * @date 2019-07-18
 */
public abstract class BaseTimeFilterInfo implements TimeFilterInfo {

    @JsonProperty
    private String column;
    @JsonProperty
    private long timeStart;
    @JsonProperty
    private int timeSegCount;
    @JsonProperty
    private TimeFilterType type;


    public BaseTimeFilterInfo(String column, String timeStart, int timeSegCount, SimpleDateFormat dateFormat, TimeFilterType type) {
        this.column = column;
        this.timeSegCount = timeSegCount;
        this.type = type;
        try {
            this.timeStart = dateFormat.parse(timeStart).getTime();
        } catch (ParseException e) {
            SwiftLoggers.getLogger().warn("Funnel Query Parse {} error! use currentTimeMillis", timeStart);
            setTimeStart(System.currentTimeMillis());
        }
    }

    public BaseTimeFilterInfo() {
    }

    @Override
    public TimeFilterType getType() {
        return type;
    }

    public void setType(TimeFilterType type) {
        this.type = type;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    @Override
    public long getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(long timeStart) {
        this.timeStart = timeStart;
    }

    @Override
    public int getTimeSegCount() {
        return timeSegCount;
    }

    public void setTimeSegCount(int timeSegCount) {
        this.timeSegCount = timeSegCount;
    }

    @Override
    public long timeSegment() {
        return type.getTimeUnit().toMillis(1);
    }

    @Override
    public FilterInfoBean createFilter() {
        NumberInRangeFilterBean numberInRangeFilterBean = new NumberInRangeFilterBean();
        numberInRangeFilterBean.setColumn(getColumn());
        RangeFilterValueBean filterValue = new RangeFilterValueBean();
        filterValue.setStart(String.valueOf(getTimeStart()));
        filterValue.setStartIncluded(true);
        filterValue.setEnd(String.valueOf(calculateEndTime()));
        filterValue.setEndIncluded(true);
        numberInRangeFilterBean.setFilterValue(filterValue);
        return numberInRangeFilterBean;
    }

    protected abstract long calculateEndTime();
}
