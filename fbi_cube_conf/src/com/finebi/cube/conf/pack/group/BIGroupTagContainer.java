package com.finebi.cube.conf.pack.group;

import com.finebi.cube.conf.pack.data.BIBusinessPackage;
import com.finebi.cube.conf.pack.data.BIGroupTagName;
import com.finebi.cube.conf.pack.data.BIPackageID;
import com.fr.bi.base.BIUser;
import com.fr.bi.common.container.BITreeMapContainer;
import com.fr.bi.common.factory.BIFactoryHelper;
import com.fr.bi.common.factory.IFactoryService;
import com.fr.bi.common.factory.annotation.BIMandatedObject;
import com.fr.bi.conf.data.pack.exception.BIGroupAbsentException;
import com.fr.bi.conf.data.pack.exception.BIGroupDuplicateException;
import com.fr.bi.conf.data.pack.exception.BIPackageAbsentException;
import com.fr.bi.conf.data.pack.exception.BIPackageDuplicateException;
import com.fr.bi.exception.BIKeyAbsentException;
import com.fr.bi.exception.BIKeyDuplicateException;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.bi.stable.utils.program.BICollectionUtils;
import com.fr.bi.stable.utils.program.BINonValueUtils;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * 分组的容器类
 * This class created on 2016/5/23.
 *
 * @author Connery
 * @since 4.0
 */
@BIMandatedObject(factory = IFactoryService.CONF_XML, implement = IGroupTagsManagerService.class)
public class BIGroupTagContainer extends BITreeMapContainer<BIGroupTagName, BIBusinessGroup> implements IGroupTagsManagerService {

    protected BIUser user;

    public BIGroupTagContainer(long userId) {
        user = BIFactoryHelper.getObject(BIUser.class, userId);
    }

    public BIBusinessGroup getBIGroup(BIGroupTagName groupName) {
        return container.get(groupName);
    }


    /**
     * 获得所有组
     *
     * @return
     */
    public Set<IBusinessGroupGetterService> getGroups() {
        Set<IBusinessGroupGetterService> result = new HashSet<IBusinessGroupGetterService>();
        Iterator<BIGroupTagName> it = getAllGroupTagName().iterator();
        while (it.hasNext()) {
            BIGroupTagName name = it.next();
            result.add(container.get(name));
        }
        return result;

    }

    public Set<BIBusinessPackage> getGroupPackages(BIGroupTagName name) {
        if (container.containsKey(name)) {
            return container.get(name).getPackages();
        } else {
            return new HashSet<BIBusinessPackage>();
        }
    }

