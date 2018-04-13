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
 * @since Advanced FineBI Analysis 1.0
 */
public class SourceReliance {

    private Map<SourceKey, SourceNode> sourceNodes;

    private Map<SourceKey, DataSource> reliances;

    private Map<SourceKey, DataSource> origins;

    public SourceReliance(List<DataSource> origins, List<DataSource> reliances) {
        this.reliances = new HashMap<SourceKey, DataSource>();
        this.origins = new HashMap<SourceKey, DataSource>();
        this.sourceNodes = new HashMap<SourceKey, SourceNode>();
        for (DataSource origin : origins) {
            addOrigin(origin);
        }
        for (DataSource reliance : reliances) {
            addReliance(reliance);
        }
    }

    public Map<SourceKey, SourceNode> getNodes() {
        return Collections.unmodifiableMap(sourceNodes);
    }

    public void addNode(SourceNode sourceNode) {
        sourceNodes.put(sourceNode.getSourceKey(), sourceNode);
    }

    public void removeNode(SourceNode sourceNode) {
        sourceNodes.remove(sourceNode.getSourceKey());
    }

    public boolean containNode(SourceKey sourceKey) {
        return sourceNodes.containsKey(sourceKey);
    }

    public SourceNode getNode(SourceKey sourceKey) {
        return sourceNodes.get(sourceKey);
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
}
