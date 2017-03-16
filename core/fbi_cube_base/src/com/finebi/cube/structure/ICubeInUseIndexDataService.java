package com.finebi.cube.structure;

import com.fr.bi.stable.gvi.GroupValueIndex;

/**
 * Created by naleite on 16/3/17.
 */
public interface ICubeInUseIndexDataService extends ICubeInUseIndexDataGetterService{


    /**
     * 在对应位置添加索引值
     * @param position        位置
     * @param groupValueIndex 索引值
     */
    void addIndex(int position, GroupValueIndex groupValueIndex);

    /**
     * 在对应位置添加空值
     * @param position        位置
     * @param groupValueIndex 索引值
     */
    void addNULLIndex(int position, GroupValueIndex groupValueIndex);

    /**
     * 版本号
     *
     * @param version 版本号
     */
    void addVersion(int version);
}
