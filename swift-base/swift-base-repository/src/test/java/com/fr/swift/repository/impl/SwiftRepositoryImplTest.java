package com.fr.swift.repository.impl;

import com.fr.swift.SwiftContext;
import com.fr.swift.file.SwiftFileSystemType;
import com.fr.swift.file.system.annotation.FileSystemFactory;
import com.fr.swift.file.system.pool.TestRemoteSystemFactory;
import com.fr.swift.repository.SwiftFileSystemConfig;
import com.fr.swift.repository.manager.SwiftRepositoryManager;
import com.fr.swift.service.SwiftRepositoryConfService;
import com.fr.swift.util.FileUtil;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;

/**
 * @author yee
 * @date 2019-01-08
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(SwiftContext.class)
public class SwiftRepositoryImplTest {
    public static final String[] CHILDREN = new String[]{"all_show_index",
            "time",
            "operate",
            "requestParam",
            "detail",
            "type",
            "row_count",
            "item",
            "resource",
            "ip",
            "username"};

    @Before
    public void setUp() throws Exception {
        String seg0 = "seg0";
        FileUtil.delete(seg0);
        for (String child : CHILDREN) {
            new File(seg0, child).mkdirs();
        }
        mock();
    }

    @Test
    public void copyFromRemote() {
        try {
            SwiftRepositoryManager.getManager().currentRepo().copyFromRemote("seg0", "seg0");
        } catch (IOException e) {
            fail();
        }
        PowerMock.verifyAll();

    }

    @Test
    public void copyToRemote() {
        try {
            SwiftRepositoryManager.getManager().currentRepo().copyToRemote("seg0", "seg0");
        } catch (IOException e) {
            fail();
        }
        PowerMock.verifyAll();
    }

    @Test
    public void delete() {
        try {
            SwiftRepositoryManager.getManager().currentRepo().delete("seg0");
        } catch (IOException e) {
            fail();
        }
        PowerMock.verifyAll();
    }

    @Test
    public void getSize() {
        try {
            assertTrue(SwiftRepositoryManager.getManager().currentRepo().getSize("seg0") > 0);
        } catch (IOException e) {
            fail();
        }
        PowerMock.verifyAll();
    }

    //
    @After
    public void tearDown() throws Exception {
        FileUtil.delete("seg0");
    }

    private void mock() {
        // Generate by Mock Plugin
        PowerMock.mockStatic(SwiftContext.class);
        SwiftContext mockSwiftContext = PowerMock.createMock(SwiftContext.class);
        EasyMock.expect(SwiftContext.get()).andReturn(mockSwiftContext).anyTimes();
        PowerMock.replay(SwiftContext.class);
        Map<String, Object> factoryMap = new HashMap<String, Object>();
        factoryMap.put("TEST", new TestRemoteSystemFactory());
        EasyMock.expect(mockSwiftContext.getBeansByAnnotations(EasyMock.eq(FileSystemFactory.class))).andReturn(factoryMap).anyTimes();
        // Generate by Mock Plugin
        SwiftRepositoryConfService mockSwiftRepositoryConfService = PowerMock.createMock(SwiftRepositoryConfService.class);
        EasyMock.expect(mockSwiftRepositoryConfService.getCurrentRepository()).andReturn(mockConfig()).anyTimes();
        mockSwiftRepositoryConfService.registerListener(EasyMock.anyObject(SwiftRepositoryConfService.ConfChangeListener.class));
        EasyMock.expectLastCall().anyTimes();
        EasyMock.expect(mockSwiftContext.getBean(EasyMock.eq(SwiftRepositoryConfService.class))).andReturn(mockSwiftRepositoryConfService).anyTimes();
        PowerMock.replay(mockSwiftContext, mockSwiftRepositoryConfService);
    }

    private SwiftFileSystemConfig mockConfig() {
        // Generate by Mock Plugin
        SwiftFileSystemConfig mockSwiftFileSystemConfig = PowerMock.createMock(SwiftFileSystemConfig.class);
        EasyMock.expect(mockSwiftFileSystemConfig.getType()).andReturn(new SwiftFileSystemType() {
            @Override
            public String name() {
                return "TEST";
            }
        }).anyTimes();
        PowerMock.replay(mockSwiftFileSystemConfig);
        return mockSwiftFileSystemConfig;
    }
}