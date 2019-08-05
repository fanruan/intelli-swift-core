package com.fr.swift.segment;

import com.fr.swift.SwiftContext;
import com.fr.swift.basics.base.selector.ProxySelector;
import com.fr.swift.config.entity.SwiftTablePathEntity;
import com.fr.swift.config.entity.key.SwiftTablePathKey;
import com.fr.swift.config.service.SwiftCubePathService;
import com.fr.swift.config.service.SwiftMetaDataService;
import com.fr.swift.config.service.SwiftSegmentLocationService;
import com.fr.swift.config.service.SwiftSegmentService;
import com.fr.swift.config.service.SwiftTablePathService;
import com.fr.swift.cube.CubePathBuilder;
import com.fr.swift.cube.CubeUtil;
import com.fr.swift.db.SwiftSchema;
import com.fr.swift.event.history.HistoryCommonLoadRpcEvent;
import com.fr.swift.event.history.HistoryLoadSegmentRpcEvent;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.repository.SwiftRepository;
import com.fr.swift.repository.exception.RepoNotFoundException;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;

/**
 * @author yee
 * @date 2018-12-04
 */
public class SegmentHelper {
    public static Map<SourceKey, Set<String>> checkSegmentExists(SwiftSegmentService segmentService, SwiftSegmentLocationService segLocationSvc, SwiftSegmentManager segmentManager) {
        final Map<SourceKey, Set<String>> needDownload = new HashMap<SourceKey, Set<String>>();
        try {
            SwiftRepository repository = SwiftRepositoryManager.getManager().currentRepo();
            Map<SourceKey, List<SegmentKey>> map = segmentService.getOwnSegments();
            if (null != repository) {
                for (Map.Entry<SourceKey, List<SegmentKey>> entry : map.entrySet()) {
                    SourceKey table = entry.getKey();
                    List<SegmentKey> value = entry.getValue();
                    List<SegmentKey> notExists = new ArrayList<SegmentKey>();

                    for (SegmentKey segmentKey : value) {
                        if (segmentKey.getStoreType().isPersistent()) {
                            if (!segmentManager.getSegment(segmentKey).isReadable()) {
                                String remotePath = new CubePathBuilder(segmentKey).build();
                                String downLoadPath = String.format("%s/seg%d", segmentKey.getTable().getId(), segmentKey.getOrder());
                                if (repository.exists(remotePath)) {
                                    if (null == needDownload.get(table)) {
                                        needDownload.put(table, new HashSet<String>());
                                    }
                                    needDownload.get(table).add(downLoadPath);
                                } else {
                                    notExists.add(segmentKey);
                                }
                            }
                        }
                    }
                    segLocationSvc.delete(new HashSet<>(notExists));
                    segmentService.removeSegments(notExists);
                }
            }
        } catch (RepoNotFoundException e) {
            SwiftLoggers.getLogger().error("Default repository not found.", e);
        }
        return needDownload;
    }

    public static Set<String> download(String sourceKey, Set<String> sets, boolean replace) {
        String path = SwiftContext.get().getBean(SwiftCubePathService.class).getSwiftPath();
        SwiftRepository repository = SwiftRepositoryManager.getManager().currentRepo();
        SwiftTablePathService tablePathService = SwiftContext.get().getBean(SwiftTablePathService.class);
        SwiftMetaDataService metaDataService = SwiftContext.get().getBean(SwiftMetaDataService.class);
        SwiftMetaData metaData = metaDataService.getMetaDataByKey(sourceKey);
        Integer prevTabDir = null;
        int downloadTabDir = 0;
        SwiftTablePathEntity entity = tablePathService.get(sourceKey);
        if (entity != null) {
            prevTabDir = entity.getTablePath();
            downloadTabDir = replace ? prevTabDir + 1 : prevTabDir;
        }
        Set<String> downloadPaths = new HashSet<>();
        for (String uri : sets) {
            String cubePath = String.format("%s/%s", new CubePathBuilder()
                    .setSwiftSchema(metaData.getSwiftSchema())
                    .setTempDir(downloadTabDir).build(), uri);
            String remotePath = String.format("%s/%s", metaData.getSwiftSchema().getDir(), uri);
            try {
                repository.copyFromRemote(remotePath, cubePath);
                downloadPaths.add(remotePath);
                // catch Error防FR FTP 下载文件过大OOM导致整个方法跳出
            } catch (Throwable e) {
                if (cubePath.matches(String.format(".+%s/seg\\d+?$", Matcher.quoteReplacement(sourceKey)))) {
                    // 若下载整个seg失败则删掉，下载all show不删
                    FileUtil.delete(cubePath);
                }
                SwiftLoggers.getLogger().error("Download {} with error! ", cubePath, e);
            }
        }

        if (replace && prevTabDir != null && downloadPaths.size() == sets.size()) {
            // 进行替换且之前有对应数据，删除之前数据
            // 替换是全量更新下的情况，是下载整个seg，单独下载allshow的情况不会出现
            // TODO: 2019/5/7 anchore 全部下载成功才进行替换，默认单次下载所有块。FR用不到的
            tablePathService.saveOrUpdate(new SwiftTablePathEntity(new SwiftTablePathKey(sourceKey, entity.getId().getClusterId()), downloadTabDir));

            String relativePreTabPath = new CubePathBuilder()
                    .setSwiftSchema(metaData.getSwiftSchema())
                    .setTempDir(prevTabDir)
                    .setTableKey(new SourceKey(sourceKey)).build();
            String prevTabPath = String.format("%s/%s", path, relativePreTabPath);
            FileUtil.delete(prevTabPath);
        }
        if (!downloadPaths.isEmpty()) {
            SourceKey table = new SourceKey(sourceKey);
            SegmentContainer.NORMAL.remove(table);
            SegmentContainer.INDEXING.remove(table);
            SwiftContext.get().getBean("localSegmentProvider", SwiftSegmentManager.class).getSegment(table);
            SwiftLoggers.getLogger().info("Download {} {} successful", sourceKey, downloadPaths);
        }
        return downloadPaths;
    }

