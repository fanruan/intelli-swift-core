package com.fr.swift.query.info.bean.element.aggregation.funnel.filter;

import com.fr.swift.base.json.annotation.JsonIgnore;
import com.fr.swift.base.json.annotation.JsonProperty;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.query.info.bean.element.filter.FilterInfoBean;
import com.fr.swift.query.info.bean.element.filter.impl.NumberInRangeFilterBean;
import com.fr.swift.query.info.bean.element.filter.impl.value.RangeFilterValueBean;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * 从dayStart开始取numberOfDays天
 * @author lyon
 * @date 2018/12/28
 *
 */
public class DayFilterInfo implements TimeFilterInfo {

    @JsonProperty
    private String column;
    @JsonProperty
    private String timeStart;
    @JsonProperty
    private int timeSegCount;
    @JsonProperty
    private TimeFilterType type = TimeFilterType.DAY;
    @JsonIgnore
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

    public DayFilterInfo() {
    }

    public DayFilterInfo(String column, String timeStart, int timeSegCount) {
        this.column = column;
        this.timeStart = timeStart;
        this.timeSegCount = timeSegCount;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    @Override
    public long getTimeStart() {
        try {
            return dateFormat.parse(timeStart).getTime();
        } catch (ParseException e) {
            SwiftLoggers.getLogger().warn("Funnel Query Parse {} error! use currentTimeMillis", timeStart);
            return System.currentTimeMillis();
        }
    }

    public void setTimeStart(String timeStart) {
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
        return TimeUnit.DAYS.toMillis(1);
    }

    @Override
    public TimeFilterType getType() {
        return type;
    }

    @Override
    public FilterInfoBean createFilter() throws ParseException {
        Calendar calendar = Calendar.getInstance();
        long start = dateFormat.parse(timeStart).getTime();
        calendar.setTimeInMillis(start);
        calendar.add(Calendar.DATE, timeSegCount);
        long end = calendar.getTimeInMillis();
        NumberInRangeFilterBean numberInRangeFilterBean = new NumberInRangeFilterBean();
        numberInRangeFilterBean.setColumn(column);
        RangeFilterValueBean filterValue = new RangeFilterValueBean();
        filterValue.setStart(String.valueOf(start));
        filterValue.setStartIncluded(true);
        filterValue.setEnd(String.valueOf(end));
        filterValue.setEndIncluded(true);
        numberInRangeFilterBean.setFilterValue(filterValue);
        return numberInRangeFilterBean;
    }
}
