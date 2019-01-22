package com.fr.swift.file.system.impl;

import com.fr.swift.file.exception.SwiftFileException;
import com.fr.swift.file.system.SwiftFileSystem;
import com.fr.swift.file.system.pool.HdfsFileSystemPool;
import com.fr.swift.repository.config.HdfsRepositoryConfig;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Scanner;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

/**
 * @author yee
 * @date 2019-01-08
 */
@Ignore
public class HdfsFileSystemImplTest {
    private static HdfsFileSystemPool hdfsPool;

    @BeforeClass
    public static void beforeClass() {
        HdfsRepositoryConfig config = new HdfsRepositoryConfig();
        config.setHdfsHost("192.168.1.67");
        config.setHdfsPort("9000");
        hdfsPool = new HdfsFileSystemPool(config);
    }

    @AfterClass
    public static void close() {
        hdfsPool.close();
    }

    @Test
    public void list() throws SwiftFileException {
        SwiftFileSystem fileSystem = hdfsPool.borrowObject("/");
        SwiftFileSystem[] list = fileSystem.listFiles();
        assertTrue(list.length > 0);
        hdfsPool.returnObject("/", fileSystem);
    }

    @Test
    public void fileSize() throws SwiftFileException {
        HdfsFileSystemImpl fileSystem = (HdfsFileSystemImpl) hdfsPool.borrowObject("/FTP1/help/FRDemo.db");
        long fileSize = fileSystem.fileSize();
        assertTrue(fileSize > 0);
        hdfsPool.returnObject("/FTP1/help/FRDemo.db", fileSystem);
    }

    @Test
    public void write() throws SwiftFileException {
        String hello = "Hello World";
        SwiftFileSystem fileSystem = hdfsPool.borrowObject("hello.txt");
        fileSystem.write(new ByteArrayInputStream(hello.getBytes()));
        InputStream is = fileSystem.read().toStream();
        Scanner scanner = new Scanner(is);
        assertEquals(scanner.nextLine(), hello);
        scanner.close();
        hdfsPool.returnObject("hello.txt", fileSystem);
    }

}