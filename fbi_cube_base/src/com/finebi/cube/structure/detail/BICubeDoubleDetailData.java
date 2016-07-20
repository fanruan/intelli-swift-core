package com.finebi.cube.structure.detail;

import com.finebi.cube.data.ICubeResourceDiscovery;
import com.finebi.cube.data.input.ICubeDoubleReaderWrapper;
import com.finebi.cube.exception.BIResourceInvalidException;
import com.finebi.cube.location.ICubeResourceLocation;
import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.io.newio.NIOConstant;
import com.fr.bi.stable.utils.code.BILogger;

/**
 * This class created on 2016/3/28.
 *
 * @author Connery
 * @since 4.0
 */
public class BICubeDoubleDetailData extends BICubeDetailData<Double> {

    public BICubeDoubleDetailData(ICubeResourceDiscovery discovery, ICubeResourceLocation superLocation) {
        super(discovery, superLocation);
    }
    public double getOriginalValueByRow(int rowNumber) {
        try {
            return ((ICubeDoubleReaderWrapper)getCubeReader()).getSpecificValue(rowNumber);
        } catch (BIResourceInvalidException e) {
            BILogger.getLogger().error(e.getMessage(), e);
            return NIOConstant.DOUBLE.NULL_VALUE;
        }
    }

    @Override
    protected ICubeResourceLocation setDetailType() {
        return currentLocation.setDoubleTypeWrapper();
    }
    @Override
    public int getClassType() {
        return DBConstant.CLASS.DOUBLE;
    }

    @Override
    public Double getOriginalObjectValueByRow(int rowNumber) {
        double value = getOriginalValueByRow(rowNumber);
        return Double.isNaN(value)? null : value;
    }
}
