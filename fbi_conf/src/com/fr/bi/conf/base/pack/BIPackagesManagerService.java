package com.fr.bi.conf.base.pack;

import com.fr.bi.conf.base.pack.data.BIBusinessPackage;
import com.fr.bi.conf.data.pack.exception.*;

import com.fr.bi.conf.base.pack.data.BIPackageID;
import com.fr.bi.conf.base.pack.data.BIPackageName;
import com.fr.bi.stable.data.BITableID;
import com.fr.bi.stable.exception.BITableAbsentException;

import java.util.Set;

/**
 * Created by Connery on 2016/1/7.
 */


public interface BIPackagesManagerService extends BIPackagesManagerGetterService {

    void rename(BIPackageID id, BIPackageName newName) throws BIPackageAbsentException;

    void removePackage(BIPackageID id) throws BIPackageAbsentException;

    void replace(BIBusinessPackage newPackage) throws BIPackageAbsentException;

    void addPackage(BIBusinessPackage newPackage) throws BIPackageDuplicateException;

    Boolean containPackage(BIBusinessPackage newPackage);

    Boolean containPackage(BIPackageID packageID);

    void clearPackages();

    boolean isNeed2BuildCube(Set<BIBusinessPackage> targetPackages);

    void parsePackageContainer(BIPackageContainer container);

    Boolean isPackageEmpty();

    void removeTable(BIPackageID packageID, BITableID tableID) throws BIPackageAbsentException, BITableAbsentException;
}