package com.finebi.cube.conf.pack;


import com.finebi.cube.conf.pack.data.BIBusinessPackage;
import com.finebi.cube.conf.pack.data.IBusinessPackageGetterService;
import com.finebi.cube.conf.pack.data.BIPackageID;
import com.finebi.cube.conf.pack.imp.BIPackageContainer;
import com.fr.bi.conf.data.pack.exception.BIPackageAbsentException;

import java.util.Set;

/**
 * Created by Connery on 2016/1/20.
 */
public interface IPackagesManagerGetterService {
    Set<BIBusinessPackage> getAllPackages();

    IBusinessPackageGetterService getPackage(BIPackageID id) throws BIPackageAbsentException;

    BIPackageContainer clonePackageContainer() throws CloneNotSupportedException;

}