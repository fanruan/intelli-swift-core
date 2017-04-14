package com.finebi.common.persist;

import com.finebi.common.resource.ResourcePool;

/**
 * This class created on 2017/4/11.
 *
 * @author Connery
 * @since Advanced FineBI Analysis 1.0
 */
public interface PoolPersistentWriter<Pool extends ResourcePool> {
    void write(Pool pool);
}
