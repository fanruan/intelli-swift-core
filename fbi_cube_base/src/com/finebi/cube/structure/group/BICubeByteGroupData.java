package com.finebi.cube.structure.group;

import com.finebi.cube.location.ICubeResourceLocation;

import java.util.Comparator;

/**
 * This class created on 2016/3/28.
 *
 * @author Connery
 * @since 4.0
 */
public class BICubeByteGroupData extends BICubeGroupData<Byte> {
    public BICubeByteGroupData(ICubeResourceLocation superLocation) {
        super(superLocation);
    }

    @Override
    protected ICubeResourceLocation setGroupType() {
        return currentLocation.setByteTypeWrapper();
    }


    @Override
    protected Comparator<Byte> defaultComparator() {
        return null;
    }
}
