package com.fr.swift.repository.impl;

import com.fr.swift.file.exception.SwiftFileException;
import com.fr.swift.file.system.SwiftFileSystem;
import com.fr.swift.repository.AbstractRepository;
import com.fr.swift.repository.SwiftFileSystemConfig;
import com.fr.swift.repository.utils.SwiftRepositoryUtils;
import com.fr.swift.repository.utils.ZipUtils;
import com.fr.swift.structure.Pair;
import com.fr.swift.util.FileUtil;
import com.fr.swift.util.IoUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

    public SwiftRepositoryImpl() {
        super(null);
    }

    @Override
    public String copyFromRemote(String remote, String local) throws IOException {
        SwiftFileSystem from = createFileSystem(remote);
        File directory = new File(local).getAbsoluteFile().getParentFile();
        if (!directory.exists()) {
            directory.mkdirs();
        }
        if (from.isExists()) {
            FileUtil.delete(new File(local));
            if (!from.isDirectory()) {
                if (from.getResourceName().endsWith(".cubes")) {
                    InputStream inputStream = from.toStream();
                    try {
                        ZipUtils.unZip(new File(local).getParent(), inputStream);
                    } catch (Exception e) {
                        throw new SwiftFileException(e);
                    } finally {
                        closeFileSystem(from);
                    }
                } else {
                    InputStream inputStream = null;
                    FileOutputStream fileOutputStream = null;
                    inputStream = from.toStream();
                    fileOutputStream = new FileOutputStream(new File(local));
                    try {
                        IoUtil.copyBinaryTo(inputStream, fileOutputStream);
                    } catch (Exception e) {
                        throw new SwiftFileException(e);
                    } finally {
                        IoUtil.close(inputStream);
                        IoUtil.close(fileOutputStream);
                        closeFileSystem(from);
                    }
                }
            } else {
                SwiftFileSystem[] systems = from.listFiles();
                for (SwiftFileSystem fileSystem : systems) {
                    copyFromRemote(fileSystem.getResourceURI(), resolve(local, fileSystem.getResourceName()));
                    closeFileSystem(fileSystem);
                }
                closeFileSystem(from);
            }
            return local;
        } else {
            from = createFileSystem(remote + ".cubes");
            if (from.isExists()) {
                return copyFromRemote(remote + ".cubes", local);
            }
            closeFileSystem(from);
            throw new SwiftFileException(String.format("Remote resource '%s' is not exists!", remote));
        }
    }

    @Override
    public boolean copyToRemote(String local, String remote) throws IOException {

        List<Pair<String, String>> dirs = new ArrayList<Pair<String, String>>();
        List<Pair<String, String>> files = new ArrayList<Pair<String, String>>();

        calculateUpload(local, remote, dirs, files);

        if (!dirs.isEmpty()) {
            for (Pair<String, String> dir : dirs) {
                zipToRemote(dir.getKey(), dir.getValue());
            }
        }

        if (!files.isEmpty()) {
            for (Pair<String, String> uri : files) {
                File file = new File(uri.getKey());
                FileInputStream inputStream = null;
                SwiftFileSystem to = createFileSystem(uri.getValue());
                try {
                    inputStream = new FileInputStream(file);
                    if (to.isExists()) {
                        to.remove();
                    }
                    to.write(inputStream);
                } catch (FileNotFoundException e) {
                    throw new SwiftFileException(e);
                } finally {
                    inputStream.close();
                    closeFileSystem(to);
                }
            }

        }
        return true;
    }

    @Override
    public boolean zipToRemote(String local, String remote) throws IOException {
        File file = new File(local);
        File zipFile = new File(TEMP_PATH, file.getName() + ".cubes");
        FileOutputStream fos = new FileOutputStream(zipFile);
        ZipUtils.toZip(file.getAbsolutePath(), fos);
        SwiftFileSystem fileSystem = createFileSystem(remote + ".cubes");
        if (fileSystem.isExists()) {
            fileSystem.remove();
        }
        SwiftFileSystem parent = fileSystem.parent();
        parent.mkdirs();
        closeFileSystem(parent);

        if (copyToRemote(zipFile.getAbsolutePath(), resolve(SwiftRepositoryUtils.getParent(remote), zipFile.getName()))) {
            zipFile.delete();
        }
        closeFileSystem(fileSystem);
        return true;
    }

    @Override
    public boolean delete(String remote) throws IOException {
        SwiftFileSystem system = createFileSystem(remote);
        try {
            return system.remove();
        } finally {
            closeFileSystem(system);
        }
    }

    @Override
    public long getSize(String path) throws IOException {
        SwiftFileSystem system = createFileSystem(path);
        if (system.isExists()) {
            try {
                return system.getSize();
            } finally {
                closeFileSystem(system);
            }
        }
        return 0;
    }

    private void calculateUpload(String root, String remote, List<Pair<String, String>> dirs, List<Pair<String, String>> files) {
        File rootFile = new File(root);
        if (rootFile.isDirectory()) {
            File[] children = rootFile.listFiles();
            List<Pair<String, String>> tempFiles = new ArrayList<Pair<String, String>>();
            for (File child : children) {
                if (child.isFile()) {
                    tempFiles.add(Pair.of(child.getAbsolutePath(), resolve(remote, child.getName())));
                } else {
                    dirs.add(Pair.of(child.getAbsolutePath(), resolve(remote, child.getName())));
                }
            }
        } else {
            files.add(Pair.of(root, remote));
        }
    }
}