    public static void uploadTable(SwiftSegmentManager manager,
                                   final DataSource dataSource,
                                   String clusterId) throws Exception {
        try {
            SwiftRepository repository = SwiftRepositoryManager.getManager().currentRepo();
            if (null != repository) {
                SwiftTablePathService tablePathService = SwiftContext.get().getBean(SwiftTablePathService.class);
                SwiftSegmentLocationService locationService = SwiftContext.get().getBean(SwiftSegmentLocationService.class);
                final SourceKey sourceKey = dataSource.getSourceKey();
                SwiftTablePathEntity entity = tablePathService.get(sourceKey.getId());
                Integer path = entity.getTablePath();
                path = null == path ? -1 : path;
                Integer tmpPath = entity.getTmpDir();
                entity.setTablePath(tmpPath);
                entity.setLastPath(path);
                List<SegmentKey> segmentKeys = SwiftContext.get().getBean("segmentServiceProvider", SwiftSegmentService.class).getSegmentByKey(sourceKey.getId());
                if (null != segmentKeys) {
                    SwiftSchema swiftSchema = dataSource.getMetadata().getSwiftSchema();
                    repository.delete(new CubePathBuilder().setSwiftSchema(swiftSchema).setTableKey(sourceKey).build());
                    for (SegmentKey segmentKey : segmentKeys) {
                        try {
                            String uploadPath = new CubePathBuilder(segmentKey).build();
                            String local = new CubePathBuilder(segmentKey).setTempDir(tmpPath).build();
                            repository.copyToRemote(local, uploadPath);
                        } catch (Exception e) {
                            SwiftLoggers.getLogger().error("upload error! ", e);
                        }
                    }
                    if (path.compareTo(tmpPath) != 0 && tablePathService.saveOrUpdate(entity)
                            && locationService.delete(sourceKey.getId(), clusterId)) {
                        String deletePath = new CubePathBuilder().asAbsolute()
                                .setSwiftSchema(swiftSchema).setTempDir(path)
                                .setTableKey(sourceKey).build();
                        FileUtil.delete(deletePath);
                        new File(deletePath).getParentFile().delete();
                    }
                    manager.remove(sourceKey);
                    SegmentContainer.INDEXING.remove(sourceKey);
                    manager.getSegment(sourceKey);
                    ProxySelector.getInstance().getFactory().getProxy(RemoteSender.class).trigger(new HistoryLoadSegmentRpcEvent(sourceKey, clusterId));
                }
            }
        } catch (RepoNotFoundException e) {
            SwiftLoggers.getLogger().error("Default repository not found.", e);
        }
    }

    public static void uploadRelation(RelationSource relation, String clusterId) {
        try {
            SwiftRepository repository = SwiftRepositoryManager.getManager().currentRepo();
            if (null != repository) {
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
                                        String.format("%s/%s/%s",
                                                new CubePathBuilder(segmentKey).setTempDir(CubeUtil.getCurrentDir(sourceKey)).build(),
                                                RelationIndexImpl.RELATIONS_KEY,
                                                primary.getId()
                                        ));
                                String dest = String.format("%s/%s/%s",
                                        new CubePathBuilder(segmentKey).build(),
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
                                                new CubePathBuilder(segmentKey).setTempDir(CubeUtil.getCurrentDir(sourceKey)).build(),
                                                RelationIndexImpl.RELATIONS_KEY,
                                                primary.getId()
                                        ));
                                String dest = String.format("%s/field/%s/%s",
                                        new CubePathBuilder(segmentKey).build(),
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
        } catch (RepoNotFoundException e) {
            SwiftLoggers.getLogger().error("Default repository not found.", e);
        }
    }

    public static Set<String> download(Map<SourceKey, Set<String>> remoteUris, boolean replace) {
        if (null == remoteUris || remoteUris.isEmpty()) {
            return Collections.emptySet();
        }
        Set<String> downloadPaths = new HashSet<>();
        for (final SourceKey sourceKey : remoteUris.keySet()) {
            Set<String> uris = remoteUris.get(sourceKey);
            if (uris.isEmpty()) {
                return Collections.emptySet();
            }
            try {
                downloadPaths.addAll(download(sourceKey.getId(), uris, replace));
                SwiftLoggers.getLogger().info("{}, {}", sourceKey, uris);
            } catch (Exception e) {
                SwiftLoggers.getLogger().warn("download seg {} of {} failed", uris, sourceKey, e);
            }
        }
        return downloadPaths;
    }
}
