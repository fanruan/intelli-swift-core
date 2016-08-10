package com.finebi.cube.structure.detail;

import com.finebi.cube.data.ICubeResourceDiscovery;
import com.finebi.cube.data.input.ICubeByteReaderWrapper;
import com.finebi.cube.exception.BIResourceInvalidException;
import com.finebi.cube.location.ICubeResourceLocation;
import com.fr.bi.stable.constant.DBConstant;

/**
 * This class created on 2016/3/28.
 *
 * @author Connery
 * @since 4.0
 */
public class BICubeByteDetailData extends BICubeDetailData<Byte> {
    public BICubeByteDetailData(ICubeResourceDiscovery discovery, ICubeResourceLocation superLocation) {
        super(discovery, superLocation);
    }

    public byte getOriginalValueByRow(int rowNumber) {
        try {
            return ((ICubeByteReaderWrapper)getCubeReader()).getSpecificValue(rowNumber);
        } catch (BIResourceInvalidException e) {
            throw new RuntimeException("read byte failed", e);
        }
    }

    @Override
    protected ICubeResourceLocation setDetailType() {
        return currentLocation.setByteTypeWrapper();
    }

    @Override
    public int getClassType() {
        return DBConstant.CLASS.BYTE;
    }

    @Override
    public Byte getOriginalObjectValueByRow(int rowNumber) {
        return getOriginalValueByRow(rowNumber);
    }
}
