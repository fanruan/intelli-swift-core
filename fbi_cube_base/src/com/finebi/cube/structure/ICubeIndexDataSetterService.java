package com.finebi.cube.structure;

import com.fr.bi.stable.gvi.GroupValueIndex;

/**
 * 生成索引数据的接口
 * <p/>
 * This class created on 2016/3/2.
 *
 * @author Connery
 * @since 4.0
 */
public interface ICubeIndexDataSetterService {

    /**
     * 在对应位置添加索引值
     *
     * @param position        位置
     * @param groupValueIndex 索引值
     */
    void addIndex(int position, GroupValueIndex groupValueIndex);

    /**
     * 在对应位置添加空值
     *
     * @param position        位置
     * @param groupValueIndex 索引值
     */
    void addNULLIndex(int position, GroupValueIndex groupValueIndex);


}
