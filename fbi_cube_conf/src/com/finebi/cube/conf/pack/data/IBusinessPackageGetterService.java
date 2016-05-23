package com.finebi.cube.conf.pack.data;

import com.finebi.cube.conf.table.IBusinessTable;
import com.fr.bi.base.BIUser;
import com.fr.bi.stable.data.BITableID;
import com.fr.bi.stable.exception.BITableAbsentException;

import java.util.Set;

/**
 * Created by Connery on 2016/1/20.
 */
public interface IBusinessPackageGetterService<T extends IBusinessTable> {

    BIUser getOwner();

    BIPackageName getName();

    BIPackageID getID();

    boolean isNeed2BuildCube(BIBusinessPackage targetPackage);

    Set<T> getBusinessTables();

    T getSpecificTable(BITableID tableID) throws BITableAbsentException;
}