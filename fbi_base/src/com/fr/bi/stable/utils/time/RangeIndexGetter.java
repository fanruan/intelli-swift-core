package com.fr.bi.stable.utils.time;

import com.finebi.cube.api.ICubeColumnIndexReader;
import com.fr.bi.stable.data.key.date.BIDay;
import com.fr.bi.stable.gvi.GVIFactory;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.utils.BICollectionUtils;
import com.fr.bi.stable.utils.DateUtils;

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

    private boolean isStartYear(BIDay day){
        return day.getMonth() == 0 && day.getDay() == 1;
    }

    private boolean isEndYear(BIDay day){
        return day.getMonth() == BIDateUtils.MAX_MONTH && day.getDay() == BIDateUtils.MAX_DAY;
    }

    private boolean isStartMonth(BIDay day){
        return day.getDay() == 1;
    }

    private boolean isEndMonth(BIDay day){
        return DateUtils.getLastDayOfMonth(day.getYear(), day.getMonth()) == day.getDay();
    }

    //算两个区间之间的index
    public GroupValueIndex createRangeIndex(BIDay start, BIDay end) {
        GroupValueIndex gvi = GVIFactory.createAllEmptyIndexGVI();
        if(start == null){
            Integer firstYear = (Integer) yearMap.firstKey();
            start = new BIDay(firstYear, 0, 0);
        }
        if(end == null){
            Integer lastYear = (Integer) yearMap.lastKey();
            end = new BIDay(lastYear,  BIDateUtils.MAX_MONTH, BIDateUtils.MAX_DAY);
        }
        if (start.compareTo(end) <= 0){
            if(start.getYear() != end.getYear()) {
                boolean isStart = isStartYear(start);
                boolean isEnd = isEndYear(end);
                //年中
                gvi = createRangeIndex(yearMap, start.getYear() + (isStart ? 0 : 1), end.getYear() - (isEnd ? 0 : 1));
                //年头
                if (!isStart){
                    BIDay newEnd = new BIDay(start.getYear(), BIDateUtils.MAX_MONTH, BIDateUtils.MAX_DAY);
                    gvi.or(createRangeIndex(start, newEnd));
                }
                //年尾
                if (!isEnd){
                    BIDay newStart = new BIDay(end.getYear(), 0, 0);
                    gvi.or(createRangeIndex(newStart, end));
                }
            } else if(start.getMonth() != end.getMonth()){
                GroupValueIndex year = yearMap.getGroupIndex(new Integer[]{start.getYear()})[0];
                if(year != null) {
                    boolean isStart = isStartMonth(start);
                    boolean isEnd = isEndMonth(end);

                    //月中
                    gvi.or(year.AND(createRangeIndex(monthMap, start.getMonth() + (isStart ? 0 : 1), end.getMonth() - (isEnd ? 0 : 1))));
                    //月头
                    if (!isStart){
                        BIDay newEnd = new BIDay(start.getYear(), start.getMonth(), BIDateUtils.MAX_DAY);
                        gvi.or(createRangeIndex(start, newEnd));
                    }
                    //月尾
                    if (!isEnd){
                        BIDay newStart = new BIDay(end.getYear(), end.getMonth(), 0);
                        gvi.or(createRangeIndex(newStart, end));
                    }
                }
            } else if(start.getDay() <= end.getDay()) {
                //日
                GroupValueIndex year = yearMap.getGroupIndex(new Integer[]{start.getYear()})[0];
                if(year != null) {
                    GroupValueIndex month = monthMap.getGroupIndex(new Integer[]{start.getMonth()})[0];
                    if(month != null) {
                        if (!isStartMonth(start) || !isEndMonth(end)){
                            gvi.or(year.AND(month).AND(createRangeIndex(dayMap, start.getDay(), end.getDay())));
                        } else {
                            gvi.or(year.AND(month));
                        }
                    }
                }
            }
        }
        return gvi;
    }

    private GroupValueIndex createRangeIndex(ICubeColumnIndexReader map, Integer start, Integer end){
        GroupValueIndex gvi = GVIFactory.createAllEmptyIndexGVI();
        for (int i = start; i <= end; i ++){
            GroupValueIndex temp = map.getGroupIndex(new Integer[]{i})[0];
            gvi = gvi.or(temp);
        }
        return gvi;
    }

}
