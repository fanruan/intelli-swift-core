package com.finebi.cube.data.disk;

import com.finebi.cube.CubeResourceRelease;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by wang on 2016/9/30.
 */
public interface NIOHandlerManager<T extends CubeResourceRelease> {
    T queryHandler();

    void releaseHandler();

    void forceReleaseHandler();

    boolean isForceReleased();

    boolean isHandlerEmpty();

    //    fortest
    void printCountOfHandler();

    void reSetHandlerValid(boolean isValid);
}
