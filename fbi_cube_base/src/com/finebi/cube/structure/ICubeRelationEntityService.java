package com.finebi.cube.structure;

import com.fr.bi.common.inter.Release;
import com.fr.bi.stable.gvi.GroupValueIndex;

/**
 * This class created on 2016/3/2.
 *
 * @author Connery
 * @since 4.0
 */
public interface ICubeRelationEntityService extends CubeRelationEntityGetterService, Release {

    void addRelationIndex(int position, GroupValueIndex groupValueIndex);

    void addRelationNULLIndex(int position, GroupValueIndex groupValueIndex);

    /**
     * 加反向关联
     * @param row 子表行号
     * @param position 主表行号
     */
    void addReverseIndex(int row, int position);

}
