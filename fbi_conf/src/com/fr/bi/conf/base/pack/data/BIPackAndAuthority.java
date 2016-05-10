package com.fr.bi.conf.base.pack.data;

/**
 * Created by wuk on 16/5/9.
 */
public class BIPackAndAuthority {
    protected String biPackageID;
    protected String[] roleIdArray;


    public void setBiPackageID(String biPackageID) {
        this.biPackageID = biPackageID;
    }

    public String getBiPackageID() {
        return biPackageID;
    }

    public String[] getRoleIdArray() {
        return roleIdArray;
    }

    public void setRoleIdArray(String[] roleIdArray) {
        this.roleIdArray = roleIdArray;
    }



}
