package com.finebi.cube.api;


/**
 * Created by 小灰灰 on 2016/6/24.
 */
public interface ICubeColumnDetailGetter {
    /**
     * 获取某行的Cube存的值
     *
     * @param row         行
     * @return
     */
    Object getValue(int row);

    PrimitiveType getPrimitiveType();

    PrimitiveDetailGetter createPrimitiveDetailGetter();

}
