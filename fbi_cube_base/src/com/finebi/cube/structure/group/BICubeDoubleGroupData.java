package com.finebi.cube.structure.group;

import com.finebi.cube.data.ICubeResourceDiscovery;
import com.finebi.cube.data.input.ICubeDoubleReaderWrapper;
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
public class BICubeDoubleGroupData extends BICubeGroupData<Double> {

    public BICubeDoubleGroupData(ICubeResourceDiscovery resourceDiscovery, ICubeResourceLocation superLocation) {
        super(resourceDiscovery, superLocation);
    }

    public double getGroupValueByPosition(int position) {
        try {
            return ((ICubeDoubleReaderWrapper)getGroupReader()).getSpecificValue(position);

        } catch (BIResourceInvalidException e) {
            BILogger.getLogger().error(e.getMessage(), e);
            return NIOConstant.DOUBLE.NULL_VALUE;
        }
    }

    @Override
    protected ICubeResourceLocation setGroupType() {
        return currentLocation.setDoubleTypeWrapper();
    }

    @Override
    protected Comparator<Double> defaultComparator() {
        return ComparatorFacotry.DOUBLE_ASC;
    }

    @Override
    public Double getGroupObjectValueByPosition(int index) {
        Double value = getGroupValueByPosition(index);
        return Double.isNaN(value) ? null : value;
    }
}
