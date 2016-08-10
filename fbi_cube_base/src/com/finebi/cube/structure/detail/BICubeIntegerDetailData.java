package com.finebi.cube.structure.detail;

import com.finebi.cube.data.ICubeResourceDiscovery;
import com.finebi.cube.data.input.ICubeIntegerReaderWrapper;
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
public class BICubeIntegerDetailData extends BICubeDetailData<Integer> {
    public BICubeIntegerDetailData(ICubeResourceDiscovery discovery, ICubeResourceLocation superLocation) {
        super(discovery, superLocation);
    }

    public int getOriginalValueByRow(int rowNumber) {
        try {
            return ((ICubeIntegerReaderWrapper)getCubeReader()).getSpecificValue(rowNumber);
        } catch (BIResourceInvalidException e) {
            BILogger.getLogger().error(e.getMessage(), e);
            return NIOConstant.INTEGER.NULL_VALUE;
        }
    }
    @Override
    protected ICubeResourceLocation setDetailType() {
        return currentLocation.setIntegerTypeWrapper();
    }
    @Override
    public int getClassType() {
        return DBConstant.CLASS.LONG;
    }

    @Override
    public Integer getOriginalObjectValueByRow(int rowNumber) {
        int value = getOriginalValueByRow(rowNumber);
        return value == NIOConstant.INTEGER.NULL_VALUE ? null : value;
    }
}
