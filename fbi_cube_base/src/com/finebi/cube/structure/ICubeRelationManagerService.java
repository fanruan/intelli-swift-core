package com.finebi.cube.structure;

import com.finebi.cube.exception.BICubeRelationAbsentException;
import com.finebi.cube.exception.IllegalRelationPathException;
import com.fr.bi.common.inter.Release;

/**
 * 管理某一张表的全部关联
 * This class created on 2016/3/14.
 *
 * @author Connery
 * @since 4.0
 */
public interface ICubeRelationManagerService extends Release {
    /**
     * 依据关联获得相应的接口
     *
     * @param relationPath 关联路径
     * @return 接口
     * @throws BICubeRelationAbsentException
     */
    ICubeRelationEntityService getRelationService(BICubeTablePath relationPath) throws BICubeRelationAbsentException, IllegalRelationPathException;


}
