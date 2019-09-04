package com.fr.swift.exception.inspect;

import com.fr.swift.exception.inspect.bean.ComponentHealthInfo;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.repository.manager.SwiftRepositoryManager;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author Marvin
 * @version 1.1
 * Created by Marvin on 8/30/2019
 */
public class SwiftRepositoryHealthInspector implements ComponentHealthInspector<Boolean, ComponentHealthInfo> {

    private static final int MAX_RETRY_TIMES = 5;

    public static final SwiftRepositoryHealthInspector INSTANCE = new SwiftRepositoryHealthInspector();

    @Override
    public Boolean inspect(ComponentHealthInfo info) {
        return false;
    }

    @Override
    public Boolean inspect() {
        try {
            File tempFile = File.createTempFile("SwiftRepositoryHealthCheckTemp", ".tmp");
            String local = tempFile.getAbsolutePath();
            String remote = new File(tempFile.getName()).getPath();
            for (int n = 0; n < MAX_RETRY_TIMES; n++) {
                try {
                    if (test(tempFile, local, remote)) {
                        return true;
                    } else {
                        TimeUnit.MILLISECONDS.sleep(5000);
                    }
                } catch (InterruptedException e) {
                    SwiftLoggers.getLogger().error("Interrupted During Accessible Testing", e);
                } finally {
                    tempFile.delete();
                }
            }
        } catch (IOException e) {
            SwiftLoggers.getLogger().error("Create Template File Failed", e);
        }
        return false;
    }

    private boolean test(File tempFile, String local, String remote) {
        try {
            if (SwiftRepositoryManager.getManager().currentRepo().copyToRemote(local, remote)) {
                try {
                    tempFile.delete();
                    SwiftRepositoryManager.getManager().currentRepo().copyFromRemote(remote, local);
                    File file = new File(local);
                    if (file.exists()) {
                        return true;
                    }
                } finally {
                    try {
                        SwiftRepositoryManager.getManager().currentRepo().delete(remote);
                    } catch (IOException e) {
                        SwiftLoggers.getLogger().error(e);
                    }
                }
            }
        } catch (IOException e) {
            SwiftLoggers.getLogger().error("Catch Exception During Accessible Testing", e);
        }
        return false;
    }

    public static ComponentHealthInspector getInstance() {
        return INSTANCE;
    }
}
