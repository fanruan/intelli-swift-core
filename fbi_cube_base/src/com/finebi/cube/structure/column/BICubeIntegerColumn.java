package com.finebi.cube.structure.column;

import com.finebi.cube.location.ICubeResourceLocation;
import com.finebi.cube.structure.detail.BICubeIntegerDetailData;
import com.finebi.cube.structure.group.BICubeIntegerGroupData;
import com.fr.bi.stable.operation.sort.comp.ComparatorFacotry;

import java.util.Comparator;

/**
 * This class created on 2016/3/28.
 *
 * @author Connery
 * @since 4.0
 */
public class BICubeIntegerColumn extends BICubeColumnEntity<Integer> {
    public BICubeIntegerColumn(ICubeResourceLocation currentLocation) {
        super(currentLocation);
    }

    @Override
    protected void initial() {
        detailDataService = new BICubeIntegerDetailData(currentLocation);
        groupDataService = new BICubeIntegerGroupData(currentLocation);
    }


}
