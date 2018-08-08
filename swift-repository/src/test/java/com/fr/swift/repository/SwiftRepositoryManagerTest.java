package com.fr.swift.repository;

import com.fr.io.utils.ResourceIOUtils;
import com.fr.stable.Filter;
import com.fr.swift.repository.manager.SwiftRepositoryManager;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author yee
 * @date 2018/5/29
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SwiftRepositoryManagerTest {
    private static SwiftRepository repository;
    private static URI rootURI;
    private static URI targetURI = URI.create(System.getProperty("user.dir") + "/");

    @BeforeClass
    public static void beforeClass() {
        repository = SwiftRepositoryManager.getManager().currentRepo();
        ClassLoader classLoader = SwiftRepositoryManagerTest.class.getClassLoader();
        URL url = classLoader.getResource("from");
        rootURI = URI.create(url.getPath() + "/");
    }

    @Test
    public void test2StepDownload() throws IOException {
        repository.copyFromRemote(targetURI.resolve("upload1.txt"), rootURI.resolve("download1.txt"));
        assertTrue(new File(rootURI.resolve("download1.txt").getPath()).exists());
    }

    @Test
    public void test1StepUpload() throws IOException {
        repository.copyToRemote(rootURI.resolve("test_file1.txt"), targetURI.resolve("upload1.txt"));
        assertTrue(ResourceIOUtils.exist(targetURI.resolve("upload1.txt").getPath()));
    }

    @Test
    public void test4DownloadDirectory() throws IOException {
        repository.copyFromRemote(targetURI.resolve("upload"), rootURI.resolve("downloads"));
        assertTrue(ResourceIOUtils.exist(rootURI.resolve("downloads").getPath()));
        assertEquals(ResourceIOUtils.list(rootURI.resolve("downloads").getPath(), new Filter<String>() {
            @Override
            public boolean accept(String s) {
                return s.endsWith(".txt");
            }
        }).length, 4);
    }

    @Test
    public void test3StepUploadDirectory() throws IOException {
        repository.copyToRemote(rootURI, targetURI.resolve("upload"));
        assertTrue(ResourceIOUtils.exist(targetURI.resolve("upload").getPath()));
        assertEquals(ResourceIOUtils.list(targetURI.resolve("upload").getPath(), new Filter<String>() {
            @Override
            public boolean accept(String s) {
                return s.endsWith(".txt");
            }
        }).length, 4);
    }
}