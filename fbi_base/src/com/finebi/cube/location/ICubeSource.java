package com.finebi.cube.location;

/**
 * CUBE资源
 * 可以通过资源ID来获得相应的URI，进而检索到读写资源
 * This class created on 2016/3/9.
 *
 * @author Connery
 * @since 4.0
 */
public interface ICubeSource {
    String getSourceID();
}
