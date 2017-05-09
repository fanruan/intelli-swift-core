package com.fr.bi.stable.operation.group.group.date;

import com.finebi.cube.api.ICubeColumnIndexReader;
import com.fr.bi.stable.operation.group.AbstractGroup;

/**
 * Created by wang 20170508
 */
public class YearWeekNumberGroup extends AbstractGroup {
    @Override
    public ICubeColumnIndexReader createGroupedMap(ICubeColumnIndexReader baseMap) {
        return baseMap;
    }

    @Override
    public boolean isNullGroup() {
        return false;
    }

}