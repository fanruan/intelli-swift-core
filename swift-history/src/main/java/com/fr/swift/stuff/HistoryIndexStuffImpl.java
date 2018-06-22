package com.fr.swift.stuff;

import java.io.Serializable;
import java.util.List;

/**
 * This class created on 2017-11-27.
 *
 * @author Lucifer
 * @describe 全量更新stuff
 * @since Advanced FineBI Analysis 1.0
 */
public class HistoryIndexStuffImpl implements HistoryIndexingStuff, Serializable {
    private List<String> tables;

    private List<String> relations;

    private List<String> relationPaths;

    public HistoryIndexStuffImpl(List<String> tables, List<String> relations, List<String> relationPaths) {
        this.tables = tables;
        this.relations = relations;
        this.relationPaths = relationPaths;
    }

    @Override
    public List<String> getTables() {
        return tables;
    }

    @Override
    public List<String> getRelations() {
        return relations;
    }

    @Override
    public List<String> getRelationPaths() {
        return relationPaths;
    }

    @Override
    public String getUpdateType() {
        return null;
    }

    private static final long serialVersionUID = 6020432061987147639L;
}
