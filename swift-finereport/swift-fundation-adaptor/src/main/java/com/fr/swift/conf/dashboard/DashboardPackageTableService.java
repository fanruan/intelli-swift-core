package com.fr.swift.conf.dashboard;

import com.finebi.base.constant.FineEngineType;
import com.finebi.common.internalimp.config.driver.CommonDataSourceDriverFactory;
import com.finebi.common.structure.config.driver.CommonDataSourceDriver;
import com.finebi.common.structure.config.entryinfo.EntryInfo;
import com.finebi.common.structure.config.pack.PackageInfo;
import com.finebi.conf.exception.FineEngineException;
import com.finebi.conf.exception.FineGroupAbsentException;
import com.finebi.conf.exception.FinePackageAbsentException;
import com.finebi.conf.exception.FineTableAbsentException;
import com.finebi.conf.internalimp.pack.FineBusinessPackageImp;
import com.finebi.conf.structure.bean.pack.FineBusinessPackage;
import com.finebi.conf.structure.bean.table.FineBusinessTable;
import com.fr.engine.config.constants.DriverType;
import com.fr.engine.utils.CollectionUtils;
import com.fr.engine.utils.StringUtils;
import com.fr.swift.conf.dashboard.store.DashboardConfManager;
import com.fr.swift.task.TaskResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author yee
 * @date 2018/5/22
 */
public class DashboardPackageTableService {
    private static DashboardPackageTableService service;

    private DashboardPackageTableService() {
    }

    public static DashboardPackageTableService getService() {
        if (null == service) {
            synchronized (DashboardPackageTableService.class) {
                service = new DashboardPackageTableService();
            }
        }
        return service;
    }

    public void listen(TaskResult result) {
        DashboardConfListener.listen(result);
    }

    public FineBusinessTable getTableByName(String name) throws FineTableAbsentException {
        EntryInfo entryInfo = DashboardConfManager.getManager().getEntryInfoSession().findByName(name);
        if (null != entryInfo) {
            return createBusinessTable(entryInfo);
        }
        throw new FineTableAbsentException();
    }

    public List<FineBusinessTable> getBusinessTables(String packageId) throws FinePackageAbsentException {
        List<FineBusinessTable> result = new ArrayList<FineBusinessTable>();
        PackageInfo info = DashboardConfManager.getManager().getPackageSession().getPackageById(packageId);
        if (null == info) {
            throw new FinePackageAbsentException();
        }
        List<String> tableIds = info.getTableIds();
        for (String tableId : tableIds) {
            try {
                FineBusinessTable table = getBusinessTable(tableId);
                if (null != table) {
                    result.add(table);
                }
            } catch (Exception ignore) {

            }
        }
        return Collections.unmodifiableList(result);
    }

    public FineBusinessTable getBusinessTable(String entryInfoId) throws FineTableAbsentException {
        if (!StringUtils.isEmpty(entryInfoId)) {
            EntryInfo info = DashboardConfManager.getManager().getEntryInfoSession().find(entryInfoId);
            if (null != info) {
                return createBusinessTable(info);
            }
        }
        throw new FineTableAbsentException();
    }

    public List<FineBusinessTable> getAllBusinessTable() {
        Map<String, EntryInfo> all = DashboardConfManager.getManager().getEntryInfoSession().getAll();
        List<FineBusinessTable> allTables = new ArrayList<FineBusinessTable>();
        Iterator<Map.Entry<String, EntryInfo>> iterator = all.entrySet().iterator();
        while (iterator.hasNext()) {
            allTables.add(createBusinessTable(iterator.next().getValue()));
        }
        return Collections.unmodifiableList(allTables);
    }

    public boolean isTableExists(String tableName) {
        return DashboardConfManager.getManager().getEntryInfoSession().findByName(tableName) != null;
    }

    public boolean isPackageExists(String packageId) {
        return DashboardConfManager.getManager().getPackageSession().getPackageById(packageId) != null;
    }

    private FineBusinessTable createBusinessTable(EntryInfo entryInfo) {
        DriverType driverType = entryInfo.getType();
        CommonDataSourceDriver driver = CommonDataSourceDriverFactory.getInstance(FineEngineType.Cube).getDriver(driverType);
        return driver.createBusinessTable(entryInfo);
    }

