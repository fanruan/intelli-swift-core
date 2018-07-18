package com.fr.swift.provider;

import com.fr.swift.reliance.RelationPathReliance;
import com.fr.swift.reliance.RelationReliance;
import com.fr.swift.reliance.SourceReliance;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.RelationSource;
import com.fr.swift.source.SourcePath;
import com.fr.swift.source.manager.IndexStuffProvider;

import java.util.List;

/**
 * This class created on 2017-11-27.
 *
 * @author Lucifer
 * @since Advanced FineBI Analysis 1.0
 */

public class IndexStuffManager implements IndexStuffProvider {
    private IndexStuffProvider provider;

    public IndexStuffManager(IndexStuffProvider provider) {
        this.provider = provider;
    }

    @Override
    public DataSource getTableById(String sourceId) {
        return provider.getTableById(sourceId);
    }

    @Override
    public List<DataSource> getTablesByIds(List<String> sourceIds) {
        return provider.getTablesByIds(sourceIds);
    }

    @Override
    public RelationSource getRelationById(String sourceId) {
        return provider.getRelationById(sourceId);
    }

    @Override
    public List<RelationSource> getRelationsByIds(List<String> sourceIds) {
        return provider.getRelationsByIds(sourceIds);
    }

    @Override
    public SourcePath getPathById(String sourceId) {
        return provider.getPathById(sourceId);
    }

    @Override
    public List<SourcePath> getPathsByIds(List<String> sourceIds) {
        return provider.getPathsByIds(sourceIds);
    }

    @Override
    public List<DataSource> getAllTables() {
        return provider.getAllTables();
    }

    @Override
    public List<RelationSource> getAllRelations() {
        return provider.getAllRelations();
    }

    @Override
    public List<SourcePath> getAllPaths() {
        return provider.getAllPaths();
    }

    @Override
    public SourceReliance getSourceReliance() {
        return provider.getSourceReliance();
    }

    @Override
    public RelationReliance getRelationReliance() {
        return provider.getRelationReliance();
    }

    @Override
    public RelationPathReliance getRelationPathReliance() {
        return provider.getRelationPathReliance();
    }

    @Override
    public IndexStuffMedium getIndexStuffMedium() {
        return provider.getIndexStuffMedium();
    }

    @Override
    public List<TaskResultListener> taskResultListeners() {
        return provider.taskResultListeners();
    }

    @Override
    public void addResultListener(TaskResultListener taskResultListener) {
        provider.addResultListener(taskResultListener);
    }
}
