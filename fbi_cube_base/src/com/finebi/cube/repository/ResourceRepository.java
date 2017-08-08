package com.finebi.cube.repository;

import com.finebi.common.resource.ResourceItem;
import com.finebi.common.resource.ResourceName;
import com.finebi.common.resource.ResourcePool;

/**
 * This class created on 2017/6/21.
 *
 * @author Connery
 * @since Advanced FineBI Analysis 1.0
 */

public interface ResourceRepository<Name extends ResourceName, Item extends ResourceItem> extends ResourcePool<Name, Item> {
    void merge(ResourceRepository<Name, Item> resourceRepository);

    ResourceRepository divideRepository(Name... names);

    /**
     * 如果当前的Item和参数item相比，不是算主要item，则删除。
     * 主Item指version比较大的一个
     *
     * @param item 当前
     * @return 删除对象返回true，否则返回false
     */
    boolean deleteIfNoLeading(Item item);


}
