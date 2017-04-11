package com.fr.bi.cal.generate;

import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.conf.CubeGenerationManager;
import com.fr.bi.cal.utils.Single2CollectionUtils;
import com.fr.bi.conf.manager.update.source.UpdateSettingSource;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Lucifer on 2017-3-22.
 *
 * @author Lucifer
 * @since Advanced FineBI Analysis 1.0
 */
public class CustomTaskPool {

    private static class SingletonHolder {
        static final CustomTaskPool INSTANCE = new CustomTaskPool();
    }

    private static CustomTaskPool getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private Map<Long, Set<String>> baseTableSourceIdMap;

    private CustomTaskPool() {
        baseTableSourceIdMap = new ConcurrentHashMap<Long, Set<String>>();
    }

    public Map<Long, Set<String>> getBaseTableSourceIdMap() {
        return baseTableSourceIdMap;
    }

    public static boolean addCustomGenerateTask(long userId, String baseTableSourceId) {
        try {
            if (getInstance().getBaseTableSourceIdMap().containsKey(userId)) {
                getInstance().getBaseTableSourceIdMap().get(userId).add(baseTableSourceId);
            } else {
                getInstance().getBaseTableSourceIdMap().put(userId, new HashSet<String>());
                getInstance().getBaseTableSourceIdMap().get(userId).add(baseTableSourceId);
            }
            return true;
        } catch (Exception e) {
            BILoggerFactory.getLogger(CustomTaskPool.class).error(e.getMessage(), e);
            return false;
        }
    }

    public static boolean startCustomGenerateTask(long userId) {
        try {
            if (getInstance().getBaseTableSourceIdMap().containsKey(userId)) {
                Set<String> baseSourceIdSet = new HashSet<String>();
                baseSourceIdSet.addAll(getInstance().getBaseTableSourceIdMap().get(userId));
                getInstance().getBaseTableSourceIdMap().remove(userId);
                List<String> baseSourceIdList = Single2CollectionUtils.setToList(baseSourceIdSet);
                List<Integer> updateTypeList = new ArrayList<Integer>();

                for (String baseSourceId : baseSourceIdList) {
                    UpdateSettingSource updateSettingSource = BIConfigureManagerCenter.getUpdateFrequencyManager().getUpdateSetting(baseSourceId, userId);
                    if (updateSettingSource == null) {
                        updateSettingSource = new UpdateSettingSource();
                    }
                    updateTypeList.add(updateSettingSource.getUpdateType());
                }
                CubeGenerationManager.getCubeManager().addCustomTableTask2Queue(userId, baseSourceIdList, updateTypeList);
            }
            return true;
        } catch (Exception e) {
            BILoggerFactory.getLogger(CustomTaskPool.class).error(e.getMessage(), e);
            return false;
        }
    }
}
