package com.finebi.cube;


import com.finebi.cube.location.provider.BILocationProvider;

import java.net.URI;

/**
 * This class created on 2016/3/8.
 *
 * @author Connery
 * @since 4.0
 */
public interface ICubeConfiguration {
    URI getRootURI();
    BILocationProvider getLocationProvider();
}
