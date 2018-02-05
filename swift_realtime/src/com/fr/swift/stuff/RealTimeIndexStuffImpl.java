package com.fr.swift.stuff;

import java.util.List;

/**
 * This class created on 2017-11-27.
 *
 * @author Lucifer
 * @describe 增量更新stuff
 * @since Advanced FineBI Analysis 1.0
 */
public class RealTimeIndexStuffImpl implements RealTimeIndexingStuff {

    private List<String> updateTableSources;
    private List<String> updateTableSourceRelations;
    private List<String> updateTableSourceRelationPaths;

    public RealTimeIndexStuffImpl(List<String> updateTableSources, List<String> updateTableSourceRelations, List<String> updateTableSourceRelationPaths) {
        this.updateTableSources = updateTableSources;
        this.updateTableSourceRelations = updateTableSourceRelations;
        this.updateTableSourceRelationPaths = updateTableSourceRelationPaths;
    }

    @Override
    public List<String> getUpdateTableSources() {
        return updateTableSources;
    }

    @Override
    public List<String> getUpdateTableSourceRelations() {
        return updateTableSourceRelations;
    }

    @Override
    public List<String> getUpdateTableSourceRelationPaths() {
        return updateTableSourceRelationPaths;
    }

    @Override
    public String getUpdateType() {
        return null;
    }
}