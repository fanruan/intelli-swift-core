package com.finebi.cube.structure.column;

import com.finebi.cube.data.ICubeResourceDiscovery;
import com.finebi.cube.location.ICubeResourceLocation;
import com.finebi.cube.structure.detail.BICubeLongDetailData;
import com.finebi.cube.structure.group.BICubeLongGroupData;
import com.fr.bi.stable.io.newio.NIOConstant;

/**
 * This class created on 2016/3/28.
 *
 * @author Connery
 * @since 4.0
 */
public class BICubeLongColumn extends BICubeColumnEntity<Long> {
    public BICubeLongColumn(ICubeResourceDiscovery discovery, ICubeResourceLocation currentLocation) {
        super(discovery, currentLocation);
    }

    @Override
    protected void initial() {
        detailDataService = new BICubeLongDetailData(discovery, currentLocation);
        groupDataService = new BICubeLongGroupData(discovery, currentLocation);
    }

    /**
     * 根据行号获得对应的原始值。
     *
     * @param rowNumber 数据库中的行号
     * @return 原始值
     */
    public long getOriginalValueByRow(int rowNumber) {
        return ((BICubeLongDetailData)detailDataService).getOriginalValueByRow(rowNumber);
    }

    public long getGroupValue(int position) {
        return ((BICubeLongGroupData)groupDataService).getGroupValueByPosition(position);
    }

    @Override
    public Long getOriginalObjectValueByRow(int rowNumber) {
        long value = getOriginalValueByRow(rowNumber);
        return value == NIOConstant.LONG.NULL_VALUE ? null : value;
    }
}
