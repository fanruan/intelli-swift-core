package com.fr.bi.conf.records;

import com.finebi.cube.conf.BISystemDataManager;
import com.finebi.cube.relation.BITableSourceRelationPath;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.conf.provider.BICubeTaskRecordProvider;
import com.fr.bi.exception.BIKeyAbsentException;
import com.fr.bi.stable.data.source.CubeTableSource;

import java.util.Set;

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
    public void saveAllSingleSourceLayers(long userId) {
        Set<CubeTableSource> allTableSourceSet = BIConfigureManagerCenter.getLogManager().getAllTableSourceSet(userId);
        getTaskRecordManager(userId).setAllSingleSourceLayers(allTableSourceSet);
    }

    @Override
    public void saveAllPaths(long userId) {
        Set<BITableSourceRelationPath> allRelationPathSet = BIConfigureManagerCenter.getLogManager().getAllRelationPathSet(userId);
        getTaskRecordManager(userId).setAllRelationPaths(allRelationPathSet);
    }

    @Override
    public void saveErrorTableLogs(long userId) {
        getTaskRecordManager(userId).setErrorTableLogs(BIConfigureManagerCenter.getLogManager().getErrorTables(userId));
    }

    @Override
    public void saveErrorPathLogs(long userId) {
        getTaskRecordManager(userId).setErrorPathLogs(BIConfigureManagerCenter.getLogManager().getErrorPaths(userId));
    }

    @Override
    public void clear(long userId) {
        getTaskRecordManager(userId).clear();
    }

    @Override
    public SingleUserBICubeTaskRecordManager getTaskRecordManager(long userId) {
        try {
            return getValue(userId);
        } catch (BIKeyAbsentException e) {
            throw new NullPointerException("Please check the userID:" + userId + ",which getIndex a empty manager");
        }

    }
}
