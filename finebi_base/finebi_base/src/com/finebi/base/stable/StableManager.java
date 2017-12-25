package com.finebi.base.stable;

import com.finebi.base.context.BaseContext;

/**
 * This class created on 2017/12/21
 *
 * @author Each.Zhang
 */
public class StableManager {

    static BaseContext context;

    private static String[] scanPath = {
            "com.finebi.conf.imp",
            "com.finebi.conf.aop",
    };

    static {
        context = new BaseContext();
        context.addScanPath(scanPath);
    }

    public static BaseContext getContext() {
        return context;
    }
}
