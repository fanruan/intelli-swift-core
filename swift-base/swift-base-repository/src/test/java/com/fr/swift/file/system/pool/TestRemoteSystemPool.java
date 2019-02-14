package com.fr.swift.file.system.pool;

import com.fr.swift.file.system.AbstractFileSystem;
import com.fr.swift.file.system.SwiftFileSystem;
import com.fr.swift.repository.SwiftFileSystemConfig;
import com.fr.swift.repository.impl.SwiftRepositoryImplTest;
import com.fr.swift.repository.utils.SwiftRepositoryUtils;
import com.fr.swift.util.Strings;
import org.easymock.EasyMock;
import org.powermock.api.easymock.PowerMock;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * @author yee
 * @date 2019-01-08
 */
public class TestRemoteSystemPool implements RemoteFileSystemPool {
    private SwiftFileSystemConfig config;

    public TestRemoteSystemPool(SwiftFileSystemConfig config) {
        this.config = config;
    }

    @Override
    public SwiftFileSystem borrowObject(String path) {
        try {
            // Generate by Mock Plugin
            AbstractFileSystem mockAbstractFileSystem = PowerMock.createMock(AbstractFileSystem.class);
            EasyMock.expect(mockAbstractFileSystem.read()).andReturn(null).anyTimes();

            if ("seg0".equals(path)) {
                SwiftFileSystem[] systems = new SwiftFileSystem[SwiftRepositoryImplTest.CHILDREN.length];
                for (int i = 0; i < SwiftRepositoryImplTest.CHILDREN.length; i++) {
                    systems[i] = borrowObject(SwiftRepositoryImplTest.CHILDREN[i] + ".cubes");
                }
                EasyMock.expect(mockAbstractFileSystem.listFiles()).andReturn(systems).anyTimes();
            }

            EasyMock.expect(mockAbstractFileSystem.getSize()).andReturn(1L).anyTimes();
            EasyMock.expect(mockAbstractFileSystem.copy(EasyMock.anyString())).andReturn(false).anyTimes();
            mockAbstractFileSystem.write(EasyMock.anyObject(InputStream.class));
            EasyMock.expectLastCall().anyTimes();
            EasyMock.expect(mockAbstractFileSystem.remove()).andReturn(false).anyTimes();
            EasyMock.expect(mockAbstractFileSystem.getResourceURI()).andReturn(path).anyTimes();
            EasyMock.expect(mockAbstractFileSystem.getConfig()).andReturn(config).anyTimes();
            EasyMock.expect(mockAbstractFileSystem.renameTo(EasyMock.anyString())).andReturn(false).anyTimes();
            EasyMock.expect(mockAbstractFileSystem.isExists()).andReturn(true).anyTimes();
            EasyMock.expect(mockAbstractFileSystem.isDirectory()).andReturn(Strings.isNotEmpty(path) && !path.endsWith(".cubes")).anyTimes();
            EasyMock.expect(mockAbstractFileSystem.getResourceName()).andReturn(SwiftRepositoryUtils.getName(path)).anyTimes();
            EasyMock.expect(mockAbstractFileSystem.toStream()).andReturn(new ByteArrayInputStream(new byte[0])).anyTimes();

            mockAbstractFileSystem.testConnection();
            EasyMock.expectLastCall().anyTimes();
            mockAbstractFileSystem.mkdirs();
            EasyMock.expectLastCall().anyTimes();
            if (Strings.isNotEmpty(path)) {
                EasyMock.expect(mockAbstractFileSystem.parent()).andReturn(borrowObject(SwiftRepositoryUtils.getParent(path))).anyTimes();
            }
            PowerMock.replay(mockAbstractFileSystem);

            return mockAbstractFileSystem;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void returnObject(String resourceURI, SwiftFileSystem fileSystem) {

    }
}
