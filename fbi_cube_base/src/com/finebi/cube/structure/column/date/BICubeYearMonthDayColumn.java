package com.finebi.cube.structure.column.date;

import com.finebi.cube.location.ICubeResourceLocation;
import com.finebi.cube.structure.column.BICubeLongColumn;
import com.fr.bi.base.ValueConverterFactory;
import com.fr.bi.stable.constant.DateConstant;

/**
 * This class created on 2016/3/30.
 *
 * @author Connery
 * @since 4.0
 */
public class BICubeYearMonthDayColumn extends BICubeDateSubColumn<Long> {
    public BICubeYearMonthDayColumn(ICubeResourceLocation currentLocation, BICubeDateColumn hostDataColumn) {
        super(currentLocation, hostDataColumn);
    }

    @Override
    protected Long convertDate(Long date) {
        return date != null ? (Long) ValueConverterFactory.createDateValueConverter(DateConstant.DATE.YMD).result2Value(date) : null;

    }

    @Override
    protected void initialColumnEntity(ICubeResourceLocation currentLocation) {
        columnEntity = new BICubeLongColumn(currentLocation);
    }
}
