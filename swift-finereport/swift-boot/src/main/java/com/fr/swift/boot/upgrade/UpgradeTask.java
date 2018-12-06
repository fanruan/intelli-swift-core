package com.fr.swift.boot.upgrade;

import com.fr.swift.SwiftContext;
import com.fr.swift.context.ContextProvider;
import com.fr.swift.context.ContextUtil;
import com.fr.swift.db.SwiftDatabase;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.util.FileUtil;

import java.io.File;
import java.io.IOException;

/**
 * @author anchore
 * @date 2018/9/3
 */
public class UpgradeTask implements Runnable {
    @Override
    public void run() {
        String encodedClassPath = ContextUtil.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        String oldPath = new File(encodedClassPath).isDirectory() ? encodedClassPath + "/../" : encodedClassPath + "/../../",
                newPath = SwiftContext.get().getBean(ContextProvider.class).getContextPath();

        try {
            handleUrlEncodedPath(oldPath, newPath);

            handleUnnecessaryDir(newPath);
        } catch (IOException e) {
            SwiftLoggers.getLogger().error(e);
        }
    }

    private static void handleUrlEncodedPath(String oldPath, String newPath) throws IOException {
        File oldDir = new File(oldPath, SwiftDatabase.DECISION_LOG.getDir()),
                newDir = new File(newPath, SwiftDatabase.DECISION_LOG.getDir());
        FileUtil.move(oldDir, newDir, true);
    }

    private static void handleUnnecessaryDir(String path) throws IOException {
        String unnecessaryPath = String.format("%s/%s/0", path, SwiftDatabase.DECISION_LOG.getBackupDir());
        File oldDir = new File(unnecessaryPath),
                newDir = new File(path, SwiftDatabase.DECISION_LOG.getBackupDir());
        FileUtil.move(oldDir, newDir, true);
    }
}