package com.fr.bi.conf.data;

import com.fr.bi.conf.base.pack.data.BIBasicBusinessPackage;
import com.fr.bi.conf.base.pack.data.BIPackageID;

/**
 * Created by Connery on 2016/1/11.
 */
public class BIBusinessPackageTestTool {

    public static BIBasicBusinessPackage generatePackage(String id) {
        return new BIBasicBusinessPackage(new BIPackageID(id));

    }
}