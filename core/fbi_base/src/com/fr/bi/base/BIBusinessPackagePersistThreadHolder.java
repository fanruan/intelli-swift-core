package com.fr.bi.base;

/**
 * Created by hasee on 2016/11/17.
 */
public class BIBusinessPackagePersistThreadHolder {
    private static BIBusinessPackagePersistThreadHolder biBusinessPackagePersistThreadHolder = new BIBusinessPackagePersistThreadHolder();
    private BIBusinessPackagePersistThread biBusinessPackagePersistThread = new BIBusinessPackagePersistThread();

    {
        biBusinessPackagePersistThread.start();
    }

    private BIBusinessPackagePersistThreadHolder() {
    }

    public static BIBusinessPackagePersistThreadHolder getInstance() {
        return biBusinessPackagePersistThreadHolder;
    }

    public BIBusinessPackagePersistThread getBiBusinessPackagePersistThread() {
        return biBusinessPackagePersistThread;
    }
}
