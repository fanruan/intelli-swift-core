package com.finebi.cube.structure;

import com.finebi.cube.exception.BIResourceInvalidException;
import com.fr.bi.common.inter.Release;

/**
 * Created by 小灰灰 on 2016/6/29.
 */
public interface ICubeReverseRelationService extends Release {
    /**
     * 添加子表行号对应的主表行号
     * @param row 子表行号
     * @param groupPosition 主表行号
     */
    void addReverseRow(int row, Integer groupPosition);

    /**
     * 获取字表行号对应的主表行号
     * @param row 子表行号
     * @return 主表行号
     * @throws BIResourceInvalidException
     */
    Integer getReverseRow(int row) throws BIResourceInvalidException;
}
