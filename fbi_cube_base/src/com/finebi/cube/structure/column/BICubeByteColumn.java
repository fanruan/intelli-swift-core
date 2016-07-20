package com.finebi.cube.structure.column;

import com.finebi.cube.data.ICubeResourceDiscovery;
import com.finebi.cube.location.ICubeResourceLocation;
import com.finebi.cube.structure.detail.BICubeByteDetailData;
import com.finebi.cube.structure.group.BICubeByteGroupData;

/**
 * This class created on 2016/3/28.
 *
 * @author Connery
 * @since 4.0
 */
public class BICubeByteColumn extends BICubeColumnEntity<Byte> {
    public BICubeByteColumn(ICubeResourceDiscovery discovery, ICubeResourceLocation currentLocation) {
        super(discovery, currentLocation);
    }

    @Override
    protected void initial() {
        detailDataService = new BICubeByteDetailData(discovery, currentLocation);
        groupDataService = new BICubeByteGroupData(discovery, currentLocation);
    }

    /**
     * 根据行号获得对应的原始值。
     *
     * @param rowNumber 数据库中的行号
     * @return 原始值
     */
    public byte getOriginalValueByRow(int rowNumber) {
        return ((BICubeByteDetailData)detailDataService).getOriginalValueByRow(rowNumber);
    }

    public byte getGroupValue(int position) {
        return ((BICubeByteGroupData)groupDataService).getGroupValueByPosition(position);
    }

    @Override
    public Byte getOriginalObjectValueByRow(int rowNumber) {
        return getOriginalValueByRow(rowNumber);
    }
}
