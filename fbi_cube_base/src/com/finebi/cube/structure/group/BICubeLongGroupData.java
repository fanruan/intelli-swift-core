package com.finebi.cube.structure.group;

import com.finebi.cube.data.ICubeResourceDiscovery;
import com.finebi.cube.location.ICubeResourceLocation;
import com.fr.bi.stable.operation.sort.comp.ComparatorFacotry;

import java.util.Comparator;

/**
 * This class created on 2016/3/28.
 *
 * @author Connery
 * @since 4.0
 */
public class BICubeLongGroupData extends BICubeGroupData<Long> {
    public BICubeLongGroupData(ICubeResourceDiscovery resourceDiscovery, ICubeResourceLocation superLocation) {
        super(resourceDiscovery, superLocation);
    }

    @Override
    protected ICubeResourceLocation setGroupType() {
        return currentLocation.setLongTypeWrapper();
    }

    @Override
    protected Comparator<Long> defaultComparator() {
        return ComparatorFacotry.LONG_ASC;
    }
}
