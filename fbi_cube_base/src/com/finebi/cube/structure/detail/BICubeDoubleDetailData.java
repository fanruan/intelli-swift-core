package com.finebi.cube.structure.detail;

import com.finebi.cube.location.ICubeResourceLocation;

/**
 * This class created on 2016/3/28.
 *
 * @author Connery
 * @since 4.0
 */
public class BICubeDoubleDetailData extends BICubeDetailData<Double> {

    public BICubeDoubleDetailData(ICubeResourceLocation superLocation) {
        super(superLocation);
    }

    @Override
    protected ICubeResourceLocation setDetailType() {
        return currentLocation.setDoubleTypeWrapper();
    }
}
