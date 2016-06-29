package com.finebi.cube.structure;

import com.finebi.cube.exception.BIResourceInvalidException;

/**
 * This class created on 2016/3/2.
 *
 * @author Connery
 * @since 4.0
 */
public interface CubeRelationEntityGetterService extends ICubeIndexDataGetterService ,ICubeVersion{
    /**
     * 获取反向的关联
      * @param row 子表行号
     * @return 主表行号
     */
    int getReverseIndex(int row) throws BIResourceInvalidException;
}
