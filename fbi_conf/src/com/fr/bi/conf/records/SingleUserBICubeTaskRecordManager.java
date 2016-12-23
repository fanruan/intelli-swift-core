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
    private int maxLogNums = 200;

    public void saveTaskRecord(BICubeTaskRecord record) {
        synchronized (this) {
            if (cubeTaskRecords.size() > (maxLogNums > 0 ? maxLogNums : 0)) {
                cubeTaskRecords.remove(0);
            }
            cubeTaskRecords.add(record);
        }
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
