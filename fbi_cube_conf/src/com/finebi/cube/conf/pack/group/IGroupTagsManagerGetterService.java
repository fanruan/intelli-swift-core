package com.finebi.cube.conf.pack.group;


import com.finebi.cube.conf.pack.data.BIBusinessPackage;
import com.finebi.cube.conf.pack.data.BIGroupTagName;
import com.finebi.cube.conf.pack.data.BIPackageID;
import com.fr.bi.conf.data.pack.exception.BIGroupAbsentException;
import com.fr.bi.conf.data.pack.exception.BIPackageAbsentException;

import java.util.Set;

/**
 * Created by Connery on 2016/1/20.
 */
public interface IGroupTagsManagerGetterService {
    Set<BIGroupTagName> getAllGroupTagName();

    IBusinessGroupGetterService getReadableGroup(BIGroupTagName groupName) throws BIGroupAbsentException;

    BIBusinessPackage getPackage(BIGroupTagName groupName, BIPackageID packageId) throws BIGroupAbsentException, BIPackageAbsentException;

    Boolean existGroupTag(BIGroupTagName groupName);

    Boolean isPackageTaggedSomeGroup(BIPackageID packageID);

    Boolean isPackageTaggedSpecificGroup(BIGroupTagName groupTagName, BIPackageID packageID) throws BIGroupAbsentException;
}