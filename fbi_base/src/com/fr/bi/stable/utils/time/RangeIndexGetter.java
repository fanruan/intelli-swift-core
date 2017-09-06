package com.fr.bi.stable.utils.time;

import com.finebi.cube.api.ICubeColumnIndexReader;
import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.bi.stable.data.key.date.BIDay;
import com.fr.bi.stable.gvi.GVIFactory;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.gvi.GroupValueIndexOrHelper;
import com.fr.bi.stable.io.sortlist.ArrayLookupHelper;
import com.fr.bi.stable.io.sortlist.MatchAndIndex;
import com.fr.bi.stable.utils.BICollectionUtils;
import com.fr.bi.stable.utils.DateUtils;
import com.fr.general.GeneralUtils;

import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;

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

    public GroupValueIndex createRangeIndex(BIDay start, BIDay end) {
        //先取出日期区间
        BIDay min = getMin();
        BIDay max = getMax();
        if (isEmptyParas(start, end, min, max)) {
            return GVIFactory.createAllEmptyIndexGVI();
        }
        //再将输入的参数与日期区间对比，如果不在区间，直接置空。
        start = start == null || start.getTime() <= min.getTime() ? null : start;
        end = end == null || end.getTime() >= max.getTime() ? null : end;
        //如果start，end都超过区间，则返回null，表示不过滤
        if (start == null && end == null) {
            return null;
        }
        GroupValueIndexOrHelper helper = new GroupValueIndexOrHelper();
        createYearRange(helper, start, end, min, max);
        createMonthRange(helper, start, end);
        createDayRange(helper, start, end);
        return helper.compute();
    }

    //如果是空区间，直接返回空索引
    private boolean isEmptyParas(BIDay start, BIDay end, BIDay min, BIDay max) {
        return min == null || max == null || (start != null && end != null && start.getTime() > end.getTime());
    }


    private void createYearRange(GroupValueIndexOrHelper helper, BIDay start, BIDay end, BIDay min, BIDay max) {
        //开始为空则从最小值开始，为年初则包含开始年
        int startYear = start == null ? min.getYear() : isStartYear(start) ? start.getYear() : start.getYear() + 1;
        //结束为空则到最大值截至，为年末则包含结束年
        int endYear = end == null ? max.getYear() : isEndYear(end) ? end.getYear() : end.getYear() - 1;
        createIndex(yearMap, startYear, endYear, helper);
    }

    //是否为年初
    private boolean isStartYear(BIDay day) {
        return day.getMonth() == 0 && day.getDay() == 1;
    }

    //是否为年末
    private boolean isEndYear(BIDay day) {
        return day.getMonth() == BIDateUtils.MAX_MONTH && day.getDay() == BIDateUtils.MAX_DAY;
    }

    private void createMonthRange(GroupValueIndexOrHelper helper, BIDay start, BIDay end) {
        //开头结尾是同一年，并且不同时是年初年末
        if (isSameYear(start, end) && isPartYear(start, end)) {
            int startMonth = isStartMonth(start) ? start.getMonth() : start.getMonth() + 1;
            int endMonth = isEndMonth(end) ? end.getMonth() : end.getMonth() - 1;
            createIndex(monthMap, new BIDay(start.getYear(), startMonth, 1).getTime(), new BIDay(end.getYear(), endMonth, 1).getTime(), helper);
            return;
        }
        if (start != null && !isStartYear(start)) {
            int startMonth = isStartMonth(start) ? start.getMonth() : start.getMonth() + 1;
            createIndex(monthMap, new BIDay(start.getYear(), startMonth, 1).getTime(), new BIDay(start.getYear(), Calendar.DECEMBER, 1).getTime(), helper);
        }
        if (end != null && !isEndYear(end)) {
            int endMonth = isEndMonth(end) ? end.getMonth() : end.getMonth() - 1;
            createIndex(monthMap, new BIDay(end.getYear(), Calendar.JANUARY, 1).getTime(), new BIDay(end.getYear(), endMonth, 1).getTime(), helper);
        }

    }

    //是同一年
    private boolean isSameYear(BIDay start, BIDay end) {
        return start != null && end != null && start.getYear() == end.getYear();
    }

    //开头不是年初或者结尾不是年末
    private boolean isPartYear(BIDay start, BIDay end) {
        return !isStartYear(start) || !isEndYear(end);
    }

    //是否为月初
    private boolean isStartMonth(BIDay day) {
        return day.getDay() == 1;
    }

    //是否为月末
    private boolean isEndMonth(BIDay day) {
        return DateUtils.getLastDayOfMonth(day.getYear(), day.getMonth()) == day.getDay();
    }

    private void createDayRange(GroupValueIndexOrHelper helper, BIDay start, BIDay end) {
        //开头结尾是同一月年，并且不同时是月初月末
        if (isSameMonth(start, end) && isPartMonth(start, end)) {
            createIndex(dayMap, start.getTime(), end.getTime(), helper);
            return;
        }
        if (start != null && !isStartMonth(start)) {
            createIndex(dayMap, start.getTime(), new BIDay(start.getYear(), start.getMonth(), DateUtils.getLastDayOfMonth(start.getYear(), start.getMonth())).getTime(), helper);
        }
        if (end != null && !isEndMonth(end)) {
            createIndex(dayMap, new BIDay(end.getYear(), end.getMonth(), 1).getTime(), end.getTime(), helper);
        }
    }

    //是同一年
    private boolean isSameMonth(BIDay start, BIDay end) {
        return start != null && end != null && start.getYear() == end.getYear() && start.getMonth() == end.getMonth();
    }

    //开头不是年初或者结尾不是年末
    private boolean isPartMonth(BIDay start, BIDay end) {
        return !isStartMonth(start) || !isEndMonth(end);
    }

    private void createIndex(ICubeColumnIndexReader reader, Number start, Number end, GroupValueIndexOrHelper helper) {
        MatchAndIndex startMatchAndIndex = ArrayLookupHelper.binarySearch(reader, start, BIBaseConstant.COMPARATOR.NUMBER.ASC);
        int startIndex = startMatchAndIndex.isMatch() ? startMatchAndIndex.getIndex() : startMatchAndIndex.getIndex() + 1;
        MatchAndIndex endMatchAndIndex = ArrayLookupHelper.binarySearch(reader, end, BIBaseConstant.COMPARATOR.NUMBER.ASC);
        //得到大于end的第一个，不管match不match都要+1。
        int endIndex = endMatchAndIndex.getIndex() + 1;
        for (int i = startIndex; i < endIndex; i++) {
            helper.add(reader.getGroupValueIndex(i));
        }
    }

    private BIDay getMin() {
        Iterator<Map.Entry<Object, GroupValueIndex>> iter = dayMap.iterator();
        Object v = null;
        //BI-6381 时间字段的最早时间要这样进行获取...
        while (iter.hasNext()) {
            v = iter.next().getKey();
            if (BICollectionUtils.isNotCubeNullKey(v)) {
                break;
            }
        }
        if (v == null) {
            return null;
        }
        return new BIDay(GeneralUtils.objectToNumber(v).longValue());
    }

    private BIDay getMax() {
        Object v = dayMap.lastKey();
        if (BICollectionUtils.isCubeNullKey(v)) {
            return null;
        }
        return new BIDay(GeneralUtils.objectToNumber(v).longValue());
    }

}
