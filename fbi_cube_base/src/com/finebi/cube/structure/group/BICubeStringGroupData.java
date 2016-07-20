package com.finebi.cube.structure.group;

import com.finebi.cube.data.ICubeResourceDiscovery;
import com.finebi.cube.data.input.ICubeStringReader;
import com.finebi.cube.exception.BIResourceInvalidException;
import com.finebi.cube.location.ICubeResourceLocation;
import com.fr.bi.stable.operation.sort.comp.ComparatorFacotry;
import com.fr.bi.stable.utils.code.BILogger;

import java.util.Comparator;

/**
 * This class created on 2016/3/29.
 *
 * @author Connery
 * @since 4.0
 */
public class BICubeStringGroupData extends BICubeGroupData<String> {
    public BICubeStringGroupData(ICubeResourceDiscovery resourceDiscovery, ICubeResourceLocation superLocation) {
        super(resourceDiscovery, superLocation);
    }

    @Override
    protected ICubeResourceLocation setGroupType() {
        return currentLocation.setStringType();
    }

    @Override
    protected Comparator<String> defaultComparator() {
        return ComparatorFacotry.CHINESE_ASC;
    }

    public String getGroupValueByPosition(int position) {
        try {
            return ((ICubeStringReader)getGroupReader()).getSpecificValue(position);
        } catch (BIResourceInvalidException e) {
            BILogger.getLogger().error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public String getGroupObjectValueByPosition(int index) {
        return getGroupValueByPosition(index);
    }

}
