package com.fr.swift.executor.task.job.impl;

import com.fr.swift.SwiftContext;
import com.fr.swift.basics.base.selector.ProxySelector;
import com.fr.swift.config.entity.SwiftSegmentEntity;
import com.fr.swift.config.service.SwiftSegmentLocationService;
import com.fr.swift.config.service.SwiftSegmentService;
import com.fr.swift.cube.CubePathBuilder;
import com.fr.swift.cube.CubeUtil;
import com.fr.swift.executor.task.job.BaseJob;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.property.SwiftProperty;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SegmentService;
import com.fr.swift.service.ServiceContext;
import com.fr.swift.util.FileUtil;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Moira
 * @date 2020/7/21
 * @description
 * @since swift-1.2.0
 */
public class MigrateJob extends BaseJob<Boolean, List<String>> {

    private static final String SEPARATOR = "/";
    private static final String SEMICOLON = ";";
    private List<String> segmentIds;
    private List<SegmentKey> segmentKeys;
    private String path;


    public MigrateJob(List<SegmentKey> segmentKeys, String path) {
        this.segmentKeys = segmentKeys;
        this.segmentIds = segmentKeys.stream().map(SegmentKey::getId).collect(Collectors.toList());
        this.path = path;
    }

    @Override
    public Boolean call() throws Exception {
        final ServiceContext serviceContext = ProxySelector.getProxy(ServiceContext.class);
        Map<SegmentKey, Map<String, byte[]>> segments = new HashMap<>();
        for (SegmentKey segmentKey : segmentKeys) {
            //文件夹路径
            Map<String, byte[]> files = new HashMap<>();
            Path path = Paths.get(new CubePathBuilder(segmentKey).setTempDir(CubeUtil.getCurrentDir(segmentKey.getTable())).asAbsolute().build());
            //遍历文件夹内所有文件
            Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                //访问文件时
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    files.put(file.toString().substring(path.toString().length()).replace("\\", SEPARATOR), Files.readAllBytes(file));
                    return FileVisitResult.CONTINUE;
                }

                //访问文件失败时，终止遍历
                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                    SwiftLoggers.getLogger().error("failed to visit {}", file.toString());
                    files.clear();
                    return FileVisitResult.TERMINATE;
                }
            });
            if (files.isEmpty()) {
                return false;
            }
            segments.put(segmentKey, files);
        }
        SwiftSegmentLocationService locationService = SwiftContext.get().getBean(SwiftSegmentLocationService.class);
        SwiftSegmentService bean = SwiftContext.get().getBean(SwiftSegmentService.class);
        SegmentService segmentService = SwiftContext.get().getBean(SegmentService.class);
        //是否迁移成功
        boolean success = serviceContext.migrate(segments, path);
        if (success) {
            List<SegmentKey> newSegments = segmentKeys.stream().map(r -> new SwiftSegmentEntity(r).setLocation(path)).collect(Collectors.toList());
            //更新配置
            bean.updateSegments(newSegments);
            //更新缓存
            segmentService.removeSegments(segmentKeys);
            segmentService.addSegments(newSegments);

            String[] split = path.split(SEMICOLON);
            //如果是远程服务器
            if (split.length == 2) {
                locationService.saveOnNode(split[0], new HashSet<>(newSegments));
                locationService.deleteOnNode(SwiftProperty.get().getMachineId(), new HashSet<>(segmentKeys));
            }
            //删除块
            segmentKeys.forEach(r -> FileUtil.delete(new CubePathBuilder(r).setTempDir(CubeUtil.getCurrentDir(r.getTable())).asAbsolute().build()));
            return true;
        }
        return false;
    }

    @Override
    public List<String> serializedTag() {
        return segmentIds;
    }
}
