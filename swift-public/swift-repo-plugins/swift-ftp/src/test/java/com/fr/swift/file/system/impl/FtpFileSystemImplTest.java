package com.fr.swift.file.system.impl;

import com.fr.swift.file.exception.SwiftFileException;
import com.fr.swift.file.system.SwiftFileSystem;
import com.fr.swift.file.system.pool.FtpFileSystemPool;
import com.fr.swift.repository.config.FtpRepositoryConfig;
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
 * @date 2018-12-26
 */
@Ignore
public class FtpFileSystemImplTest {
    private static FtpFileSystemPool ftpPool;
    private static FtpFileSystemPool sftpPool;

    @BeforeClass
    public static void beforeClass() {
        FtpRepositoryConfig config = new FtpRepositoryConfig();
        config.setHost("192.168.1.67");
        config.setPort("21");
        config.setUsername("anchore");
        config.setPassword("anchore");
        config.setCharset("GBK");
        config.setRootPath("/");
        ftpPool = new FtpFileSystemPool(config);
        FtpRepositoryConfig config1 = new FtpRepositoryConfig();
        config1.setHost("192.168.1.67");
        config1.setPort("22");
        config1.setUsername("root");
        config1.setPassword("root");
        config1.setRootPath("/root");
        config1.setProtocol("SFTP");
        sftpPool = new FtpFileSystemPool(config1);
    }

    @AfterClass
    public static void close() {
        ftpPool.close();
        sftpPool.close();
    }

    @Test
    public void list() throws SwiftFileException {
        FtpFileSystemImpl fileSystem = ftpPool.borrowObject("/");
        SwiftFileSystem[] list = fileSystem.list();
        assertTrue(list.length > 0);
        ftpPool.returnObject("/", fileSystem);
        fileSystem = sftpPool.borrowObject("/");
        SwiftFileSystem[] list1 = fileSystem.list();
        assertTrue(list1.length > 0);
        sftpPool.returnObject("/", fileSystem);
    }

    @Test
    public void fileSize() throws SwiftFileException {
        FtpFileSystemImpl fileSystem = ftpPool.borrowObject("/FTP1/help/FRDemo.db");
        long fileSize = fileSystem.fileSize();
        assertTrue(fileSize > 0);
        ftpPool.returnObject("/FTP1/help/FRDemo.db", fileSystem);
        fileSystem = sftpPool.borrowObject("reportlets/WorkBook1.cpt");
        fileSize = fileSystem.fileSize();
        assertTrue(fileSize > 0);
        sftpPool.returnObject("reportlets/WorkBook1.cpt", fileSystem);
    }

    @Test
    public void write() throws SwiftFileException {
        String hello = "Hello World";
        FtpFileSystemImpl fileSystem = ftpPool.borrowObject("hello.txt");
        fileSystem.write(new ByteArrayInputStream(hello.getBytes()));
        InputStream is = fileSystem.read().toStream();
        Scanner scanner = new Scanner(is);
        assertEquals(scanner.nextLine(), hello);
        scanner.close();
        ftpPool.returnObject("hello.txt", fileSystem);
        fileSystem = sftpPool.borrowObject("hello.txt");
        fileSystem.write(new ByteArrayInputStream(hello.getBytes()));
        is = fileSystem.read().toStream();
        scanner = new Scanner(is);
        assertEquals(scanner.nextLine(), hello);
        scanner.close();
        sftpPool.returnObject("hello.txt", fileSystem);
    }
}