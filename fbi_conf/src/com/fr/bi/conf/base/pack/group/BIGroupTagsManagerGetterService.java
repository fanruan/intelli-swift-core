package com.fr.bi.conf.base.pack.group;

import com.fr.bi.conf.base.pack.data.BIBusinessPackage;
import com.fr.bi.conf.base.pack.data.BIGroupTagName;
import com.fr.bi.conf.base.pack.data.BIPackageID;
import com.fr.bi.conf.data.pack.exception.BIGroupAbsentException;
import com.fr.bi.conf.data.pack.exception.BIPackageAbsentException;

import java.util.Set;

/**
 * Created by Connery on 2016/1/20.
 */
public interface BIGroupTagsManagerGetterService {
    Set<BIGroupTagName> getAllGroupTagName();

    BIBusinessGroupGetterService getReadableGroup(BIGroupTagName groupName) throws BIGroupAbsentException;

    BIBusinessPackage getPackage(BIGroupTagName groupName, BIPackageID packageId) throws BIGroupAbsentException, BIPackageAbsentException;

    Boolean existGroupTag(BIGroupTagName groupName);

    Boolean isPackageTaggedSomeGroup(BIPackageID packageID);

    Boolean isPackageTaggedSpecificGroup(BIGroupTagName groupTagName, BIPackageID packageID) throws BIGroupAbsentException;
}