    /**
     * 获得所有组下面的业务包
     *
     * @return
     */
    public Set<BIBusinessPackage> getTaggedPackages() {
        synchronized (container) {
            Set<BIBusinessPackage> result = new HashSet<BIBusinessPackage>();
            Iterator<IBusinessGroupGetterService> it = getGroups().iterator();
            while (it.hasNext()) {
                IBusinessGroupGetterService biBusinessGroup = it.next();
                result.addAll(biBusinessGroup.getPackages());
            }
            return result;
        }
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public void clear() {
        super.clear();
    }


    @Override
    public Set<BIGroupTagName> getAllGroupTagName() {
        synchronized (container) {
            return BICollectionUtils.unmodifiedCollection(container.keySet());
        }
    }

    @Override
    public void createEmptyGroup(BIGroupTagName groupName, long position) throws BIGroupDuplicateException {
        synchronized (container) {
            BINonValueUtils.checkNull(groupName);
            if (!container.containsKey(groupName)) {
                BIBusinessGroup group = new BIBusinessGroup(groupName);
                group.setPosition(position);
                container.put(groupName, group);
            } else {
                throw new BIGroupDuplicateException("there have been same groupName in container.If want to add package,please use addPackage method to add package");
            }
        }
    }

    @Override
    public IBusinessGroupGetterService getReadableGroup(BIGroupTagName groupName) throws BIGroupAbsentException {
        return getGroup(groupName);
    }

    private BIBusinessGroup getGroup(BIGroupTagName groupName) throws BIGroupAbsentException {
        synchronized (container) {
            if (this.containsKey(groupName)) {
                return this.getBIGroup(groupName);
            } else {
                throw new BIGroupAbsentException();
            }
        }
    }

    @Override
    public void addPackage(BIGroupTagName groupName, BIBusinessPackage pack) throws BIGroupAbsentException, BIPackageDuplicateException {
        synchronized (container) {
            BINonValueUtils.checkNull(groupName, pack);
            if (container.containsKey(groupName)) {
                container.get(groupName).addPackage(pack);
            } else {
                throw new BIGroupAbsentException("there doesn't have that specified groupName in container.Please use newGroup method firstly to add package");
            }

        }
    }


    private void checkValue(String groupName, String packageId) {
        if (groupName == null || packageId == null) {
            throw new NullPointerException("GroupName and packageID can't be null,please check");
        }
    }


    @Override
    public void removeGroup(BIGroupTagName groupTagName) throws BIGroupAbsentException {
        synchronized (container) {
            try {
                this.remove(groupTagName);
            } catch (BIKeyAbsentException e) {
                throw new BIGroupAbsentException();
            }
        }
    }

    @Override
    public void removeSpecificGroupPackageTag(BIGroupTagName groupTagName, BIBusinessPackage pack) throws BIGroupAbsentException, BIPackageAbsentException {
        synchronized (container) {
            checkContainTagExist(groupTagName);
            BIBusinessGroup group = getGroup(groupTagName);
            group.removePackage(pack);
        }
    }

    @Override
    public void removePackage(BIPackageID packageID) throws BIPackageAbsentException {
        synchronized (container) {

            Iterator<Map.Entry<BIGroupTagName, BIBusinessGroup>> it = this.getContainer().entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<BIGroupTagName, BIBusinessGroup> entry = it.next();
                BIBusinessGroup group = entry.getValue();
                if (group.containPackage(packageID)) {
                    group.removePackage(packageID);
                }
            }
        }
    }

    private void checkContainTagExist(BIGroupTagName groupTagName) throws BIGroupAbsentException {
        if (!this.containsKey(groupTagName)) {
            throw new BIGroupAbsentException();
        }
    }

    private void checkContainTagDupl(BIGroupTagName groupTagName) throws BIGroupDuplicateException {
        if (this.containsKey(groupTagName)) {
            throw new BIGroupDuplicateException();
        }
    }

    @Override
    public void rename(BIGroupTagName oldName, BIGroupTagName newName) throws BIGroupAbsentException, BIGroupDuplicateException {
        synchronized (container) {
            checkContainTagExist(oldName);
            BIBusinessGroup group = getGroup(oldName);
            removeGroup(oldName);
            addGroup(newName, group);
        }
    }

    @Override
    public void addGroup(BIGroupTagName groupName, BIBusinessGroup group) throws BIGroupDuplicateException {
        synchronized (container) {
            try {
                this.putKeyValue(groupName, group);
            } catch (BIKeyDuplicateException e) {
                throw new BIGroupDuplicateException();
            }
        }
    }

    @Override
    public BIBusinessPackage getPackage(BIGroupTagName groupName, BIPackageID packageId) throws BIGroupAbsentException, BIPackageAbsentException {
        BIBusinessGroup group = getGroup(groupName);
        return group.getPackage(packageId);
    }

    @Override
    public Boolean existGroupTag(BIGroupTagName groupName) {
        return this.containsKey(groupName);
    }

    @Override
    public Boolean isPackageTaggedSomeGroup(BIPackageID packageID) {
        Iterator<BIGroupTagName> it = getAllGroupTagName().iterator();
        while (it.hasNext()) {
            try {
                if (isPackageTaggedSpecificGroup(it.next(), packageID)) {
                    return true;
                }
            } catch (BIGroupAbsentException ignore) {
                BILogger.getLogger().error(ignore.getMessage(), ignore);
                continue;
            }
        }
        return false;
    }

    @Override
    public Boolean isPackageTaggedSpecificGroup(BIGroupTagName groupTagName, BIPackageID packageID) throws BIGroupAbsentException {
        BIBusinessGroup group = getGroup(groupTagName);
        return group.containPackage(packageID);
    }
}