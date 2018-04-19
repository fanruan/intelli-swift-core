package com.fr.swift.provider;

import com.fr.swift.increment.Increment;
import com.fr.swift.reliance.RelationPathReliance;
import com.fr.swift.reliance.RelationReliance;
import com.fr.swift.reliance.SourceReliance;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.RelationSource;
import com.fr.swift.source.SourcePath;
import com.fr.swift.source.container.SourceContainerManager;
import com.fr.swift.source.manager.IndexStuffProvider;
import com.fr.swift.stuff.IndexingStuff;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class created on 2017-11-28.
 *
 * @author Lucifer
 * @since Advanced FineBI Analysis 1.0
 */
public class IndexStuffInfoProvider implements IndexStuffProvider {

    private IndexingStuff indexingStuff;
    private SourceContainerManager sourceContainer;
    private Map<String, List<Increment>> incrementMap;
    private SourceReliance sourceReliance;
    private RelationReliance relationReliance;
    private RelationPathReliance relationPathReliance;

    public IndexStuffInfoProvider(IndexingStuff indexingStuff, SourceContainerManager sourceContainer) {
        this.indexingStuff = indexingStuff;
        this.sourceContainer = sourceContainer;
        this.incrementMap = new HashMap<String, List<Increment>>();
    }

    public IndexStuffInfoProvider(IndexingStuff indexingStuff, SourceContainerManager sourceContainer,
                                  Map<String, List<Increment>> incrementMap, SourceReliance sourceReliance, RelationReliance relationReliance, RelationPathReliance relationPathReliance) {
        this.indexingStuff = indexingStuff;
        this.sourceContainer = sourceContainer;
        this.incrementMap = incrementMap;
        this.sourceReliance = sourceReliance;
        this.relationReliance = relationReliance;
        this.relationPathReliance = relationPathReliance;
    }

    @Override
    public DataSource getTableById(String sourceId) {
        return sourceContainer.getDataSourceContainer().getSourceByKey(sourceId);
    }

    @Override
    public List<DataSource> getTablesByIds(List<String> sourceIds) {
        return new ArrayList<DataSource>(sourceContainer.getDataSourceContainer().getSourcesByKeys(sourceIds));
    }

    @Override
    public RelationSource getRelationById(String sourceId) {
        return sourceContainer.getRelationSourceContainer().getSourceByKey(sourceId);
    }

    @Override
    public List<RelationSource> getRelationsByIds(List<String> sourceIds) {
        return new ArrayList<RelationSource>(sourceContainer.getRelationSourceContainer().getSourcesByKeys(sourceIds));
    }

    @Override
    public SourcePath getPathById(String sourceId) {
        return sourceContainer.getPathSourceContainer().getSourceByKey(sourceId);
    }

    @Override
    public List<SourcePath> getPathsByIds(List<String> sourceIds) {
        return new ArrayList<SourcePath>(sourceContainer.getPathSourceContainer().getSourcesByKeys(sourceIds));
    }

    @Override
    public List<DataSource> getAllTables() {
        return new ArrayList<DataSource>(sourceContainer.getDataSourceContainer().getAllSources());
    }

    @Override
    public List<RelationSource> getAllRelations() {
        return new ArrayList<RelationSource>(sourceContainer.getRelationSourceContainer().getAllSources());
    }

    @Override
    public List<SourcePath> getAllPaths() {
        return new ArrayList<SourcePath>(sourceContainer.getPathSourceContainer().getAllSources());
    }

    @Override
    public List<Increment> getIncrementBySourceId(String sourceId) {
        return new ArrayList<Increment>(incrementMap.get(sourceId));
    }

    @Override
    public SourceReliance getSourceReliance() {
        return sourceReliance;
    }

    @Override
    public RelationReliance getRelationReliance() {
        return relationReliance;
    }

    @Override
    public RelationPathReliance getRelationPathReliance() {
        return relationPathReliance;
    }
}
