package com.fr.swift.stuff;

import com.fr.swift.source.DataSource;
import com.fr.swift.source.RelationSource;
import com.fr.swift.source.SourcePath;
import com.fr.swift.task.TaskKey;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;

/**
 * @author yee
 * @date 2018/8/7
 */
public class DefaultIndexingStuff implements IndexingStuff, Serializable {
    private static final long serialVersionUID = -6401878706988042192L;
    private Map<TaskKey, DataSource> tables;

    private Map<TaskKey, RelationSource> relations;

    private Map<TaskKey, SourcePath> relationPaths;

    public DefaultIndexingStuff(Map<TaskKey, DataSource> tables) {
        this(tables, Collections.<TaskKey, RelationSource>emptyMap(), Collections.<TaskKey, SourcePath>emptyMap());
    }

    public DefaultIndexingStuff(Map<TaskKey, DataSource> tables, Map<TaskKey, RelationSource> relations, Map<TaskKey, SourcePath> relationPaths) {
        this.tables = tables;
        this.relations = relations;
        this.relationPaths = relationPaths;
    }

    @Override
    public Map<TaskKey, DataSource> getTables() {
        return tables;
    }

    @Override
    public Map<TaskKey, RelationSource> getRelations() {
        return relations;
    }

    @Override
    public Map<TaskKey, SourcePath> getRelationPaths() {
        return relationPaths;
    }
}
