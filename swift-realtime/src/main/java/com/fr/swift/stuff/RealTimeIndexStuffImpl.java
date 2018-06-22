package com.fr.swift.stuff;

import java.io.Serializable;
import java.util.List;

/**
 * This class created on 2017-11-27.
 *
 * @author Lucifer
 * @describe 增量更新stuff
 * @since Advanced FineBI Analysis 1.0
 */
public class RealTimeIndexStuffImpl implements RealTimeIndexingStuff, Serializable {

    private static final long serialVersionUID = -4101014331273135113L;
    private List<String> updateTableSources;
    private List<String> updateTableSourceRelations;
    private List<String> updateTableSourceRelationPaths;

    public RealTimeIndexStuffImpl(List<String> updateTableSources, List<String> updateTableSourceRelations, List<String> updateTableSourceRelationPaths) {
        this.updateTableSources = updateTableSources;
        this.updateTableSourceRelations = updateTableSourceRelations;
        this.updateTableSourceRelationPaths = updateTableSourceRelationPaths;
    }

    @Override
    public List<String> getTables() {
        return updateTableSources;
    }

    @Override
    public List<String> getRelations() {
        return updateTableSourceRelations;
    }

    @Override
    public List<String> getRelationPaths() {
        return updateTableSourceRelationPaths;
    }

    @Override
    public String getUpdateType() {
        return null;
    }
}