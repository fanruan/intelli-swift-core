package com.fr.bi.conf.provider;

import com.fr.bi.conf.records.SingleUserBICubeTaskRecordManager;

/**
 * This class created on 16-12-9.
 *
 * @author Kary
 * @since Advanced FineBI Analysis 1.0
 */
public interface BICubeTaskRecordProvider {
    String XML_TAG = "BICubeOperationRecordManagerProvider";

    void saveErrorTableLogs(long userId);

    void saveErrorPathLogs(long userId);

    void clear(long userId);

    SingleUserBICubeTaskRecordManager getTaskRecordManager(long userId);

    void saveAllSingleSourceLayers(long userId);

    void saveAllPaths(long userId);
}
