package com.fr.bi.conf.provider;

import com.fr.bi.conf.records.BICubeTaskRecord;
import com.fr.bi.conf.records.SingleUserBICubeTaskRecordManager;

import java.util.List;

/**
 * This class created on 16-12-9.
 *
 * @author Kary
 * @since Advanced FineBI Analysis 1.0
 */
public interface BICubeTaskRecordProvider {
    String XML_TAG = "BICubeTaskRecordProvider";

    void saveCubeTaskRecord(long userId, BICubeTaskRecord record);

    void clear(long userId);

    SingleUserBICubeTaskRecordManager getSingleUserBICubeTaskRecordManager(long userId);

    List<BICubeTaskRecord> getAllTaskRecords();

    @Deprecated
    void persistData(long userId);
}
