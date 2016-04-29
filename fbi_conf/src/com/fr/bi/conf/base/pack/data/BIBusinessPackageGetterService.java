package com.fr.bi.conf.base.pack.data;

import com.fr.bi.base.BIUser;
import com.fr.bi.stable.data.BITableID;
import com.fr.bi.stable.exception.BITableAbsentException;

import java.util.Set;

/**
 * Created by Connery on 2016/1/20.
 */
public interface BIBusinessPackageGetterService<T extends BIBusinessTable> {
    BIUser getOwner();

    BIPackageName getName();

    BIPackageID getID();

    boolean isNeed2BuildCube(BIBusinessPackage targetPackage);

    Set<T> getBusinessTables();

    T getSpecificTable(BITableID tableID) throws BITableAbsentException;
}