package com.finebi.conf.provider;

import com.finebi.base.constant.FineEngineType;
import com.finebi.common.internalimp.config.driver.CommonDataSourceDriverFactory;
import com.finebi.common.internalimp.config.intercept.InterceptModelImpl;
import com.finebi.common.internalimp.config.manager.DefaultEngineSessionManager;
import com.finebi.common.internalimp.config.session.CommonConfigManager;
import com.finebi.common.service.engine.table.AbstractEngineTableManager;
import com.finebi.common.structure.config.driver.CommonDataSourceDriver;
import com.finebi.common.structure.config.entryinfo.EntryInfo;
import com.finebi.common.structure.config.fieldinfo.FieldInfo;
import com.finebi.common.structure.config.pack.PackageInfo;
import com.finebi.common.structure.config.relation.Relation;
import com.finebi.common.structure.config.session.InterceptSession;
import com.finebi.conf.constant.BICommonConstants;
import com.finebi.conf.exception.FineEngineException;
import com.finebi.conf.exception.FinePackageAbsentException;
import com.finebi.conf.exception.FineTableAbsentException;
import com.finebi.conf.internalimp.analysis.table.FineAnalysisTableImpl;
import com.finebi.conf.internalimp.response.bean.FineTableResponed;
import com.finebi.conf.structure.bean.field.FineBusinessField;
import com.finebi.conf.structure.bean.table.FineBusinessTable;
import com.fr.general.ComparatorUtils;
import com.fr.swift.adaptor.transformer.DataSourceFactory;
import com.fr.swift.adaptor.transformer.FieldFactory;
import com.fr.swift.conf.business.field.FieldInfoHelper;
import com.fr.swift.conf.business.table2source.TableToSource;
import com.fr.swift.conf.business.table2source.dao.TableToSourceConfigDao;
import com.fr.swift.conf.business.table2source.dao.TableToSourceConfigDaoImpl;
import com.fr.swift.conf.business.table2source.unique.TableToSourceUnique;
import com.fr.swift.conf.dashboard.DashboardPackageTableService;
import com.fr.swift.conf.dashboard.store.DashboardConfManager;
import com.fr.swift.conf.updateInfo.TableUpdateInfoConfigService;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.source.DataSource;
import com.fr.swift.util.Crasher;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author yee
 * @date 2018/04/08
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public class SwiftTableManager extends AbstractEngineTableManager {
    private TableToSourceConfigDao tableToSourceConfigDao;
    private TableUpdateInfoConfigService updateInfoConfigService;
    private InterceptSession interceptSession;
    private SwiftLogger logger = SwiftLoggers.getLogger(SwiftTableManager.class);

    public SwiftTableManager() {
        tableToSourceConfigDao = new TableToSourceConfigDaoImpl();
        updateInfoConfigService = TableUpdateInfoConfigService.getService();
//        interceptSession = CommonConfigManager.getInterceptSession(FineEngineType.Cube);
    }

    @Override
    protected FieldInfo createFieldInfo(EntryInfo entryInfo) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<FineBusinessTable> getAllTable() {
        List<FineBusinessTable> fineBusinessTables = new ArrayList<FineBusinessTable>();
        Map<String, EntryInfo> all = DefaultEngineSessionManager.getInstance().getAllEntryInfo(getEngineType());
//        all.putAll(DashboardConfManager.getManager().getEntryInfoSession().getAll());
        for (Map.Entry<String, EntryInfo> entry : all.entrySet()) {
            EntryInfo entryInfo = entry.getValue();
            CommonDataSourceDriver driver = CommonDataSourceDriverFactory.getInstance(getEngineType()).getDriver(entryInfo);
            FineBusinessTable businessTable = driver.createBusinessTable(entryInfo);
            setRealTime(businessTable, entryInfo);
            fineBusinessTables.add(businessTable);
        }
        return fineBusinessTables;
    }

    @Override
    public FineBusinessTable getSingleTable(String tableName) throws FineTableAbsentException {
        try {
            return super.getSingleTable(tableName);
        } catch (FineTableAbsentException e) {
            return DashboardPackageTableService.getService().getTableByName(tableName);
        }
    }

    @Override
    public List<FineBusinessTable> getAllTableByPackId(String packId) throws FinePackageAbsentException {
        try {
            List<FineBusinessTable> fineBusinessTables = new ArrayList<FineBusinessTable>();
            PackageInfo packageInfo = CommonConfigManager.getPackageSession(getEngineType()).getPackageById(packId);
            checkPackageExist(packageInfo);
            List<String> tableIds = packageInfo.getTableIds();
            for (String tableId : tableIds) {
                EntryInfo entryInfo = CommonConfigManager.getEntryInfoSession(getEngineType()).find(tableId);
                CommonDataSourceDriver driver = CommonDataSourceDriverFactory.getInstance(getEngineType()).getDriver(entryInfo);
                FineBusinessTable businessTable = driver.createBusinessTable(entryInfo);
                setRealTime(businessTable, entryInfo);
                fineBusinessTables.add(businessTable);
            }
            return fineBusinessTables;
        } catch (FinePackageAbsentException e) {
            return DashboardPackageTableService.getService().getBusinessTables(packId);
        }
    }

    @Override
    public boolean isTableExist(String tableName) {
        if (!super.isTableExist(tableName)) {
            return DashboardPackageTableService.getService().isTableExists(tableName);
        }
        return true;
    }

    @Override
    public List<FineBusinessTable> getBelongAnalysisTables(String tableName) {
        List<FineBusinessTable> result = new ArrayList<FineBusinessTable>();
        List<FineBusinessTable> allTable = getAllTable();
        for (FineBusinessTable table : allTable) {
            if (ComparatorUtils.equals(BICommonConstants.TABLE.ANALYSIS, table.getType())) {
                FineBusinessTable baseTable = ((FineAnalysisTableImpl) table).getBaseTable();
                if (ComparatorUtils.equals(baseTable.getName(), tableName)) {
                    result.add(table);
                }
            }
        }
        return result;
    }

    @Override
    public FineEngineType getEngineType() {
        return FineEngineType.Cube;
    }

    @Override
    protected void saveEntryInfo(Map.Entry<String, List<FineBusinessTable>> entry, FineTableResponed responed) {
        Iterator iterator = ((List) entry.getValue()).iterator();

        try {
            while (iterator.hasNext()) {
                FineBusinessTable table = (FineBusinessTable) iterator.next();
                DataSource dataSource = DataSourceFactory.getDataSourceInCache(table);
                tableToSourceConfigDao.addConfig(table.getId(), dataSource.getSourceKey().getId());
                EntryInfo entryInfo = this.createEntryInfo(table);
                saveRealTimeStatus(table, entryInfo);
                this.addEntryInfo(entryInfo, entry.getKey());
                table.setFields(FieldFactory.transformColumns2Fields(dataSource.getMetadata(), table.getId(), entryInfo.getEscapeMap()));
                this.saveFieldInfo(FieldInfoHelper.createFieldInfo(entryInfo, table));
                List<Relation> relationList = this.developDatabaseRelations(entryInfo, entry.getKey());
                this.updateFineTableResponed(responed, entryInfo, relationList);
            }
        } catch (Exception e) {
            logger.error("add table error: ", e);
            Crasher.crash(e);
        }
    }

    @Override
    public boolean updateTables(List<FineBusinessTable> needUpdateTables) throws FineEngineException {
        try {
            Iterator iterator = needUpdateTables.iterator();

            while (iterator.hasNext()) {
                FineBusinessTable table = (FineBusinessTable) iterator.next();
                DataSource dataSource = DataSourceFactory.getDataSourceInCache(table);
                TableToSource tableToSource = new TableToSourceUnique(table.getId(), dataSource.getSourceKey().getId());
                tableToSourceConfigDao.updateConfig(tableToSource);
                EntryInfo entryInfo = this.createEntryInfo(table);
                saveRealTimeStatus(table, entryInfo);
                this.updateEntryInfo(entryInfo);
                table.setFields(FieldFactory.transformColumns2Fields(dataSource.getMetadata(), table.getId(), entryInfo.getEscapeMap()));
                this.saveFieldInfo(FieldInfoHelper.createFieldInfo(entryInfo, table));
            }

            return true;
        } catch (Exception var5) {
            throw new FineTableAbsentException();
        }
    }

    public boolean removeTable(List<String> tableNames) {
        boolean success = false;
        try {
            for (String tableName : tableNames) {
                EntryInfo entryInfo = this.getEntryInfoByName(tableName);
                this.deleteEntryInfo(entryInfo);
            }
            success = true;
        } catch (Exception e) {
            success = false;
        }
        if (success) {
            return updateInfoConfigService.removeUpdateInfo(tableNames);
        }
        return false;
    }

    @Override
    public boolean updateField(String tableName, FineBusinessField field) throws FineTableAbsentException {
        return super.updateField(tableName, field);
    }

    private void saveRealTimeStatus(FineBusinessTable table, EntryInfo entryInfo) {
        boolean isIntercept = isIntercept(table);
        if (getInterceptSession().hasModel(entryInfo.getID()) != isIntercept) {
            if (isIntercept) {
                getInterceptSession().put(new InterceptModelImpl(entryInfo.getID()));
            } else {
                getInterceptSession().delete(new InterceptModelImpl(entryInfo.getID()));
            }
        }
    }

    private void setRealTime(FineBusinessTable table, EntryInfo entryInfo) {
        table.setRealTimeData(!getInterceptSession().hasModel(entryInfo.getID()));
    }

    private boolean isIntercept(FineBusinessTable table) {
        return !table.isRealTimeData();
    }

    protected FineBusinessTable createFineBusinessTable(EntryInfo entryInfo) {
        CommonDataSourceDriver driver = CommonDataSourceDriverFactory.getInstance(getEngineType()).getDriver(entryInfo);
        FineBusinessTable businessTable = driver.createBusinessTable(entryInfo);
        setRealTime(businessTable, entryInfo);
        return businessTable;
    }

    public InterceptSession getInterceptSession() {
        if (null == interceptSession) {
            interceptSession = CommonConfigManager.getInterceptSession(FineEngineType.Cube);
        }
        return interceptSession;
    }
}
