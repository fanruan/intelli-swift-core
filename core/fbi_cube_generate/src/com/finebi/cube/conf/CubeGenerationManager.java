package com.finebi.cube.conf;

import com.fr.stable.bridge.StableFactory;

/**
 * This class created on 2016/6/1.
 *
 * @author Connery
 * @since 4.0
 */
public class CubeGenerationManager {
    public static BICubeManagerProvider getCubeManager() {
        return StableFactory.getMarkedObject(BICubeManagerProvider.XML_TAG, BICubeManagerProvider.class);
    }

}
