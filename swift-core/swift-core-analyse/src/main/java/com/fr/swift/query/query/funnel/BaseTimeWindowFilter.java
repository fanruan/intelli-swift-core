package com.fr.swift.query.query.funnel;

import com.fr.swift.query.filter.match.MatchFilter;
import com.fr.swift.query.info.funnel.filter.TimeFilterInfo;
import com.fr.swift.query.info.funnel.group.time.TimeGroup;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author yee
 * @date 2019-07-04
 */
public abstract class BaseTimeWindowFilter implements TimeWindowFilter {
    protected final long dayWindow;

    protected long timeWindow;

    protected int numberOfDates;

    protected MatchFilter timeGroupMatchFilter;
    protected TimeFilterInfo filter;
    // 漏斗定义的顺序步骤
    protected IStep step;
    private long dateStart;
    private boolean allTime;
    protected String[] timeDetail;


    public BaseTimeWindowFilter(TimeWindowBean timeWindow, TimeGroup timeGroup, MatchFilter timeGroupMatchFilter, TimeFilterInfo info, IStep step) {
        this.timeWindow = timeWindow.toMillis();
        this.dayWindow = this.timeWindow / info.timeSegment() + 1;
        this.filter = info;
        this.timeGroupMatchFilter = timeGroupMatchFilter;
        this.dateStart = info.getTimeStart();
        this.numberOfDates = info.getTimeSegCount();
        if (timeGroup == TimeGroup.ALL) {
            allTime = true;
        } else {
            initTimeDetail(new SimpleDateFormat(timeGroup.getDatePattern()));
        }
        this.step = step;
    }

    private void initTimeDetail(SimpleDateFormat sdf) {
        this.timeDetail = new String[numberOfDates];
        for (int i = 0; i < timeDetail.length; i++) {
            long timeStep = i * filter.timeSegment();
            timeDetail[i] = sdf.format(new Date(dateStart + timeStep));
        }
    }

    protected int getDateIndex(long timestamp) {
        int dateIndex = 0;
        switch (filter.getType()) {
            case DAY:
                dateIndex = (int) TimeUnit.MILLISECONDS.toDays(timestamp - dateStart);
                break;
            case HOER:
                dateIndex = (int) TimeUnit.MILLISECONDS.toHours(timestamp - dateStart);
                break;
            case MINUTE:
                dateIndex = (int) TimeUnit.MILLISECONDS.toMinutes(timestamp - dateStart);
                break;
            case MONTH:
                dateIndex = (int) (TimeUnit.MILLISECONDS.toDays(timestamp - dateStart) / 30);
            default:
        }
        return dateIndex;
    }

    public boolean isAllTime() {
        return allTime;
    }
}
