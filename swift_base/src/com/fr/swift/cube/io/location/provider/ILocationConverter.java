package com.fr.swift.cube.io.location.provider;

import com.fr.swift.cube.io.location.IResourceLocation;

/**
 * @author Boris
 * @date 2017/8/8
 */
public interface ILocationConverter {
    IResourceLocation getRealLocation(String path, String child, boolean ifNeed);
}
