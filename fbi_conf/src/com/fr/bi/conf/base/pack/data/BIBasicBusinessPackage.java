package com.fr.bi.conf.base.pack.data;


import com.fr.bi.base.BIUser;

/**
 * ID唯一确定
 */
public class BIBasicBusinessPackage extends BIBusinessPackage<BIBusinessTable> {

    public BIBasicBusinessPackage(BIPackageID ID, BIPackageName name, BIUser owner, long position) {
        super(ID, name, owner, position);
    }

    public BIBasicBusinessPackage(BIPackageID id) {
        super(id);
    }


    protected BIBusinessTable createTable() {
        return new BIBusinessTable("", this.owner.getUserId());
    }


}