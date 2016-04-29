package com.fr.bi.tool;

import java.io.IOException;

/**
 * Created by Connery on 2016/1/23.
 */
public class JavaFileAnalyser {
    protected String javaContent;
    protected String sourceAbsoluteFilePath;
    protected String targetPath;
    protected FileOperation fileOperation;

    public JavaFileAnalyser(String sourceAbsoluteFilePath, FileOperation fileOperation, String targetPath) throws IOException {
        this.fileOperation = fileOperation;
        this.sourceAbsoluteFilePath = sourceAbsoluteFilePath;
        javaContent = readFile();
        this.targetPath = targetPath;
    }

    protected String readFile() throws IOException {
        return fileOperation.readFile(sourceAbsoluteFilePath);
    }

    protected int classBodyStartIndex() {
        return javaContent.indexOf("{");
    }

    protected void writeFile() {
        fileOperation.writerFile(javaContent, targetPath);
    }


}