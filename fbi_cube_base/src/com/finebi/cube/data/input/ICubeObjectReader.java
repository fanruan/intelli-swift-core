package com.finebi.cube.data.input;

import com.finebi.cube.exception.BIResourceInvalidException;

/**
 * Created by 小灰灰 on 2016/7/20.
 */
public interface ICubeObjectReader<T> {
    T getSpecificValue(int rowNumber) throws BIResourceInvalidException;

}
