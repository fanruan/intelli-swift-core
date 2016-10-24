package com.finebi.cube.data.output.primitive;

import com.finebi.cube.CubeResourceRelease;
import com.finebi.cube.data.ICubeSourceReleaseManager;
import com.finebi.cube.data.disk.NIOHandlerManager;

/**
 * This class created on 2016/3/2.
 *
 * @author Connery
 * @since 4.0
 */
public interface ICubePrimitiveWriter<T> extends CubeResourceRelease {
    /**
     * 在指定的位置写入相应的值
     *
     * @param position 指定的位置
     * @param value    值
     */
    void recordSpecificPositionValue(long position, T value);

    void flush();

    void setReleaseManager(ICubeSourceReleaseManager releaseHelper);

    void setHandlerReleaseHelper(NIOHandlerManager releaseHelper);
    NIOHandlerManager getHandlerReleaseHelper();
    void releaseSource();

    String getWriterHandler();

    boolean canWriter();

    void reSetValid(boolean isValid);

    void destoryResource();
}


