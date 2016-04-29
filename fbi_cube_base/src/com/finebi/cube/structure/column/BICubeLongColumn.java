package com.finebi.cube.structure.column;

import com.finebi.cube.location.ICubeResourceLocation;
import com.finebi.cube.structure.detail.BICubeLongDetailData;
import com.finebi.cube.structure.group.BICubeLongGroupData;
import com.fr.bi.stable.operation.sort.comp.ComparatorFacotry;

import java.util.Comparator;

/**
 * This class created on 2016/3/28.
 *
 * @author Connery
 * @since 4.0
 */
public class BICubeLongColumn extends BICubeColumnEntity<Long> {
    public BICubeLongColumn(ICubeResourceLocation currentLocation) {
        super(currentLocation);
    }

    @Override
    protected void initial() {
        detailDataService = new BICubeLongDetailData(currentLocation);
        groupDataService = new BICubeLongGroupData(currentLocation);
    }


}
