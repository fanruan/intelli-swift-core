package com.finebi.cube.structure.property;

import com.finebi.cube.data.ICubeResourceDiscovery;
import com.finebi.cube.exception.BIResourceInvalidException;
import com.finebi.cube.location.ICubeResourceLocation;
import com.finebi.cube.structure.ICubeColumnPositionOfGroupService;

/**
 * Created by 小灰灰 on 2016/6/28.
 */
public class BICubeColumnPositionOfGroupService extends BICubeIntegerProperty implements ICubeColumnPositionOfGroupService{
    private static final String GROUP_POSITION = "group.position";

    public BICubeColumnPositionOfGroupService(ICubeResourceLocation currentLocation, ICubeResourceDiscovery discovery) {
        super(currentLocation, discovery);
    }

    @Override
    public void addPositionOfGroup(int position, int groupPosition) {
        getWriter().recordSpecificValue(position, groupPosition);
    }

    @Override
    public int getPositionOfGroup(int row) throws BIResourceInvalidException {
        return getReader().getSpecificValue(row);
    }

    @Override
    protected String getPropertyName() {
        return GROUP_POSITION;
    }
}
