package com.fr.swift.stuff;

import com.fr.swift.source.DataSource;
import com.fr.swift.source.RelationSource;
import com.fr.swift.source.SourcePath;
import com.fr.swift.task.TaskKey;

import java.util.Map;

/**
 * This class created on 2017-11-27.
 *
 * @author Lucifer
 * @since Advanced FineBI Analysis 1.0
 */
public interface IndexingStuff {
    Map<TaskKey, DataSource> getTables();

    Map<TaskKey, RelationSource> getRelations();

    Map<TaskKey, SourcePath> getRelationPaths();
}