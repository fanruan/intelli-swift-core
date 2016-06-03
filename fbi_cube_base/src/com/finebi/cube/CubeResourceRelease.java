package com.finebi.cube;

/**
 * This class created on 2016/6/3.
 *
 * @author Connery
 * @since 4.0
 */
public interface CubeResourceRelease {
    void releaseHandler();

    void forceRelease();

    boolean isForceReleased();

}
