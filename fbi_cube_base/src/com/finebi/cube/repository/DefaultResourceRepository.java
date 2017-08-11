package com.finebi.cube.repository;
/**
 * This class created on 2017/6/21.
 *
 * @author Connery
 * @since Advanced FineBI Analysis 1.0
 */

import com.finebi.common.name.Name;
import com.finebi.common.resource.DefaultResourcePool;
import com.finebi.common.resource.ResourceItem;
import com.finebi.common.resource.ResourceName;
import com.finebi.common.resource.ResourcePool;
import com.finebi.cube.common.log.BILogger;
import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.utils.BICubeUtils;
import com.finebi.cube.utils.BIExceptionUtils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class DefaultResourceRepository<NAME extends ResourceName, ITEM extends ResourceItem> implements ResourceRepository<NAME, ITEM> {

    private final static BILogger LOGGER = BILoggerFactory.getLogger(DefaultResourceRepository.class);
    protected ResourcePool<NAME, ITEM> resourcePool = new DefaultResourcePool<NAME, ITEM>();
    protected ModifyStrategy<NAME, ITEM> modifyStrategy = new DefaultModifyStrategy<NAME, ITEM>();


    public DefaultResourceRepository(ResourcePool<NAME, ITEM> resourcePool, ModifyStrategy<NAME, ITEM> modifyStrategy) {
        this.resourcePool = resourcePool;
        this.modifyStrategy = modifyStrategy;
    }

    @Override
    public Name getName() {
        return null;
    }

    @Override
    public ResourceName getResourceName() {
        return null;
    }

    @Override
    public void merge(ResourceRepository<NAME, ITEM> resourceRepository) {
        synchronized (this) {
            for (NAME name : resourceRepository.getAllNames()) {
                ITEM newItem = resourceRepository.getResourceItem(name);
                if (contain(name)) {
                    updateResourceItem(name, newItem);
                } else {
                    addResourceItem(name, newItem);
                }
            }
        }
    }

    @Override
    public ResourceRepository<NAME, ITEM> divideRepository(NAME[] names) {
        synchronized (this) {
            ResourceRepository<NAME, ITEM> resourceRepository = generateRepository();
            for (NAME name : names) {
                ITEM item = getResourceItem(name);
                if (item != null) {
                    resourceRepository.addResourceItem(name, item);
                }
            }
            return resourceRepository;
        }
    }

    protected ResourceRepository<NAME, ITEM> generateRepository() {
        throw BIExceptionUtils.createUnsupportedException("use subtype to generate repository");
    }

    public boolean contain(ITEM item) {
        return resourcePool.contain(item);
    }

    @Override
    public ITEM getResourceItem(NAME name) {
        return resourcePool.getResourceItem(name);
    }

    @Override
    public ITEM getResourceItem(String name) {
        return resourcePool.getResourceItem(name);
    }

    @Override
    public void addResourceItem(NAME name, ITEM item) {
        synchronized (this) {
            if (modifyStrategy.checkAddCondition(name, item)) {
                resourcePool.addResourceItem(name, item);
            }
        }
    }

    @Override
    public void deleteResourceItem(NAME name) {
        synchronized (this) {
            if (modifyStrategy.checkDeleteCondition(name)) {
                resourcePool.deleteResourceItem(name);
            }
        }
    }

    @Override
    public void deleteResourceItem(String name) {
        synchronized (this) {
            deleteResourceItem((NAME) getResourceItem(name).getResourceName());
        }
    }

    @Override
    public void updateResourceItem(NAME name, ITEM item) {
        synchronized (this) {
            if (modifyStrategy.checkUpdateCondition(name, item)) {
                if (BICubeUtils.equals(name, item.getName())) {
                    resourcePool.updateResourceItem(name, item);
                }
            }
        }
    }

    @Override
    public boolean contain(NAME name) {
        return resourcePool.contain(name);
    }

    @Override
    public boolean contain(String name) {
        return resourcePool.contain(name);
    }


    @Override
    public Set<NAME> getAllNames() {
        return resourcePool.getAllNames();
    }

    @Override
    public Collection<ITEM> getAllItems() {
        return resourcePool.getAllItems();
    }

    @Override
    public boolean isEmpty() {
        return resourcePool.isEmpty();
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public Set<ITEM> popSpecificItems(NAME... resourceNames) {
        synchronized (this) {
            Set<ITEM> result = new HashSet<ITEM>();
            for (NAME name : resourceNames) {
                if (contain(name)) {
                    ITEM item = getResourceItem(name);
                    deleteResourceItem(name);
                    result.add(item);
                }
            }
            return result;
        }
    }

    @Override
    public long version() {
        return 0;
    }


    @Override
    public boolean deleteIfNoLeading(ITEM item) {
        synchronized (this) {
            NAME name = (NAME) item.getResourceName();
            if (contain(name)) {
                ITEM freshItem = getResourceItem(name);
                if (item.version() >= freshItem.version()) {
                    deleteResourceItem(name);
                    return true;
                }
            }
            return false;
        }
    }
}
