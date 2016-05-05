package com.finebi.cube.data.cache;

import com.finebi.cube.location.ICubeResourceLocation;
import com.fr.bi.common.container.BIMapContainer;
import com.fr.bi.exception.BIKeyAbsentException;
import com.fr.bi.stable.utils.program.BINonValueUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * This class created on 2016/5/3.
 *
 * @author Connery
 * @since 4.0
 */
public class BIResourceCache<R> extends BIMapContainer<ICubeResourceLocation, IResourceCacheItem<R>> implements IResourceCache<R> {
    @Override
    protected Map<ICubeResourceLocation, IResourceCacheItem<R>> initContainer() {
        return new HashMap<ICubeResourceLocation, IResourceCacheItem<R>>();
    }

    @Override
    protected IResourceCacheItem<R> generateAbsentValue(ICubeResourceLocation key) {
        return null;
    }

    private IResourceCacheItem<R> getCacheItem(ICubeResourceLocation location) {
        try {
            return getValue(location);
        } catch (BIKeyAbsentException e) {
            throw BINonValueUtils.beyondControl(e);
        }
    }

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
}
