package com.finebi.cube.conf.pack.group;


import com.finebi.cube.conf.pack.data.BIBusinessPackage;
import com.finebi.cube.conf.pack.data.BIGroupTagName;
import com.finebi.cube.conf.pack.data.BIPackageID;
import com.fr.bi.conf.data.pack.exception.BIGroupAbsentException;
import com.fr.bi.conf.data.pack.exception.BIPackageAbsentException;

import java.util.Set;

/**
 * This class created on 2016/5/23.
 *
 * @author Connery
 * @since 4.0
 */
public interface IGroupTagsManagerGetterService {
    Set<BIGroupTagName> getAllGroupTagName();

    IBusinessGroupGetterService getReadableGroup(BIGroupTagName groupName) throws BIGroupAbsentException;

    BIBusinessPackage getPackage(BIGroupTagName groupName, BIPackageID packageId) throws BIGroupAbsentException, BIPackageAbsentException;

    Boolean existGroupTag(BIGroupTagName groupName);

    Boolean isPackageTaggedSomeGroup(BIPackageID packageID);

    Boolean isPackageTaggedSpecificGroup(BIGroupTagName groupTagName, BIPackageID packageID) throws BIGroupAbsentException;
}