package com.finebi.cube.structure.column;

import com.finebi.cube.data.ICubeResourceDiscovery;
import com.finebi.cube.location.ICubeResourceLocation;
import com.finebi.cube.structure.detail.BICubeIntegerDetailData;
import com.finebi.cube.structure.group.BICubeIntegerGroupData;

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


}
