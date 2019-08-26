package com.fr.swift.service;

import com.fr.swift.annotation.SwiftService;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.cube.CubePathBuilder;
import com.fr.swift.cube.CubeUtil;
import com.fr.swift.exception.ExceptionInfoBean;
import com.fr.swift.exception.ExceptionInfoType;
import com.fr.swift.exception.UploadExceptionContext;
import com.fr.swift.exception.reporter.ExceptionReporter;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.repository.exception.RepoNotFoundException;
import com.fr.swift.repository.manager.SwiftRepositoryManager;
import com.fr.swift.segment.BaseSegment;
import com.fr.swift.segment.SegmentHelper;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.source.SourceKey;
import com.fr.swift.util.Util;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author anchore
 * @date 2019/1/22
 */
@SwiftService(name = "swiftUploadService")
@SwiftBean(name = "swiftUploadService")
public class SwiftUploadService extends AbstractSwiftService implements UploadService, Serializable {

    private static final long serialVersionUID = 1;

    @Override
    public void upload(Set<SegmentKey> segKeys) {
        if (segKeys == null || segKeys.isEmpty()) {
            return;
        }
        int currentDir = CubeUtil.getCurrentDir(Util.firstItemOf(segKeys).get().getTable());

        for (SegmentKey segKey : segKeys) {
            String local = new CubePathBuilder(segKey).setTempDir(currentDir).build();
            String remote = new CubePathBuilder(segKey).build();
            try {
                SwiftRepositoryManager.getManager().currentRepo().copyToRemote(local, remote);
            } catch (Exception e) {
                SwiftLoggers.getLogger().error("Cannot upload Segment which path is {}", local, e);
                ExceptionInfoBean bean=new ExceptionInfoBean.Builder()
                        .setNowAndHere()
                        .setType(ExceptionInfoType.UPLOAD_SEGMENT)
                        .setContext(new UploadExceptionContext(segKey,false)).build();
                ExceptionReporter.report(bean);
            }
        }
    }

    @Override
    public Set<String> download(Set<SegmentKey> segKeys, boolean replace) {
        if (segKeys == null || segKeys.isEmpty()) {
            return Collections.emptySet();
        }

        Map<SourceKey, Set<String>> needLoadSegments = new HashMap<SourceKey, Set<String>>();
        for (SegmentKey segmentKey : segKeys) {
            SourceKey sourceKey = segmentKey.getTable();
            if (!needLoadSegments.containsKey(sourceKey)) {
                needLoadSegments.put(sourceKey, new HashSet<String>());
            }
            needLoadSegments.get(sourceKey).add(String.format("%s/seg%d", segmentKey.getTable(), segmentKey.getOrder()));
        }
        return SegmentHelper.download(needLoadSegments, replace);
    }

    @Override
    public void uploadAllShow(Set<SegmentKey> segKeys) {
        if (segKeys == null || segKeys.isEmpty()) {
            return;
        }
        int currentDir = CubeUtil.getCurrentDir(Util.firstItemOf(segKeys).get().getTable());

        for (SegmentKey segKey : segKeys) {
            String absoluteSegPath = new CubePathBuilder(segKey).asAbsolute().setTempDir(currentDir).build();
            String local = String.format("%s/%s", absoluteSegPath, BaseSegment.ALL_SHOW_INDEX);
            String remote = String.format("%s/%s", new CubePathBuilder(segKey).build(), BaseSegment.ALL_SHOW_INDEX);

            try {
                SwiftRepositoryManager.getManager().currentRepo().zipToRemote(local, remote);
            } catch (RepoNotFoundException e) {
                SwiftLoggers.getLogger().warn("default repository not found", e);
            } catch (IOException e) {
                SwiftLoggers.getLogger().error("upload segment's all show {} failed", segKey, e);
                ExceptionInfoBean bean=new ExceptionInfoBean.Builder()
                        .setNowAndHere()
                        .setType(ExceptionInfoType.UPLOAD_SEGMENT)
                        .setContext(new UploadExceptionContext(segKey,false)).build();
                ExceptionReporter.report(bean);
            }
        }
    }

    @Override
    public void downloadAllShow(Set<SegmentKey> segKeys) {
        if (segKeys == null || segKeys.isEmpty()) {
            return;
        }

        Map<SourceKey, Set<String>> needLoadSegments = new HashMap<SourceKey, Set<String>>();
        for (SegmentKey segmentKey : segKeys) {
            SourceKey sourceKey = segmentKey.getTable();
            if (!needLoadSegments.containsKey(sourceKey)) {
                needLoadSegments.put(sourceKey, new HashSet<String>());
            }
            needLoadSegments.get(sourceKey).add(String.format("%s/seg%d/%s", segmentKey.getTable(), segmentKey.getOrder(), BaseSegment.ALL_SHOW_INDEX));
        }
        SegmentHelper.download(needLoadSegments, false);
    }

    @Override
    public ServiceType getServiceType() {
        return ServiceType.UPLOAD;
    }
}