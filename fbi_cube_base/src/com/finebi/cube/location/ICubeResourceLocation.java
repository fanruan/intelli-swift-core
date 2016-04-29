package com.finebi.cube.location;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * This class created on 2016/3/8.
 *
 * @author Connery
 * @since 4.0
 */
public interface ICubeResourceLocation {

    URI getBaseLocation();

    URI getChildLocation();

    URI getResolvedURI();

    void setChildLocation(URI childLocation);

    void setBaseLocation(URI baseLocation);

    String getAbsolutePath();

    ICubeResourceLocation buildChildLocation(String childPath) throws URISyntaxException;

    String getFragment();

    String getQuery();

    ICubeResourceLocation setWriterSourceLocation();

    ICubeResourceLocation setReaderSourceLocation();

    ICubeResourceLocation setByteType();

    ICubeResourceLocation setLongType();

    ICubeResourceLocation setIntegerType();

    ICubeResourceLocation setDoubleType();

    ICubeResourceLocation setByteTypeWrapper();

    ICubeResourceLocation setLongTypeWrapper();

    ICubeResourceLocation setIntegerTypeWrapper();

    ICubeResourceLocation setDoubleTypeWrapper();

    ICubeResourceLocation setByteArrayType();

    ICubeResourceLocation setStringType();

    ICubeResourceLocation setGroupValueIndexType();


    /**
     * 针对当前Location的Path，构建新对象，并添加后缀
     *
     * @param suffix 后缀
     * @return 添加后缀的Path
     */
    ICubeResourceLocation generateWithSuffix(String suffix) throws URISyntaxException;

    ICubeResourceLocation copy();


}
