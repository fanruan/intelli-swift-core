package com.fr.swift.cloud.query.info.funnel.filter;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * 从dayStart开始取numberOfDays天
 *
 * @author lyon
 * @date 2018/12/28
 */
public class MinuteFilterInfo extends BaseTimeFilterInfo {

    public MinuteFilterInfo(String column, String timeStart, int timeSegCount) {
        super(column, timeStart, timeSegCount, new SimpleDateFormat("yyyyMMddHHmm"), TimeFilterType.MINUTE);
    }

    public MinuteFilterInfo() {
    }

    @Override
    public long calculateEndTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(getTimeStart());
        calendar.add(Calendar.MINUTE, getTimeSegCount());
        return calendar.getTimeInMillis();

    }
}
