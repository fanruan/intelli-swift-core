package com.finebi.conf.provider;

import com.finebi.conf.exception.FineEngineException;
import com.finebi.conf.exception.FinePackageAbsentException;
import com.finebi.conf.exception.FineTableAbsentException;
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
    public FineBusinessPackage getBusinessPackage(String s) throws FinePackageAbsentException {
        return packProvider.getSinglePackage(s);
    }

    @Override
    public FineBusinessTable getBusinessTable(String s) throws FineTableAbsentException {
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
    public List<FineBusinessTable> getBusinessTables(String s) throws FinePackageAbsentException {
        return tableProvider.getAllTableByPackId(s);
    }

    @Override
    public List<FineBusinessPackage> getBusinessPackages(String s) throws FineEngineException {
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