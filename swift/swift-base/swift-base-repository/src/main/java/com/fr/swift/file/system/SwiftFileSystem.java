package com.fr.swift.file.system;

import com.fr.swift.file.exception.SwiftFileException;
import com.fr.swift.repository.SwiftFileSystemConfig;

import java.io.Closeable;
import java.io.InputStream;

/**
 * @author yee
 * @date 2018/5/28
 */
public interface SwiftFileSystem extends Closeable {
    void write(String remote, InputStream inputStream) throws SwiftFileException;

    void write(InputStream inputStream) throws SwiftFileException;

    @Deprecated
    SwiftFileSystem read(String remote) throws SwiftFileException;

    @Deprecated
    SwiftFileSystem read() throws SwiftFileException;

    SwiftFileSystem parent();

    boolean remove(String remote) throws SwiftFileException;

    boolean remove() throws SwiftFileException;

    boolean renameTo(String src, String dest) throws SwiftFileException;

    boolean renameTo(String dest) throws SwiftFileException;

    boolean copy(String src, String dest) throws SwiftFileException;

    boolean copy(String dest) throws SwiftFileException;

    SwiftFileSystem[] listFiles() throws SwiftFileException;

    boolean isExists();

    boolean isDirectory();

    InputStream toStream() throws SwiftFileException;

    String getResourceURI();

    String getResourceName();

    void mkdirs();

    @Override
    void close() throws SwiftFileException;

    SwiftFileSystemConfig getConfig();

    long getSize();

    void testConnection() throws Exception;
}
