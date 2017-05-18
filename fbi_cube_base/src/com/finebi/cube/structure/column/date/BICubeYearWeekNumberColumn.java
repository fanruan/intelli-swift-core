package com.finebi.cube.structure.column.date;

import com.finebi.cube.data.ICubeResourceDiscovery;
import com.finebi.cube.location.ICubeResourceLocation;
import com.finebi.cube.structure.column.BICubeLongColumn;
import com.fr.bi.base.ValueConverterFactory;
import com.fr.bi.stable.constant.DateConstant;
import com.fr.bi.stable.io.newio.NIOConstant;
/**
 * Created by wang on 2017/3/28.
 * 年-周数
 */
public class BICubeYearWeekNumberColumn extends BICubeDateSubColumn<Long> {
    public BICubeYearWeekNumberColumn(ICubeResourceDiscovery discovery, ICubeResourceLocation currentLocation, BICubeDateColumn hostDataColumn) {
        super(discovery, currentLocation, hostDataColumn);
    }

    @Override
    protected Long convertDate(Long date) {
        return date != null ? (Long) ValueConverterFactory.createDateValueConverter(DateConstant.DATE.YEAR_WEEKNUMBER).result2Value(date) : null;
    }

    @Override
    protected void initialColumnEntity(ICubeResourceLocation currentLocation) {
        selfColumnEntity = new BICubeLongColumn(discovery, currentLocation);
    }

    public Long getGroupValue(int position) {
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
