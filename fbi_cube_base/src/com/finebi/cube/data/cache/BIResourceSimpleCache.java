package com.finebi.cube.data.cache;

import com.finebi.cube.location.ICubeResourceLocation;

import java.util.HashMap;
import java.util.Map;

/**
 * This class created on 2016/5/5.
 *
 * @author Connery
 * @since 4.0
 */
public class BIResourceSimpleCache<R> implements IResourceCache<R> {
    Map<ICubeResourceLocation, R> contents = new HashMap<ICubeResourceLocation, R>();

    @Override
    public R getResource(ICubeResourceLocation location) {
        return contents.get(location);
    }

    @Override
    public void makeAvailable(ICubeResourceLocation location, R resource) {
        contents.put(location, resource);
    }

    @Override
    public boolean isAvailableResource(ICubeResourceLocation location) {
        return contents.containsKey(location);
    }


}
