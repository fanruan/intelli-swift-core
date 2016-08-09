package com.finebi.cube.structure.group;

import com.finebi.cube.data.ICubeResourceDiscovery;
import com.finebi.cube.data.input.ICubeByteReaderWrapper;
import com.finebi.cube.exception.BIResourceInvalidException;
import com.finebi.cube.location.ICubeResourceLocation;
import com.fr.bi.stable.utils.code.BILogger;

import java.util.Comparator;

/**
 * This class created on 2016/3/28.
 *
 * @author Connery
 * @since 4.0
 */
public class BICubeByteGroupData extends BICubeGroupData<Byte> {
    public BICubeByteGroupData(ICubeResourceDiscovery resourceDiscovery, ICubeResourceLocation superLocation) {
        super(resourceDiscovery, superLocation);
    }

    @Override
    protected ICubeResourceLocation setGroupType() {
        return currentLocation.setByteTypeWrapper();
    }


    public byte getGroupValueByPosition(int position) {
        try {
            return ((ICubeByteReaderWrapper)getGroupReader()).getSpecificValue(position);

        } catch (BIResourceInvalidException e) {
            BILogger.getLogger().error(e.getMessage(), e);
            throw new RuntimeException("read byte failed", e);
        }
    }

    @Override
    protected Comparator<Byte> defaultComparator() {
        return null;
    }

    @Override
    public Byte getGroupObjectValueByPosition(int index) {
        return getGroupValueByPosition(index);
    }
}
