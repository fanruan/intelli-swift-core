package com.fr.swift.cube.io.location;


import com.fr.swift.cube.io.Types.StoreType;

import java.net.URI;

/**
 * @author anchore
 * @date 17/11/24
 */
public interface IResourceLocation extends Cloneable {
    URI getUri();

    StoreType getStoreType();

    String getPath();

    String getAbsolutePath();

    IResourceLocation buildChildLocation(String child);

    IResourceLocation getParent();

    String getName();

    /**
     * 针对当前Location的Path，构建新对象，并添加后缀
     *
     * @param suffix 后缀
     * @return 添加后缀的Path
     */
    IResourceLocation addSuffix(String suffix);

    /**
     * @inheritDoc
     */
    @Override
    String toString();
}