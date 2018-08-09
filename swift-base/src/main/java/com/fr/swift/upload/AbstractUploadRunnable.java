package com.fr.swift.upload;

import com.fr.swift.config.entity.SwiftTablePathEntity;
import com.fr.swift.config.service.SwiftCubePathService;
import com.fr.swift.config.service.SwiftSegmentLocationService;
import com.fr.swift.config.service.SwiftSegmentService;
import com.fr.swift.config.service.SwiftTablePathService;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.event.history.HistoryCommonLoadRpcEvent;
import com.fr.swift.event.history.HistoryLoadSegmentRpcEvent;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.relation.RelationIndexImpl;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.RelationSource;
import com.fr.swift.source.RelationSourceType;
import com.fr.swift.source.SourceKey;
import com.fr.swift.structure.Pair;
import com.fr.swift.task.TaskKey;
import com.fr.swift.task.TaskResult;
import com.fr.swift.util.FileUtil;
import com.fr.swift.util.Strings;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static com.fr.swift.task.TaskResult.Type.SUCCEEDED;

/**
 * @author yee
 * @date 2018/8/6
 */
public abstract class AbstractUploadRunnable implements UploadRunnable {

    protected Pair<TaskKey, TaskResult> result;
    private transient SwiftCubePathService pathService;
    private transient SwiftTablePathService tablePathService;
    private transient SwiftSegmentLocationService locationService;
    private String id;

    public AbstractUploadRunnable(Pair<TaskKey, TaskResult> result, String id) {
        this.pathService = SwiftContext.get().getBean(SwiftCubePathService.class);
        this.tablePathService = SwiftContext.get().getBean(SwiftTablePathService.class);
        this.locationService = SwiftContext.get().getBean(SwiftSegmentLocationService.class);
        this.result = result;
        this.id = id;
    }

    @Override
    public void uploadTable(final DataSource dataSource) throws Exception {
        final SourceKey sourceKey = dataSource.getSourceKey();
        SwiftTablePathEntity entity = SwiftContext.get().getBean(SwiftTablePathService.class).get(sourceKey.getId());
        Integer path = entity.getTablePath();
        path = null == path ? -1 : path;
        Integer tmpPath = entity.getTmpDir();
        entity.setTablePath(tmpPath);
        entity.setLastPath(path);
        List<SegmentKey> segmentKeys = SwiftContext.get().getBean("segmentServiceProvider", SwiftSegmentService.class).getSegmentByKey(sourceKey.getId());
        String cubePath = pathService.getSwiftPath();
        if (null != segmentKeys) {
            for (SegmentKey segmentKey : segmentKeys) {
                try {
                    String uploadPath = String.format("%s",
//                            segmentKey.getSwiftSchema().getDir(),
                            segmentKey.getUri().getPath());
                    URI local = URI.create(String.format("%s/%s",
                            cubePath,
//                            segmentKey.getSwiftSchema().getDir(),
//                            tmpPath,
                            segmentKey.getUri().getPath()));
                    upload(local, URI.create(uploadPath));
                } catch (Exception e) {
                    SwiftLoggers.getLogger().error("upload error! ", e);
                }
            }
            if (path.compareTo(tmpPath) != 0 && tablePathService.saveOrUpdate(entity)
                    && locationService.delete(sourceKey.getId(), id)) {
                String deletePath = String.format("%s/%s/%d/%s",
                        pathService.getSwiftPath(),
                        dataSource.getMetadata().getSwiftSchema().getDir(),
                        path,
                        sourceKey.getId());
                FileUtil.delete(deletePath);
                new File(deletePath).getParentFile().delete();
            }
            doAfterUpload(new HistoryLoadSegmentRpcEvent(sourceKey.getId()));
        }
    }

    @Override
    public void uploadRelation(RelationSource relation) throws Exception {
        SourceKey sourceKey = relation.getForeignSource();
        SourceKey primary = relation.getPrimarySource();
        List<URI> needUpload = new ArrayList<URI>();
        List<SegmentKey> segmentKeys = SwiftContext.get().getBean("segmentServiceProvider", SwiftSegmentService.class).getSegmentByKey(sourceKey.getId());
        if (null != segmentKeys) {
            if (relation.getRelationType() != RelationSourceType.FIELD_RELATION) {
                for (SegmentKey segmentKey : segmentKeys) {
                    try {
                        URI src = URI.create(String.format("%s/%s/%s",
                                Strings.trimSeparator(segmentKey.getAbsoluteUri().getPath() + "/", "/"),
                                RelationIndexImpl.RELATIONS_KEY,
                                primary.getId()));
                        URI dest = URI.create(String.format("%s/%s/%s/%s",
                                segmentKey.getSwiftSchema().getDir(),
                                Strings.trimSeparator(segmentKey.getUri().getPath() + "/", "/"),
                                RelationIndexImpl.RELATIONS_KEY,
                                primary.getId()));
                        upload(src, dest);
                        needUpload.add(dest);
                    } catch (IOException e) {
                        SwiftLoggers.getLogger().error("upload error! ", e);
                    }
                }
            } else {
                for (SegmentKey segmentKey : segmentKeys) {
                    try {
                        URI src = URI.create(String.format("%s/field/%s/%s",
                                Strings.trimSeparator(segmentKey.getAbsoluteUri().getPath() + "/", "/"),
                                RelationIndexImpl.RELATIONS_KEY,
                                primary.getId()));
                        URI dest = URI.create(String.format("%s/%s/field/%s/%s",
                                segmentKey.getSwiftSchema().getDir(),
                                Strings.trimSeparator(segmentKey.getUri().getPath() + "/", "/"),
                                RelationIndexImpl.RELATIONS_KEY,
                                primary.getId()));
                        upload(src, dest);
                        needUpload.add(dest);
                    } catch (IOException e) {
                        SwiftLoggers.getLogger().error("upload error! ", e);
                    }
                }
            }
            doAfterUpload(new HistoryCommonLoadRpcEvent(Pair.of(sourceKey.getId(), needUpload)));
        }
    }

    protected abstract void upload(URI src, URI dest) throws IOException;

    @Override
    public void run() {
        if (result.getValue().getType() == SUCCEEDED) {
            TaskKey key = result.getKey();
            Object obj = ReadyUploadContainer.instance().get(key);
            try {
                if (null != obj) {
                    if (obj instanceof DataSource) {
                        uploadTable((DataSource) obj);
                    } else if (obj instanceof RelationSource) {
                        uploadRelation((RelationSource) obj);
                    }
                    ReadyUploadContainer.instance().remove(key);
                }

            } catch (Exception e) {
                SwiftLoggers.getLogger().error(e);
            }
        }
    }
}
