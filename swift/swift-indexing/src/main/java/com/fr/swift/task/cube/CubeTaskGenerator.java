package com.fr.swift.task.cube;

import com.fr.swift.SwiftContext;
import com.fr.swift.generate.history.TableBuilder;
import com.fr.swift.generate.history.index.FieldPathIndexer;
import com.fr.swift.generate.history.index.MultiRelationIndexer;
import com.fr.swift.generate.history.index.TablePathIndexer;
import com.fr.swift.manager.LocalSegmentProvider;
import com.fr.swift.relation.utils.RelationPathHelper;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.RelationSource;
import com.fr.swift.source.relation.FieldRelationSource;
import com.fr.swift.task.TaskKey;
import com.fr.swift.task.WorkerTask;
import com.fr.swift.task.impl.WorkerTaskImpl;
import com.fr.swift.util.function.BinaryFunction;

/**
 * @author anchore
 * @date 2018/7/11
 */
public class CubeTaskGenerator implements BinaryFunction<TaskKey, Object, WorkerTask> {
    @Override
    public WorkerTask apply(TaskKey taskKey, Object data) {
        if (taskKey.operation() == CubeOperation.NULL) {
            return new WorkerTaskImpl(taskKey);
        }

        WorkerTask wt = null;
        if (data instanceof DataSource) {
            wt = new WorkerTaskImpl(taskKey, new TableBuilder(taskKey.getRound(), (DataSource) data));
            return wt;
        }
        if (data instanceof RelationSource) {
            RelationSource source = (RelationSource) data;
            switch (source.getRelationType()) {
                case RELATION:
                    wt = new WorkerTaskImpl(taskKey, new MultiRelationIndexer(RelationPathHelper.convert2CubeRelation(source), SwiftContext.get().getBean(LocalSegmentProvider.class)));
                    break;
                case RELATION_PATH:
                    wt = new WorkerTaskImpl(taskKey, new TablePathIndexer(RelationPathHelper.convert2CubeRelationPath(source), SwiftContext.get().getBean(LocalSegmentProvider.class)));
                    break;
                case FIELD_RELATION:
                    FieldRelationSource fieldRelationSource = (FieldRelationSource) source;
                    wt = new WorkerTaskImpl(taskKey, new FieldPathIndexer(RelationPathHelper.convert2CubeRelationPath(fieldRelationSource.getRelationSource()), fieldRelationSource.getColumnKey(), SwiftContext.get().getBean(LocalSegmentProvider.class)));
                    break;
                default:
            }
            return wt;
        }
        return null;
    }
}