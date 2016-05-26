package com.finebi.cube.conf.data;


import com.finebi.cube.conf.pack.data.BIBasicBusinessPackage;
import com.finebi.cube.conf.pack.data.BIPackageID;

/**
 * Created by Connery on 2016/1/11.
 */
public class BIBusinessPackageTestTool {

    public static BIBasicBusinessPackage generatePackage(String id) {
        return new BIBasicBusinessPackage(new BIPackageID(id));

    }
}