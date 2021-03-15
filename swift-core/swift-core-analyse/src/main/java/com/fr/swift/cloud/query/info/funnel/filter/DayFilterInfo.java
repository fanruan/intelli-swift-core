package com.fr.swift.cloud.query.info.funnel.filter;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * 从dayStart开始取numberOfDays天
 *
 * @author lyon
 * @date 2018/12/28
 */
public class DayFilterInfo extends BaseTimeFilterInfo {

    public DayFilterInfo(String column, String timeStart, int timeSegCount) {
        super(column, timeStart, timeSegCount, new SimpleDateFormat("yyyyMMdd"), TimeFilterType.DAY);

    }

    public DayFilterInfo() {
    }


    @Override
    protected long calculateEndTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(getTimeStart());
        calendar.add(Calendar.DATE, getTimeSegCount());
        return calendar.getTimeInMillis();
    }
}
