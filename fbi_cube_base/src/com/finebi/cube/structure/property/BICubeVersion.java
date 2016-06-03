package com.finebi.cube.structure.property;

import com.finebi.cube.data.ICubeResourceDiscovery;
import com.finebi.cube.exception.BIResourceInvalidException;
import com.finebi.cube.location.ICubeResourceLocation;
import com.finebi.cube.structure.ICubeVersion;
import com.fr.bi.stable.utils.program.BINonValueUtils;

/**
 * This class created on 2016/3/29.
 *
 * @author Connery
 * @since 4.0
 */
public class BICubeVersion extends BICubeLongProperty implements ICubeVersion {


    private static String CUBE_VERSION = "version";

    public BICubeVersion(ICubeResourceLocation currentLocation, ICubeResourceDiscovery discovery) {
        super(currentLocation, discovery);
    }

    @Override
    protected String getPropertyName() {
        return CUBE_VERSION;
    }


    @Override
    public long getVersion() {
        try {
            return getReader().getSpecificValue(0);
        } catch (BIResourceInvalidException e) {
            throw BINonValueUtils.beyondControl(e);
        }
    }

    @Override
    public void addVersion(long version) {
        getWriter().recordSpecificValue(0, version);
    }
}
