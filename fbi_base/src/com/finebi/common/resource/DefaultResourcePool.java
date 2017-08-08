package com.finebi.common.resource;


import com.finebi.common.name.NameImp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * This class created on 2017/4/11.
 *
 * @author Connery
 * @since Advanced FineBI Analysis 1.0
 */
public class DefaultResourcePool<Name extends ResourceName, Item extends ResourceItem> implements ResourcePool<Name, Item> {
    private ConcurrentMap<Name, Item> pool = new ConcurrentHashMap<Name, Item>();

    @Override
    public com.finebi.common.name.Name getName() {
        return new NameImp("ResourcePool");
    }

    @Override
    public ResourceName getResourceName() {
        return new ResourceNameImpl(getName());
    }

    @Override
    public Item getResourceItem(Name name) {
        return pool.get(name);
    }

    @Override
    public Item getResourceItem(String name) {
        return pool.get(buildName(name));
    }

    protected Name buildName(String name) {
        return (Name) new ResourceNameImpl(name);
    }

    @Override
    public void addResourceItem(Name name, Item item) {
        pool.put(name, item);
    }

    @Override
    public void deleteResourceItem(Name name) {
        pool.remove(name);
    }

    @Override
    public void deleteResourceItem(String name) {
        deleteResourceItem(buildName(name));
    }

    @Override
    public void updateResourceItem(Name name, Item item) {
        if (pool.containsKey(name)) {
            Item poolItem = pool.get(name);
            if (poolItem.version() <= item.version()) {
                pool.remove(name);
                pool.put(name, item);
            }
        } else {
            pool.put(name, item);
        }
    }

    @Override
    public boolean contain(Name name) {
        return pool.containsKey(name);
    }

    @Override
    public boolean contain(Item item) {
        if (item != null) {
            return pool.containsKey(item.getName());
        } else {
            return false;
        }
    }

    @Override
    public boolean contain(String name) {
        return contain(buildName(name));
    }

    @Override
    public boolean isEmpty() {
        return pool.isEmpty();
    }

    @Override
    public Collection<Item> getAllItems() {
        /**
         * 防止多线程操作，item在遍历时被删除抛错。
         */
        return new ArrayList<Item>(pool.values());
    }

    @Override
    public Set<Name> getAllNames() {
        /**
         * 防止多线程操作，item在遍历时被删除抛错。
         */
        return new HashSet<Name>(pool.keySet());

    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public long version() {
        return 0;
    }
}
