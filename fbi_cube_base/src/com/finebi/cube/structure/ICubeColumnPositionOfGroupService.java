package com.finebi.cube.structure;

import com.finebi.cube.exception.BIResourceInvalidException;
import com.fr.bi.common.inter.Release;

/**
 * Created by 小灰灰 on 2016/6/28.
 */
public interface ICubeColumnPositionOfGroupService extends Release{
    /**
     * 给每一个原始值添加在分组中的位置
     * @param position 原始值的位置
     * @param groupPosition 分组的位置
     */
    void addPositionOfGroup(int position, Integer groupPosition);

    /**
     * 获取一个原始值对应的行号在分组中的位置
     * @param row
     * @return
     * @throws BIResourceInvalidException
     */
    int getPositionOfGroup(int row) throws BIResourceInvalidException;
}
