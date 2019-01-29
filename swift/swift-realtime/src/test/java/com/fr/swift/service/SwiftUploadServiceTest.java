package com.fr.swift.service;

import com.fr.swift.SwiftContext;
import com.fr.swift.beans.factory.BeanFactory;
import com.fr.swift.config.bean.SegmentKeyBean;
import com.fr.swift.config.service.SwiftCubePathService;
import com.fr.swift.cube.CubeUtil;
import com.fr.swift.cube.io.Types.StoreType;
import com.fr.swift.db.SwiftDatabase;
import com.fr.swift.repository.SwiftRepository;
import com.fr.swift.repository.manager.SwiftRepositoryManager;
import com.fr.swift.segment.BaseSegment;
import com.fr.swift.segment.SegmentHelper;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.source.SourceKey;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * @author anchore
 * @date 2019/1/23
 */
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(MockitoJUnitRunner.class)
@PrepareForTest({SwiftRepositoryManager.class, SwiftContext.class, CubeUtil.class, SegmentHelper.class})
public class SwiftUploadServiceTest {

    private SwiftUploadService uploadService;
    @Mock
    private SwiftRepository repo;
    private HashSet<SegmentKey> segKeys;

    @Before
    public void setUp() throws Exception {
        mockStatic(SwiftRepositoryManager.class, SwiftContext.class, CubeUtil.class, SegmentHelper.class);

        SwiftRepositoryManager repoManager = mock(SwiftRepositoryManager.class);
        when(SwiftRepositoryManager.getManager()).thenReturn(repoManager);
        when(repoManager.currentRepo()).thenReturn(repo);

        BeanFactory beanFactory = mock(BeanFactory.class);
        when(SwiftContext.get()).thenReturn(beanFactory);

        SwiftCubePathService service = mock(SwiftCubePathService.class);
        when(beanFactory.getBean(SwiftCubePathService.class)).thenReturn(service);

        when(service.getSwiftPath()).thenReturn("/a");

        when(CubeUtil.getCurrentDir(ArgumentMatchers.<SourceKey>any())).thenReturn(0);

        uploadService = new SwiftUploadService();
        uploadService.start();

        SourceKey tableKey = new SourceKey("t");
        segKeys = new HashSet<SegmentKey>(Arrays.<SegmentKey>asList(
                new SegmentKeyBean(tableKey, 0, StoreType.FINE_IO, SwiftDatabase.CUBE),
                new SegmentKeyBean(tableKey, 1, StoreType.FINE_IO, SwiftDatabase.CUBE),
                new SegmentKeyBean(tableKey, 2, StoreType.FINE_IO, SwiftDatabase.CUBE)
        ));
    }

    @Test
    public void upload() throws Exception {
        uploadService.upload(segKeys);
        verify(repo).copyToRemote("/a/cubes/0/t/seg0", "cubes/t/seg0");
        verify(repo).copyToRemote("/a/cubes/0/t/seg1", "cubes/t/seg1");
        verify(repo).copyToRemote("/a/cubes/0/t/seg2", "cubes/t/seg2");
    }

    @Test
    public void download() throws Exception {
        uploadService.download(segKeys, true);
        verifyStatic(SegmentHelper.class);
        SegmentHelper.download(
                Collections.<SourceKey, Set<String>>singletonMap(
                        new SourceKey("t"),
                        new HashSet<String>(Arrays.asList("t/seg0", "t/seg1", "t/seg2"))
                ), true);

        uploadService.download(segKeys, false);
        verifyStatic(SegmentHelper.class);
        SegmentHelper.download(
                Collections.<SourceKey, Set<String>>singletonMap(
                        new SourceKey("t"),
                        new HashSet<String>(Arrays.asList("t/seg0", "t/seg1", "t/seg2"))
                ), false);
    }

    @Test
    public void uploadAllShow() throws Exception {
        uploadService.uploadAllShow(segKeys);
        verify(repo).zipToRemote(String.format("/a/cubes/0/t/seg0/%s", BaseSegment.ALL_SHOW_INDEX), String.format("cubes/t/seg0/%s", BaseSegment.ALL_SHOW_INDEX));
        verify(repo).zipToRemote(String.format("/a/cubes/0/t/seg1/%s", BaseSegment.ALL_SHOW_INDEX), String.format("cubes/t/seg1/%s", BaseSegment.ALL_SHOW_INDEX));
        verify(repo).zipToRemote(String.format("/a/cubes/0/t/seg2/%s", BaseSegment.ALL_SHOW_INDEX), String.format("cubes/t/seg2/%s", BaseSegment.ALL_SHOW_INDEX));
    }

    @Test
    public void downloadAllShow() {
        uploadService.downloadAllShow(segKeys);

        verifyStatic(SegmentHelper.class);
        SegmentHelper.download(
                Collections.<SourceKey, Set<String>>singletonMap(
                        new SourceKey("t"),
                        new HashSet<String>(Arrays.asList(
                                String.format("t/seg0/%s", BaseSegment.ALL_SHOW_INDEX),
                                String.format("t/seg1/%s", BaseSegment.ALL_SHOW_INDEX),
                                String.format("t/seg2/%s", BaseSegment.ALL_SHOW_INDEX)
                        ))
                ), false);
    }

    @Test
    public void handleEmpty() {
        uploadService.upload(null);
        uploadService.upload(Collections.<SegmentKey>emptySet());

        uploadService.uploadAllShow(null);
        uploadService.uploadAllShow(Collections.<SegmentKey>emptySet());

        uploadService.download(null, true);
        uploadService.download(Collections.<SegmentKey>emptySet(), true);

        uploadService.downloadAllShow(null);
        uploadService.downloadAllShow(Collections.<SegmentKey>emptySet());

        verifyZeroInteractions(repo);

        verifyStatic(SegmentHelper.class, never());
        SegmentHelper.download(ArgumentMatchers.<Map<SourceKey, Set<String>>>any(), anyBoolean());
    }

    @Test
    public void getServiceType() {
        assertEquals(ServiceType.UPLOAD, uploadService.getServiceType());
    }
}