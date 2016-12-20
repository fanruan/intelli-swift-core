package com.finebi.cube.structure.detail;

import com.finebi.cube.data.ICubeResourceDiscovery;
import com.finebi.cube.data.input.ICubeStringReader;
import com.finebi.cube.exception.BIResourceInvalidException;
import com.finebi.cube.location.ICubeResourceLocation;
import com.fr.bi.stable.constant.DBConstant;
import com.finebi.cube.common.log.BILoggerFactory;

/**
 * This class created on 2016/3/29.
 *
 * @author Connery
 * @since 4.0
 */
public class BICubeStringDetailData extends BICubeDetailData<String> {
    public BICubeStringDetailData(ICubeResourceDiscovery discovery, ICubeResourceLocation superLocation) {
        super(discovery, superLocation);
    }


    public String getOriginalValueByRow(int rowNumber) {
        try {
            return ((ICubeStringReader)getCubeReader()).getSpecificValue(rowNumber);
        } catch (BIResourceInvalidException e) {
            BILoggerFactory.getLogger().error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    protected ICubeResourceLocation setDetailType(boolean isIncrease) {
        if(!isIncrease) {
            return currentLocation.setStringType();
        }
        return currentLocation.setStringIncreaseType();
    }

    @Override
    public String getOriginalObjectValueByRow(int rowNumber) {
        return getOriginalValueByRow(rowNumber);
    }

    @Override
    public int getClassType() {
        return DBConstant.CLASS.STRING;
    }
}
