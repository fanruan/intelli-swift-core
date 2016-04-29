package com.finebi.cube;

import com.fr.bi.base.key.BIKey;
import com.finebi.cube.structure.ICubeTableEntityGetterService;

/**
 * This class created on 2016/3/2.
 *
 * @author Connery
 * @since 4.0
 */
public interface ICubeEntityGetterService {
    int getVersion();

    /**
     * 获得table的只读接口
     *
     * @param tableKey table的key值
     * @return table的只读接口
     */
    ICubeTableEntityGetterService getCubeTableGetter(BIKey tableKey);
}
