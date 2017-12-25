package com.finebi.base.common.resource;

import java.util.Collection;
import java.util.Set;

/**
 * This class created on 2017/4/10.
 *
 * @author Connery
 * @since Advanced FineBI Analysis 1.0
 */
public interface ResourcePool<Name extends ResourceName, Item extends ResourceItem> extends ResourceItem {
    Item getResourceItem(Name name);

    Item getResourceItem(String name);

    void addResourceItem(Name name, Item item);

    void deleteResourceItem(Name name);

    void deleteResourceItem(String name);

    void updateResourceItem(Name name, Item item);

    boolean contain(Name name);

    boolean contain(Item item);

    boolean contain(String name);

    boolean isEmpty();

    Set<Name> getAllNames();

    Collection<Item> getAllItems();
}
