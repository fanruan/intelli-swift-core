package com.finebi.cube.structure.detail;

import com.finebi.cube.data.ICubeResourceDiscovery;
import com.finebi.cube.location.ICubeResourceLocation;

/**
 * This class created on 2016/3/28.
 *
 * @author Connery
 * @since 4.0
 */
public class BICubeDoubleDetailData extends BICubeDetailData<Double> {

    public BICubeDoubleDetailData(ICubeResourceDiscovery discovery, ICubeResourceLocation superLocation) {
        super(discovery, superLocation);
    }

    @Override
    protected ICubeResourceLocation setDetailType() {
        return currentLocation.setDoubleTypeWrapper();
    }
}
