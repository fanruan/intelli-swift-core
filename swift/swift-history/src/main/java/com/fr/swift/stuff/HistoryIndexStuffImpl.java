//package com.fr.swift.stuff;
//
//import com.fr.swift.cube.task.TaskKey;
//import com.fr.swift.source.DataSource;
//import com.fr.swift.source.RelationSource;
//import com.fr.swift.source.SourcePath;
//
//import java.io.Serializable;
//import java.util.Collections;
//import java.util.Map;
//
///**
// * This class created on 2017-11-27.
// *
// * @author Lucifer
// * @describe 全量更新stuff
// * @since Advanced FineBI Analysis 1.0
// */
//public class HistoryIndexStuffImpl implements HistoryIndexingStuff, Serializable {
//    private Map<TaskKey, DataSource> tables;
//
//    private Map<TaskKey, RelationSource> relations;
//
//    private Map<TaskKey, SourcePath> relationPaths;
//
//    public HistoryIndexStuffImpl(Map<TaskKey, DataSource> tables) {
//        this(tables, Collections.<TaskKey, RelationSource>emptyMap(), Collections.<TaskKey, SourcePath>emptyMap());
//    }
//
//    public HistoryIndexStuffImpl(Map<TaskKey, DataSource> tables, Map<TaskKey, RelationSource> relations, Map<TaskKey, SourcePath> relationPaths) {
//        this.tables = tables;
//        this.relations = relations;
//        this.relationPaths = relationPaths;
//    }
//
//    @Override
//    public Map<TaskKey, DataSource> getTables() {
//        return tables;
//    }
//
//    @Override
//    public Map<TaskKey, RelationSource> getRelations() {
//        return relations;
//    }
//
//    @Override
//    public Map<TaskKey, SourcePath> getRelationPaths() {
//        return relationPaths;
//    }
//
//    private static final long serialVersionUID = 6020432061987147639L;
//}
