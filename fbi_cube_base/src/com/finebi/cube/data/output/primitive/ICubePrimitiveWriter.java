package com.finebi.cube.data.output.primitive;

import com.fr.bi.common.inter.Release;

/**
 * This class created on 2016/3/2.
 *
 * @author Connery
 * @since 4.0
 */
public interface ICubePrimitiveWriter<T> extends Release {
    /**
     * 在指定的位置写入相应的值
     *
     * @param position 指定的位置
     * @param value    值
     */
    void recordSpecificPositionValue(long position, T value);

    void flush();
}
