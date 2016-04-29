package com.fr.bi.tool;

import com.fr.bi.stable.utils.code.BILogger;

import java.io.*;

/**
 * Created by Connery on 2016/1/23.
 */
public class FileOperation {

    public String readFile(String path) throws IOException {
        InputStream in = null;
        byte[] tempContent = new byte[100];
        int count;
        in = new FileInputStream(path);
        StringBuffer sb = new StringBuffer();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while ((count = in.read(tempContent)) != -1) {
            bos.write(tempContent, 0, count);
        }
        return bos.toString();

    }

    public void writerFile(String javaContent, String targetPath) {
        try {
            FileWriter writer = new FileWriter(targetPath, false);
            writer.write(javaContent);
            writer.close();
        } catch (IOException e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }
    }
}