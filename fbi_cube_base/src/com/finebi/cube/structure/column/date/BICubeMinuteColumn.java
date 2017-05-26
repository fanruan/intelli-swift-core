package com.finebi.cube.structure.column.date;

import com.finebi.cube.data.ICubeResourceDiscovery;
import com.finebi.cube.location.ICubeResourceLocation;
import com.finebi.cube.structure.column.BICubeIntegerColumn;
import com.fr.bi.base.ValueConverterFactory;
import com.fr.bi.stable.constant.DateConstant;
import com.fr.bi.stable.io.newio.NIOConstant;
import com.fr.bi.stable.utils.BICollectionUtils;

import java.util.Calendar;


/**
 * This class created on 2017/03/28
 * 分0-59
 * @author wang
 */
public class BICubeMinuteColumn extends BICubeDateSubColumn<Integer> {
    public BICubeMinuteColumn(ICubeResourceDiscovery discovery, ICubeResourceLocation currentLocation, BICubeDateColumn hostDataColumn) {
        super(discovery, currentLocation, hostDataColumn);
    }

    @Override
    protected Integer convertDate(Long date) {
        //return date != null ? (Integer) ValueConverterFactory.createDateValueConverter(DateConstant.DATE.MINUTE).result2Value(date) : null;
        if(BICollectionUtils.isCubeNullKey(date)){
            return NIOConstant.INTEGER.NULL_VALUE;
        }else {
            return (Integer) ValueConverterFactory.createDateValueConverter(DateConstant.DATE.MINUTE).result2Value(date);
        }
    }

    @Override
    protected Integer convertDate(Long date, Calendar calendar) {
        //return date != null ? (Integer) ValueConverterFactory.createDateValueConverter(DateConstant.DATE.MINUTE).result2Value(date, calendar) : null;
        if(BICollectionUtils.isCubeNullKey(date)){
            return NIOConstant.INTEGER.NULL_VALUE;
        }else {
            return (Integer) ValueConverterFactory.createDateValueConverter(DateConstant.DATE.MINUTE).result2Value(date, calendar) ;
        }
    }


    @Override
    protected void initialColumnEntity(ICubeResourceLocation currentLocation) {
        selfColumnEntity = new BICubeIntegerColumn(discovery, currentLocation);
    }

    public int getGroupValue(int position) {
        return ((BICubeIntegerColumn)selfColumnEntity).getGroupValue(position);
    }

    /**
     * 获取空值表示对象
     *
     * @return
     */
    @Override
    public Integer getCubeNullValue() {
        return NIOConstant.INTEGER.NULL_VALUE;
    }

}
