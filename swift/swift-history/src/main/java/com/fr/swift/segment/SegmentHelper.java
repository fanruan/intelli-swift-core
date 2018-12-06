package com.fr.swift.segment;

import com.fr.swift.SwiftContext;
import com.fr.swift.basics.base.selector.ProxySelector;
import com.fr.swift.config.bean.SwiftTablePathBean;
import com.fr.swift.config.service.SwiftCubePathService;
import com.fr.swift.config.service.SwiftMetaDataService;
import com.fr.swift.config.service.SwiftSegmentLocationService;
import com.fr.swift.config.service.SwiftSegmentService;
import com.fr.swift.config.service.SwiftTablePathService;
import com.fr.swift.cube.CubeUtil;
import com.fr.swift.event.history.HistoryCommonLoadRpcEvent;
import com.fr.swift.event.history.HistoryLoadSegmentRpcEvent;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.repository.SwiftRepository;
import com.fr.swift.repository.manager.SwiftRepositoryManager;
import com.fr.swift.segment.container.SegmentContainer;
import com.fr.swift.segment.relation.RelationIndexImpl;
import com.fr.swift.service.listener.RemoteSender;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.RelationSource;
import com.fr.swift.source.RelationSourceType;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.structure.Pair;
import com.fr.swift.util.FileUtil;
import com.fr.swift.util.Strings;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author yee
 * @date 2018-12-04
 */
public class SegmentHelper {
    public static Map<SourceKey, Set<String>> checkSegmentExists(SwiftSegmentService segmentService, SwiftSegmentManager segmentManager) {
        SwiftRepository repository = SwiftRepositoryManager.getManager().currentRepo();
        Map<SourceKey, List<SegmentKey>> map = segmentService.getOwnSegments();
        final Map<SourceKey, Set<String>> needDownload = new HashMap<SourceKey, Set<String>>();
        if (null != repository) {
            for (Map.Entry<SourceKey, List<SegmentKey>> entry : map.entrySet()) {
                SourceKey table = entry.getKey();
                List<SegmentKey> value = entry.getValue();
                List<SegmentKey> notExists = new ArrayList<SegmentKey>();

                for (SegmentKey segmentKey : value) {
                    if (segmentKey.getStoreType().isPersistent()) {
                        if (!segmentManager.getSegment(segmentKey).isReadable()) {
                            String remotePath = String.format("%s/%s", segmentKey.getSwiftSchema().getDir(), segmentKey.getUri().getPath());
                            if (repository.exists(remotePath)) {
                                if (null == needDownload.get(table)) {
                                    needDownload.put(table, new HashSet<String>());
                                }
                                needDownload.get(table).add(remotePath);
                            } else {
                                notExists.add(segmentKey);
                            }
                        }
                    }
                }
                segmentService.removeSegments(notExists);
            }
        }
        return needDownload;
    }

    public static void download(String sourceKey, Set<String> sets, boolean replace) {
        SwiftRepository repository = SwiftRepositoryManager.getManager().currentRepo();
        if (null != repository) {
            SwiftCubePathService pathService = SwiftContext.get().getBean(SwiftCubePathService.class);
            SwiftTablePathService tablePathService = SwiftContext.get().getBean(SwiftTablePathService.class);
            SwiftMetaDataService metaDataService = SwiftContext.get().getBean(SwiftMetaDataService.class);
            String path = pathService.getSwiftPath();
            SwiftMetaData metaData = metaDataService.getMetaDataByKey(sourceKey);
            int tmp = 0;
            SwiftTablePathBean entity = tablePathService.get(sourceKey);
            if (null == entity) {
                entity = new SwiftTablePathBean(sourceKey, tmp);
                tablePathService.saveOrUpdate(entity);
                replace = true;
            } else {
                tmp = entity.getTablePath() == null ? -1 : entity.getTablePath();
                if (replace) {
                    tmp += 1;
                    entity.setTmpDir(tmp);
                    tablePathService.saveOrUpdate(entity);
                }
            }

            boolean downloadSuccess = true;
            for (String uri : sets) {
                String cubePath = String.format("%s/%s/%d/%s", path, metaData.getSwiftDatabase().getDir(), tmp, uri);
                String remotePath = String.format("%s/%s", metaData.getSwiftDatabase().getDir(), uri);
                try {
                    repository.copyFromRemote(remotePath, cubePath);
                } catch (Exception e) {
                    downloadSuccess = false;
                    SwiftLoggers.getLogger().error("Download " + sourceKey + " with error! ", e);
                    break;
                }
            }

            if (replace && downloadSuccess) {
                entity = tablePathService.get(sourceKey);
                int current = entity.getTablePath() == null ? -1 : entity.getTablePath();
                entity.setLastPath(current);
                entity.setTablePath(tmp);
                tablePathService.saveOrUpdate(entity);
                String cubePath = String.format("%s/%s/%d/%s", path, metaData.getSwiftDatabase().getDir(), current, sourceKey);
                FileUtil.delete(cubePath);
                new File(cubePath).getParentFile().delete();
            }
            if (downloadSuccess) {
                SourceKey table = new SourceKey(sourceKey);
                SegmentContainer.NORMAL.remove(table);
                SegmentContainer.INDEXING.remove(table);
                SwiftContext.get().getBean("localSegmentProvider", SwiftSegmentManager.class).getSegment(table);
                SwiftLoggers.getLogger().info("Download {} {}successful", sourceKey, sets);
            }
        }
    }

