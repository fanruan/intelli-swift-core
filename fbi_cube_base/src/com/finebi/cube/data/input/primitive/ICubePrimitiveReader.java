package com.finebi.cube.data.input.primitive;

import com.finebi.cube.data.ICubeSourceReleaseManager;
import com.fr.bi.common.inter.Release;
import com.finebi.cube.exception.BIResourceInvalidException;

/**
 * This class created on 2016/3/2.
 *
 * @author Connery
 * @since 4.0
 */
public interface ICubePrimitiveReader<T> extends Release {
    /**
     * 获得指点位置的值
     *
     * @param valuePosition 位置
     * @return 值
     * @throws BIResourceInvalidException
     */
    T getSpecificValue(long valuePosition) throws BIResourceInvalidException;

    boolean canReader();

    void setReleaseHelper(ICubeSourceReleaseManager releaseHelper);

    void releaseSource();

}
