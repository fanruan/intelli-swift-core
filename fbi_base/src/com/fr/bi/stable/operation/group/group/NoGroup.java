package com.fr.bi.stable.operation.group.group;

import com.fr.bi.stable.operation.group.AbstractGroup;
import com.finebi.cube.api.ICubeColumnIndexReader;

/**
 * Created by GUY on 2015/4/9.
 */
public class NoGroup extends AbstractGroup {
    @Override
    public ICubeColumnIndexReader createGroupedMap(ICubeColumnIndexReader baseMap) {
        return baseMap;
    }

    @Override
    public boolean isNullGroup() {
        return false;
    }
}