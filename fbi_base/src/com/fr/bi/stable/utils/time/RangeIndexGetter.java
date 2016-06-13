package com.fr.bi.stable.utils.time;

import com.finebi.cube.api.ICubeColumnIndexReader;
import com.fr.bi.stable.data.key.date.BIDay;
import com.fr.bi.stable.gvi.GVIFactory;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.utils.BICollectionUtils;

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


    //算两个区间之间的index
    public GroupValueIndex createRangeIndex(BIDay start, BIDay end) {
        GroupValueIndex gvi = GVIFactory.createAllEmptyIndexGVI();
        if(start == null){
            Number firstYear = (Number)yearMap.firstKey();
            Number firstMonth = (Number) monthMap.firstKey();
            Number firstDay = (Number)dayMap.firstKey();
            if(firstYear == null) {
                firstYear = 0;
            }
            if(firstMonth == null) {
                firstMonth = 0;
            }
            if(firstDay == null) {
                firstDay = 0;
            }
            start = new BIDay(Integer.parseInt(firstYear.toString()), Integer.parseInt(firstMonth.toString()), Integer.parseInt(firstDay.toString()));
        }
        if(end == null){
            Number lastYear = (Number)BICollectionUtils.lastUnNullKey(yearMap);
            Number lastMonth = (Number) BICollectionUtils.lastUnNullKey(monthMap);
            Number lastDay = (Number)BICollectionUtils.lastUnNullKey(dayMap);
            if(lastYear == null) {
                lastYear = 0;
            }
            if(lastMonth == null) {
                lastMonth = 0;
            }
            if(lastDay == null) {
                lastDay = 0;
            }
            end = new BIDay(Integer.parseInt(lastYear.toString()), Integer.parseInt(lastMonth.toString()), Integer.parseInt(lastDay.toString()));
        }
        if (start.compareTo(end) <= 0){
            if(start.getYear() != end.getYear()) {
                //年中
                gvi = createRangeIndex(yearMap, start.getYear() + 1, end.getYear() - 1);
                //年头
                BIDay newEnd = new BIDay(start.getYear(), BIDateUtils.MAX_MONTH, BIDateUtils.MAX_DAY);
                gvi.or(createRangeIndex(start, newEnd));
                //年尾
                BIDay newStart = new BIDay(end.getYear(), 0, 0);
                gvi.or(createRangeIndex(newStart, end));
            } else if(start.getMonth() != end.getMonth()){
                GroupValueIndex year = yearMap.getGroupIndex(new Integer[]{start.getYear()})[0];
                if(year != null) {
                    //月中
                    gvi.or(year.AND(createRangeIndex(monthMap, start.getMonth() + 1, end.getMonth() - 1)));
                    //月头
                    BIDay newEnd = new BIDay(start.getYear(), start.getMonth(), BIDateUtils.MAX_DAY);
                    gvi.or(createRangeIndex(start, newEnd));
                    //月尾
                    BIDay newStart = new BIDay(end.getYear(), end.getMonth(), 0);
                    gvi.or(createRangeIndex(newStart, end));
                }
            } else if(start.getDay() <= end.getDay()) {
                //日
                GroupValueIndex year = yearMap.getGroupIndex(new Integer[]{start.getYear()})[0];
                if(year != null) {
                    GroupValueIndex month = monthMap.getGroupIndex(new Integer[]{start.getMonth()})[0];
                    if(month != null) {
                        gvi.or(year.AND(month).AND(createRangeIndex(dayMap, start.getDay(), end.getDay())));
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
