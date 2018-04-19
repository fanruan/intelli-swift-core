package com.fr.swift.reliance;

import com.fr.swift.source.DataSource;
import com.fr.swift.source.SourceKey;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class created on 2018/4/9
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class SourceReliance {

    //头节点
    private Map<SourceKey, SourceNode> headSourceNodes;

    //所有节点
    private Map<SourceKey, SourceNode> allSourceNodes;

    //所有依赖的datasource
    private Map<SourceKey, DataSource> reliances;

    //原始的datasource
    private Map<SourceKey, DataSource> origins;

    public SourceReliance(List<DataSource> origins, List<DataSource> reliances) {
        this.reliances = new HashMap<SourceKey, DataSource>();
        this.origins = new HashMap<SourceKey, DataSource>();
        this.headSourceNodes = new HashMap<SourceKey, SourceNode>();
        this.allSourceNodes = new HashMap<SourceKey, SourceNode>();
        for (DataSource origin : origins) {
            addOrigin(origin);
        }
        for (DataSource reliance : reliances) {
            addReliance(reliance);
        }
    }

    public Map<SourceKey, SourceNode> getHeadNodes() {
        return Collections.unmodifiableMap(headSourceNodes);
    }

    public void addHeadNode(SourceNode sourceNode) {
        headSourceNodes.put(sourceNode.getSourceKey(), sourceNode);
    }

    public void removeHeadNode(SourceNode sourceNode) {
        headSourceNodes.remove(sourceNode.getSourceKey());
    }

    public boolean containHeadNode(SourceKey sourceKey) {
        return headSourceNodes.containsKey(sourceKey);
    }

    public SourceNode getHeadNode(SourceKey sourceKey) {
        return headSourceNodes.get(sourceKey);
    }

    public void addNode(SourceNode sourceNode) {
        allSourceNodes.put(sourceNode.getSourceKey(), sourceNode);
    }

    public SourceNode getNode(SourceKey sourceKey) {
        return allSourceNodes.get(sourceKey);
    }

    public boolean containNode(SourceNode sourceNode) {
        return allSourceNodes.containsKey(sourceNode.getSourceKey());
    }

    public boolean containReliance(SourceKey sourceKey) {
        return reliances.containsKey(sourceKey);
    }

    public void addReliance(DataSource reliance) {
        if (!reliances.containsKey(reliance.getSourceKey())) {
            reliances.put(reliance.getSourceKey(), reliance);
        }
    }

    private void addOrigin(DataSource origin) {
        if (!origins.containsKey(origin.getSourceKey())) {
            origins.put(origin.getSourceKey(), origin);
        }
    }

    public List<DataSource> getReliances() {
        return new ArrayList<DataSource>(reliances.values());
    }

    public List<DataSource> getOrigins() {
        return new ArrayList<DataSource>(origins.values());
    }

    public DataSource getRelianceSource(SourceKey sourceKey) {
        return reliances.get(sourceKey);
    }

    public DataSource getOriginSource(SourceKey sourceKey) {
        return origins.get(sourceKey);
    }
}
