package com.finebi.cube.conf.pack.data;

import com.fr.bi.base.BIName;

/**
 * Created by Connery on 2015/12/28.
 */
public class BIPackageName extends BIName {
    public static BIPackageName DEFAULT = new BIPackageName("BI_EMPTY_NAME");

    public BIPackageName(String name) {
        super(name);
    }

    public BIPackageName() {
    }
}