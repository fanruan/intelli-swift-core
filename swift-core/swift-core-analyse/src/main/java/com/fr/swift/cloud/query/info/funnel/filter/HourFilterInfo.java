package com.fr.swift.cloud.query.info.funnel.filter;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * 从dayStart开始取numberOfDays天
 *
 * @author lyon
 * @date 2018/12/28
 */
public class HourFilterInfo extends BaseTimeFilterInfo {

    public HourFilterInfo(String column, String timeStart, int timeSegCount) {
        super(column, timeStart, timeSegCount, new SimpleDateFormat("yyyyMMddHH"), TimeFilterType.HOER);
    }

    public HourFilterInfo() {
    }

    @Override
    public long calculateEndTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(getTimeStart());
        calendar.add(Calendar.HOUR, getTimeSegCount());
        return calendar.getTimeInMillis();

    }
}
