package com.finebi.cube.conf.pack.data;

import com.finebi.cube.conf.table.BusinessTable;
import com.fr.bi.base.BIUser;
import com.fr.bi.stable.data.BITableID;
import com.fr.bi.stable.exception.BITableAbsentException;

import java.util.Set;

/**
 * This class created on 2016/5/23.
 *
 * @author Connery
 * @since 4.0
 */
public interface IBusinessPackageGetterService<T extends BusinessTable> {

    BIUser getOwner();

    BIPackageName getName();

    BIPackageID getID();

    boolean isNeed2BuildCube(BIBusinessPackage targetPackage);

    Set<T> getBusinessTables();

    T getSpecificTable(BITableID tableID) throws BITableAbsentException;
}