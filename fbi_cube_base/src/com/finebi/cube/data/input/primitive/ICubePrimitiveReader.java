package com.finebi.cube.data.input.primitive;

import com.finebi.cube.CubeResourceRelease;
import com.finebi.cube.data.ICubeSourceReleaseManager;
import com.finebi.cube.data.disk.NIOHandlerManager;

/**
 * This class created on 2016/3/2.
 *
 * @author Connery
 * @since 4.0
 */
public interface ICubePrimitiveReader extends CubeResourceRelease {

    boolean canReader();

    void setReleaseHelper(ICubeSourceReleaseManager releaseHelper);

    void releaseSource();

    void destroySource();

    String getReaderHandler();

    void setHandlerReleaseHelper(NIOHandlerManager releaseHelper);

    NIOHandlerManager getHandlerReleaseHelper();

    void reSetValid(boolean isValid);

    void releaseBuffer();
}
