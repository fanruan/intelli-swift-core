package com.finebi.cube.structure.column;

import com.finebi.cube.data.ICubeResourceDiscovery;
import com.finebi.cube.location.ICubeResourceLocation;
import com.finebi.cube.structure.detail.BICubeIntegerDetailData;
import com.finebi.cube.structure.group.BICubeIntegerGroupData;
import com.fr.bi.stable.io.newio.NIOConstant;

/**
 * This class created on 2016/3/28.
 *
 * @author Connery
 * @since 4.0
 */
public class BICubeIntegerColumn extends BICubeColumnEntity<Integer> {
    public BICubeIntegerColumn(ICubeResourceDiscovery discovery, ICubeResourceLocation currentLocation) {
        super(discovery, currentLocation);
    }

    @Override
    protected void initial() {
        detailDataService = new BICubeIntegerDetailData(discovery, currentLocation);
        groupDataService = new BICubeIntegerGroupData(discovery, currentLocation);
    }

    /**
     * 根据行号获得对应的原始值。
     *
     * @param rowNumber 数据库中的行号
     * @return 原始值
     */
    public int getOriginalValueByRow(int rowNumber) {
        return ((BICubeIntegerDetailData)detailDataService).getOriginalValueByRow(rowNumber);
    }

    public int getGroupValue(int position) {
        return ((BICubeIntegerGroupData)groupDataService).getGroupValueByPosition(position);
    }

    @Override
    public Integer getOriginalObjectValueByRow(int rowNumber) {
        int value = getOriginalValueByRow(rowNumber);
        return value == NIOConstant.INTEGER.NULL_VALUE ? null : value;
    }

}
