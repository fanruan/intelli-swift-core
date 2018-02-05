package com.fr.swift.provider;

import com.fr.swift.increment.Increment;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.IRelationSource;
import com.fr.swift.source.SourcePath;
import com.fr.swift.source.container.SourceContainer;
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
    private SourceContainer sourceContainer;
    private Map<String, List<Increment>> incrementMap;

    public IndexStuffInfoProvider(IndexingStuff indexingStuff, SourceContainer sourceContainer) {
        this.indexingStuff = indexingStuff;
        this.sourceContainer = sourceContainer;
        this.incrementMap = new HashMap<String, List<Increment>>();
    }

    public IndexStuffInfoProvider(IndexingStuff indexingStuff, SourceContainer sourceContainer, Map<String, List<Increment>> incrementMap) {
        this.indexingStuff = indexingStuff;
        this.sourceContainer = sourceContainer;
        this.incrementMap = incrementMap;
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
    public IRelationSource getRelationById(String sourceId) {
        return sourceContainer.getRelationSourceContainer().getSourceByKey(sourceId);
    }

    @Override
    public List<IRelationSource> getRelationsByIds(List<String> sourceIds) {
        return new ArrayList<IRelationSource>(sourceContainer.getRelationSourceContainer().getSourcesByKeys(sourceIds));
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
    public List<IRelationSource> getAllRelations() {
        return new ArrayList<IRelationSource>(sourceContainer.getRelationSourceContainer().getAllSources());
    }

    @Override
    public List<SourcePath> getAllPaths() {
        return new ArrayList<SourcePath>(sourceContainer.getPathSourceContainer().getAllSources());
    }

    @Override
    public List<Increment> getIncrementBySourceId(String sourceId) {
        return new ArrayList<Increment>(incrementMap.get(sourceId));
    }
}
