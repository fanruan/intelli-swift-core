package com.finebi.cube.structure.group;

import com.finebi.cube.data.ICubeResourceDiscovery;
import com.finebi.cube.data.input.ICubeLongReaderWrapper;
import com.finebi.cube.exception.BIResourceInvalidException;
import com.finebi.cube.location.ICubeResourceLocation;
import com.fr.bi.stable.io.newio.NIOConstant;
import com.fr.bi.stable.operation.sort.comp.ComparatorFacotry;
import com.fr.bi.stable.utils.code.BILogger;

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

    @Override
    public Long getGroupObjectValueByPosition(int index) {
        long value = getGroupValueByPosition(index);
        return value == NIOConstant.LONG.NULL_VALUE ? null : value;
    }

    public long getGroupValueByPosition(int position) {
        try {
            return ((ICubeLongReaderWrapper)getGroupReader()).getSpecificValue(position);

        } catch (BIResourceInvalidException e) {
            BILogger.getLogger().error(e.getMessage(), e);
            return NIOConstant.LONG.NULL_VALUE;
        }
    }

}
