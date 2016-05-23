package com.finebi.cube.conf.pack.group;


import com.finebi.cube.conf.pack.imp.BIPackageContainer;
import com.finebi.cube.conf.pack.IPackagesManagerGetterService;
import com.finebi.cube.conf.pack.IPackagesManagerService;
import com.finebi.cube.conf.pack.data.BIBusinessPackage;
import com.finebi.cube.conf.pack.data.BIGroupTagName;
import com.finebi.cube.conf.pack.data.BIPackageID;
import com.fr.bi.conf.data.pack.exception.BIPackageAbsentException;
import com.fr.bi.conf.data.pack.exception.BIPackageDuplicateException;
import com.fr.bi.stable.utils.program.BICollectionUtils;
import com.fr.general.ComparatorUtils;

import java.util.Iterator;
import java.util.Set;

/**
 * Created by Connery on 2015/12/28.
 */
public class BIBusinessGroup implements IBusinessGroupGetterService {
    private BIGroupTagName name;
    private IPackagesManagerService packageManager;
    private long position;

    public BIBusinessGroup(BIGroupTagName groupName) {
        this.name = groupName;
        packageManager = new BIPackageContainer();
    }

    public static BIBusinessGroup generateEmpty() {
        return new BIBusinessGroup(new BIGroupTagName("__FINE_BI_EMPTY__"));
    }

    @Override
    public BIGroupTagName getName() {
        return name;
    }

    public void setName(BIGroupTagName name) {
        this.name = name;
    }

    @Override
    public IPackagesManagerGetterService getPackageManager() {
        return packageManager;
    }

    public void setPackageManager(IPackagesManagerService packageManager) {
        this.packageManager = packageManager;
    }

    @Override
    public Set<BIBusinessPackage> getPackages() {
        return BICollectionUtils.unmodifiedCollection(packageManager.getAllPackages());
    }


    @Override
    public BIBusinessPackage getPackage(BIPackageID packageID) throws BIPackageAbsentException {
        Iterator<BIBusinessPackage> it = getPackages().iterator();
        while (it.hasNext()) {
            BIBusinessPackage pack = it.next();
            if (ComparatorUtils.equals(pack.getID(), packageID)) {
                return pack;
            }
        }
        throw new BIPackageAbsentException();
    }

    protected void addPackage(BIBusinessPackage pack) throws BIPackageDuplicateException {
        if (!containPackage(pack)) {
            packageManager.addPackage(pack);
        } else {
            throw new BIPackageDuplicateException();
        }
    }


    protected void removePackage(BIBusinessPackage pack) throws BIPackageAbsentException {
        if (containPackage(pack)) {
            packageManager.removePackage(pack.getID());
        } else {
            throw new BIPackageAbsentException("The Package " + pack.toString() + "don't hava the Group Tag:" + this.toString());
        }
    }

    protected void removePackage(BIPackageID packageID) throws BIPackageAbsentException {
        BIBusinessPackage pack = getPackage(packageID);
        removePackage(pack);
    }

    @Override
    public Boolean containPackage(BIBusinessPackage pack) {
        return packageManager.containPackage(pack);
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
    public long getPosition() {
        return this.position;
    }

    @Override
    public void setPosition(long position) {
        this.position = position;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("BIBusinessGroup{");
        sb.append("name=").append(name);
        sb.append('}');
        return sb.toString();
    }
}