package com.finebi.cube.adapter;

import com.finebi.cube.exception.BICubeIndexException;
import com.finebi.cube.structure.ICubeIndexDataGetterService;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.gvi.array.ICubeTableIndexReader;
import com.fr.bi.stable.utils.program.BINonValueUtils;

/**
 * This class created on 2016/4/18.
 *
 * @author Connery
 * @since 4.0
 */
public class BICubeTableRelationIndexReader implements ICubeTableIndexReader {
    private ICubeIndexDataGetterService getterService;

    public BICubeTableRelationIndexReader(ICubeIndexDataGetterService getterService) {
        this.getterService = getterService;
    }

    @Override
    public GroupValueIndex get(long row) {
        try {
            return getterService.getBitmapIndex((int) row);
        } catch (BICubeIndexException e) {
            throw BINonValueUtils.beyondControl(e);
        }
    }

    @Override
    public void releaseResource() {

    }
}