    public FineBusinessPackage getPackageByPackageId(String packageId) throws FinePackageAbsentException {
        PackageInfo info = DashboardConfManager.getManager().getPackageSession().getPackageById(packageId);
        if (null != info) {
            return createFinePackageWithTableId(info);
        }
        throw new FinePackageAbsentException();
    }

    public List<FineBusinessPackage> getAllPackage() {
        List<PackageInfo> infos = DashboardConfManager.getManager().getPackageSession().getAllPackages();
        List<FineBusinessPackage> packages = new ArrayList<FineBusinessPackage>();
        for (PackageInfo info : infos) {
            try {
                packages.add(createFinePackageWithTableId(info));
            } catch (Exception ignore) {
            }
        }
        return Collections.unmodifiableList(packages);
    }

    protected FineBusinessPackage createFinePackageWithTableId(PackageInfo packageInfo) {
        FineBusinessPackageImp fineBusinessPackageImp = new FineBusinessPackageImp(packageInfo.getId(), packageInfo.getName(), packageInfo.getUserId(), packageInfo.getCreateTime(), FineEngineType.Cube, packageInfo.getVersion());
        Map<String, EntryInfo> all = DashboardConfManager.getManager().getEntryInfoSession().getAll();
        for (Map.Entry<String, EntryInfo> entry : all.entrySet()) {
            EntryInfo entryInfo = entry.getValue();
            if (packageInfo.contains(entryInfo.getID())) {
                fineBusinessPackageImp.addTable(entryInfo.getAlias());
            }
        }
        return fineBusinessPackageImp;
    }

    public List<FineBusinessPackage> getAllPackageByGroupId(String groupId) throws FineEngineException {
        List<PackageInfo> infos = DashboardConfManager.getManager().getPackageSession().getAllPackages();
        List<FineBusinessPackage> fineBusinessPackages = new ArrayList<FineBusinessPackage>();
        try {
            for (PackageInfo packageInfo : infos) {
                Set<String> allGroupNames = packageInfo.getAllGroupNames();
                if (!CollectionUtils.isEmpty(allGroupNames) && allGroupNames.contains(groupId)) {
                    fineBusinessPackages.add(createFinePackageWithTableId(packageInfo));
                }
            }
        } catch (Exception e) {
            throw new FineGroupAbsentException();
        }
        return fineBusinessPackages;
    }

    public void cleanPackage(String packageId) {
        PackageInfo info = DashboardConfManager.getManager().getPackageSession().getPackageById(packageId);
        cleanPackage(info);
    }

    private void cleanPackage(PackageInfo info) {
        synchronized (this) {
            if (null != info) {
                deleteTableInPackage(info.getTableIds());
                DashboardConfManager.getManager().getPackageSession().delPackage(info.getId());
            }
        }
    }

    public void cleanTable(String tableName) {
        EntryInfo info = DashboardConfManager.getManager().getEntryInfoSession().findByName(tableName);
        if (null != info) {
            deleteTableInfo(info.getID());
        }
    }

    public void cleanAll() {
        List<PackageInfo> infoList = DashboardConfManager.getManager().getPackageSession().getAllPackages();
        for (PackageInfo info : infoList) {
            cleanPackage(info);
        }
    }

    protected void deleteTableInfo(String entryInfoId) {
        EntryInfo entryInfo = DashboardConfManager.getManager().getEntryInfoSession().findInterceptEntryInfo(entryInfoId);
        DashboardConfManager.getManager().getEntryInfoSession().delete(entryInfo.getID());
        delEntryInfoInPackage(entryInfoId);
        DashboardConfManager.getManager().getRelationConfigurationSession().delete(entryInfoId);
        DashboardConfManager.getManager().getFieldSession().delete(entryInfoId);
    }

    protected void deleteTableInPackage(List<String> entryInfoIds) {
        List<String> synchronizedList = new ArrayList<String>(entryInfoIds);
        for (String entryInfoId : synchronizedList) {
            synchronized (this) {
                deleteTableInfo(entryInfoId);
            }
        }
    }

    protected void delEntryInfoInPackage(String entryInfoId) {
        try {
            PackageInfo packageInfo = DashboardConfManager.getManager().getPackageSession().getPackageByEntryInfoId(entryInfoId);
            packageInfo.removeTable(entryInfoId);
            DashboardConfManager.getManager().getPackageSession().put(packageInfo);
        } catch (Exception iognore) {

        }
    }
}
