package com.finebi.conf.provider;

import com.finebi.conf.exception.FineEngineException;
import com.finebi.conf.exception.FinePackageAbsentException;
import com.finebi.conf.exception.FineTableAbsentException;
import com.finebi.conf.service.engine.analysis.EngineAnalysisConfManager;
import com.finebi.conf.structure.bean.pack.FineBusinessPackage;
import com.finebi.conf.structure.bean.table.FineBusinessTable;
import com.fr.swift.conf.dashboard.DashboardPackageTableService;
import java.util.List;

/**
 * @author anchore
 * @date 2018/3/19
 */
public class SwiftAnalysisConfManager implements EngineAnalysisConfManager {

    private DashboardPackageTableService service;

    public SwiftAnalysisConfManager() {
        service = DashboardPackageTableService.getService();
    }

    @Override
    public FineBusinessPackage getBusinessPackage(String s) throws FinePackageAbsentException {
        return service.getPackageByPackageId(s);
    }

    @Override
    public FineBusinessTable getBusinessTable(String s) throws FineTableAbsentException {
        return service.getTableByName(s);
    }

    @Override
    public List<FineBusinessTable> getBusinessTables() {
        return service.getAllBusinessTable();
    }

    @Override
    public List<FineBusinessPackage> getBusinessPackages() {
        return service.getAllPackage();
    }

    @Override
    public List<FineBusinessTable> getBusinessTables(String s) throws FinePackageAbsentException {
        return service.getBusinessTables(s);
    }

    @Override
    public List<FineBusinessPackage> getBusinessPackages(String s) throws FineEngineException {
//        return packProvider.getAllPackageByGroupId(s);
        return service.getAllPackageByGroupId(s);
    }

    @Override
    public boolean isTableExist(String s) {
        return service.isTableExists(s);
//        return tableProvider.isTableExist(s);
    }

    @Override
    public boolean isPackageExist(String s) {
//        return packProvider.isPackageExist(s);
        return service.isPackageExists(s);
    }
}