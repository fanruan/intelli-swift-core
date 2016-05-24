package com.finebi.cube.conf.pack.imp;

import com.finebi.cube.conf.pack.IPackagesManagerService;
import com.finebi.cube.conf.pack.data.*;
import com.fr.bi.base.BIUser;
import com.fr.bi.common.container.BISetContainer;
import com.fr.bi.common.factory.IFactoryService;
import com.fr.bi.common.factory.annotation.BIMandatedObject;
import com.fr.bi.conf.data.pack.exception.BIPackageAbsentException;
import com.fr.bi.conf.data.pack.exception.BIPackageDuplicateException;
import com.fr.bi.stable.data.BITableID;
import com.fr.bi.stable.exception.BITableAbsentException;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.general.ComparatorUtils;

import java.util.Iterator;
import java.util.Set;

/**
 * 业务包的容器
 * This class created on 2016/5/23.
 *
 * @author Connery
 * @since 4.0
 */
@BIMandatedObject(factory = IFactoryService.CONF_XML, implement = IPackagesManagerService.class)
public class BIPackageContainer extends BISetContainer<BIBusinessPackage> implements IPackagesManagerService {
    protected BIUser user;

    public BIPackageContainer(long userId) {
        this.user = new BIUser(userId);
    }

    public BIPackageContainer() {
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        BIPackageContainer packageContainer = (BIPackageContainer) super.clone();
        packageContainer.container = initCollection();
        try {
            Iterator<BIBusinessPackage> it = this.container.iterator();
            while (it.hasNext()) {
                BIBusinessPackage t = it.next();
                packageContainer.container.add((BIBasicBusinessPackage) t.clone());
            }
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }
        return packageContainer;
    }

    @Override
    protected Set<BIBusinessPackage> getContainer() {
        return super.getContainer();
    }

    @Override
    public IBusinessPackageGetterService getPackage(BIPackageID packageId) throws BIPackageAbsentException {
        return getPackageObj(packageId);
    }

    private BIBusinessPackage getPackageObj(BIPackageID packageId) throws BIPackageAbsentException {
        synchronized (container) {
            Iterator<BIBusinessPackage> it = this.getContainer().iterator();
            while (it.hasNext()) {
                BIBusinessPackage pack = it.next();
                if (ComparatorUtils.equals(pack.getID(), packageId)) {
                    return pack;
                }
            }
            throw new BIPackageAbsentException();
        }
    }

    @Override
    public void rename(BIPackageID id, BIPackageName newName) throws BIPackageAbsentException {
        synchronized (container) {
            BIBusinessPackage pack = getPackageObj(id);
            pack.setName(newName);
        }
    }

    @Override
    public Set<BIBusinessPackage> getAllPackages() {
        return this.getContainer();
    }

    @Override
    public void removePackage(BIPackageID id) throws BIPackageAbsentException {
        synchronized (container) {
            BIBusinessPackage pack = getPackageObj(id);
            this.remove(pack);

        }
    }


    @Override
    public void replace(BIBusinessPackage newPackage) throws BIPackageAbsentException {
        synchronized (container) {
            removePackage(newPackage.getID());
            try {
                addPackage(newPackage);
            } catch (BIPackageDuplicateException e) {
                BILogger.getLogger().error(e.getMessage(), e);
            }
        }
    }

    @Override
    public void addPackage(BIBusinessPackage newPackage) throws BIPackageDuplicateException {
        if (!this.contain(newPackage)) {
            this.add(newPackage);
        } else {
            throw new BIPackageDuplicateException();
        }
    }

    @Override
    public void clearPackages() {
        super.clear();
    }

    @Override
    public Boolean containPackage(BIBusinessPackage newPackage) {
        return super.contain(newPackage);
    }

    @Override
    public Boolean containPackage(BIPackageID packageID) {
        try {
            getPackage(packageID);
            return true;
        } catch (BIPackageAbsentException ignore) {
            return false;
        }
    }

    @Override
    public boolean isNeed2BuildCube(Set<BIBusinessPackage> targetPackages) {
        Set<BIBusinessPackage> currentPackages = getAllPackages();
        if (currentPackages.size() == targetPackages.size()) {
            Iterator<BIBusinessPackage> currentIt = currentPackages.iterator();
            while (currentIt.hasNext()) {
                BIBusinessPackage currentPackage = currentIt.next();
                Iterator<BIBusinessPackage> targetIt = targetPackages.iterator();
                while (targetIt.hasNext()) {
                    BIBusinessPackage targetPackage = targetIt.next();
                    if (!currentPackage.isNeed2BuildCube(targetPackage)) {
                        continue;
                    } else {
                        return false;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public BIPackageContainer clonePackageContainer() throws CloneNotSupportedException {
        return (BIPackageContainer) this.clone();
    }

    @Override
    public void parsePackageContainer(BIPackageContainer container) {
        synchronized (container) {
            clear();
            this.useContent(container);
        }
    }

    @Override
    public Boolean isPackageEmpty() {
        return super.isEmpty();
    }

    public void removeTable(BIPackageID packageID, BITableID tableID) throws BIPackageAbsentException, BITableAbsentException {
        BIBusinessPackage biBusinessPackage = getPackageObj(packageID);
        biBusinessPackage.removeBusinessTableByID(tableID);
    }
}
