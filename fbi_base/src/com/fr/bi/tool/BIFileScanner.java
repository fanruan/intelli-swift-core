package com.fr.bi.tool;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Connery on 2016/1/26.
 */
public class BIFileScanner {
    private String startRootFolder;

    public BIFileScanner(String startRootFolder) {
        this.startRootFolder = startRootFolder;
    }
    public List<String> findAllJavaFile(){
        return findAllSpecificFile(Pattern.compile(".*\\.java$"));
    }
    public List<String> findAllSpecificFile(final Pattern pattern) {
        return findAllSpecificFileInFolder(new File(startRootFolder), pattern);
    }

    private List<String> findAllSpecificFileInFolder(File folderPath, final Pattern pattern) {
        File root = folderPath;
        List<String> result = new ArrayList<String>();
        if (root.isDirectory()) {

            File[] files = root.listFiles();
            for (int i = 0; i < files.length; i++) {
                File child = files[i];
                if (child.isDirectory()) {
                    result.addAll(findAllSpecificFileInFolder(child, pattern));
                } else {
                    Matcher matcher = pattern.matcher(child.getName());
                    if (matcher.find()) {
                        result.add(child.getAbsolutePath());
                    }
                }
            }
        }
        return result;
    }
}