package com.fr.bi.stable.operation.group.group;


import com.finebi.cube.api.ICubeColumnIndexReader;
import com.fr.bi.stable.operation.group.AbstractGroup;

/**
 * Created by User on 2016/4/15.
 */
public class IdGroup extends AbstractGroup {

    @Override
    public ICubeColumnIndexReader createGroupedMap(ICubeColumnIndexReader baseMap) {
        return baseMap;
    }

    @Override
    public boolean isNullGroup() {
        return false;
    }
}
