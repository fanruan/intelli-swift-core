package com.finebi.cube.conf.pack.data;

import com.fr.bi.base.BIName;

/**
 * This class created on 2016/5/23.
 *
 * @author Connery
 * @since 4.0
 */
public class BIPackageName extends BIName {
    private static final long serialVersionUID = -8246876373658350155L;
    public static BIPackageName DEFAULT = new BIPackageName("BI_EMPTY_NAME");

    public BIPackageName(String name) {
        super(name);
    }

    public BIPackageName() {
    }
}