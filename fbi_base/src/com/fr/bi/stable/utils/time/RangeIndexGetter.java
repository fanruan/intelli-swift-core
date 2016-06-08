package com.fr.bi.stable.utils.time;

import com.finebi.cube.api.ICubeColumnIndexReader;
import com.fr.bi.stable.data.key.date.BIDay;
import com.fr.bi.stable.gvi.GVIFactory;
import com.fr.bi.stable.gvi.GroupValueIndex;

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
            start = new BIDay(Integer.parseInt(yearMap.firstKey().toString()), Integer.parseInt(monthMap.firstKey().toString()), Integer.parseInt(dayMap.firstKey().toString()));
        }
        if(end == null){
            end = new BIDay(Integer.parseInt(yearMap.lastKey().toString()), Integer.parseInt(monthMap.lastKey().toString()), Integer.parseInt(dayMap.lastKey().toString()));
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
            } else if(start.getDay() != end.getDay()) {
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
