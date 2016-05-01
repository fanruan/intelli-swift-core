package com.finebi.cube.data;

import com.finebi.cube.location.ICubeResourceLocation;

/**
 * This class created on 2016/5/1.
 *
 * @author Connery
 * @since 4.0
 */
public interface IResourceCache<R> {
    /**
     * 获得location所指向的资源
     *
     * @param location 定位符
     * @return 目标资源
     */
    R getResource(ICubeResourceLocation location);

    /**
     * 使得该资源可用。
     *
     * @param location 定位符
     * @param resource 目标资源
     */
    void makeAvailable(ICubeResourceLocation location, R resource);

    /**
     * 当前location所指的资源是否可获得
     *
     * @param location 定位符
     * @return 是否可获得
     */
    boolean isAvailableResource(ICubeResourceLocation location);
}
