package com.finebi.cube.structure.column.date;

import com.finebi.cube.data.ICubeResourceDiscovery;
import com.finebi.cube.location.ICubeResourceLocation;
import com.finebi.cube.structure.column.BICubeStringColumn;
import com.fr.bi.base.ValueConverterFactory;
import com.fr.bi.stable.constant.DateConstant;
import com.fr.stable.StringUtils;

/**
 * Created by wang on 2017/3/28.
 * 年-季度
 */
public class BICubeYearSeasonColumn extends BICubeDateSubColumn<String> {
    public BICubeYearSeasonColumn(ICubeResourceDiscovery discovery, ICubeResourceLocation currentLocation, BICubeDateColumn hostDataColumn) {
        super(discovery, currentLocation, hostDataColumn);
    }

    @Override
    protected String convertDate(Long date) {
        return date != null ? (String) ValueConverterFactory.createDateValueConverter(DateConstant.DATE.YEAR_SEASON).result2Value(date) : null;
    }

    @Override
    protected void initialColumnEntity(ICubeResourceLocation currentLocation) {
        selfColumnEntity = new BICubeStringColumn(discovery, currentLocation);
    }

    public String getGroupValue(int position) {
        return ((BICubeStringColumn)selfColumnEntity).getGroupValue(position);
    }

    /**
     * 获取空值表示对象
     *
     * @return
     */
    @Override
    public String getCubeNullValue() {
        return StringUtils.EMPTY;
    }
}
