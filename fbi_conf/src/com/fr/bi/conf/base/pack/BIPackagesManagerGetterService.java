package com.fr.bi.conf.base.pack;

import com.fr.bi.conf.base.pack.data.BIBusinessPackage;
import com.fr.bi.conf.base.pack.data.BIBusinessPackageGetterService;
import com.fr.bi.conf.base.pack.data.BIPackageID;
import com.fr.bi.conf.data.pack.exception.BIPackageAbsentException;

import java.util.Set;

/**
 * Created by Connery on 2016/1/20.
 */
public interface BIPackagesManagerGetterService {
    Set<BIBusinessPackage> getAllPackages();

    BIBusinessPackageGetterService getPackage(BIPackageID id) throws BIPackageAbsentException;

    BIPackageContainer clonePackageContainer() throws CloneNotSupportedException;

}