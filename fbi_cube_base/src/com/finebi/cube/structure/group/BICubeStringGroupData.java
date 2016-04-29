package com.finebi.cube.structure.group;

import com.finebi.cube.location.ICubeResourceLocation;
import com.fr.bi.stable.operation.sort.comp.ComparatorFacotry;

import java.util.Comparator;

/**
 * This class created on 2016/3/29.
 *
 * @author Connery
 * @since 4.0
 */
public class BICubeStringGroupData extends BICubeGroupData<String> {
    public BICubeStringGroupData(ICubeResourceLocation superLocation) {
        super(superLocation);
    }

    @Override
    protected ICubeResourceLocation setGroupType() {
        return currentLocation.setStringType();
    }

    @Override
    protected Comparator<String> defaultComparator() {
        return ComparatorFacotry.CHINESE_ASC;
    }
}
