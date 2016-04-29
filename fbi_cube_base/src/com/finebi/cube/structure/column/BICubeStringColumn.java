package com.finebi.cube.structure.column;

import com.finebi.cube.location.ICubeResourceLocation;
import com.finebi.cube.structure.detail.BICubeStringDetailData;
import com.finebi.cube.structure.group.BICubeStringGroupData;
import com.fr.bi.stable.operation.sort.comp.ComparatorFacotry;

import java.util.Comparator;

/**
 * This class created on 2016/3/29.
 *
 * @author Connery
 * @since 4.0
 */
public class BICubeStringColumn extends BICubeColumnEntity<String> {
    public BICubeStringColumn(ICubeResourceLocation currentLocation) {
        super(currentLocation);
    }

    @Override
    protected void initial() {
        detailDataService = new BICubeStringDetailData(currentLocation);
        groupDataService = new BICubeStringGroupData(currentLocation);
    }


}

