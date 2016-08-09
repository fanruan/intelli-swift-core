package com.finebi.cube.structure.group;

import com.finebi.cube.data.ICubeResourceDiscovery;
import com.finebi.cube.data.input.ICubeIntegerReaderWrapper;
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
public class BICubeIntegerGroupData extends BICubeGroupData<Integer> {
    public BICubeIntegerGroupData(ICubeResourceDiscovery resourceDiscovery, ICubeResourceLocation superLocation) {
        super(resourceDiscovery, superLocation);
    }

    public int getGroupValueByPosition(int position) {
        try {
            return ((ICubeIntegerReaderWrapper)getGroupReader()).getSpecificValue(position);

        } catch (BIResourceInvalidException e) {
            BILogger.getLogger().error(e.getMessage(), e);
            return NIOConstant.INTEGER.NULL_VALUE;
        }
    }

    @Override
    protected ICubeResourceLocation setGroupType() {
        return currentLocation.setIntegerTypeWrapper();
    }

    @Override
    protected Comparator<Integer> defaultComparator() {
        return ComparatorFacotry.INTEGER_ASC;
    }

    @Override
    public Integer getGroupObjectValueByPosition(int index) {
        int value = getGroupValueByPosition(index);
        return value == NIOConstant.INTEGER.NULL_VALUE ? null : value;
    }

}
