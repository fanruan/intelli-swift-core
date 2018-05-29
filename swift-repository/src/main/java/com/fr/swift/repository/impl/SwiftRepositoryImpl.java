package com.fr.swift.repository.impl;

import com.fr.swift.file.conf.SwiftFileSystemConfig;
import com.fr.swift.file.exception.SwiftFileException;
import com.fr.swift.file.system.SwiftFileSystem;
import com.fr.swift.repository.AbstractRepository;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

/**
 * @author yee
 * @date 2018/5/28
 */
public class SwiftRepositoryImpl extends AbstractRepository {
    public SwiftRepositoryImpl(SwiftFileSystemConfig configuration) {
        super(configuration);
    }

    @Override
    public URI copyFromRemote(URI remote, URI local) throws IOException {
        SwiftFileSystem from = getFileSystem(remote);
        SwiftFileSystem target = from.read();
        if (target.isFile()) {
            InputStream inputStream = null;
            FileOutputStream fileOutputStream = null;
            try {
                inputStream = target.toStream();
                fileOutputStream = new FileOutputStream(new File(local.getPath()));
                byte[] bytes = new byte[1024];
                int len = 0;
                while ((len = inputStream.read(bytes, 0, len)) > 0) {
                    fileOutputStream.write(bytes, 0, len);
                }
                fileOutputStream.flush();
            } catch (Exception e) {
                throw new SwiftFileException(e);
            } finally {
                if (null != inputStream) {
                    inputStream.close();
                }
                if (null != fileOutputStream) {
                    fileOutputStream.close();
                }
            }
        } else {
            SwiftFileSystem[] systems = from.listFiles();
            for (SwiftFileSystem fileSystem : systems) {
                copyFromRemote(fileSystem.getResourceURI(), local.resolve(fileSystem.getResourceName()));
            }
        }
        return local;
    }

    @Override
    public boolean copyToRemote(URI local, URI remote) throws IOException {
        File file = new File(local.getPath());
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : files) {
                copyToRemote(f.toURI(), remote.resolve(f.getName()));
            }
        } else {
            FileInputStream inputStream = null;
            try {
                inputStream = new FileInputStream(file);
                SwiftFileSystem to = getFileSystem(remote);
                to.write(inputStream);
            } catch (FileNotFoundException e) {
                throw new SwiftFileException(e);
            } finally {
                if (null != inputStream) {
                    inputStream.close();
                }
            }
        }
        return true;
    }

}
