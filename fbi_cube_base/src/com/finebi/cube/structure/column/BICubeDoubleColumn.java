package com.finebi.cube.structure.column;

import com.finebi.cube.data.ICubeResourceDiscovery;
import com.finebi.cube.location.ICubeResourceLocation;
import com.finebi.cube.structure.detail.BICubeDoubleDetailData;
import com.finebi.cube.structure.group.BICubeDoubleGroupData;

/**
 * This class created on 2016/3/28.
 *
 * @author Connery
 * @since 4.0
 */
public class BICubeDoubleColumn extends BICubeColumnEntity<Double> {
    public BICubeDoubleColumn(ICubeResourceDiscovery discovery, ICubeResourceLocation currentLocation) {
        super(discovery, currentLocation);
    }

    @Override
    protected void initial() {
        detailDataService = new BICubeDoubleDetailData(discovery, currentLocation);
        groupDataService = new BICubeDoubleGroupData(discovery, currentLocation);
    }

}
