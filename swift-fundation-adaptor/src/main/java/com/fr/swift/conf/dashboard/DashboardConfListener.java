package com.fr.swift.conf.dashboard;

import com.finebi.base.constant.FineEngineType;
import com.finebi.common.internalimp.config.pack.PackageInfoImpl;
import com.finebi.common.internalimp.config.session.CommonConfigManager;
import com.finebi.common.structure.config.entryinfo.EntryInfo;
import com.finebi.common.structure.config.fieldinfo.FieldInfo;
import com.finebi.common.structure.config.pack.PackageInfo;
import com.finebi.common.structure.config.relation.Relation;
import com.finebi.common.structure.config.session.EntryInfoSession;
import com.finebi.conf.exception.FinePackageAbsentException;
import com.finebi.conf.internalimp.update.TableUpdateInfo;
import com.fr.general.ComparatorUtils;
import com.fr.swift.conf.dashboard.store.DashboardConfManager;
import com.fr.swift.conf.updateInfo.TableUpdateInfoConfigService;
import com.fr.swift.constants.UpdateConstants;
import com.fr.swift.task.TaskResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author yee
 * @date 2018/5/22
 */
class DashboardConfListener {
    static void listen(TaskResult result) {
        if (result.getType() == TaskResult.Type.SUCCEEDED) {
            TableUpdateInfoConfigService service = TableUpdateInfoConfigService.getService();
            Map<String, TableUpdateInfo> infos = service.getAllTableUpdateInfo();
            if (!infos.isEmpty()) {
                Iterator<Map.Entry<String, TableUpdateInfo>> iterator = infos.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, TableUpdateInfo> entry = iterator.next();
                    String tableName = entry.getKey();
                    if (ComparatorUtils.equals(UpdateConstants.GLOBAL_KEY, tableName)) {
                        List<PackageInfo> packageInfos = CommonConfigManager.getPackageSession(FineEngineType.Cube).getAllPackages();
                        for (PackageInfo info : packageInfos) {
                            if (null != info) {
                                savePackage(info);
                            }
                        }
                    } else if (entry.getValue().getUpdateType() != UpdateConstants.TableUpdateType.NEVER) {
                        EntryInfo entryInfo = CommonConfigManager.getEntryInfoSession(FineEngineType.Cube).findByName(tableName);
                        if (null != entryInfo) {
                            FieldInfo fieldInfo = CommonConfigManager.getFieldSession(FineEngineType.Cube).find(entryInfo.getID());
                            if (null != fieldInfo) {
                                DashboardConfManager.getManager().getEntryInfoSession().save(entryInfo);
                                DashboardConfManager.getManager().getFieldSession().save(fieldInfo);
                                PackageInfo info = CommonConfigManager.getPackageSession(FineEngineType.Cube).getPackageByEntryInfoId(entryInfo.getID());
                                try {
                                    PackageInfo dashboardInfo = DashboardConfManager.getManager().getPackageSession().getPackageById(info.getId());
                                    dealDashboardPackage(dashboardInfo, entryInfo.getID());
                                } catch (FinePackageAbsentException e) {
                                    dealCommonPackage(info, entryInfo.getID());
                                }
                            }
                        } else {
                            PackageInfo info = CommonConfigManager.getPackageSession(FineEngineType.Cube).getPackageById(tableName);
                            if (null != info) {
                                if (entry.getValue().getUpdateType() == UpdateConstants.PackageUpdateType.UPDATE) {
                                    savePackage(info);
                                } else {
                                    removePackage(info);
                                }
                            }
                        }
                    } else {
                        EntryInfo entryInfo = CommonConfigManager.getEntryInfoSession(FineEngineType.Cube).findByName(tableName);
                        removeEntryInfo(entryInfo);
                    }
                }
            } else {
                List<PackageInfo> packageInfos = CommonConfigManager.getPackageSession(FineEngineType.Cube).getAllPackages();
                for (PackageInfo info : packageInfos) {
                    if (null != info) {
                        savePackage(info);
                    }
                }
            }
            updateRelations();
        }
    }

    private static void dealDashboardPackage(PackageInfo packageInfo, String tableId) throws FinePackageAbsentException {
        if (null == packageInfo) {
            throw new FinePackageAbsentException();
        }
        packageInfo.addTable(tableId);
        DashboardConfManager.getManager().getPackageSession().put(packageInfo);
    }

    private static void dealCommonPackage(PackageInfo packageInfo, String tableId) {
        PackageInfo target = PackageInfoImpl.create(packageInfo.getId(), packageInfo.getName(), packageInfo.getAllGroupNames(), packageInfo.getUserId());
        ((PackageInfoImpl) target).setTableIds(Arrays.asList(tableId));
        DashboardConfManager.getManager().getPackageSession().put(target);
    }

    private static void savePackage(PackageInfo info) {
        List<String> tableIds = info.getTableIds();
        for (String tableId : tableIds) {
            EntryInfo updated = CommonConfigManager.getEntryInfoSession(FineEngineType.Cube).find(tableId);
            if (null != updated) {
                FieldInfo fieldInfo = CommonConfigManager.getFieldSession(FineEngineType.Cube).find(updated.getID());
                if (null != fieldInfo) {
                    DashboardConfManager.getManager().getEntryInfoSession().save(updated);
                    DashboardConfManager.getManager().getFieldSession().save(fieldInfo);
                }
            } else {
                info.removeTable(tableId);
            }
        }
        DashboardConfManager.getManager().getPackageSession().put(info);
    }

    private static void removePackage(PackageInfo info) {
        if (null != info && null != DashboardConfManager.getManager().getPackageSession().getPackageById(info.getId())) {
            DashboardConfManager.getManager().getPackageSession().delPackage(info.getId());
        }
    }

    private static void removeEntryInfo(EntryInfo entryInfo) {
        if (null != entryInfo && null != DashboardConfManager.getManager().getEntryInfoSession().find(entryInfo.getID())) {
            DashboardConfManager.getManager().getEntryInfoSession().delete(entryInfo.getID());
        }
    }

    private static void updateRelations() {
        List<Relation> relations = CommonConfigManager.getRelationConfigurationSession(FineEngineType.Cube).getCheckedConfig().getRelations();
        Map<String, List<Relation>> updateMap = new HashMap<String, List<Relation>>();
        for (Relation relation : relations) {
            String primary = relation.getPrimaryTableId();
            String foreign = relation.getAttachedTableId();
            EntryInfoSession session = DashboardConfManager.getManager().getEntryInfoSession();
            if (null != session.find(primary) && null != session.find(foreign)) {
                if (!updateMap.containsKey(primary)) {
                    updateMap.put(primary, new ArrayList<Relation>());
                }
                updateMap.get(primary).add(relation);
            }
        }
        Iterator<Map.Entry<String, List<Relation>>> iterator = updateMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, List<Relation>> entry = iterator.next();
            DashboardConfManager.getManager().getRelationConfigurationSession().updateRelation(entry.getKey(), entry.getValue());
        }
    }
}
