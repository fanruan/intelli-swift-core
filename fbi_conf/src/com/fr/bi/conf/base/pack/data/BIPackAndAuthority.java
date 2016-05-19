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


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BIPackAndAuthority that = (BIPackAndAuthority) o;

        return biPackageID != null ? biPackageID.equals(that.biPackageID) : that.biPackageID == null;

    }

    @Override
    public int hashCode() {
        return biPackageID != null ? biPackageID.hashCode() : 0;
    }
}
