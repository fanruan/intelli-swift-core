package com.finebi.cube.conf.pack.group;

import com.finebi.cube.conf.pack.IPackagesManagerGetterService;
import com.finebi.cube.conf.pack.data.BIBusinessPackage;
import com.finebi.cube.conf.pack.data.BIGroupTagName;
import com.finebi.cube.conf.pack.data.BIPackageID;
import com.fr.bi.conf.data.pack.exception.BIPackageAbsentException;

import java.util.Set;

/**
 * This class created on 2016/5/23.
 *
 * @author Connery
 * @since 4.0
 */
public interface IBusinessGroupGetterService {
    BIGroupTagName getName();

    IPackagesManagerGetterService getPackageManager();

    Set<BIBusinessPackage> getPackages();

    BIBusinessPackage getPackage(BIPackageID packageID) throws BIPackageAbsentException;

    Boolean containPackage(BIBusinessPackage pack);

    Boolean containPackage(BIPackageID packageID);

    long getPosition();

    void setPosition(long position);
}