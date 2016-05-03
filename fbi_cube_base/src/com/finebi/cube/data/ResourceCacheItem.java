package com.finebi.cube.data;

import com.finebi.cube.exception.BICubeResourceAvailableException;
import com.finebi.cube.exception.BICubeResourceUnavailableException;
import com.finebi.cube.location.ICubeResourceLocation;
import com.fr.bi.stable.utils.program.BINonValueUtils;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * This class created on 2016/5/1.
 *
 * @author Connery
 * @since 4.0
 */
public abstract class ResourceCacheItem<R> implements IResourceCacheItem<R> {
    private BlockingQueue<R> availableResource;
    private BlockingQueue<R> unavailableResource;
    private ICubeResourceLocation targetLocation;
    private static int DEFAULT_SIZE = 1;
    private int capacity;

    public ResourceCacheItem(ICubeResourceLocation targetLocation, int capacity) {
        this.targetLocation = targetLocation;
        this.capacity = DEFAULT_SIZE;
        if (capacity > 0) {
            this.capacity = capacity;
        }
        availableResource = new LinkedBlockingQueue<R>(this.capacity);
        unavailableResource = new LinkedBlockingQueue<R>(this.capacity);
    }


    @Override
    public void makeAvailable(R resource) throws BICubeResourceAvailableException {
        synchronized (this) {
            if (unavailableResource.contains(resource)) {
                unavailableResource.remove(resource);
                availableResource.add(resource);
            } else {
                throw new BICubeResourceAvailableException("The resource:" + resource + " is available now.");
            }
        }
    }

    @Override
    public boolean isAvailableResource() {
        return isFull();
    }

    private boolean isFull() {
        synchronized (this) {
            return capacity - unavailableResource.size() == 0;
        }
    }

    public R getAvailableResource() throws BICubeResourceUnavailableException {
        synchronized (this) {
            if (isAvailableResource()) {
                R resource;
                if (!availableResource.isEmpty()) {
                    try {
                        resource = availableResource.take();
                    } catch (InterruptedException e) {
                        throw BINonValueUtils.beyondControl(e);
                    }
                } else {
                    resource = buildResource();
                }
                unavailableResource.add(resource);
                return resource;
            } else {
                throw new BICubeResourceUnavailableException("There is no available resource");
            }
        }
    }

    public abstract R buildResource();
}
