package com.finebi.conf.provider;

import com.finebi.conf.service.engine.analysis.EngineAnalysisConfManager;
import com.finebi.conf.structure.bean.pack.FineBusinessPackage;
import com.finebi.conf.structure.bean.table.FineBusinessTable;
import com.fr.third.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author anchore
 * @date 2018/3/19
 */
public class SwiftAnalysisConfManager implements EngineAnalysisConfManager {
    @Autowired
    private SwiftPackageConfProvider packProvider;

    @Autowired
    private SwiftTableManager tableProvider;

    @Override
    public FineBusinessPackage getBusinessPackage(String s) {
        return packProvider.getSinglePackage(s);
    }

    @Override
    public FineBusinessTable getBusinessTable(String s) {
        return tableProvider.getSingleTable(s);
    }

    @Override
    public List<FineBusinessTable> getBusinessTables() {
        return tableProvider.getAllTable();
    }

    @Override
    public List<FineBusinessPackage> getBusinessPackages() {
        return packProvider.getAllPackage();
    }

    @Override
    public List<FineBusinessTable> getBusinessTables(String s) {
        return tableProvider.getAllTableByPackId(s);
    }

    @Override
    public List<FineBusinessPackage> getBusinessPackages(String s) {
        return packProvider.getAllPackageByGroupId(s);
    }

    @Override
    public boolean isTableExist(String s) {
        return tableProvider.isTableExist(s);
    }

    @Override
    public boolean isPackageExist(String s) {
        return packProvider.isPackageExist(s);
    }
}