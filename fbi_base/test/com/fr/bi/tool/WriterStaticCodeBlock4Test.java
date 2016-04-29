package com.fr.bi.tool;

import java.io.IOException;

/**
 * Created by Connery on 2016/1/23.
 */
public class WriterStaticCodeBlock4Test extends WriteStaticCodeBlock {

    public WriterStaticCodeBlock4Test(String sourceAbsoluteFilePath, FileOperation fileOperation, String targetPath) throws IOException {

        super(sourceAbsoluteFilePath, fileOperation, targetPath);
    }

    @Override
    public void writeFile() {
        super.writeFile();
    }

    @Override
    public String readFile() throws IOException {
        return "{}";
    }

    @Override
    public String generateStaticCodeBlock() {
        return "";
    }
}