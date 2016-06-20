package com.finebi.cube.structure;

import com.finebi.cube.exception.BICubeIndexException;
import com.finebi.cube.location.ICubeResource;
import com.fr.bi.common.inter.Release;
import com.fr.bi.stable.gvi.GroupValueIndex;

/**
 * 读取索引数据的接口。
 * This class created on 2016/3/2.
 *
 * @author Connery
 * @since 4.0
 */
public interface ICubeIndexDataGetterService extends Release, ICubeResource {

    /**
     * 获取对应位置的索引值
     *
     * @param position
     * @return
     */
    GroupValueIndex getBitmapIndex(int position) throws BICubeIndexException;

    /**
     * 获取对应位置的空值索引
     *
     * @param position
     * @return
     */
    GroupValueIndex getNULLIndex(int position) throws BICubeIndexException;

    boolean isEmpty();


}
