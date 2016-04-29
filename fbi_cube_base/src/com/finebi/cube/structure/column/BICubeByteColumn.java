package com.finebi.cube.structure.column;

import com.finebi.cube.location.ICubeResourceLocation;
import com.finebi.cube.structure.detail.BICubeByteDetailData;
import com.finebi.cube.structure.group.BICubeByteGroupData;

import java.util.Comparator;

/**
 * This class created on 2016/3/28.
 *
 * @author Connery
 * @since 4.0
 */
public class BICubeByteColumn extends BICubeColumnEntity<Byte> {
    public BICubeByteColumn(ICubeResourceLocation currentLocation) {
        super(currentLocation);
    }

    @Override
    protected void initial() {
        detailDataService = new BICubeByteDetailData(currentLocation);
        groupDataService = new BICubeByteGroupData(currentLocation);
    }

}
