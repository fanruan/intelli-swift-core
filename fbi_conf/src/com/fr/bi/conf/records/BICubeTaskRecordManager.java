package com.fr.bi.conf.records;

import com.finebi.cube.conf.BISystemDataManager;
import com.fr.bi.conf.provider.BICubeTaskRecordProvider;
import com.fr.bi.exception.BIKeyAbsentException;

/**
 * This class created on 16-12-9.
 *
 * @author Kary
 * @since Advanced FineBI Analysis 1.0
 */
public class BICubeTaskRecordManager extends BISystemDataManager<SingleUserBICubeTaskRecordManager> implements BICubeTaskRecordProvider {

    @Override
    public SingleUserBICubeTaskRecordManager constructUserManagerValue(Long userId) {
        return new SingleUserBICubeTaskRecordManager();
    }

    @Override
    public String managerTag() {
        return "BICubeTaskRecordManager";
    }

    @Override
    public String persistUserDataName(long key) {
        return managerTag();
    }

    @Override
    public void saveCubeTaskRecord(long userId, BICubeTaskRecord record) {
getSingleUserBICubeTaskRecordManager(userId).saveTaskRecord(record);
    }


    @Override
    public void clear(long userId) {
        synchronized (this.getClass()) {
            getSingleUserBICubeTaskRecordManager(userId).clear();
        }
    }

    @Override
    public SingleUserBICubeTaskRecordManager getSingleUserBICubeTaskRecordManager(long userId) {
        try {
            return getValue(userId);
        } catch (BIKeyAbsentException e) {
            throw new NullPointerException("Please check the userID:" + userId + ",which getIndex a empty manager");
        }

    }

    @Override
    public void persistData(long userId) {
        super.persistUserData(userId);
    }
}
