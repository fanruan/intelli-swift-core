package com.finebi.cube.conf.pack.data;


import com.finebi.cube.conf.table.BIBusinessTable;
import com.finebi.cube.conf.table.IBusinessTable;
import com.fr.bi.base.BIUser;
import com.fr.bi.stable.data.BITableID;

/**
 * ID唯一确定
 */
public class BIBasicBusinessPackage extends BIBusinessPackage<IBusinessTable> {

    public BIBasicBusinessPackage(BIPackageID ID, BIPackageName name, BIUser owner, long position) {
        super(ID, name, owner, position);
    }

    public BIBasicBusinessPackage(BIPackageID id) {
        super(id);
    }


    protected IBusinessTable createTable() {
        return new BIBusinessTable(new BITableID(""));
    }


}