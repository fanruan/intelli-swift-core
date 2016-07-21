package com.finebi.cube.structure.column;

import com.finebi.cube.data.ICubeResourceDiscovery;
import com.finebi.cube.location.ICubeResourceLocation;
import com.finebi.cube.structure.detail.BICubeStringDetailData;
import com.finebi.cube.structure.group.BICubeStringGroupData;

/**
 * This class created on 2016/3/29.
 *
 * @author Connery
 * @since 4.0
 */
public class BICubeStringColumn extends BICubeColumnEntity<String> {
    public BICubeStringColumn(ICubeResourceDiscovery discovery, ICubeResourceLocation currentLocation) {
        super(discovery, currentLocation);
    }

    @Override
    protected void initial() {
        detailDataService = new BICubeStringDetailData(discovery, currentLocation);
        groupDataService = new BICubeStringGroupData(discovery, currentLocation);
    }

    /**
     * 根据行号获得对应的原始值。
     *
     * @param rowNumber 数据库中的行号
     * @return 原始值
     */
    public String getOriginalValueByRow(int rowNumber) {
        return ((BICubeStringDetailData)detailDataService).getOriginalValueByRow(rowNumber);
    }

    public String getGroupValue(int position) {
        return ((BICubeStringGroupData)groupDataService).getGroupValueByPosition(position);
    }

    @Override
    public String getOriginalObjectValueByRow(int rowNumber) {
        return getOriginalValueByRow(rowNumber);
    }
}

