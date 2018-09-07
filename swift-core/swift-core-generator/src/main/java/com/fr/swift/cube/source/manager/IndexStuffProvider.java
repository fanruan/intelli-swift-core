package com.fr.swift.source.manager;

import com.fr.swift.provider.IndexStuffMedium;
import com.fr.swift.reliance.RelationPathReliance;
import com.fr.swift.reliance.RelationReliance;
import com.fr.swift.reliance.SourceReliance;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.RelationSource;
import com.fr.swift.source.SourcePath;
import com.fr.swift.task.TaskResult;

import java.util.List;

/**
 * This class created on 2017-11-27.
 *
 * @author Lucifer
 * @since Advanced FineBI Analysis 1.0
 */
public interface IndexStuffProvider {

    DataSource getTableById(String sourceId);

    List<DataSource> getTablesByIds(List<String> sourceIds);

    RelationSource getRelationById(String sourceId);

    List<RelationSource> getRelationsByIds(List<String> sourceIds);

    SourcePath getPathById(String sourceId);

    List<SourcePath> getPathsByIds(List<String> sourceIds);

    List<DataSource> getAllTables();

    List<RelationSource> getAllRelations();

    List<SourcePath> getAllPaths();

    SourceReliance getSourceReliance();

    RelationReliance getRelationReliance();

    RelationPathReliance getRelationPathReliance();

    IndexStuffMedium getIndexStuffMedium();

    List<TaskResultListener> taskResultListeners();

    void addResultListener(TaskResultListener taskResultListener);

    interface TaskResultListener {
        void call(TaskResult result);
    }
}
