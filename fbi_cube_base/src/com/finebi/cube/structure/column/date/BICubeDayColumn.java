package com.finebi.cube.structure.column.date;

import com.finebi.cube.data.ICubeResourceDiscovery;
import com.finebi.cube.location.ICubeResourceLocation;
import com.finebi.cube.structure.column.BICubeIntegerColumn;
import com.fr.bi.base.ValueConverterFactory;
import com.fr.bi.stable.constant.DateConstant;
import com.fr.bi.stable.io.newio.NIOConstant;

import java.util.Calendar;


/**
 * This class created on 2016/3/30.
 * 日（1-31）
 * @author Connery
 * @since 4.0
 */
public class BICubeDayColumn extends BICubeDateSubColumn<Integer> {
    public BICubeDayColumn(ICubeResourceDiscovery discovery, ICubeResourceLocation currentLocation, BICubeDateColumn hostDataColumn) {
        super(discovery, currentLocation, hostDataColumn);
    }

    @Override
    protected Integer convertDate(Long date) {
        return date != null ? (Integer) ValueConverterFactory.createDateValueConverter(DateConstant.DATE.DAY).result2Value(date) : NIOConstant.INTEGER.NULL_VALUE;
    }

    @Override
    protected Integer convertDate(Long date, Calendar calendar) {
        return date != null ? (Integer) ValueConverterFactory.createDateValueConverter(DateConstant.DATE.DAY).result2Value(date, calendar) : NIOConstant.INTEGER.NULL_VALUE;
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
