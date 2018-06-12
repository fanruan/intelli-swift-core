package com.fr.swift.stuff;

import java.util.List;

/**
 * This class created on 2017-11-27.
 *
 * @author Lucifer
 * @describe 全量更新stuff
 * @since Advanced FineBI Analysis 1.0
 */
public class HistoryIndexStuffImpl implements HistoryIndexingStuff {

    private static final long serialVersionUID = 6020432061987147639L;
    private List<String> updateTableSources;
    private List<String> updateTableSourceRelations;
    private List<String> updateTableSourceRelationPaths;

    public HistoryIndexStuffImpl(List<String> updateTableSources, List<String> updateTableSourceRelations, List<String> updateTableSourceRelationPaths) {
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
