package com.fr.bi.api;

import com.finebi.cube.common.log.BILogger;
import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.conf.BICubeConfigureCenter;
import com.finebi.cube.conf.CubeGenerationManager;
import com.finebi.cube.conf.pack.data.BIBasicBusinessPackage;
import com.finebi.cube.conf.pack.data.BIBusinessPackage;
import com.finebi.cube.conf.pack.data.BIPackageName;
import com.finebi.cube.conf.pack.data.IBusinessPackageGetterService;
import com.finebi.cube.conf.pack.group.IBusinessGroupGetterService;
import com.finebi.cube.conf.table.BusinessTable;
import com.fr.bi.conf.manager.update.source.UpdateSettingSource;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.exception.BITableAbsentException;
import com.fr.stable.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.finebi.cube.conf.BICubeConfigureCenter.getPackageManager;

/**
 * Created by Roy on 2017/8/24 0024.
 */
public class BIConfigAPIUtils {
    private static BILogger LOGGER = BILoggerFactory.getLogger(BIConfigAPIUtils.class);

    /**
     * 所有的分组的名字，以及对应业务包的名字
     *
     * @param userId
     * @return
     */
    public static Map<String, List<String>> getAllPackagesWithGroupInfo(long userId) {
        Map<String, List<String>> resultMap = new LinkedHashMap<String, List<String>>();

        /**
         * 获取未分组的业务包
         */
        List<String> noneGroupPackageNameList = new ArrayList<String>();
        for (IBusinessPackageGetterService businessPackage : getPackageManager().getAllPackages(userId)) {
            if (!getPackageManager().isPackageTaggedSomeGroup(userId, businessPackage.getID())) {
                noneGroupPackageNameList.add(businessPackage.getName().getName());
            }
        }
        resultMap.put(StringUtils.EMPTY, noneGroupPackageNameList);

        /**
         * 获取已分组的业务包
         */
        List<IBusinessGroupGetterService> allGroups = getPackageManager().getAllGroups(userId);
        for (IBusinessGroupGetterService group : allGroups) {
            List<String> packageNameList = new ArrayList<String>();
            for (BIBusinessPackage biBusinessPackage : group.getPackages()) {
                packageNameList.add(biBusinessPackage.getName().getName());
            }
            resultMap.put(group.getName().getName(), packageNameList);
        }
        return resultMap;
    }

    /**
     * 这个业务包之下所有表的名字（转义之后的名字）以及表id
     *
     * @param useId
     * @param packageName
     * @return
     */
    public static List<Map<String, String>> getTablesInfoOfOnePackage(long useId, String packageName) {
        List<Map<String, String>> resultList = new ArrayList<Map<String, String>>();
        Set<BIBusinessPackage> packageSet = BICubeConfigureCenter.getPackageManager().getPackageByName(useId, new BIPackageName(packageName));
        if (packageSet.size() > 1) {
            LOGGER.warn("There are more packages than one with the same package name. Package name is: " + packageName);
        }
        for (BIBusinessPackage biBusinessPackage : packageSet) {
            for (BusinessTable table : ((BIBasicBusinessPackage) biBusinessPackage).getBusinessTables()) {
                Map<String, String> tableInfoMap = new HashMap<String, String>();
                tableInfoMap.put("tableAliasName", BICubeConfigureCenter.getAliasManager().getAliasName(table.getID().getIdentity(), useId));
                tableInfoMap.put("tableId", table.getID().getIdentityValue());
                resultList.add(tableInfoMap);
            }
        }
        return resultList;
    }

    /**
     * 根据当前业务包的状态选择是全局更新/Check更新/配置更新
     *
     * @param userId
     */
    public static void addCubeGenerateTask(long userId) {
        CubeGenerationManager.getCubeManager().addCubeGenerateTask2Queue(userId, null, null, false);
    }

    /**
     * 单表全量更新，如果此表是etl表就对此etl表所有的基础表单表全量更新
     *
     * @param userId
     * @param businessTableId 业务包表的tableId
     */
    public static void addSingleTableGenerateTask(long userId, String businessTableId) {
        try {
            BusinessTable businessTable = BICubeConfigureCenter.getPackageManager().getConfigTableById(userId, businessTableId);
            CubeTableSource tableSource = businessTable.getTableSource();
            Set<CubeTableSource> baseTableSourceSet = tableSource.createGenerateTablesMap().get(0);
            Map<String, Integer> baseTableSourceUpdateTypeMap = new HashMap<String, Integer>();
            int i = 0;
            for (CubeTableSource cubeTableSource : baseTableSourceSet) {
                LOGGER.info("The baseTableSource" + i + " is :" + cubeTableSource.getSourceID());
                baseTableSourceUpdateTypeMap.put(cubeTableSource.getSourceID(), DBConstant.SINGLE_TABLE_UPDATE_TYPE.ALL);
                i++;
            }
            CubeGenerationManager.getCubeManager().addCubeGenerateTask2Queue(userId, false, null, baseTableSourceUpdateTypeMap);
        } catch (BITableAbsentException e) {
            LOGGER.error("The business table is absent.The table id is: " + businessTableId);
        }


    }


    /**
     * 根据业务包名找到包含的所有的业务包表，在找到这些业务包表所有的基础表，对所有的基础表进行单表更新
     * 每一个基础表的更新方式都是设置的此表在全局更新时的更新
     *
     * @param userId
     * @param packageName
     */
    public static void addPackageCubeGenerateTask(long userId, String packageName) {
        Set<CubeTableSource> allBaseTableSourceSet = new HashSet<CubeTableSource>();
        Set<BIBusinessPackage> packages = BICubeConfigureCenter.getPackageManager().getPackageByName(userId, new BIPackageName(packageName));
        if (packages.size() > 1) {
            LOGGER.warn("There are more packages than one with the same package name. Package name is: " + packageName);
        }
        for (BIBusinessPackage biBusinessPackage : packages) {
            for (BusinessTable table : ((BIBasicBusinessPackage) biBusinessPackage).getBusinessTables()) {
                CubeTableSource tableSource = table.getTableSource();
                Set<CubeTableSource> baseTableSourceSet = tableSource.createGenerateTablesList().get(0);
                allBaseTableSourceSet.addAll(baseTableSourceSet);
            }
        }

        Map<String, Integer> updateSettingMap = new HashMap<String, Integer>();
        for (CubeTableSource tableSource : allBaseTableSourceSet) {
            LOGGER.info("tableSourceID is: " + tableSource.getSourceID());
            Integer updateType = DBConstant.SINGLE_TABLE_UPDATE_TYPE.ALL;
            UpdateSettingSource updateSettingSource = BIConfigureManagerCenter.getUpdateFrequencyManager().getUpdateSetting(tableSource.getSourceID(), userId);

            if (updateSettingSource == null) {
                LOGGER.info("update Type is null.Set it to All");
            } else {
                updateType = updateSettingSource.getUpdateType();
                LOGGER.info("update Type is: " + updateType);
            }
            updateSettingMap.put(tableSource.getSourceID(), updateType);
        }

        CubeGenerationManager.getCubeManager().addCubeGenerateTask2Queue(userId, false, null, updateSettingMap);

    }

}
