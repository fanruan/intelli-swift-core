package com.finebi.cube.data.input;

import com.fr.bi.common.inter.Release;

/**
 * This class created on 2016/3/16.
 *
 * @author Connery
 * @since 4.0
 */
public interface ICubeReader extends Release {

    long getLastPosition(long rowCount);

    boolean canRead();

    void forceRelease();

    boolean isForceReleased();
}
