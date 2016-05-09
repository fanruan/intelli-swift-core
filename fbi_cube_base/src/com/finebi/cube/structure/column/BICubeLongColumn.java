package com.finebi.cube.structure.column;

import com.finebi.cube.data.ICubeResourceDiscovery;
import com.finebi.cube.location.ICubeResourceLocation;
import com.finebi.cube.structure.detail.BICubeLongDetailData;
import com.finebi.cube.structure.group.BICubeLongGroupData;

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


}
