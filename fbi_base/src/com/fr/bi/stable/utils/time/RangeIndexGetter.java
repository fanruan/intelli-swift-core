package com.fr.bi.stable.utils.time;

import com.finebi.cube.api.ICubeColumnIndexReader;
import com.fr.bi.stable.data.key.date.BIDay;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.gvi.GroupValueIndexOrHelper;
import com.fr.bi.stable.utils.DateUtils;
import com.fr.general.GeneralUtils;

/**
 * Created by daniel on 2016/6/8.
 */
public class RangeIndexGetter {

    private ICubeColumnIndexReader yearMap;
    private ICubeColumnIndexReader monthMap;
    private ICubeColumnIndexReader dayMap;

    public RangeIndexGetter(ICubeColumnIndexReader yearMap, ICubeColumnIndexReader monthMap, ICubeColumnIndexReader dayMap) {
        this.yearMap = yearMap;
        this.monthMap = monthMap;
        this.dayMap = dayMap;
    }

    private boolean isStartYear(BIDay day) {
        return day.getMonth() == 0 && day.getDay() == 1;
    }

    private boolean isEndYear(BIDay day) {
        return day.getMonth() == BIDateUtils.MAX_MONTH && day.getDay() == BIDateUtils.MAX_DAY;
    }

    private boolean isStartMonth(BIDay day) {
        return day.getDay() == 1;
    }

    private boolean isEndMonth(BIDay day) {
        return DateUtils.getLastDayOfMonth(day.getYear(), day.getMonth()) == day.getDay();
    }

    public GroupValueIndex createRangeIndex(BIDay start, BIDay end){
        GroupValueIndexOrHelper helper = new GroupValueIndexOrHelper();
        createRangeIndex(helper, start, end);
        return helper.compute();
    }

    //算两个区间之间的index
    private void createRangeIndex(GroupValueIndexOrHelper helper, BIDay start, BIDay end) {
        if (start == null) {
            Integer firstYear = GeneralUtils.objectToNumber(yearMap.firstKey()).intValue();
            start = new BIDay(firstYear, 0, 0);
        }
        if (end == null) {
            Integer lastYear = GeneralUtils.objectToNumber(yearMap.lastKey()).intValue();
            end = new BIDay(lastYear, BIDateUtils.MAX_MONTH, BIDateUtils.MAX_DAY);
        }
        if (start.compareTo(end) <= 0) {
            if (start.getYear() != end.getYear()) {
                boolean isStart = isStartYear(start);
                boolean isEnd = isEndYear(end);
                //年中
                createRangeIndex(yearMap, helper,  start.getYear() + (isStart ? 0 : 1), end.getYear() - (isEnd ? 0 : 1));
                //年头
                if (!isStart) {
                    BIDay newEnd = new BIDay(start.getYear(), BIDateUtils.MAX_MONTH, BIDateUtils.MAX_DAY);
                    createRangeIndex(helper, start, newEnd);
                }
                //年尾
                if (!isEnd) {
                    BIDay newStart = new BIDay(end.getYear(), 0, 0);
                    createRangeIndex(helper, newStart, end);
                }
            } else if (start.getMonth() != end.getMonth()) {
                GroupValueIndex year = yearMap.getGroupIndex(new Integer[]{start.getYear()})[0];
                if (year != null) {
                    boolean isStart = isStartMonth(start);
                    boolean isEnd = isEndMonth(end);

                    //月中
                    GroupValueIndexOrHelper momthHelper = new GroupValueIndexOrHelper();
                    createRangeIndex(monthMap, momthHelper, start.getMonth() + (isStart ? 0 : 1), end.getMonth() - (isEnd ? 0 : 1));
                    helper.add(year.AND(momthHelper.compute()));
                    //月头
                    if (!isStart) {
                        BIDay newEnd = new BIDay(start.getYear(), start.getMonth(), BIDateUtils.MAX_DAY);
                        createRangeIndex(helper, start, newEnd);
                    }
                    //月尾
                    if (!isEnd) {
                        BIDay newStart = new BIDay(end.getYear(), end.getMonth(), 0);
                        createRangeIndex(helper, newStart, end);
                    }
                }
            } else if (start.getDay() <= end.getDay()) {
                //日
                GroupValueIndex year = yearMap.getGroupIndex(new Integer[]{start.getYear()})[0];
                if (year != null) {
                    GroupValueIndex month = monthMap.getGroupIndex(new Integer[]{start.getMonth()})[0];
                    if (month != null) {
                        if (!isStartMonth(start) || !isEndMonth(end)) {
                            GroupValueIndexOrHelper dayHelper = new GroupValueIndexOrHelper();
                            createRangeIndex(dayMap, dayHelper, start.getDay(), end.getDay());
                            helper.add(year.AND(month).AND(dayHelper.compute()));
                        } else {
                            helper.add(year.AND(month));
                        }
                    }
                }
            }
        }
    }

    private void createRangeIndex(ICubeColumnIndexReader map, GroupValueIndexOrHelper helper, Integer start, Integer end) {
        for (int i = start; i <= end; i++) {
            GroupValueIndex temp = map.getGroupIndex(new Integer[]{i})[0];
            helper.add(temp);
        }
    }
}
