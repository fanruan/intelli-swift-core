package com.finebi.cube.location.meta;

import com.finebi.common.resource.ResourceName;
import com.finebi.common.resource.ResourcePool;

import java.util.Collection;

/**
 * Created by wang on 2017/6/20.
 */
public interface BILocationPool extends ResourcePool<ResourceName,BILocationInfo> {
    BILocationPool getBILocationPool();
    BILocationPool getBILocationPool(Collection<ResourceName> nameCollection);
    void mergePool(BILocationPool input);
}