    public static void uploadTable(SwiftSegmentManager manager,
                                   final DataSource dataSource,
                                   String clusterId) throws Exception {
        SwiftRepository repository = SwiftRepositoryManager.getManager().currentRepo();
        if (null != repository) {
            SwiftCubePathService pathService = SwiftContext.get().getBean(SwiftCubePathService.class);
            SwiftTablePathService tablePathService = SwiftContext.get().getBean(SwiftTablePathService.class);
            SwiftSegmentLocationService locationService = SwiftContext.get().getBean(SwiftSegmentLocationService.class);
            final SourceKey sourceKey = dataSource.getSourceKey();
            SwiftTablePathBean entity = SwiftContext.get().getBean(SwiftTablePathService.class).get(sourceKey.getId());
            Integer path = entity.getTablePath();
            path = null == path ? -1 : path;
            Integer tmpPath = entity.getTmpDir();
            entity.setTablePath(tmpPath);
            entity.setLastPath(path);
            List<SegmentKey> segmentKeys = SwiftContext.get().getBean("segmentServiceProvider", SwiftSegmentService.class).getSegmentByKey(sourceKey.getId());
            String cubePath = pathService.getSwiftPath();
            if (null != segmentKeys) {
                repository.delete(String.format("%s/%s", dataSource.getMetadata().getSwiftDatabase().getDir(), sourceKey.getId()));
                for (SegmentKey segmentKey : segmentKeys) {
                    try {
                        String uploadPath = String.format("%s/%s",
                                segmentKey.getSwiftSchema().getDir(),
                                segmentKey.getUri().getPath());
                        String local = String.format("%s/%s", cubePath, CubeUtil.getHistorySegPath(dataSource, tmpPath, segmentKey.getOrder()));
                        repository.copyToRemote(local, uploadPath);
                    } catch (Exception e) {
                        SwiftLoggers.getLogger().error("upload error! ", e);
                    }
                }
                if (path.compareTo(tmpPath) != 0 && tablePathService.saveOrUpdate(entity)
                        && locationService.delete(sourceKey.getId(), clusterId)) {
                    String deletePath = String.format("%s/%s/%d/%s",
                            pathService.getSwiftPath(),
                            dataSource.getMetadata().getSwiftDatabase().getDir(),
                            path,
                            sourceKey.getId());
                    FileUtil.delete(deletePath);
                    new File(deletePath).getParentFile().delete();
                }
                manager.remove(sourceKey);
                SegmentContainer.INDEXING.remove(sourceKey);
                manager.getSegment(sourceKey);
                ProxySelector.getInstance().getFactory().getProxy(RemoteSender.class).trigger(new HistoryLoadSegmentRpcEvent(sourceKey, clusterId));
            }
        }
    }

    public static void uploadRelation(RelationSource relation, String clusterId) {
        SwiftRepository repository = SwiftRepositoryManager.getManager().currentRepo();
        if (null != repository) {
            SwiftCubePathService pathService = SwiftContext.get().getBean(SwiftCubePathService.class);
            SourceKey sourceKey = relation.getForeignSource();
            SourceKey primary = relation.getPrimarySource();
            Map<SegmentKey, List<String>> segNeedUpload = new HashMap<SegmentKey, List<String>>();
            List<SegmentKey> segmentKeys = SwiftContext.get().getBean("segmentServiceProvider", SwiftSegmentService.class).getSegmentByKey(sourceKey.getId());
            if (null != segmentKeys) {
                if (relation.getRelationType() != RelationSourceType.FIELD_RELATION) {
                    for (SegmentKey segmentKey : segmentKeys) {
                        if (null == segNeedUpload.get(segmentKey)) {
                            segNeedUpload.put(segmentKey, new ArrayList<String>());
                        }
                        try {
                            String src = Strings.unifySlash(
                                    String.format("%s/%s/%s/%s",
                                            pathService.getSwiftPath(),
                                            CubeUtil.getSegPath(segmentKey),
                                            RelationIndexImpl.RELATIONS_KEY,
                                            primary.getId()
                                    ));
                            String dest = String.format("%s/%s/%s/%s",
                                    segmentKey.getSwiftSchema().getDir(),
                                    Strings.unifySlash(segmentKey.getUri().getPath() + "/"),
                                    RelationIndexImpl.RELATIONS_KEY,
                                    primary.getId());
                            repository.copyToRemote(src, dest);
                            segNeedUpload.get(segmentKey).add(dest);
                        } catch (IOException e) {
                            SwiftLoggers.getLogger().error("upload error! ", e);
                        }
                    }
                } else {
                    for (SegmentKey segmentKey : segmentKeys) {
                        if (null == segNeedUpload.get(segmentKey)) {
                            segNeedUpload.put(segmentKey, new ArrayList<String>());
                        }
                        try {
                            String src = Strings.unifySlash(
                                    String.format("%s/field/%s/%s",
                                            CubeUtil.getSegPath(segmentKey),
                                            RelationIndexImpl.RELATIONS_KEY,
                                            primary.getId()
                                    ));
                            String dest = String.format("%s/%s/field/%s/%s",
                                    segmentKey.getSwiftSchema().getDir(),
                                    Strings.unifySlash(segmentKey.getUri().getPath() + "/"),
                                    RelationIndexImpl.RELATIONS_KEY,
                                    primary.getId());
                            repository.copyToRemote(src, dest);
                            segNeedUpload.get(segmentKey).add(dest);
                        } catch (IOException e) {
                            SwiftLoggers.getLogger().error("upload error! ", e);
                        }
                    }
                }
                ProxySelector.getInstance().getFactory().getProxy(RemoteSender.class).trigger(new HistoryCommonLoadRpcEvent(Pair.of(sourceKey, segNeedUpload), clusterId));
            }
        }
    }
}
