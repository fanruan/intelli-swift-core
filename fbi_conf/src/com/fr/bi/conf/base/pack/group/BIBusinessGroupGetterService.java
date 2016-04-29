package com.fr.bi.conf.base.pack.group;

import com.fr.bi.conf.base.pack.BIPackagesManagerGetterService;
import com.fr.bi.conf.base.pack.data.BIBusinessPackage;
import com.fr.bi.conf.base.pack.data.BIGroupTagName;
import com.fr.bi.conf.base.pack.data.BIPackageID;
import com.fr.bi.conf.data.pack.exception.BIPackageAbsentException;

import java.util.Set;

/**
 * Created by Connery on 2016/1/19.
 */
public interface BIBusinessGroupGetterService {
    BIGroupTagName getName();

    BIPackagesManagerGetterService getPackageManager();

    Set<BIBusinessPackage> getPackages();

    BIBusinessPackage getPackage(BIPackageID packageID) throws BIPackageAbsentException;

    Boolean containPackage(BIBusinessPackage pack);

    Boolean containPackage(BIPackageID packageID);
}