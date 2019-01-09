package com.fr.swift.file.impl;

import com.fr.swift.file.exception.SwiftFileException;
import com.fr.swift.file.system.SwiftFileSystem;
import org.junit.Ignore;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Scanner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author yee
 * @date 2019-01-08
 */
@Ignore
public class DefaultFileSystemImplTest {

    @Test
    public void list() throws SwiftFileException {
        SwiftFileSystem fileSystem = new DefaultFileSystemImpl("/");
        SwiftFileSystem[] list = fileSystem.listFiles();
        assertTrue(list.length > 0);
    }

    @Test
    public void fileSize() throws SwiftFileException {
        DefaultFileSystemImpl fileSystem = new DefaultFileSystemImpl("/");
        long fileSize = fileSystem.fileSize();
        assertTrue(fileSize > 0);
    }

    @Test
    public void write() throws SwiftFileException {
        String hello = "Hello World";
        SwiftFileSystem fileSystem = new DefaultFileSystemImpl("hello.txt");
        fileSystem.write(new ByteArrayInputStream(hello.getBytes()));
        InputStream is = fileSystem.read().toStream();
        Scanner scanner = new Scanner(is);
        assertEquals(scanner.nextLine(), hello);
        scanner.close();
    }

}