package com.finebi.conf.provider;

import com.finebi.base.constant.FineEngineType;
import com.finebi.conf.service.engine.relation.EngineRelationPathManager;
import com.finebi.conf.structure.bean.table.FineBusinessTable;
import com.finebi.conf.structure.path.FineBusinessTableRelationPath;
import com.finebi.conf.structure.relation.FineBusinessTableRelation;
import com.fr.general.ComparatorUtils;
import com.fr.swift.conf.business.path.SwiftRelationPathDao;
import com.fr.swift.conf.business.relation.SwiftRelationDao;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.manager.ProviderManager;

import java.util.ArrayList;
import java.util.List;

/**
 * This class created on 2018-1-23 13:59:38
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public class SwiftRelationPathConfProvider implements EngineRelationPathManager {
    
    // FIXME SwiftRelationPathDAO没有实现
    private SwiftRelationDao businessRelationDAO;
    private SwiftRelationPathDao businessPathDAO;
    
    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(ProviderManager.class);
    
    private String xmlFileName = "relation.xml";

    @Override
    public List<FineBusinessTableRelation> getAllRelations() {
        List<FineBusinessTableRelation> allConfig = new ArrayList<FineBusinessTableRelation>();
        allConfig.addAll(businessRelationDAO.getAllConfig());
        return allConfig;
    }

    @Override
    public List<FineBusinessTableRelation> getRelationsByTables(String primaryTableName, String foreignTableName) {
        List<FineBusinessTableRelation> resultRelations = new ArrayList<FineBusinessTableRelation>();
        List<FineBusinessTableRelation> allRelations = getAllRelations();
        for (FineBusinessTableRelation relation : allRelations) {
            String primaryTable = relation.getPrimaryBusinessTable().getName();
            String foreignTable = relation.getForeignBusinessTable().getName();
            if (ComparatorUtils.equals(primaryTableName, primaryTable) && ComparatorUtils.equals(foreignTableName, foreignTable)) {
                resultRelations.add(relation);
            }
        }
        return resultRelations;
    }

    @Override
    public List<FineBusinessTableRelation> getRelationsByTableId(String tableName) {
        List<FineBusinessTableRelation> resultRelations = new ArrayList<FineBusinessTableRelation>();
        List<FineBusinessTableRelation> allRelations = getAllRelations();
        for (FineBusinessTableRelation relation : allRelations) {
            String primaryName = relation.getPrimaryBusinessTable().getName();
            String foreignName = relation.getForeignBusinessTable().getName();
            if (ComparatorUtils.equals(primaryName, tableName) || ComparatorUtils.equals(foreignName, tableName)) {
                resultRelations.add(relation);
            }
        }
        return resultRelations;
    }

    @Override
    public boolean addRelations(List<FineBusinessTableRelation> relations) {
        return businessRelationDAO.saveConfigs(relations);
    }

    @Override
    public boolean updateRelations(List<FineBusinessTableRelation> relations) {
        
        return false;
    }

    @Override
    public boolean removeRelations(List<FineBusinessTableRelation> relations) {
        return businessRelationDAO.removeConfigs(relations);
    }

    @Override
    public List<FineBusinessTableRelationPath> getAllRelationPaths() {
        
        return new ArrayList<FineBusinessTableRelationPath>();
    }

    @Override
    public List<FineBusinessTableRelationPath> getRelationPaths(String fromTable, String toTable) {
        return null;
    }

    @Override
    public FineBusinessTableRelationPath getPath(String pathName) {
        return null;
    }

    @Override
    public boolean addRelationPaths(List<FineBusinessTableRelationPath> newPaths) {
        return false;
    }

    @Override
    public boolean removeRelationPath(List<FineBusinessTableRelationPath> paths) {
        return false;
    }

    @Override
    public boolean updateRelationPath(List<FineBusinessTableRelationPath> paths) {
        return false;
    }

    @Override
    public boolean isRelationExist(List<FineBusinessTableRelation> relation) {
        List<FineBusinessTableRelation> allRelations = getAllRelations();
        return allRelations.containsAll(relation);
    }

    @Override
    public boolean isRelationExist(String primaryTableName, String foreignTableName) {
        List<FineBusinessTableRelation> allRelations = getAllRelations();
        for (FineBusinessTableRelation relation : allRelations) {
            String primaryName = relation.getPrimaryBusinessTable().getName();
            String foreignName = relation.getForeignBusinessTable().getName();
            if (ComparatorUtils.equals(primaryName, primaryTableName) && ComparatorUtils.equals(foreignName, foreignTableName)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isRelationPathExist(String pathName) {
        return false;
    }

    @Override
    public boolean isRelationPathExist(String fromTableId, String toTableId) {
        return false;
    }

    @Override
    public FineEngineType getEngineType() {
        return FineEngineType.Cube;
    }
}
