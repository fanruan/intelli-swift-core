package com.fr.swift.file.system;

import com.fr.swift.file.exception.SwiftFileException;

import java.io.Closeable;
import java.io.InputStream;
import java.net.URI;

/**
 * @author yee
 * @date 2018/5/28
 */
public interface SwiftFileSystem extends Closeable {
    boolean write(URI remote, InputStream inputStream) throws SwiftFileException;

    boolean write(InputStream inputStream) throws SwiftFileException;

    SwiftFileSystem read(URI remote) throws SwiftFileException;

    SwiftFileSystem read() throws SwiftFileException;

    boolean remove(URI remote) throws SwiftFileException;

    boolean remove() throws SwiftFileException;

    boolean renameTo(URI src, URI dest) throws SwiftFileException;

    boolean renameTo(URI dest) throws SwiftFileException;

    boolean copy(URI src, URI dest) throws SwiftFileException;

    boolean copy(URI dest) throws SwiftFileException;

    SwiftFileSystem[] listFiles() throws SwiftFileException;

    boolean isExists(URI remote);

    boolean isDirectory();

    boolean isFile();

    InputStream toStream();

    URI getResourceURI();

    String getResourceName();

    @Override
    void close() throws SwiftFileException;
}
