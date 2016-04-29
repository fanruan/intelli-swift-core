package com.finebi.cube.structure.detail;

import com.finebi.cube.location.ICubeResourceLocation;

/**
 * This class created on 2016/3/29.
 *
 * @author Connery
 * @since 4.0
 */
public class BICubeStringDetailData extends BICubeDetailData<String> {
    public BICubeStringDetailData(ICubeResourceLocation superLocation) {
        super(superLocation);
    }

    @Override
    protected ICubeResourceLocation setDetailType() {
        return currentLocation.setStringType();
    }
}
