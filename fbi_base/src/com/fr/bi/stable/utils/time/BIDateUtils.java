package com.fr.bi.stable.utils.time;

import com.finebi.cube.api.ICubeColumnIndexReader;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.constant.DateConstant;
import com.fr.bi.stable.data.BIBasicField;
import com.fr.bi.stable.data.key.date.BIDay;
import com.fr.bi.stable.gvi.GVIFactory;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.general.DateUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 *
 * Created by GUY on 2015/3/6.
 */
public class BIDateUtils {

    public static final Integer MAX_DAY = 31;

    public static final Integer MAX_MONTH = 11;

    /**
     * 生成cube开始生成时间
     *
     * @param hour 几点钟
     * @return Date日期
     */
    public static Date createStartDate(int hour, int frequency) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        if (c.getTime().before(new Date())) {
            c.add(Calendar.DAY_OF_MONTH, 1);
        }
        if (frequency != DBConstant.UPDATE_FREQUENCY.EVER_DAY) {
            int day_of_week = c.get(Calendar.DAY_OF_WEEK);
            int add = frequency - day_of_week;
            add = add >= 0 ? add : BIBaseConstant.SIZE7 + add;
            c.add(Calendar.DAY_OF_MONTH, add);
        }
        return c.getTime();
    }

    /**
     * 创建schecule时间
     *
     * @param frequency 频率
     * @return long值
     */
    public static long createScheduleTime(int hour, int frequency) {
        if (frequency == DBConstant.UPDATE_FREQUENCY.EVER_DAY) {
            return DateConstant.DATEDELTRA.DAY;
        } else if (frequency == DBConstant.UPDATE_FREQUENCY.EVER_MONTH) {
            return createMonthPeriod(hour);
        }
        return DateConstant.DATEDELTRA.WEEK;
    }

    private static long createMonthPeriod(int day) {
        return createMonthStartDate(day).getTime() - new Date().getTime();
    }

    private static Date createMonthStartDate(int day) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        int month = c.get(Calendar.MONTH);
        c.add(Calendar.DAY_OF_MONTH, 1);
        boolean isLastDay = c.get(Calendar.MONTH) != month;
        c.add(Calendar.DAY_OF_MONTH, -1);
        if (c.get(Calendar.DAY_OF_MONTH) < day && !isLastDay) {
            c.set(Calendar.DAY_OF_MONTH, day);     //可能加一个月
            if (c.get(Calendar.DAY_OF_MONTH) != day) {
                c.set(Calendar.DAY_OF_MONTH, 1);
                c.add(Calendar.DAY_OF_MONTH, -1);
            }
        } else {
            c.set(Calendar.DAY_OF_MONTH, 1);
            c.add(Calendar.MONTH, 1);
            c.set(Calendar.DAY_OF_MONTH, day);
            if (c.get(Calendar.DAY_OF_MONTH) != day) {
                c.set(Calendar.DAY_OF_MONTH, 1);
                c.add(Calendar.DAY_OF_MONTH, -1);
            }
        }
        return c.getTime();
    }

    public static long toSimpleDay(long t){
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(t);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTimeInMillis();
    }

    /**
     * 获取当前时间
     *
     * @return
     */
    public static String getCurrentDateTime() {
        return DateUtils.DATETIMEFORMAT2.format(new Date());
    }

	public static void checkDatefieldType(Map<BIKey, ? extends BIBasicField> map, BIKey key) {
		BIBasicField field = map.get(key);
		if(field == null || field.getFieldType() != DBConstant.COLUMN.DATE){
			throw NOT_DATE_FIELD_EXCEPTION;
		}
	}
	
	public static final RuntimeException NOT_DATE_FIELD_EXCEPTION = new RuntimeException("not date field");

    public static GroupValueIndex createFilterIndex(ICubeColumnIndexReader yearMap, ICubeColumnIndexReader monthMap, ICubeColumnIndexReader dayMap, BIDay start, BIDay end){
        GroupValueIndex gvi = GVIFactory.createAllEmptyIndexGVI();
        if(start == null){
            start = new BIDay(Integer.parseInt(yearMap.firstKey().toString()), Integer.parseInt(monthMap.firstKey().toString()), Integer.parseInt(dayMap.firstKey().toString()));
        }
        if(end == null){
            end = new BIDay(Integer.parseInt(yearMap.lastKey().toString()), Integer.parseInt(monthMap.lastKey().toString()), Integer.parseInt(dayMap.lastKey().toString()));
        }
        if (start.compareTo(end) < 0){
            gvi = createRangeIndex(yearMap, start.getYear() + 1, end.getYear());
            GroupValueIndex startYear = yearMap.getGroupIndex(new Integer[]{start.getYear()})[0];
            if (startYear != null){
                GroupValueIndex startMonth = monthMap.getGroupIndex(new Integer[]{start.getMonth()})[0];
                if (startMonth != null){
                    startMonth = startMonth.and(createRangeIndex(dayMap, start.getDay(), MAX_DAY + 1));
                    gvi = gvi.or(startYear.and(startMonth.or(createRangeIndex(monthMap, start.getMonth() + 1, MAX_MONTH + 1))));
                }
            }
            GroupValueIndex endYear = yearMap.getGroupIndex(new Integer[]{end.getYear()})[0];
            if (endYear != null){
                GroupValueIndex endMonth = monthMap.getGroupIndex(new Integer[]{end.getMonth()})[0];
                if (endMonth != null){
                    endMonth = endMonth.and(createRangeIndex(dayMap,0, end.getDay() + 1));
                    if(start.getYear() == end.getYear()){
                        gvi = gvi.and(endYear.and(endMonth.or(createRangeIndex(monthMap, 0, end.getMonth()))));
                    }else{
                        gvi = gvi.or(endYear.and(endMonth.or(createRangeIndex(monthMap, 0, end.getMonth()))));
                    }
                }
            }
        }
        return gvi;
    }

    private static GroupValueIndex createRangeIndex(ICubeColumnIndexReader map, Integer start, Integer end){
        GroupValueIndex gvi = GVIFactory.createAllEmptyIndexGVI();
        for (int i = start; i < end; i ++){
            GroupValueIndex temp = map.getGroupIndex(new Integer[]{i})[0];
            gvi = gvi.or(temp);
        }
        return gvi;
    }
	
}