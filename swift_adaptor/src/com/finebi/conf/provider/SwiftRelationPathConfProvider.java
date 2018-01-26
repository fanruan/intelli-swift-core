package com.finebi.conf.provider;

import com.finebi.base.constant.FineEngineType;
import com.finebi.conf.service.engine.relation.EngineRelationPathManager;
import com.finebi.conf.structure.path.FineBusinessTableRelationPath;
import com.finebi.conf.structure.relation.FineBusinessTableRelation;

import java.util.List;

/**
 * This class created on 2018-1-23 13:59:38
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public class SwiftRelationPathConfProvider implements EngineRelationPathManager {

    @Override
    public List<FineBusinessTableRelation> getAllRelations() {
        return null;
    }

    @Override
    public List<FineBusinessTableRelation> getRelationsByTables(String primaryTableId, String foreignTableId) {
        return null;
    }

    @Override
    public List<FineBusinessTableRelation> getRelationsByTableId(String tableId) {
        return null;
    }

    @Override
    public boolean addRelations(List<FineBusinessTableRelation> relations) {
        return false;
    }

    @Override
    public boolean updateRelations(List<FineBusinessTableRelation> relations) {
        return false;
    }

    @Override
    public boolean removeRelations(List<FineBusinessTableRelation> relations) {
        return false;
    }

    @Override
    public List<FineBusinessTableRelationPath> getAllRelationPaths() {
        return null;
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
        return false;
    }

    @Override
    public boolean isRelationExist(String primaryTableId, String foreignTableId) {
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
