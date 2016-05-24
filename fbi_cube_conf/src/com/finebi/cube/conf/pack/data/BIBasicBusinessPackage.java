package com.finebi.cube.conf.pack.data;


import com.finebi.cube.conf.table.BIBusinessTable;
import com.finebi.cube.conf.table.BusinessTable;
import com.fr.bi.base.BIUser;
import com.fr.bi.stable.data.BITableID;

/**
 * This class created on 2016/5/23.
 *
 * @author Connery
 * @since 4.0
 * <p/>
 * ID唯一确定
 */
public class BIBasicBusinessPackage extends BIBusinessPackage<BusinessTable> {

    public BIBasicBusinessPackage(BIPackageID ID, BIPackageName name, BIUser owner, long position) {
        super(ID, name, owner, position);
    }

    public BIBasicBusinessPackage(BIPackageID id) {
        super(id);
    }


    protected BusinessTable createTable() {
        return new BIBusinessTable(new BITableID(""));
    }


}