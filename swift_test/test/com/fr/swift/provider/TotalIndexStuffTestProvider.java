package com.fr.swift.provider;

import com.fr.swift.creater.StuffSourceCreater;
import com.fr.swift.increment.Increment;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.RelationSource;
import com.fr.swift.source.SourcePath;
import com.fr.swift.source.manager.IndexStuffProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class created on 2017-11-29.
 *
 * @author Lucifer
 * @since Advanced FineBI Analysis 1.0
 */
public class TotalIndexStuffTestProvider implements IndexStuffProvider {

    private Map<String, DataSource> tableMap = new HashMap<String, DataSource>();

    private Map<String, RelationSource> relationMap = new HashMap<String, RelationSource>();

    private Map<String, SourcePath> pathMap = new HashMap<String, SourcePath>();

    public TotalIndexStuffTestProvider() {
        tableMap.put(StuffSourceCreater.createTableA().getSourceKey().getId(), StuffSourceCreater.createTableA());
        tableMap.put(StuffSourceCreater.createTableB().getSourceKey().getId(), StuffSourceCreater.createTableB());
        tableMap.put(StuffSourceCreater.createTableC().getSourceKey().getId(), StuffSourceCreater.createTableC());
        tableMap.put(StuffSourceCreater.createTableD().getSourceKey().getId(), StuffSourceCreater.createTableD());
        tableMap.put(StuffSourceCreater.createTableE().getSourceKey().getId(), StuffSourceCreater.createTableE());

        relationMap.put(StuffSourceCreater.createRelationAB().getSourceKey().getId(), StuffSourceCreater.createRelationAB());
        relationMap.put(StuffSourceCreater.createRelationBC().getSourceKey().getId(), StuffSourceCreater.createRelationBC());
        relationMap.put(StuffSourceCreater.createRelationCD().getSourceKey().getId(), StuffSourceCreater.createRelationCD());
        relationMap.put(StuffSourceCreater.createRelationDE().getSourceKey().getId(), StuffSourceCreater.createRelationDE());

        pathMap.put(StuffSourceCreater.createPathABC().getSourceKey().getId(), StuffSourceCreater.createPathABC());
        pathMap.put(StuffSourceCreater.createPathBCD().getSourceKey().getId(), StuffSourceCreater.createPathBCD());
        pathMap.put(StuffSourceCreater.createPathCDE().getSourceKey().getId(), StuffSourceCreater.createPathCDE());
        pathMap.put(StuffSourceCreater.createPathABCD().getSourceKey().getId(), StuffSourceCreater.createPathABCD());
        pathMap.put(StuffSourceCreater.createPathBCDE().getSourceKey().getId(), StuffSourceCreater.createPathBCDE());
        pathMap.put(StuffSourceCreater.createPathABCDE().getSourceKey().getId(), StuffSourceCreater.createPathABCDE());
    }

    @Override
    public DataSource getTableById(String sourceId) {
        return tableMap.get(sourceId);
    }

    @Override
    public List<DataSource> getTablesByIds(List<String> sourceIds) {
        List<DataSource> list = new ArrayList<DataSource>();
        for (String sourceId : sourceIds) {
            list.add(tableMap.get(sourceId));
        }
        return list;
    }

    @Override
    public RelationSource getRelationById(String sourceId) {
        return relationMap.get(sourceId);
    }

    @Override
    public List<RelationSource> getRelationsByIds(List<String> sourceIds) {
        List<RelationSource> list = new ArrayList<RelationSource>();
        for (String sourceId : sourceIds) {
            list.add(relationMap.get(sourceId));
        }
        return list;
    }

    @Override
    public SourcePath getPathById(String sourceId) {
        return pathMap.get(sourceId);
    }

    @Override
    public List<SourcePath> getPathsByIds(List<String> sourceIds) {
        List<SourcePath> list = new ArrayList<SourcePath>();
        for (String sourceId : sourceIds) {
            list.add(pathMap.get(sourceId));
        }
        return list;
    }

    @Override
    public List<DataSource> getAllTables() {
        return new ArrayList<DataSource>(tableMap.values());
    }

    @Override
    public List<RelationSource> getAllRelations() {
        return new ArrayList<RelationSource>(relationMap.values());
    }

    @Override
    public List<SourcePath> getAllPaths() {
        return new ArrayList<SourcePath>(pathMap.values());
    }

    @Override
    public List<Increment> getIncrementBySourceId(String sourceId) {
        return null;
    }
}
