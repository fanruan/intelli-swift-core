package com.finebi.cube;

import com.finebi.cube.data.cache.IResourceCache;
import com.finebi.cube.data.cache.IResourceCacheItem;
import com.finebi.cube.location.ICubeResourceLocation;
import com.sun.javafx.collections.MappingChange;

/**
 * This class created on 2016/5/1.
 *
 * @author Connery
 * @since 4.0
 */
public class ResourceCache<R> implements IResourceCache<R> {
    private static int WRITE_SIZE = 1;
    private static int READER_SIZE = Integer.MAX_VALUE;
    private MappingChange.Map<ICubeResourceLocation, IResourceCacheItem<R>> cache;

    @Override
    public R getResource(ICubeResourceLocation location) {
        return null;
    }

    @Override
    public void makeAvailable(ICubeResourceLocation location, R resource) {

    }

    @Override
    public boolean isAvailableResource(ICubeResourceLocation location) {
        return false;
    }

    @Override
    public void forceRelease() {

    }
}
