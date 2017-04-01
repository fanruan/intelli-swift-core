package com.finebi.cube.structure.column.date;

import com.finebi.cube.data.ICubeResourceDiscovery;
import com.finebi.cube.location.ICubeResourceLocation;
import com.finebi.cube.structure.column.BICubeDoubleColumn;
import com.finebi.cube.structure.column.BICubeStringColumn;
import com.fr.bi.base.ValueConverterFactory;
import com.fr.bi.stable.constant.DateConstant;

/**
 * Created by wang on 2017/3/28.
 * 年月
 */
public class BICubeYearMonthColumn extends BICubeDateSubColumn<String> {
    public BICubeYearMonthColumn(ICubeResourceDiscovery discovery, ICubeResourceLocation currentLocation, BICubeDateColumn hostDataColumn) {
        super(discovery, currentLocation, hostDataColumn);
    }

    @Override
    protected String convertDate(Long date) {
        return date != null ? (String) ValueConverterFactory.createDateValueConverter(DateConstant.DATE.YEAR_MONTH).result2Value(date) : null;
    }

    @Override
    protected void initialColumnEntity(ICubeResourceLocation currentLocation) {
        selfColumnEntity = new BICubeStringColumn(discovery, currentLocation);
    }

    public String getGroupValue(int position) {
        return ((BICubeStringColumn)selfColumnEntity).getGroupValue(position);
    }
}
