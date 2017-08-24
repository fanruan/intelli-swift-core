package com.finebi.cube.location.provider;

import com.finebi.cube.location.ICubeResourceLocation;

import java.net.URISyntaxException;

/**
 * Created by Boris on 2017/8/8.
 */
public interface ILocationConverter {
    ICubeResourceLocation getRealLocation(String path, String child)throws URISyntaxException;
}
