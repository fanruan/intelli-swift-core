package com.fr.bi.conf.records;

import java.util.*;

/**
 * This class created on 16-12-10.
 *
 * @author Kary
 * @since Advanced FineBI Analysis 1.0
 */
public class SingleUserBICubeTaskRecordManager {
    private final static String XML_TAG = "SingleUserBICubeTaskRecordManager";
    private List<BICubeTaskRecord> cubeTaskRecords = new ArrayList<BICubeTaskRecord>();

    public void saveTaskRecord(BICubeTaskRecord record) {
        this.cubeTaskRecords.add(record);
    }

    public void clear() {
        synchronized (this) {
            cubeTaskRecords.clear();
        }
    }

    public List<BICubeTaskRecord> getCubeTaskRecords() {
        return cubeTaskRecords;
    }
}
