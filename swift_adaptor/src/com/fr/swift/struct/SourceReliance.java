package com.fr.swift.struct;

import com.fr.swift.source.DataSource;
import com.fr.swift.source.SourceKey;

import java.util.ArrayList;
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

    private List<SourceChain> heads;

    private Map<SourceKey, DataSource> reliances;

    private Map<SourceKey, DataSource> origins;

    public SourceReliance(List<DataSource> origins, List<DataSource> reliances) {
        this.reliances = new HashMap<SourceKey, DataSource>();
        this.heads = new ArrayList<SourceChain>();
        this.origins = new HashMap<SourceKey, DataSource>();

        for (DataSource origin : origins) {
            addOrigin(origin);
        }
        for (DataSource reliance : reliances) {
            addReliance(reliance);
        }
    }

    public void addChain(SourceChain sourceChain) {
        this.heads.add(sourceChain);
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

    public List<SourceChain> getHeads() {
        return new ArrayList<SourceChain>(heads);
    }

    public List<DataSource> getReliances() {
        return new ArrayList<DataSource>(reliances.values());
    }

    public List<DataSource> getOrigins() {
        return new ArrayList<DataSource>(origins.values());
    }
}
