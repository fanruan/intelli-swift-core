package com.fr.bi.conf.base.pack.data;

import com.sun.org.apache.xpath.internal.operations.String;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wuk on 16/5/9.
 */
public class BIPackageAndAuthority {
    protected String packageId;
    protected List<String> roleNameList = new ArrayList<String>();

    public String getPackageId() {
        return packageId;
    }

    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }

    public List<String> getRoleNameList() {
        return roleNameList;
    }

    public void setRoleNameList(List<String> roleNameList) {
        this.roleNameList = roleNameList;
    }
}
