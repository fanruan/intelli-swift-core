package com.fr.bi.etl.analysis.manager;

import com.fr.bi.base.BIBusinessPackagePersistThread;
import com.fr.bi.base.BIBusinessPackagePersistThreadHolder;

/**
 * This class created on 17/2/9.
 *
 * @author Neil Wang
 * @since Advanced FineBI Analysis 1.0
 */
public class BIUserETLBusinessPackagePersistThreadHolder {
    private static BIUserETLBusinessPackagePersistThreadHolder biBusinessPackagePersistThreadHolder = new BIUserETLBusinessPackagePersistThreadHolder();
    private BIBusinessPackagePersistThread biBusinessPackagePersistThread = new BIBusinessPackagePersistThread();

    {
        biBusinessPackagePersistThread.start();
    }

    private BIUserETLBusinessPackagePersistThreadHolder() {
    }

    public static BIUserETLBusinessPackagePersistThreadHolder getInstance() {
        return biBusinessPackagePersistThreadHolder;
    }

    public BIBusinessPackagePersistThread getBiBusinessPackagePersistThread() {
        return biBusinessPackagePersistThread;
    }
}
