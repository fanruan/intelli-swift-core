package com.fr.swift.stuff;

import com.fr.swift.source.DataSource;
import com.fr.swift.source.RelationSource;
import com.fr.swift.source.SourcePath;
import com.fr.swift.task.TaskKey;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;

/**
 * Created by pony on 2017/11/2.
 */
public class HistoryIndexingStuff implements IndexingStuff, Serializable {
    private Map<TaskKey, DataSource> tables;

    private Map<TaskKey, RelationSource> relations;

    private Map<TaskKey, SourcePath> relationPaths;

    public HistoryIndexingStuff(Map<TaskKey, DataSource> tables) {
        this(tables, Collections.<TaskKey, RelationSource>emptyMap(), Collections.<TaskKey, SourcePath>emptyMap());
    }

    public HistoryIndexingStuff(Map<TaskKey, DataSource> tables, Map<TaskKey, RelationSource> relations, Map<TaskKey, SourcePath> relationPaths) {
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

    private static final long serialVersionUID = 6020432061987147639L;
}
