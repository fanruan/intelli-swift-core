package com.fr.swift.repository.impl;

import com.fr.io.utils.ResourceIOUtils;
import com.fr.swift.file.conf.SwiftFileSystemConfig;
import com.fr.swift.file.exception.SwiftFileException;
import com.fr.swift.file.system.SwiftFileSystem;
import com.fr.swift.repository.AbstractRepository;
import com.fr.swift.repository.utils.ZipUtils;
import com.fr.swift.structure.Pair;
import com.fr.swift.util.FileUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yee
 * @date 2018/5/28
 */
public class SwiftRepositoryImpl extends AbstractRepository {

    private static final String TEMP_PATH = System.getProperty("java.io.tmpdir");

    public SwiftRepositoryImpl(SwiftFileSystemConfig configuration) {
        super(configuration);
    }

    @Override
    public URI copyFromRemote(URI remote, URI local) throws IOException {
        SwiftFileSystem from = createFileSystem(remote);
        File directory = new File(local.getPath()).getParentFile();
        if (!directory.exists()) {
            directory.mkdirs();
        }
        if (from.isExists()) {
            FileUtil.delete(new File(local.getPath()));
            SwiftFileSystem target = from.read();
            if (!target.isDirectory()) {
                if (target.getResourceName().endsWith(".cubes")) {
                    InputStream inputStream = from.toStream();
                    try {
                        ZipUtils.unZip(new File(local.getPath()).getParent(), inputStream);
                    } catch (Exception e) {
                        throw new SwiftFileException(e);
                    }
                } else {
                    InputStream inputStream = null;
                    FileOutputStream fileOutputStream = null;
                    try {
                        inputStream = target.toStream();
                        fileOutputStream = new FileOutputStream(new File(local.getPath()));
                        byte[] bytes = new byte[1024];
                        int len = 0;
                        while ((len = inputStream.read(bytes, 0, bytes.length)) != -1) {
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
                }
            } else {
                SwiftFileSystem[] systems = from.listFiles();
                for (SwiftFileSystem fileSystem : systems) {
                    copyFromRemote(fileSystem.getResourceURI(), resolve(local, fileSystem.getResourceName()));
                }
            }
            return local;
        } else {
            from = createFileSystem(URI.create(remote.getPath() + ".cubes"));
            if (from.isExists()) {
                return copyFromRemote(URI.create(remote.getPath() + ".cubes"), local);
            }
            throw new SwiftFileException(String.format("Remote resource '%s' is not exists!", remote.getPath()));
        }
    }

    @Override
    public boolean copyToRemote(URI local, URI remote) throws IOException {

        List<Pair<URI, URI>> dirs = new ArrayList<Pair<URI, URI>>();
        List<Pair<URI, URI>> files = new ArrayList<Pair<URI, URI>>();

        calculateUpload(local, remote, dirs, files);

        if (!dirs.isEmpty()) {
            for (Pair<URI, URI> dir : dirs) {
                File file = new File(dir.getKey().getPath());
                File zipFile = new File(TEMP_PATH, file.getName() + ".cubes");
                FileOutputStream fos = new FileOutputStream(zipFile);
                ZipUtils.toZip(file.getAbsolutePath(), fos);
                fos.close();
                SwiftFileSystem fileSystem = createFileSystem(dir.getValue());
                if (fileSystem.isExists()) {
                    fileSystem.remove();
                }
                fileSystem.mkdirs();
                fileSystem.remove();
                if (copyToRemote(zipFile.toURI(), resolve(URI.create(ResourceIOUtils.getParent(dir.getValue().getPath())), zipFile.getName()))) {
                    zipFile.delete();
                }
            }
        }

        if (!files.isEmpty()) {
            for (Pair<URI, URI> uri : files) {
                File file = new File(uri.getKey().getPath());
                FileInputStream inputStream = null;
                try {
                    inputStream = new FileInputStream(file);
                    SwiftFileSystem to = createFileSystem(uri.getValue());
                    if (to.isExists()) {
                        to.remove();
                    }
                    to.write(inputStream);
                } catch (FileNotFoundException e) {
                    throw new SwiftFileException(e);
                } finally {
                    if (null != inputStream) {
                        inputStream.close();
                    }
                }
            }

        }
        return true;
    }

    private void calculateUpload(URI root, URI remote, List<Pair<URI, URI>> dirs, List<Pair<URI, URI>> files) {
        File rootFile = new File(root.getPath());
        if (rootFile.isDirectory()) {
            File[] children = rootFile.listFiles();
            List<Pair<URI, URI>> tempFiles = new ArrayList<Pair<URI, URI>>();
            for (File child : children) {
                if (child.isFile()) {
                    tempFiles.add(Pair.of(child.toURI(), resolve(remote, child.getName())));
                } else {
                    dirs.add(Pair.of(child.toURI(), resolve(remote, child.getName())));
                }
            }
        } else {
            files.add(Pair.of(root, remote));
        }
    }

    private URI resolve(URI uri, String resolve) {
        String path = uri.getPath();
        if (path.endsWith("/")) {
            return uri.resolve(resolve);
        }
        return URI.create(path + "/").resolve(resolve);
    }
}
