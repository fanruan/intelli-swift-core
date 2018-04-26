package com.finebi.conf.provider;

import com.finebi.base.constant.FineEngineType;
import com.finebi.conf.service.engine.analysis.EngineAnalysisRelationPathManager;
import com.finebi.conf.structure.bean.table.FineBusinessTable;
import com.finebi.conf.structure.path.FineBusinessTableRelationPath;
import com.finebi.conf.structure.relation.FineBusinessTableRelation;
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

    @Override
    public List<FineBusinessTableRelation> getAllAnalysisRelations() {
        return relationPathConfProvider.getAllRelations();
    }

    @Override
    public List<FineBusinessTableRelation> getAnalysisRelationsByTables(String s, String s1) {
        return relationPathConfProvider.getRelationsByTables(s, s1);
    }

    @Override
    public List<FineBusinessTableRelation> getAnalysisRelationsByTable(String s) {
        return relationPathConfProvider.getRelationsByTableId(s);
    }

    @Override
    public boolean isAnalysisRelationExist(String s, String s1) {
        return relationPathConfProvider.isRelationExist(s, s1);
    }

    @Override
    public boolean isAnalysisRelationExist(String s) {
        return null != relationPathConfProvider.getRelationsByTableId(s);
    }

    @Override
    public List<FineBusinessTableRelationPath> getAllAnalysisRelationPaths() {
        return relationPathConfProvider.getAllRelationPaths();
    }

    @Override
    public List<FineBusinessTableRelationPath> getRelationPathsByTables(String s, String s1) {
        return relationPathConfProvider.getRelationPaths(s, s1);
    }

    @Override
    public List<FineBusinessTableRelationPath> getRelationPaths(String s) {
        List<FineBusinessTableRelationPath> paths = new ArrayList<FineBusinessTableRelationPath>();
        List<FineBusinessTable> tables = tableProvider.getAllTable();
        for (FineBusinessTable table : tables) {
            paths.addAll(relationPathConfProvider.getRelationPaths(s, table.getName()));
        }
        return paths;
    }

    @Override
    public boolean isRelationPathExist(String s, String s1) {
        return relationPathConfProvider.isRelationPathExist(s, s1);
    }

    @Override
    public boolean isRelationPathExist(String s) {
        return !getRelationPaths(s).isEmpty();
    }

    @Override
    public FineEngineType getEngineType() {
        return FineEngineType.Cube;
    }
}