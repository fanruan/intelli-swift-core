package com.finebi.conf.provider;

import com.finebi.base.constant.FineEngineType;
import com.finebi.conf.service.engine.analysis.EngineAnalysisRelationPathManager;
import com.finebi.conf.structure.bean.table.FineBusinessTable;
import com.finebi.conf.structure.path.FineBusinessTableRelationPath;
import com.finebi.conf.structure.relation.FineBusinessTableRelation;
import com.fr.swift.conf.dashboard.DashboardRelationPathService;
import com.fr.third.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yee
 * @date 2018/4/16
 */
public class SwiftAnalysisRelationPathManager implements EngineAnalysisRelationPathManager {

    @Autowired
    private SwiftRelationPathConfProvider relationPathConfProvider;
    @Autowired
    private SwiftTableManager tableProvider;

    private DashboardRelationPathService service;

    public SwiftAnalysisRelationPathManager() {
        service = DashboardRelationPathService.getService();
    }

    @Override
    public List<FineBusinessTableRelation> getAllAnalysisRelations() {
        return service.getAllAnalysisRelations();
    }

    @Override
    public List<FineBusinessTableRelation> getAnalysisRelationsByTables(String s, String s1) {
        return service.getAnalysisRelationsByTables(s, s1);
    }

    @Override
    public List<FineBusinessTableRelation> getAnalysisRelationsByTable(String s) {
        return service.getAnalysisRelationsByTable(s);
    }

    @Override
    public boolean isAnalysisRelationExist(String s, String s1) {
        return service.isAnalysisRelationExist(s, s1);
    }

    @Override
    public boolean isAnalysisRelationExist(String s) {
        return service.isAnalysisRelationExist(s);
    }

    @Override
    public List<FineBusinessTableRelationPath> getAllAnalysisRelationPaths() {
        return service.getAllAnalysisRelationPaths();
    }

    @Override
    public List<FineBusinessTableRelationPath> getRelationPathsByTables(String s, String s1) {
        return service.getRelationPathsByTables(s, s1);
    }

    @Override
    public List<FineBusinessTableRelationPath> getRelationPaths(String s) {
        return service.getRelationPaths(s);
    }

    @Override
    public boolean isRelationPathExist(String s, String s1) {
        return service.isRelationPathExist(s, s1);
    }

    @Override
    public boolean isRelationPathExist(String s) {
        return service.isRelationPathExist(s);
    }

    @Override
    public FineEngineType getEngineType() {
        return FineEngineType.Cube;
    }
}