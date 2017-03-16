package com.finebi.cube.data.cache;


import com.finebi.cube.exception.BICacheInaccessibleException;
import com.finebi.cube.exception.BICubeResourceAvailableException;
import com.finebi.cube.exception.BICubeResourceUnavailableException;

/**
 * This class created on 2016/5/1.
 *
 * @author Connery
 * @since 4.0
 */
public interface IResourceCacheItem<R> {
    /**
     *
     */
    /**
     * 使得该资源可用。
     *
     * @param resource 目标资源
     * @throws BICubeResourceAvailableException 当前资源已经可获得
     */
    void makeAvailable(R resource) throws BICubeResourceAvailableException;

    /**
     * 当前location所指的资源是否可获得
     *
     * @param location 定位符
     * @return 是否可获得
     */

    boolean isAvailableResource();

    /**
     * 获得相应的资源
     *
     * @return 目标资源
     * @throws BICubeResourceUnavailableException 当前资源不可获得
     */
    R getAvailableResource() throws BICubeResourceUnavailableException;

    void cache(R resource) throws BICacheInaccessibleException;
}
