package com.finebi.cube.structure.column.date;

import com.finebi.cube.data.ICubeResourceDiscovery;
import com.finebi.cube.location.ICubeResourceLocation;
import com.finebi.cube.structure.column.BICubeLongColumn;
import com.fr.bi.base.ValueConverterFactory;
import com.fr.bi.stable.constant.DateConstant;
import com.fr.bi.stable.io.newio.NIOConstant;
import com.fr.bi.stable.utils.BICollectionUtils;

import java.util.Calendar;

/**
 * Created by wang on 2017/3/28.
 * 年月日时
 * @author wang
 */
public class BICubeYearMonthDayHourColumn extends BICubeDateSubColumn<Long> {
    public BICubeYearMonthDayHourColumn(ICubeResourceDiscovery discovery, ICubeResourceLocation currentLocation, BICubeDateColumn hostDataColumn) {
        super(discovery, currentLocation, hostDataColumn);
    }

    @Override
    protected Long convertDate(Long date) {
        //return date != null ? (Long) ValueConverterFactory.createDateValueConverter(DateConstant.DATE.YEAR_MONTH_DAY_HOUR).result2Value(date) : null;
        if(BICollectionUtils.isCubeNullKey(date)){
            return NIOConstant.LONG.NULL_VALUE;
        }else{
            return (Long) ValueConverterFactory.createDateValueConverter(DateConstant.DATE.YEAR_MONTH_DAY_HOUR).result2Value(date)  ;
        }
    }

    @Override
    protected Long convertDate(Long date, Calendar calendar) {
        //return date != null ? (Long) ValueConverterFactory.createDateValueConverter(DateConstant.DATE.YEAR_MONTH_DAY_HOUR).result2Value(date, calendar) : null;
        if(BICollectionUtils.isCubeNullKey(date)){
            return NIOConstant.LONG.NULL_VALUE;
        }else{
            return (Long) ValueConverterFactory.createDateValueConverter(DateConstant.DATE.YEAR_MONTH_DAY_HOUR).result2Value(date, calendar) ;
        }
    }


    @Override
    protected void initialColumnEntity(ICubeResourceLocation currentLocation) {
        selfColumnEntity = new BICubeLongColumn(discovery, currentLocation);
    }

    public long getGroupValue(int position) {
        return ((BICubeLongColumn)selfColumnEntity).getGroupValue(position);
    }

    /**
     * 获取空值表示对象
     *
     * @return
     */
    @Override
    public Long getCubeNullValue() {
        return NIOConstant.LONG.NULL_VALUE;
    }
}
