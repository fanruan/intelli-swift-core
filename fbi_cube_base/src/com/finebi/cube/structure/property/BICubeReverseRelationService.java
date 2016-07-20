package com.finebi.cube.structure.property;

import com.finebi.cube.data.ICubeResourceDiscovery;
import com.finebi.cube.exception.BIResourceInvalidException;
import com.finebi.cube.location.ICubeResourceLocation;
import com.finebi.cube.structure.ICubeReverseRelationService;

/**
 * Created by 小灰灰 on 2016/6/29.
 */
public class BICubeReverseRelationService extends BICubeIntegerProperty implements ICubeReverseRelationService
{
    private static final String REVERSE = "reverse";

    public BICubeReverseRelationService(ICubeResourceLocation currentLocation, ICubeResourceDiscovery discovery) {
        super(currentLocation, discovery);
    }

    @Override
    public void addReverseRow(int row, Integer groupPosition) {
        getWriter().recordSpecificValue(row, groupPosition);
    }

    @Override
    public int getReverseRow(int row) throws BIResourceInvalidException {
        return getReader().getSpecificValue(row);
    }

    @Override
    protected String getPropertyName() {
        return REVERSE;
    }


}

