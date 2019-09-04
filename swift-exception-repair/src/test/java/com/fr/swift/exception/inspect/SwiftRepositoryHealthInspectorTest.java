package com.fr.swift.exception.inspect;

import com.fr.swift.repository.SwiftRepository;
import com.fr.swift.repository.manager.SwiftRepositoryManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;

/**
 * @author Marvin
 * @version 1.1
 * Created by Marvin on 9/3/2019
 */

@PrepareForTest({SwiftRepositoryManager.class, File.class, SwiftRepositoryHealthInspector.class})
@RunWith(PowerMockRunner.class)
public class SwiftRepositoryHealthInspectorTest {

    SwiftRepository repo;

    String local;
    String remote;
    File tempFile;
    File file;

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(SwiftRepositoryHealthInspector.class);
        PowerMockito.mockStatic(SwiftRepositoryManager.class);
        PowerMockito.mockStatic(File.class);

        SwiftRepositoryManager manager = PowerMockito.mock(SwiftRepositoryManager.class);
        repo = PowerMockito.mock(SwiftRepository.class);
        PowerMockito.when(SwiftRepositoryManager.getManager()).thenReturn(manager);
        PowerMockito.when(manager.currentRepo()).thenReturn(repo);

        local = "local";
        remote = "remote";
        tempFile = PowerMockito.mock(File.class);
        file = PowerMockito.mock(File.class);
        PowerMockito.when(File.createTempFile((String) ArgumentMatchers.any(), (String) ArgumentMatchers.any())).thenReturn(tempFile);
        PowerMockito.when(tempFile.getAbsolutePath()).thenReturn(local);
        PowerMockito.whenNew(File.class).withAnyArguments().thenReturn(file);
        PowerMockito.when(file.getPath()).thenReturn(remote);
        PowerMockito.whenNew(File.class).withAnyArguments().thenReturn(file);

        PowerMockito.when(repo.delete(remote)).thenReturn(true);
    }

    @Test
    public void test() throws Exception {
        SwiftRepositoryHealthInspector inspector = PowerMockito.spy(new SwiftRepositoryHealthInspector());

        PowerMockito.when(repo.copyToRemote(local, remote)).thenReturn(true);
        PowerMockito.when(repo.copyFromRemote(remote, local)).thenReturn(local);
        PowerMockito.when(file.exists()).thenReturn(true);
        Assert.assertTrue(inspector.inspect());

        PowerMockito.when(repo.copyToRemote(local, remote)).thenReturn(false);
        PowerMockito.when(repo.copyFromRemote(remote, local)).thenReturn(local);
        PowerMockito.when(file.exists()).thenReturn(true);
        Assert.assertFalse(inspector.inspect());
        PowerMockito.verifyPrivate(inspector, Mockito.times(1 + 5)).invoke("test", tempFile, local, remote);

        PowerMockito.when(repo.copyToRemote(local, remote)).thenReturn(true);
        PowerMockito.when(repo.copyFromRemote(remote, local)).thenReturn(local);
        PowerMockito.when(file.exists()).thenReturn(false);
        Assert.assertFalse(inspector.inspect());
        PowerMockito.verifyPrivate(inspector, Mockito.times(1 + 5 + 5)).invoke("test", tempFile, local, remote);
    }
}
