package com.fr.swift.reliance;

import com.fr.swift.source.DataSource;
import com.fr.swift.source.RelationSource;
import com.fr.swift.source.SourceKey;
//import com.fr.swift.source.etl.EtlSource;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yee
 * @date 2018/4/18
 */
public abstract class AbstractRelationReliance<T extends IRelationNode, R extends RelationSource> {

    protected Map<SourceKey, T> headNode;

    protected Map<SourceKey, R> allRelationSource;
    protected Map<SourceKey, DataSource> allDataSourceList;

    public AbstractRelationReliance(List<R> allRelationSource, SourceReliance sourceReliance) {
        handleSingleRelationSource(allRelationSource);
        handleDataSource(sourceReliance);
        this.headNode = new LinkedHashMap<SourceKey, T>();
    }

    public AbstractRelationReliance(List<R> allRelationSource, List<DataSource> sourceReliance) {
        handleSingleRelationSource(allRelationSource);
        allDataSourceList = new HashMap<SourceKey, DataSource>();
        for (DataSource dataSource :
                sourceReliance) {
            allDataSourceList.put(dataSource.getSourceKey(), dataSource);
        }
        this.headNode = new LinkedHashMap<SourceKey, T>();
    }

    public AbstractRelationReliance(List<R> allRelationSource) {
        handleSingleRelationSource(allRelationSource);
        this.headNode = new LinkedHashMap<SourceKey, T>();
    }

    private void handleDataSource(SourceReliance reliance) {
        allDataSourceList = new HashMap<SourceKey, DataSource>();
        Iterator<Map.Entry<SourceKey, SourceNode>> iterator = reliance.getHeadNodes().entrySet().iterator();
        while (iterator.hasNext()) {
            SourceNode node = iterator.next().getValue();
            initDataSource(node);
        }
    }

    private void initDataSource(SourceNode node) {
        DataSource source = node.getNode();
        initDataSource(source);
        List<SourceNode> nodes = node.next();
        for (SourceNode child : nodes) {
            initDataSource(child);
        }
    }

    private void initDataSource(DataSource source) {
        allDataSourceList.put(source.getSourceKey(), source);
//        if (source instanceof EtlSource) {
//            EtlSource etl = (EtlSource) source;
//            List<DataSource> dataSources = etl.getBasedSources();
//            for (DataSource dataSource : dataSources) {
//                initDataSource(dataSource);
//            }
//        }
    }

    protected abstract void handleSingleRelationSource(List<R> allRelationSource);

    public void addNode(T node) {
        headNode.put(node.getKey(), node);
    }

    public Map<SourceKey, T> getHeadNode() {
        return Collections.unmodifiableMap(headNode);
    }

    public Map<SourceKey, DataSource> getAllDataSourceList() {
        return Collections.unmodifiableMap(allDataSourceList);
    }

    public Map<SourceKey, R> getAllRelationSource() {
        return Collections.unmodifiableMap(allRelationSource);
    }
}
