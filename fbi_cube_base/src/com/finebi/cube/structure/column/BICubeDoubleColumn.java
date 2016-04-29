package com.finebi.cube.structure.column;

import com.finebi.cube.location.ICubeResourceLocation;
import com.finebi.cube.structure.detail.BICubeDoubleDetailData;
import com.finebi.cube.structure.group.BICubeDoubleGroupData;
import com.fr.bi.stable.operation.sort.comp.ComparatorFacotry;

import java.util.Comparator;

/**
 * This class created on 2016/3/28.
 *
 * @author Connery
 * @since 4.0
 */
public class BICubeDoubleColumn extends BICubeColumnEntity<Double> {
    public BICubeDoubleColumn(ICubeResourceLocation currentLocation) {
        super(currentLocation);
    }

    @Override
    protected void initial() {
        detailDataService = new BICubeDoubleDetailData(currentLocation);
        groupDataService = new BICubeDoubleGroupData(currentLocation);
    }

}
